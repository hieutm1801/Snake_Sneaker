package hieutm.dev.snakesneaker.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.interfaces.PaymentSubmit;
import hieutm.dev.snakesneaker.response.PayableAmountRP;
import hieutm.dev.snakesneaker.response.PaymentSubmitRP;
import hieutm.dev.snakesneaker.response.StripRp;
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
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.jetbrains.annotations.NotNull;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StripAppPayment extends AppCompatActivity {

    private Method method;
    private MaterialButton button;
    private ProgressDialog progressDialog;
    private PaymentSheet paymentSheet;
    private String type, couponId, addressId, cartIds, paymentId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strip_app_payment);

        method = new Method(StripAppPayment.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(StripAppPayment.this);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_stripApp_payment);
        toolbar.setTitle(getResources().getString(R.string.strip));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        button = findViewById(R.id.payButton_stripApp_payment);

        type = getIntent().getStringExtra("type");
        couponId = getIntent().getStringExtra("coupon_id");
        addressId = getIntent().getStringExtra("address_id");
        cartIds = getIntent().getStringExtra("cart_ids");
        String stripeKey = getIntent().getStringExtra("stripe_key");

        PaymentConfiguration.init(StripAppPayment.this, stripeKey);
        paymentSheet = new PaymentSheet(StripAppPayment.this, result -> {
            if (result instanceof PaymentSheetResult.Canceled) {
                method.alertBox(getResources().getString(R.string.payment_cancel));
            } else if (result instanceof PaymentSheetResult.Failed) {
                method.alertBox(getResources().getString(R.string.payment_fail));
            } else if (result instanceof PaymentSheetResult.Completed) {
                new PaymentSubmitData(StripAppPayment.this).payMeantSubmit(method.userId(), couponId, addressId, cartIds, type, "stripe", paymentId, "0", new PaymentSubmit() {
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
        });

        if (method.isNetworkAvailable()) {
            getPayableAmount(method.userId(), cartIds);
        } else {
            method.alertBox(getResources().getString(R.string.internet_connection));
        }

    }

    private void getPayableAmount(String userId, String cartIds) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(StripAppPayment.this));
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

                            button.setOnClickListener(v -> getToken(method.userId(), cartIds));

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

    private void getToken(String userId, String cartIds) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.processing));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(StripAppPayment.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("cart_ids", cartIds);
        if (type.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<StripRp> call = apiService.getStripToken(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<StripRp>() {
            @Override
            public void onResponse(@NotNull Call<StripRp> call, @NotNull Response<StripRp> response) {

                try {

                    StripRp stripRp = response.body();

                    assert stripRp != null;
                    if (stripRp.getStatus().equals("1")) {

                        if (stripRp.getSuccess().equals("1")) {

                            paymentId = stripRp.getPayment_id();

                            paymentSheet.presentWithPaymentIntent(stripRp.getPaymentIntent(),
                                    new PaymentSheet.Configuration(getResources().getString(R.string.app_name),
                                            new PaymentSheet.CustomerConfiguration(
                                                    stripRp.getCustomer(),
                                                    stripRp.getEphemeralKey())));

                        } else if (stripRp.getSuccess().equals("2")) {
                            method.showStatus(stripRp.getMsg());
                        } else {
                            method.alertBox(stripRp.getMsg());
                        }

                    } else if (stripRp.getStatus().equals("2")) {
                        method.suspend(stripRp.getMessage());
                    } else {
                        method.alertBox(stripRp.getMessage());
                    }

                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    method.alertBox(getResources().getString(R.string.failed_try_again));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<StripRp> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                progressDialog.dismiss();
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
