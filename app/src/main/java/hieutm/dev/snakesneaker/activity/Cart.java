package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.adapter.CartAdapter;
import hieutm.dev.snakesneaker.item.CartList;
import hieutm.dev.snakesneaker.response.CartRP;
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

public class Cart extends AppCompatActivity {

    private Method method;
    private String sign;
    private View viewSave;
    private MaterialToolbar toolbar;
    private ProgressBar progressBar;
    private List<CartList> cartLists;
    private CartAdapter cartAdapter;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private RelativeLayout relMain;
    private LinearLayout llEmpty;
    private ImageView imageView;
    private MaterialButton button, buttonEmpty;
    private MaterialTextView textViewNoData, textViewItem, textViewTotalPrice, textViewDelivery, textViewTotalAmount, textViewSave, textViewPlacePrice;
    private String distance = " ";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        GlobalBus.getBus().register(this);

        cartLists = new ArrayList<>();

        sign = distance + Constant.currency;

        method = new Method(Cart.this);
        method.forceRTLIfSupported();

        toolbar = findViewById(R.id.toolbar_cart);
        toolbar.setTitle(getResources().getString(R.string.my_cart));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relMain = findViewById(R.id.rel_cart);
        progressBar = findViewById(R.id.progressBar_cart);
        llEmpty = findViewById(R.id.ll_empty_cart);
        imageView = findViewById(R.id.imageView_cart);
        textViewNoData = findViewById(R.id.textView_noData_cart);
        buttonEmpty = findViewById(R.id.button_cart);
        recyclerView = findViewById(R.id.recyclerView_cart);
        textViewItem = findViewById(R.id.textView_item_cart);
        textViewTotalPrice = findViewById(R.id.textView_total_price_cart);
        textViewDelivery = findViewById(R.id.textView_delivery_cart);
        textViewTotalAmount = findViewById(R.id.textView_totalAmount_cart);
        textViewSave = findViewById(R.id.textView_save_cart);
        textViewPlacePrice = findViewById(R.id.textView_placePrice_cart);
        viewSave = findViewById(R.id.view_save_cart);
        button = findViewById(R.id.button_order_cart);

        relMain.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        linearLayout = findViewById(R.id.linearLayout_cart);
        method.bannerAd(linearLayout);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Cart.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        if (method.isNetworkAvailable()) {
            if (method.isLogin()) {
                cart(method.userId());
            } else {
                changView(true, getResources().getString(R.string.you_have_not_login));
            }
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changView(boolean isValue, String message) {
        llEmpty.setVisibility(View.VISIBLE);
        if (isValue) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_login));
            textViewNoData.setText(message);
            buttonEmpty.setText(getResources().getString(R.string.login));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.empty_cart));
            textViewNoData.setText(message);
            buttonEmpty.setText(getResources().getString(R.string.shop_now));
        }
        buttonEmpty.setOnClickListener(v -> {
            if (isValue) {
                startActivity(new Intent(Cart.this, Login.class));
            } else {
                startActivity(new Intent(Cart.this, MainActivity.class));
                finishAffinity();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    public void geString(Events.UpdateCart updateCart) {

        if (cartLists.size() == 0) {
            relMain.setVisibility(View.GONE);
            changView(false, updateCart.getMsg());
        }

        if (cartAdapter != null) {

            String item = getResources().getString(R.string.price) + " " + "(" + updateCart.getTotal_item() + " "
                    + getResources().getString(R.string.items) + ")";

            textViewItem.setText(item);
            textViewTotalPrice.setText(DecimalConverter.currencyFormat(updateCart.getPrice()) + sign);
            textViewTotalAmount.setText(DecimalConverter.currencyFormat(updateCart.getPayable_amt()) + sign);
            textViewDelivery.setText(updateCart.getDelivery_charge());
            if (method.isNumeric(updateCart.getDelivery_charge())) {
                textViewDelivery.setText(DecimalConverter.currencyFormat(updateCart.getDelivery_charge()) + sign);
            } else {
                textViewDelivery.setText(updateCart.getDelivery_charge());
            }
            textViewPlacePrice.setText(DecimalConverter.currencyFormat(updateCart.getPayable_amt()) + sign);
            if (updateCart.getYou_save_msg().equals("")) {
                textViewSave.setVisibility(View.GONE);
                viewSave.setVisibility(View.GONE);
            } else {
                textViewSave.setVisibility(View.VISIBLE);
                viewSave.setVisibility(View.VISIBLE);
                textViewSave.setText(DecimalConverter.saveFormat(updateCart.getYou_save_msg()));
            }

        }
    }

    private void cart(String userId) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(Cart.this));
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CartRP> call = apiService.getCartList(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CartRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<CartRP> call, @NotNull Response<CartRP> response) {

                try {

                    CartRP cartRP = response.body();

                    assert cartRP != null;
                    if (cartRP.getStatus().equals("1")) {

                        if (cartRP.getSuccess().equals("1")) {

                            cartLists.addAll(cartRP.getCartLists());

                            String item = getResources().getString(R.string.price) + " " + "(" + cartRP.getTotal_item() + " "
                                    + getResources().getString(R.string.items) + ")";

                            textViewItem.setText(item);
                            textViewTotalPrice.setText(DecimalConverter.currencyFormat(cartRP.getPrice()) + sign);
                            if (method.isNumeric(cartRP.getDelivery_charge())) {
                                textViewDelivery.setText(DecimalConverter.currencyFormat(cartRP.getDelivery_charge()) + sign);
                            } else {
                                textViewDelivery.setText(cartRP.getDelivery_charge());
                            }
                            textViewTotalAmount.setText(DecimalConverter.currencyFormat(cartRP.getPayable_amt()) + sign);
                            textViewPlacePrice.setText(DecimalConverter.currencyFormat(cartRP.getPayable_amt()) + sign);
                            if (cartRP.getYou_save_msg().equals("")) {
                                textViewSave.setVisibility(View.GONE);
                                viewSave.setVisibility(View.GONE);
                            } else {
                                textViewSave.setVisibility(View.VISIBLE);
                                viewSave.setVisibility(View.VISIBLE);
                                textViewSave.setText(DecimalConverter.saveFormat(cartRP.getYou_save_msg()));
                            }

                            if (cartLists.size() != 0) {
                                cartAdapter = new CartAdapter(Cart.this, cartLists);
                                recyclerView.setAdapter(cartAdapter);
                                relMain.setVisibility(View.VISIBLE);
                            } else {
                                changView(false, getResources().getString(R.string.your_cart_is_empty));
                            }

                            Events.CartItem cartItem = new Events.CartItem(cartRP.getTotal_item());
                            GlobalBus.getBus().post(cartItem);

                            button.setOnClickListener(v -> {
                                if (cartRP.getAddress_count().equals("0")) {
                                    //add to address then after go to the order summary page
                                    startActivity(new Intent(Cart.this, AddAddress.class)
                                            .putExtra("type", "check_address")
                                            .putExtra("type_order", "cart")
                                            .putExtra("product_id", "")
                                            .putExtra("product_size", ""));
                                } else {
                                    startActivity(new Intent(Cart.this, OrderSummary.class)
                                            .putExtra("type", "cart")
                                            .putExtra("product_id", "")
                                            .putExtra("product_size", ""));
                                }
                            });

                        } else {
                            changView(false, cartRP.getMsg());
                        }

                    } else if (cartRP.getStatus().equals("2")) {
                        method.suspend(cartRP.getMessage());
                    } else {
                        method.alertBox(cartRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<CartRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        GlobalBus.getBus().unregister(this);
        super.onDestroy();
    }

}
