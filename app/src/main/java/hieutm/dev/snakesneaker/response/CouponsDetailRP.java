package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CouponsDetailRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private String id;

    @SerializedName("coupon_code")
    private String coupon_code;

    @SerializedName("coupon_amt")
    private String coupon_amt;

    @SerializedName("coupon_desc")
    private String coupon_desc;

    @SerializedName("coupon_image")
    private String coupon_image;

    @SerializedName("coupon_image_thumb")
    private String coupon_image_thumb;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public String getCoupon_amt() {
        return coupon_amt;
    }

    public String getCoupon_desc() {
        return coupon_desc;
    }

    public String getCoupon_image() {
        return coupon_image;
    }

    public String getCoupon_image_thumb() {
        return coupon_image_thumb;
    }
}
