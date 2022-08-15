package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BraintreeRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("braintree_client_token")
    private String braintree_client_token;

    @SerializedName("currency_code")
    private String currency_code;

    @SerializedName("braintree_mode")
    private String braintree_mode;

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

    public String getBraintree_client_token() {
        return braintree_client_token;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public String getBraintree_mode() {
        return braintree_mode;
    }

    public String getPayable_amt() {
        return payable_amt;
    }
}
