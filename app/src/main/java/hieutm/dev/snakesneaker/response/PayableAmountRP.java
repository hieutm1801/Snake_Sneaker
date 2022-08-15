package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PayableAmountRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("payable_amt")
    private String payable_amt;

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

    public String getPayable_amt() {
        return payable_amt;
    }
}
