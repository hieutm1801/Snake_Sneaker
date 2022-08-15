package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeCatSubProList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("products")
    private List<ProductList> productLists;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<ProductList> getProductLists() {
        return productLists;
    }
}
