package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.MyOrderList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserProfileRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("id")
    private String id;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("user_phone")
    private String user_phone;

    @SerializedName("user_image")
    private String user_image;

    @SerializedName("address")
    private String address;

    @SerializedName("address_count")
    private String address_count;

    @SerializedName("bank_count")
    private String bank_count;

    @SerializedName("bank_details")
    private String bank_details;

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

    public String getId() {
        return id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getAddress() {
        return address;
    }

    public String getAddress_count() {
        return address_count;
    }

    public String getBank_count() {
        return bank_count;
    }

    public String getBank_details() {
        return bank_details;
    }

    public List<MyOrderList> getMyOrderLists() {
        return myOrderLists;
    }
}
