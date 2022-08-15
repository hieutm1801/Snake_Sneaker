package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.FaqList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FaqRp implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("ECOMMERCE_APP")
    private List<FaqList> faqLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<FaqList> getFaqLists() {
        return faqLists;
    }
}
