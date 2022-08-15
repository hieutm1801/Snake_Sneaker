package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CouponsList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("coupon_code")
    private String coupon_code;

    @SerializedName("coupon_image")
    private String coupon_image;

    @SerializedName("coupon_image_thumb")
    private String coupon_image_thumb;

    public CouponsList(String id, String coupon_code, String coupon_image, String coupon_image_thumb) {
        this.id = id;
        this.coupon_code = coupon_code;
        this.coupon_image = coupon_image;
        this.coupon_image_thumb = coupon_image_thumb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCoupon_image() {
        return coupon_image;
    }

    public void setCoupon_image(String coupon_image) {
        this.coupon_image = coupon_image;
    }

    public String getCoupon_image_thumb() {
        return coupon_image_thumb;
    }

    public void setCoupon_image_thumb(String coupon_image_thumb) {
        this.coupon_image_thumb = coupon_image_thumb;
    }
}
