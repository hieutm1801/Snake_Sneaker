package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentMethodRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("cod_status")
    private String cod_status;

    @SerializedName("braintree_status")
    private String braintree_status;

    @SerializedName("stripe_status")
    private String stripe_status;

    @SerializedName("razorpay_status")
    private String razorpay_status;

    @SerializedName("paystack_status")
    private String paystack_status;

    @SerializedName("paymomo_status")
    private String paymomo_status;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public String getCod_status() {
        return cod_status;
    }

    public String getBraintree_status() {
        return braintree_status;
    }

    public String getStripe_status() {
        return stripe_status;
    }

    public String getRazorpay_status() {
        return razorpay_status;
    }

    public String getPaystack_status() {
        return paystack_status;
    }

}
