package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TermsOfUseRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("content")
    private String content;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getContent() {
        return content;
    }
}
