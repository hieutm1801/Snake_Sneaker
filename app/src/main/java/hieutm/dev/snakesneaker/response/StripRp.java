package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StripRp implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("paymentIntent")
    private String paymentIntent;

    @SerializedName("ephemeralKey")
    private String ephemeralKey;

    @SerializedName("customer")
    private String customer;

    @SerializedName("payment_id")
    private String payment_id;

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

    public String getPaymentIntent() {
        return paymentIntent;
    }

    public String getEphemeralKey() {
        return ephemeralKey;
    }

    public String getCustomer() {
        return customer;
    }

    public String getPayment_id() {
        return payment_id;
    }
}
