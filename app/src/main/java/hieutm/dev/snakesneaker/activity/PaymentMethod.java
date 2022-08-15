package hieutm.dev.snakesneaker.activity;

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

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.interfaces.PaymentSubmit;
import hieutm.dev.snakesneaker.response.MoMoPayRP;
import hieutm.dev.snakesneaker.response.PaymentMethodRP;
import hieutm.dev.snakesneaker.response.PaymentSubmitRP;
import hieutm.dev.snakesneaker.response.RazorPayRP;
import hieutm.dev.snakesneaker.response.StripPaymentCheckRp;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.util.PaymentSubmitData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hieutm.dev.snakesneaker.util.PaymentSubmitDataMoMo;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.momo.momo_partner.AppMoMoLib;

public class PaymentMethod extends AppCompatActivity implements PaymentResultListener {

    private Method method;
    private ProgressDialog progressDialog;
    private LinearLayout llPayment;
    private ProgressBar progressBar;
    private RelativeLayout relNoData;
    private String type, couponId, addressId, cartIds, razorPayOrderId = "", orderIDMomo;
    private MaterialCardView cardViewMoMo, cardViewPayPal, cardViewStrip, cardViewCash, cardViewRazorPay, cardViewPayStack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION

        method = new Method(PaymentMethod.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(PaymentMethod.this);

        type = getIntent().getStringExtra("type");
        couponId = getIntent().getStringExtra("coupon_id");
        addressId = getIntent().getStringExtra("address_id");
        cartIds = getIntent().getStringExtra("cart_ids");

        MaterialToolbar toolbar = findViewById(R.id.toolbar_payment_method);
        toolbar.setTitle(getResources().getString(R.string.payment));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        relNoData = findViewById(R.id.rel_noDataFound);
        progressBar = findViewById(R.id.progressBar_payment_method);
        cardViewCash = findViewById(R.id.cardView_cash_payment_method);
        cardViewPayPal = findViewById(R.id.cardView_payPal_payment_method);
        cardViewMoMo = findViewById(R.id.cardView_momo_payment_method);
        cardViewStrip = findViewById(R.id.cardView_strip_payment_method);
        cardViewRazorPay = findViewById(R.id.cardView_razorpay_payment_method);
        cardViewPayStack = findViewById(R.id.cardView_payStack_payment_method);
        llPayment = findViewById(R.id.ll_main_payment_method);
        LinearLayout linearLayout = findViewById(R.id.linearLayout_payment_method);

        llPayment.setVisibility(View.GONE);
        relNoData.setVisibility(View.GONE);
        method.bannerAd(linearLayout);

        cardViewCash.setVisibility(View.GONE);
        cardViewPayPal.setVisibility(View.GONE);
        cardViewStrip.setVisibility(View.GONE);
        cardViewRazorPay.setVisibility(View.GONE);
        cardViewPayStack.setVisibility(View.GONE);

        if (method.isNetworkAvailable()) {
            paymentDetail();
        } else {
            progressBar.setVisibility(View.GONE);
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void paymentDetail() {

        progressBar.setVisibility(View.VISIBLE);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PaymentMethod.this));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentMethodRP> call = apiService.getPaymentMethod(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaymentMethodRP>() {
            @Override
            public void onResponse(@NotNull Call<PaymentMethodRP> call, @NotNull Response<PaymentMethodRP> response) {

                try {

                    PaymentMethodRP paymentMethodRP = response.body();

                    assert paymentMethodRP != null;
                    if (paymentMethodRP.getStatus().equals("1")) {

                        if (paymentMethodRP.getSuccess().equals("1")) {

                            if (paymentMethodRP.getCod_status().equals("true")) {
                                cardViewCash.setVisibility(View.VISIBLE);
                                cardViewCash.setOnClickListener(v -> {
                                    new PaymentSubmitData(PaymentMethod.this).payMeantSubmit(method.userId(), couponId, addressId, cartIds, type, "cod", "0", "0", new PaymentSubmit() {
                                        @Override
                                        public void paymentStart() {
                                            progressDialog.show();
                                            progressDialog.setMessage(getResources().getString(R.string.processing));
                                            progressDialog.setCancelable(false);
                                        }

                                        @Override
                                        public void paymentSuccess(PaymentSubmitRP paymentSubmitRP) {
                                            progressDialog.dismiss();
                                            method.conformDialog(paymentSubmitRP);
                                        }

                                        @Override
                                        public void paymentFail(String message) {
                                            progressDialog.dismiss();
                                            method.showStatus(message);
                                        }

                                        @Override
                                        public void suspendUser(String message) {
                                            progressDialog.dismiss();
                                            method.suspend(message);
                                        }
                                    });
                                });
                            }

                            cardViewMoMo.setVisibility(View.VISIBLE);
                            cardViewMoMo.setOnClickListener(v -> {
                                moMoPayPayment(method.userId(), cartIds);
                            });

                            if (paymentMethodRP.getBraintree_status().equals("true")) {
                                cardViewPayPal.setVisibility(View.VISIBLE);
                                cardViewPayPal.setOnClickListener(v -> {
                                    startActivity(new Intent(PaymentMethod.this, BraintreePayment.class)
                                            .putExtra("type", type)
                                            .putExtra("coupon_id", couponId)
                                            .putExtra("address_id", addressId)
                                            .putExtra("cart_ids", cartIds));
                                    finish();
                                });
                            }

                            if (paymentMethodRP.getStripe_status().equals("true")) {
                                cardViewStrip.setVisibility(View.VISIBLE);
                                cardViewStrip.setOnClickListener(v -> stripPaymentCheck(method.userId()));
                            }

                            if (paymentMethodRP.getRazorpay_status().equals("true")) {
                                cardViewRazorPay.setVisibility(View.VISIBLE);
                                cardViewRazorPay.setOnClickListener(v -> razorPayPayment(method.userId(), cartIds));
                            }

                            if (paymentMethodRP.getPaystack_status().equals("true")) {
                                cardViewPayStack.setVisibility(View.VISIBLE);
                                cardViewPayStack.setOnClickListener(v -> {
                                    startActivity(new Intent(PaymentMethod.this, PayStackPayment.class)
                                            .putExtra("type", type)
                                            .putExtra("coupon_id", couponId)
                                            .putExtra("address_id", addressId)
                                            .putExtra("cart_ids", cartIds));
                                    finish();
                                });
                            }

                            llPayment.setVisibility(View.VISIBLE);

                        } else {
                            relNoData.setVisibility(View.VISIBLE);
                            method.alertBox(paymentMethodRP.getMsg());
                        }

                    } else if (paymentMethodRP.getStatus().equals("2")) {
                        method.suspend(paymentMethodRP.getMessage());
                    } else {
                        relNoData.setVisibility(View.VISIBLE);
                        method.alertBox(paymentMethodRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(@NotNull Call<PaymentMethodRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                relNoData.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    //----------------------- MoMo payment call -------------------------------//

    public void moMoPayPayment(String userId, String cartIds) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PaymentMethod.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("cart_ids", cartIds);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MoMoPayRP> call = apiService.getMoMoPayData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<MoMoPayRP>() {
            @Override
            public void onResponse(@NotNull Call<MoMoPayRP> call, @NotNull Response<MoMoPayRP> response) {

                try {
                    MoMoPayRP moMoPayRP = response.body();
                    assert moMoPayRP != null;

                    if (moMoPayRP.getStatus().equals("1")) {

                        if (moMoPayRP.getSuccess().equals("1")) {
                            orderIDMomo = moMoPayRP.getOrder_id();
                            String amount = "1801";
                            String fee = "0";
                            int environment = 0;//developer default
                            String merchantName = "Snake Sneaker";
                            String merchantCode = "MOMO8ULW20220708";
                            String merchantNameLabel = "Nhà cung cấp";
                            String orderId = moMoPayRP.getOrder_id();
                            String description = moMoPayRP.getDescription();

                            AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
                            AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
//                          if (edAmount != null && edAmount.trim().length() != 0)
                            amount = moMoPayRP.getPayableAmt().substring(0, moMoPayRP.getPayableAmt().length() - 2);

                            Map<String, Object> eventValue = new HashMap<>();
                            //client Required
                            eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
                            eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
                            eventValue.put("amount", amount); //Kiểu integer
                            eventValue.put("orderId", orderId); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
                            eventValue.put("orderLabel", "Mã đơn hàng"); //gán nhãn

                            //client Optional - bill info
                            eventValue.put("merchantnamelabel", merchantNameLabel);//gán nhãn
                            eventValue.put("fee", fee); //Kiểu integer
                            eventValue.put("description", description); //mô tả đơn hàng - short description

                            //client extra data
                            eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
                            eventValue.put("partnerCode", merchantCode);
                            //Example extra data
                            JSONObject objExtraData = new JSONObject();
                            try {
                                objExtraData.put("site_code", "008");
                                objExtraData.put("site_name", "CGV Cresent Mall");
                                objExtraData.put("screen_code", 0);
                                objExtraData.put("screen_name", "Special");
                                objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
                                objExtraData.put("movie_format", "2D");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            eventValue.put("extraData", objExtraData.toString());

                            eventValue.put("extra", "");
                            AppMoMoLib.getInstance().requestMoMoCallBack(PaymentMethod.this, eventValue);

                        } else if (moMoPayRP.getSuccess().equals("2")) {
                            method.showStatus(moMoPayRP.getMsg());
                        } else {
                            method.alertBox(moMoPayRP.getMsg());
                        }

                    } else if (moMoPayRP.getStatus().equals("2")) {
                        method.suspend(moMoPayRP.getMessage());
                    } else {
                        method.alertBox(moMoPayRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<MoMoPayRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
//                    method.alertBox("message: " + "Get token " + data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order

                        new PaymentSubmitDataMoMo(PaymentMethod.this).payMeantSubmit(method.userId(), couponId, addressId, cartIds, type, "MoMo", token, "0", orderIDMomo, new PaymentSubmit() {
                            @Override
                            public void paymentStart() {
                                progressDialog.show();
                                progressDialog.setMessage(getResources().getString(R.string.processing));
                                progressDialog.setCancelable(false);
                            }

                            @Override
                            public void paymentSuccess(PaymentSubmitRP paymentSubmitRP) {
                                progressDialog.dismiss();
                                method.conformDialog(paymentSubmitRP);
                            }

                            @Override
                            public void paymentFail(String message) {
                                progressDialog.dismiss();
                                method.showStatus(message);
                            }

                            @Override
                            public void suspendUser(String message) {
                                progressDialog.dismiss();
                                method.suspend(message);
                            }
                        });
                    } else {
                        method.alertBox("message: " + this.getString(R.string.not_receive_info));
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    method.alertBox("message: " + message);
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    method.alertBox("message: " + this.getString(R.string.not_receive_info));
                } else {
                    //TOKEN FAIL
                    method.alertBox("message: " + this.getString(R.string.not_receive_info));
                }
            } else {
                method.alertBox("message: " + this.getString(R.string.not_receive_info));
            }
        } else {
            method.alertBox("message: " + this.getString(R.string.not_receive_info));
        }
    }

    //----------------------- razorpay payment call -------------------------------//

    public void razorPayPayment(String userId, String cartIds) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PaymentMethod.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("cart_ids", cartIds);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RazorPayRP> call = apiService.getRazorPayData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RazorPayRP>() {
            @Override
            public void onResponse(@NotNull Call<RazorPayRP> call, @NotNull Response<RazorPayRP> response) {

                try {
                    RazorPayRP razorPayRP = response.body();
                    assert razorPayRP != null;

                    if (razorPayRP.getStatus().equals("1")) {

                        if (razorPayRP.getSuccess().equals("1")) {

                            Checkout checkout = new Checkout();
                            checkout.setKeyID(razorPayRP.getRazorpay_key_id());
                            razorPayOrderId = razorPayRP.getOrder_id();

                            JSONObject options = new JSONObject();
                            options.put("name", razorPayRP.getName());
                            options.put("description", razorPayRP.getDescription());
                            options.put("image", razorPayRP.getImage());
                            options.put("order_id", razorPayRP.getOrder_id());
                            options.put("theme.color", razorPayRP.getTheme_color());
                            options.put("currency", razorPayRP.getCurrency());
                            options.put("amount", razorPayRP.getAmount());
                            options.put("prefill.email", razorPayRP.getEmail());
                            options.put("prefill.contact", razorPayRP.getContact());
                            checkout.open(PaymentMethod.this, options);

                        } else if (razorPayRP.getSuccess().equals("2")) {
                            method.showStatus(razorPayRP.getMsg());
                        } else {
                            method.alertBox(razorPayRP.getMsg());
                        }

                    } else if (razorPayRP.getStatus().equals("2")) {
                        method.suspend(razorPayRP.getMessage());
                    } else {
                        method.alertBox(razorPayRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RazorPayRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    @Override
    public void onPaymentSuccess(String s) {
        new PaymentSubmitData(PaymentMethod.this).payMeantSubmit(method.userId(), couponId, addressId, cartIds, type, "razorpay", s, razorPayOrderId, new PaymentSubmit() {
            @Override
            public void paymentStart() {
                progressDialog.show();
                progressDialog.setMessage(getResources().getString(R.string.processing));
                progressDialog.setCancelable(false);
            }

            @Override
            public void paymentSuccess(PaymentSubmitRP paymentSubmitRP) {
                progressDialog.dismiss();
                method.conformDialog(paymentSubmitRP);
            }

            @Override
            public void paymentFail(String message) {
                progressDialog.dismiss();
                method.showStatus(message);
            }

            @Override
            public void suspendUser(String message) {
                progressDialog.dismiss();
                method.suspend(message);
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        method.alertBox(getResources().getString(R.string.payment_fail));
    }

    //----------------------- razorpay payment call -------------------------------//

    //----------------------- strip check payment is eligible or not -------------------------------//

    private void stripPaymentCheck(String userId) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PaymentMethod.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("cart_ids", cartIds);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<StripPaymentCheckRp> call = apiService.checkStripPayment(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<StripPaymentCheckRp>() {
            @Override
            public void onResponse(@NotNull Call<StripPaymentCheckRp> call, @NotNull Response<StripPaymentCheckRp> response) {

                try {
                    StripPaymentCheckRp stripPaymentCheckRp = response.body();
                    assert stripPaymentCheckRp != null;
                    if (stripPaymentCheckRp.getStatus().equals("1")) {
                        if (stripPaymentCheckRp.getSuccess().equals("1")) {
                            startActivity(new Intent(PaymentMethod.this, StripAppPayment.class)
                                    .putExtra("type", type)
                                    .putExtra("coupon_id", couponId)
                                    .putExtra("address_id", addressId)
                                    .putExtra("cart_ids", cartIds)
                                    .putExtra("stripe_key", stripPaymentCheckRp.getStripe_key()));
                            finish();
                        } else if (stripPaymentCheckRp.getSuccess().equals("2")) {
                            method.showStatus(stripPaymentCheckRp.getMsg());
                        } else {
                            method.alertBox(stripPaymentCheckRp.getMsg());
                        }
                    } else if (stripPaymentCheckRp.getStatus().equals("2")) {
                        method.suspend(stripPaymentCheckRp.getMessage());
                    } else {
                        method.alertBox(stripPaymentCheckRp.getMessage());
                    }
                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<StripPaymentCheckRp> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    //----------------------- strip check payment is eligible or not -------------------------------//

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
