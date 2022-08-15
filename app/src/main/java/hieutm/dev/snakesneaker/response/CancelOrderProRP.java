package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.MyOrderList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CancelOrderProRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("ECOMMERCE_APP")
    private List<MyOrderList> myOrderLists;

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

    public List<MyOrderList> getMyOrderLists() {
        return myOrderLists;
    }
}
