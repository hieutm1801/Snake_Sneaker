package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.BraintreeRequestCodes;
import com.braintreepayments.api.BrowserSwitchResult;
import com.braintreepayments.api.PayPalCheckoutRequest;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.PayPalPaymentIntent;
import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.interfaces.PaymentSubmit;
import hieutm.dev.snakesneaker.response.BraintreePayIdRP;
import hieutm.dev.snakesneaker.response.BraintreeRP;
import hieutm.dev.snakesneaker.response.PayableAmountRP;
import hieutm.dev.snakesneaker.response.PaymentSubmitRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import hieutm.dev.snakesneaker.util.API;
import hieutm.dev.snakesneaker.util.Constant;
import hieutm.dev.snakesneaker.util.Method;
import hieutm.dev.snakesneaker.util.PaymentSubmitData;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BraintreePayment extends AppCompatActivity {

    private Method method;
    private String type, cartIds;
    private MaterialButton button;
    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braintree_payment);

        method = new Method(BraintreePayment.this);
        method.forceRTLIfSupported();

        if (getIntent().getStringExtra("type") != null) {
            type = getIntent().getStringExtra("type");
            String couponId = getIntent().getStringExtra("coupon_id");
            String addressId = getIntent().getStringExtra("address_id");
            cartIds = getIntent().getStringExtra("cart_ids");

            Constant.braintreeType = type;
            Constant.braintreeCouponId = couponId;
            Constant.braintreeAddressId = addressId;
            Constant.braintreeCartIds = cartIds;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_braintree_payment);
        toolbar.setTitle(getResources().getString(R.string.paypal));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(BraintreePayment.this);
        button = findViewById(R.id.payButton_braintree_payment);

        if (cartIds != null && type != null) {
            getPayableAmount(method.userId(), cartIds, type);
        }

    }

    private void getPayableAmount(String userId, String cartIds, String type) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(BraintreePayment.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("cart_ids", cartIds);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PayableAmountRP> call = apiService.getPayableAmount(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PayableAmountRP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<PayableAmountRP> call, @NotNull Response<PayableAmountRP> response) {

                try {
                    PayableAmountRP payableAmountRP = response.body();
                    assert payableAmountRP != null;

                    if (payableAmountRP.getStatus().equals("1")) {

                        if (payableAmountRP.getSuccess().equals("1")) {

                            button.setText(getResources().getText(R.string.pay_now) + " " + payableAmountRP.getPayable_amt()
                                    + " " + Constant.currency);

                            button.setOnClickListener(v -> {
                                if (method.isNetworkAvailable()) {
                                    if (cartIds != null) {
                                        braintreeAmount(method.userId(), cartIds, type);
                                    } else {
                                        method.alertBox(getResources().getString(R.string.wrong));
                                    }
                                } else {
                                    method.alertBox(getResources().getString(R.string.internet_connection));
                                }
                            });

                            progressDialog.dismiss();

                        } else if (payableAmountRP.getSuccess().equals("2")) {
                            progressDialog.dismiss();
                            method.showStatus(payableAmountRP.getMsg());
                        } else {
                            progressDialog.dismiss();
                            method.alertBox(payableAmountRP.getMsg());
                        }

                    } else if (payableAmountRP.getStatus().equals("2")) {
                        progressDialog.dismiss();
                        method.suspend(payableAmountRP.getMessage());
                    } else {
                        progressDialog.dismiss();
                        method.alertBox(payableAmountRP.getMessage());
                    }

                } catch (Exception e) {
                    progressDialog.dismiss();
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(@NotNull Call<PayableAmountRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });

    }

    private void braintreeAmount(String userId, String cartIds, String type) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(BraintreePayment.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("cart_ids", cartIds);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BraintreeRP> call = apiService.getBraintreeAmount(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<BraintreeRP>() {
            @Override
            public void onResponse(@NotNull Call<BraintreeRP> call, @NotNull Response<BraintreeRP> response) {

                try {
                    BraintreeRP braintreeRP = response.body();
                    assert braintreeRP != null;

                    if (braintreeRP.getStatus().equals("1")) {

                        if (braintreeRP.getSuccess().equals("1")) {

                            Constant.braintreeClient = new BraintreeClient(BraintreePayment.this, braintreeRP.getBraintree_client_token());
                            Constant.payPalClient = new PayPalClient(Constant.braintreeClient);

                            PayPalCheckoutRequest request = new PayPalCheckoutRequest(braintreeRP.getPayable_amt());
                            request.setCurrencyCode(braintreeRP.getCurrency_code());
                            request.setIntent(PayPalPaymentIntent.AUTHORIZE);
                            Constant.payPalClient.tokenizePayPalAccount(BraintreePayment.this, request, (error) -> {
                                progressDialog.dismiss();
                                if (error != null) {
                                    Toast.makeText(BraintreePayment.this, getResources().getString(R.string.payment_fail), Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else if (braintreeRP.getSuccess().equals("2")) {
                            progressDialog.dismiss();
                            method.showStatus(braintreeRP.getMsg());
                        } else {
                            progressDialog.dismiss();
                            method.alertBox(braintreeRP.getMsg());
                        }

                    } else if (braintreeRP.getStatus().equals("2")) {
                        progressDialog.dismiss();
                        method.suspend(braintreeRP.getMessage());
                    } else {
                        progressDialog.dismiss();
                        method.alertBox(braintreeRP.getMessage());
                    }

                } catch (Exception e) {
                    progressDialog.dismiss();
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(@NotNull Call<BraintreeRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    private void braintreePayId(String nonce) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(BraintreePayment.this));
        jsObj.addProperty("nonce", nonce);
        jsObj.addProperty("user_id", method.userId());
        jsObj.addProperty("cart_ids", Constant.braintreeCartIds);
        if (Constant.braintreeType.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BraintreePayIdRP> call = apiService.getBraintreePayId(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<BraintreePayIdRP>() {
            @Override
            public void onResponse(@NotNull Call<BraintreePayIdRP> call, @NotNull Response<BraintreePayIdRP> response) {

                try {
                    BraintreePayIdRP braintreePayIdRP = response.body();
                    assert braintreePayIdRP != null;

                    if (braintreePayIdRP.getStatus().equals("1")) {

                        if (braintreePayIdRP.getSuccess().equals("1")) {
                            new PaymentSubmitData(BraintreePayment.this).payMeantSubmit(method.userId(), Constant.braintreeCouponId, Constant.braintreeAddressId, Constant.braintreeCartIds, Constant.braintreeType, "paypal", braintreePayIdRP.getPayment_id(), "0", new PaymentSubmit() {
                                @Override
                                public void paymentStart() {

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
                            progressDialog.dismiss();
                            method.showStatus(braintreePayIdRP.getMsg());
                        }

                    } else if (braintreePayIdRP.getStatus().equals("2")) {
                        progressDialog.dismiss();
                        method.suspend(braintreePayIdRP.getMessage());
                    } else {
                        progressDialog.dismiss();
                        method.showStatus(braintreePayIdRP.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    progressDialog.dismiss();
                    method.showStatus(getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(@NotNull Call<BraintreePayIdRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
                method.showStatus(getResources().getString(R.string.failed_try_again));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.braintreeClient != null && Constant.payPalClient != null) {
            BrowserSwitchResult browserSwitchResult = Constant.braintreeClient.deliverBrowserSwitchResult(BraintreePayment.this);
            if (browserSwitchResult != null && browserSwitchResult.getRequestCode() == BraintreeRequestCodes.PAYPAL) {
                Constant.payPalClient.onBrowserSwitchResult(browserSwitchResult, (payPalAccountNonce, error) -> {
                    if (payPalAccountNonce != null) {
                        String nonce = payPalAccountNonce.getString();
                        braintreePayId(nonce);
                        Constant.braintreeClient = null;
                        Constant.payPalClient = null;
                    } else {
                        method.showStatus(getResources().getString(R.string.payment_fail));
                    }
                });
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}