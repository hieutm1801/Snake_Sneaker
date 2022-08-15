package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.BrandList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BrandRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("ECOMMERCE_APP")
    private List<BrandList> brandLists;

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

    public List<BrandList> getBrandLists() {
        return brandLists;
    }

    public void setBrandLists(List<BrandList> brandLists) {
        this.brandLists = brandLists;
    }
}
