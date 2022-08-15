package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.OrderSummaryAdapter;
import hieutm.dev.snakesneaker.item.CartList;
import hieutm.dev.snakesneaker.response.DataRP;
import hieutm.dev.snakesneaker.response.OrderSummaryRP;
import hieutm.dev.snakesneaker.response.RemoveCouponRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.DecimalConverter;
import hieutm.dev.snakesneaker.util.Events;
import hieutm.dev.snakesneaker.util.GlobalBus;
import hieutm.dev.snakesneaker.util.Method;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderSummary extends AppCompatActivity {

    private Method method;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private View viewSave;
    private String sign;
    private boolean isCartEmpty = false;
    private RelativeLayout relMain;
    private List<CartList> cartLists;
    private OrderSummaryAdapter orderSummaryAdapter;
    private LinearLayout llEmpty;
    private LinearLayout linearLayoutAddress;
    private MaterialButton buttonPayment, buttonAddress, buttonShop;
    private String type, productId, productSize, couponId, addressId;
    private MaterialTextView textViewNoData, textViewName, textViewAddType, textViewAddress, textViewPhoneNo, textViewItem, textViewTotalPrice,
            textViewDelivery, textViewTotalAmount, textViewSave, textViewPlacePrice, textViewCoupon;
    private String distance = " ";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        GlobalBus.getBus().register(this);

        method = new Method(OrderSummary.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(OrderSummary.this);

        cartLists = new ArrayList<>();

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        productId = intent.getStringExtra("product_id");
        productSize = intent.getStringExtra("product_size");

        sign = distance + Constant.currency;

        MaterialToolbar toolbar = findViewById(R.id.toolbar_order_sum);
        toolbar.setTitle(getResources().getString(R.string.order_summery));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relMain = findViewById(R.id.rel_main_order_sum);
        llEmpty = findViewById(R.id.ll_empty_order_sum);
        textViewNoData = findViewById(R.id.textView_noData_order_sum);
        progressBar = findViewById(R.id.progressBar_order_sum);
        recyclerView = findViewById(R.id.recyclerView_order_sum);
        textViewItem = findViewById(R.id.textView_item_order_sum);
        textViewName = findViewById(R.id.textView_name_order_sum);
        textViewAddType = findViewById(R.id.textView_addType_order_sum);
        textViewAddress = findViewById(R.id.textView_add_order_sum);
        textViewPhoneNo = findViewById(R.id.textView_phoneNo_order_sum);
        textViewTotalPrice = findViewById(R.id.textView_totalPrice_order_sum);
        textViewDelivery = findViewById(R.id.textView_delivery_order_sum);
        textViewTotalAmount = findViewById(R.id.textView_totalAmount_order_sum);
        textViewSave = findViewById(R.id.textView_save_order_sum);
        textViewPlacePrice = findViewById(R.id.textView_placePrice_order_sum);
        textViewCoupon = findViewById(R.id.textView_coupon_order_sum);
        viewSave = findViewById(R.id.view_save_order_sum);
        buttonAddress = findViewById(R.id.button_address_order_sum);
        buttonPayment = findViewById(R.id.button_by_order_sum);
        buttonShop = findViewById(R.id.button_cart);
        linearLayoutAddress = findViewById(R.id.linearLayout_address_order_sum);

        relMain.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderSummary.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        LinearLayout linearLayout = findViewById(R.id.linearLayout_order_sum);
        method.bannerAd(linearLayout);

        buttonShop.setOnClickListener(v -> {
            startActivity(new Intent(OrderSummary.this, MainActivity.class));
            finishAffinity();
        });

        if (method.isNetworkAvailable()) {
            order(method.userId());
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getData(Events.CouponCodeAmount couponCodeAmount) {

        textViewTotalPrice.setText(DecimalConverter.currencyFormat(couponCodeAmount.getPrice()) + sign);
        textViewTotalAmount.setText(DecimalConverter.currencyFormat(couponCodeAmount.getPayable_amt()) + sign);
        textViewPlacePrice.setText(DecimalConverter.currencyFormat(couponCodeAmount.getPayable_amt()) + sign);
        if (couponCodeAmount.getYou_save_msg().equals("")) {
            textViewSave.setVisibility(View.GONE);
            viewSave.setVisibility(View.GONE);
        } else {
            textViewSave.setVisibility(View.VISIBLE);
            viewSave.setVisibility(View.VISIBLE);
            textViewSave.setText(DecimalConverter.saveFormat(couponCodeAmount.getYou_save_msg()));
        }

        textViewCoupon.setText(getResources().getString(R.string.remove_coupon_code));
        textViewCoupon.setTextColor(getResources().getColor(R.color.red));
        couponId = couponCodeAmount.getCoupon_id();

    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void geString(Events.UpdateCartOrderSum updateCartOrderSum) {

        if (cartLists.size() == 0) {
            relMain.setVisibility(View.GONE);
            llEmpty.setVisibility(View.VISIBLE);
        }

        if (orderSummaryAdapter != null) {

            String item = getResources().getString(R.string.price) + " " + "(" + updateCartOrderSum.getTotal_item() + " "
                    + getResources().getString(R.string.items) + ")";

            textViewItem.setText(item);
            textViewTotalPrice.setText(DecimalConverter.currencyFormat(updateCartOrderSum.getPrice()) + sign);
            textViewTotalAmount.setText(DecimalConverter.currencyFormat(updateCartOrderSum.getPayable_amt()) + sign);
            textViewDelivery.setText(updateCartOrderSum.getDelivery_charge());
            if (method.isNumeric(updateCartOrderSum.getDelivery_charge())) {
                textViewDelivery.setText(DecimalConverter.currencyFormat(updateCartOrderSum.getDelivery_charge()) + sign);
            } else {
                textViewDelivery.setText(updateCartOrderSum.getDelivery_charge());
            }
            textViewPlacePrice.setText(DecimalConverter.currencyFormat(updateCartOrderSum.getPayable_amt()) + sign);
            if (updateCartOrderSum.getYou_save_msg().equals("")) {
                textViewSave.setVisibility(View.GONE);
                viewSave.setVisibility(View.GONE);
            } else {
                textViewSave.setVisibility(View.VISIBLE);
                viewSave.setVisibility(View.VISIBLE);
                textViewSave.setText(DecimalConverter.saveFormat(updateCartOrderSum.getYou_save_msg()));
            }

        }
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void getData(Events.UpdateDeliveryAddress updateDeliveryAddress) {

        linearLayoutAddress.setVisibility(View.VISIBLE);

        addressId = updateDeliveryAddress.getAddressId();
        textViewName.setText(updateDeliveryAddress.getName());
        textViewAddress.setText(updateDeliveryAddress.getAddress());
        textViewPhoneNo.setText(updateDeliveryAddress.getMobileNo());
        textViewAddType.setText(updateDeliveryAddress.getAddressType());

    }

    private void order(String userId) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(OrderSummary.this));
        if (type.equals("by_now")) {
            jsObj.addProperty("buy_now", "true");
            jsObj.addProperty("product_id", productId);
            jsObj.addProperty("product_size", productSize);
        } else {
            jsObj.addProperty("buy_now", "false");
        }
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("device_id", method.getDeviceId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<OrderSummaryRP> call = apiService.getOrderDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<OrderSummaryRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<OrderSummaryRP> call, @NotNull Response<OrderSummaryRP> response) {

                try {

                    OrderSummaryRP orderSummaryRP = response.body();

                    assert orderSummaryRP != null;
                    if (orderSummaryRP.getStatus().equals("1")) {

                        if (orderSummaryRP.getSuccess().equals("1")) {

                            couponId = orderSummaryRP.getCoupon_id();
                            addressId = orderSummaryRP.getAddress_id();

                            cartLists.addAll(orderSummaryRP.getCartLists());

                            Events.CartItem cartItem = new Events.CartItem(orderSummaryRP.getCart_items());
                            GlobalBus.getBus().post(cartItem);

                            if (couponId.equals("0")) {
                                textViewCoupon.setText(getResources().getString(R.string.apply_coupon_code));
                                textViewCoupon.setTextColor(getResources().getColor(R.color.green));
                            } else {
                                textViewCoupon.setText(getResources().getString(R.string.remove_coupon_code));
                                textViewCoupon.setTextColor(getResources().getColor(R.color.red));
                            }

                            if (orderSummaryRP.getAddress().equals("")) {
                                linearLayoutAddress.setVisibility(View.GONE);
                            }

                            String item = getResources().getString(R.string.price) + " " + "(" + orderSummaryRP.getTotal_item() + " "
                                    + getResources().getString(R.string.items) + ")";

                            textViewName.setText(orderSummaryRP.getName());
                            textViewAddress.setText(orderSummaryRP.getAddress());
                            textViewPhoneNo.setText(orderSummaryRP.getMobile_no());
                            textViewAddType.setText(orderSummaryRP.getAddress_type());
                            textViewItem.setText(item);
                            textViewTotalPrice.setText(DecimalConverter.currencyFormat(orderSummaryRP.getPrice()) + sign);
                            if (method.isNumeric(orderSummaryRP.getDelivery_charge())) {
                                textViewDelivery.setText(DecimalConverter.currencyFormat(orderSummaryRP.getDelivery_charge()) + sign);
                            } else {
                                textViewDelivery.setText(orderSummaryRP.getDelivery_charge());
                            }
                            textViewTotalAmount.setText(DecimalConverter.currencyFormat(orderSummaryRP.getPayable_amt()) + sign);
                            textViewPlacePrice.setText(DecimalConverter.currencyFormat(orderSummaryRP.getPayable_amt()) + sign);

                            if (orderSummaryRP.getYou_save_msg().equals("")) {
                                textViewSave.setVisibility(View.GONE);
                                viewSave.setVisibility(View.GONE);
                            } else {
                                textViewSave.setVisibility(View.VISIBLE);
                                viewSave.setVisibility(View.VISIBLE);
                                textViewSave.setText(DecimalConverter.saveFormat(orderSummaryRP.getYou_save_msg()));
                            }

                            if (cartLists.size() != 0) {

                                orderSummaryAdapter = new OrderSummaryAdapter(OrderSummary.this, type, cartLists);
                                recyclerView.setAdapter(orderSummaryAdapter);

                                relMain.setVisibility(View.VISIBLE);

                            } else {
                                llEmpty.setVisibility(View.VISIBLE);
                            }


                            buttonAddress.setOnClickListener(v -> startActivity(new Intent(OrderSummary.this, ChangeAddress.class)));

                            buttonPayment.setOnClickListener(v -> {
                                if (!addressId.equals("")) {
                                    startActivity(new Intent(OrderSummary.this, PaymentMethod.class)
                                            .putExtra("type", type)
                                            .putExtra("coupon_id", couponId)
                                            .putExtra("address_id", addressId)
                                            .putExtra("cart_ids", orderSummaryRP.getCart_ids()));
                                } else {
                                    method.alertBox(getResources().getString(R.string.please_add_address));
                                }
                            });

                            textViewCoupon.setOnClickListener(v -> {
                                if (couponId.equals("0")) {
                                    startActivity(new Intent(OrderSummary.this, Coupons.class)
                                            .putExtra("cart_ids", orderSummaryRP.getCart_ids())
                                            .putExtra("type", type));
                                } else {
                                    removeCoupon(method.userId(), couponId);
                                }
                            });

                        } else {
                            isCartEmpty = true;
                            textViewNoData.setText(orderSummaryRP.getMsg());
                            llEmpty.setVisibility(View.VISIBLE);
                        }

                    } else if (orderSummaryRP.getStatus().equals("2")) {
                        method.suspend(orderSummaryRP.getMessage());
                    } else {
                        method.alertBox(orderSummaryRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<OrderSummaryRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void removeCoupon(String userId, String sendCouponId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(OrderSummary.this));
        jsObj.addProperty("user_id", userId);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        jsObj.addProperty("coupon_id", sendCouponId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RemoveCouponRP> call = apiService.getRemoveCouponDetail(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RemoveCouponRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<RemoveCouponRP> call, @NotNull Response<RemoveCouponRP> response) {


                try {

                    RemoveCouponRP removeCouponRP = response.body();

                    assert removeCouponRP != null;
                    if (removeCouponRP.getStatus().equals("1")) {

                        if (removeCouponRP.getSuccess().equals("1")) {

                            couponId = "0";

                            textViewTotalPrice.setText(DecimalConverter.currencyFormat(removeCouponRP.getPrice()) + sign);
                            textViewTotalAmount.setText(DecimalConverter.currencyFormat(removeCouponRP.getPayable_amt()) + sign);
                            textViewPlacePrice.setText(DecimalConverter.currencyFormat(removeCouponRP.getPayable_amt()) + sign);

                            if (removeCouponRP.getYou_save_msg().equals("")) {
                                textViewSave.setVisibility(View.GONE);
                                viewSave.setVisibility(View.GONE);
                            } else {
                                textViewSave.setVisibility(View.VISIBLE);
                                viewSave.setVisibility(View.VISIBLE);
                                textViewSave.setText(DecimalConverter.saveFormat(removeCouponRP.getYou_save_msg()));
                            }

                            textViewCoupon.setText(getResources().getString(R.string.apply_coupon_code));
                            textViewCoupon.setTextColor(getResources().getColor(R.color.green));

                        } else if (removeCouponRP.getSuccess().equals("2")) {
                            method.showStatus(removeCouponRP.getMsg());
                        } else {
                            method.alertBox(removeCouponRP.getMsg());
                        }

                    } else if (removeCouponRP.getStatus().equals("2")) {
                        method.suspend(removeCouponRP.getMessage());
                    } else {
                        method.alertBox(removeCouponRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RemoveCouponRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    public void destroyData(String userId, String deviceId) {

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(OrderSummary.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("device_id", deviceId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DataRP> call = apiService.removeTempCart(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DataRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<DataRP> call, @NotNull Response<DataRP> response) {

                try {
                    DataRP body = response.body();
                    assert body != null;
                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                }

            }

            @Override
            public void onFailure(@NotNull Call<DataRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isCartEmpty) {
            startActivity(new Intent(OrderSummary.this, MainActivity.class));
            finishAffinity();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        destroyData(method.userId(), method.getDeviceId());
        Log.d("onDestroy_orderSummery", "onDestroy_orderSummery");
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}
