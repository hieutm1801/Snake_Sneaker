package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ISAddRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("is_address_avail")
    private String is_address_avail;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getIs_address_avail() {
        return is_address_avail;
    }
}
