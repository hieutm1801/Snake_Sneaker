package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.ProReviewTypeList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProReviewTypeRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("total_rate")
    private String total_rate;

    @SerializedName("rate_avg")
    private String rate_avg;

    @SerializedName("filter_list")
    private List<ProReviewTypeList> proReviewTypeLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public String getRate_avg() {
        return rate_avg;
    }

    public List<ProReviewTypeList> getProReviewTypeLists() {
        return proReviewTypeLists;
    }
}
