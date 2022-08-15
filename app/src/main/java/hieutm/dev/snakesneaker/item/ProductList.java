package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProductList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("category_name")
    private String category_name;

    @SerializedName("sub_category_name")
    private String sub_category_name;

    @SerializedName("brand_id")
    private String brand_id;

    @SerializedName("product_title")
    private String product_title;

    @SerializedName("product_desc")
    private String product_desc;

    @SerializedName("product_image")
    private String product_image;

    @SerializedName("product_image_portrait")
    private String product_image_portrait;

    @SerializedName("product_image_square")
    private String product_image_square;

    @SerializedName("product_mrp")
    private String product_mrp;

    @SerializedName("product_sell_price")
    private String product_sell_price;

    @SerializedName("you_save")
    private String you_save;

    @SerializedName("you_save_per")
    private String you_save_per;

    @SerializedName("total_rate")
    private String total_rate;

    @SerializedName("rate_avg")
    private String rate_avg;

    @SerializedName("product_status")
    private String product_status;

    @SerializedName("product_status_lbl")
    private String product_status_lbl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSub_category_name() {
        return sub_category_name;
    }

    public void setSub_category_name(String sub_category_name) {
        this.sub_category_name = sub_category_name;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_desc() {
        return product_desc;
    }

    public void setProduct_desc(String product_desc) {
        this.product_desc = product_desc;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_image_portrait() {
        return product_image_portrait;
    }

    public void setProduct_image_portrait(String product_image_portrait) {
        this.product_image_portrait = product_image_portrait;
    }

    public String getProduct_image_square() {
        return product_image_square;
    }

    public void setProduct_image_square(String product_image_square) {
        this.product_image_square = product_image_square;
    }

    public String getProduct_mrp() {
        return product_mrp;
    }

    public void setProduct_mrp(String product_mrp) {
        this.product_mrp = product_mrp;
    }

    public String getProduct_sell_price() {
        return product_sell_price;
    }

    public void setProduct_sell_price(String product_sell_price) {
        this.product_sell_price = product_sell_price;
    }

    public String getYou_save() {
        return you_save;
    }

    public void setYou_save(String you_save) {
        this.you_save = you_save;
    }

    public String getYou_save_per() {
        return you_save_per;
    }

    public void setYou_save_per(String you_save_per) {
        this.you_save_per = you_save_per;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public void setTotal_rate(String total_rate) {
        this.total_rate = total_rate;
    }

    public String getRate_avg() {
        return rate_avg;
    }

    public void setRate_avg(String rate_avg) {
        this.rate_avg = rate_avg;
    }

    public String getProduct_status() {
        return product_status;
    }

    public void setProduct_status(String product_status) {
        this.product_status = product_status;
    }

    public String getProduct_status_lbl() {
        return product_status_lbl;
    }

    public void setProduct_status_lbl(String product_status_lbl) {
        this.product_status_lbl = product_status_lbl;
    }
}
