package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.OrderTrackingList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OrderTrackingRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("ECOMMERCE_APP")
    private List<OrderTrackingList> orderStatusLists;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OrderTrackingList> getOrderStatusLists() {
        return orderStatusLists;
    }

    public void setOrderStatusLists(List<OrderTrackingList> orderStatusLists) {
        this.orderStatusLists = orderStatusLists;
    }
}
