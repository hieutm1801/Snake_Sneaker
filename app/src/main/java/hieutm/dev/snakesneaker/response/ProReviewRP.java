package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.ProReviewList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProReviewRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("reviews")
    private List<ProReviewList> proReviewLists;

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

    public List<ProReviewList> getProReviewLists() {
        return proReviewLists;
    }

    public void setProReviewLists(List<ProReviewList> proReviewLists) {
        this.proReviewLists = proReviewLists;
    }
}
