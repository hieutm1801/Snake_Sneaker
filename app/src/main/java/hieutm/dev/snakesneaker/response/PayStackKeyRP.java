package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PayStackKeyRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("paystack_key")
    private String paystack_key;

    @SerializedName("payable_amt")
    private int payable_amt;

    @SerializedName("email")
    private String email;

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

    public String getPaystack_key() {
        return paystack_key;
    }

    public int getPayable_amt() {
        return payable_amt;
    }

    public String getEmail() {
        return email;
    }
}
