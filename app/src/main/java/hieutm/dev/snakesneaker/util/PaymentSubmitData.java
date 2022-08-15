package hieutm.dev.snakesneaker.util;

import android.app.Activity;
import android.util.Log;

import hieutm.dev.snakesneaker.R;
import hieutm.dev.snakesneaker.response.PaymentSubmitRP;
import hieutm.dev.snakesneaker.rest.ApiClient;
import hieutm.dev.snakesneaker.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import hieutm.dev.snakesneaker.interfaces.PaymentSubmit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentSubmitData {

    private final Activity activity;

    public PaymentSubmitData(Activity activity) {
        this.activity = activity;
    }

    public void payMeantSubmit(String userId, String couponId, String addressId, String cartIds, String cartType, String gateway, String paymentId, String razorpayOrderId, PaymentSubmit paymentSubmit) {

        paymentSubmit.paymentStart();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("coupon_id", couponId);
        jsObj.addProperty("address_id", addressId);
        jsObj.addProperty("cart_ids", cartIds);
        jsObj.addProperty("gateway", gateway);
        jsObj.addProperty("payment_id", paymentId);
        jsObj.addProperty("razorpay_order_id", razorpayOrderId);
        if (cartType.equals("by_now")) {
            jsObj.addProperty("cart_type", "temp_cart");
        } else {
            jsObj.addProperty("cart_type", "main_cart");
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentSubmitRP> call = apiService.submitPayment(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaymentSubmitRP>() {
            @Override
            public void onResponse(@NotNull Call<PaymentSubmitRP> call, @NotNull Response<PaymentSubmitRP> response) {

                try {
                    PaymentSubmitRP paymentSubmitRP = response.body();
                    assert paymentSubmitRP != null;
                    if (paymentSubmitRP.getStatus().equals("1")) {
                        if (paymentSubmitRP.getSuccess().equals("1")) {
                            paymentSubmit.paymentSuccess(paymentSubmitRP);
                        } else if (paymentSubmitRP.getSuccess().equals("2")) {
                            paymentSubmit.paymentFail(paymentSubmitRP.getMsg());
                        } else {
                            paymentSubmit.paymentFail(paymentSubmitRP.getMsg());
                        }
                    } else if (paymentSubmitRP.getStatus().equals("2")) {
                        paymentSubmit.suspendUser(paymentSubmitRP.getMessage());
                    } else {
                        paymentSubmit.paymentFail(paymentSubmitRP.getMessage());
                    }
                } catch (Exception e) {
                    Log.d(Constant.exceptionError, e.toString());
                    paymentSubmit.paymentFail(activity.getResources().getString(R.string.failed_try_again));
                }

            }

            @Override
            public void onFailure(@NotNull Call<PaymentSubmitRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e(Constant.failApi, t.toString());
                paymentSubmit.paymentFail(activity.getResources().getString(R.string.failed_try_again));
            }
        });

    }

}
