package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.SubCategoryList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SubCatRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("ECOMMERCE_APP")
    private List<SubCategoryList> subCategoryLists;

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

    public List<SubCategoryList> getSubCategoryLists() {
        return subCategoryLists;
    }

    public void setSubCategoryLists(List<SubCategoryList> subCategoryLists) {
        this.subCategoryLists = subCategoryLists;
    }
}
