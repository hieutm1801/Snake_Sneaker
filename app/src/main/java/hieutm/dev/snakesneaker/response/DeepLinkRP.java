package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DeepLinkRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
