package hieutm.dev.snakesneaker.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("app_developed_by")
    private String app_developed_by;

    @SerializedName("publisher_id")
    private String publisher_id;

    @SerializedName("interstitial_ad_id")
    private String interstitial_ad_id;

    @SerializedName("interstitial_ad_click")
    private String interstitial_ad_click;

    @SerializedName("banner_ad_id")
    private String banner_ad_id;

    @SerializedName("app_currency_code")
    private String app_currency_code;

    @SerializedName("interstitial_ad")
    private boolean interstitial_ad = false;

    @SerializedName("banner_ad")
    private boolean banner_ad = false;

    @SerializedName("banner_ad_type")
    private String banner_ad_type ;

    @SerializedName("interstitial_ad_type")
    private String interstitial_ad_type ;

    @SerializedName("cart_items")
    private String cart_items ;

    @SerializedName("privacy_policy")
    private String privacy_policy ;

    @SerializedName("user_name")
    private String user_name ;

    @SerializedName("user_email")
    private String user_email ;

    @SerializedName("user_image")
    private String user_image ;

    @SerializedName("app_update_status")
    private boolean app_update_status ;

    @SerializedName("app_new_version")
    private int app_new_version ;

    @SerializedName("app_update_desc")
    private String app_update_desc ;

    @SerializedName("app_redirect_url")
    private String app_redirect_url ;

    @SerializedName("cancel_update_status")
    private boolean cancel_update_status ;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getApp_developed_by() {
        return app_developed_by;
    }

    public String getPublisher_id() {
        return publisher_id;
    }

    public String getInterstitial_ad_id() {
        return interstitial_ad_id;
    }

    public String getInterstitial_ad_click() {
        return interstitial_ad_click;
    }

    public String getBanner_ad_id() {
        return banner_ad_id;
    }

    public String getApp_currency_code() {
        return app_currency_code;
    }

    public boolean isInterstitial_ad() {
        return interstitial_ad;
    }

    public boolean isBanner_ad() {
        return banner_ad;
    }

    public String getBanner_ad_type() {
        return banner_ad_type;
    }

    public String getInterstitial_ad_type() {
        return interstitial_ad_type;
    }

    public String getCart_items() {
        return cart_items;
    }

    public String getPrivacy_policy() {
        return privacy_policy;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_image() {
        return user_image;
    }

    public boolean getApp_update_status() {
        return app_update_status;
    }

    public int getApp_new_version() {
        return app_new_version;
    }

    public String getApp_update_desc() {
        return app_update_desc;
    }

    public String getApp_redirect_url() {
        return app_redirect_url;
    }

    public boolean getCancel_update_status() {
        return cancel_update_status;
    }
}
