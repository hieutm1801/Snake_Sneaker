package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BrandFilterList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("brand_name")
    private String brand_name;

    @SerializedName("total_cnt")
    private String total_cnt;

    @SerializedName("selected")
    private String selected;

    public BrandFilterList(String id, String brand_name, String total_cnt, String selected) {
        this.id = id;
        this.brand_name = brand_name;
        this.total_cnt = total_cnt;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getTotal_cnt() {
        return total_cnt;
    }

    public void setTotal_cnt(String total_cnt) {
        this.total_cnt = total_cnt;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }
}
