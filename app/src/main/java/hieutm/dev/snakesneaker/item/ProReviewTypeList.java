package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProReviewTypeList implements Serializable {

    @SerializedName("value")
    private String value;

    @SerializedName("title")
    private String title;

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
}
