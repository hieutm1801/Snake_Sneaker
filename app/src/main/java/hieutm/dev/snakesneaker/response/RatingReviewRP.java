package hieutm.dev.snakesneaker.response;

import hieutm.dev.snakesneaker.item.ReviewProImageList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RatingReviewRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("review_id")
    private String review_id;

    @SerializedName("product_title")
    private String product_title;

    @SerializedName("product_image")
    private String product_image;

    @SerializedName("rate")
    private String rate;

    @SerializedName("rating_desc")
    private String rating_desc;

    @SerializedName("rating_date")
    private String rating_date;

    @SerializedName("ECOMMERCE_APP")
    private List<ReviewProImageList> reviewProImageLists;

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

    public String getReview_id() {
        return review_id;
    }

    public void setReview_id(String review_id) {
        this.review_id = review_id;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getRating_desc() {
        return rating_desc;
    }

    public void setRating_desc(String rating_desc) {
        this.rating_desc = rating_desc;
    }

    public String getRating_date() {
        return rating_date;
    }

    public void setRating_date(String rating_date) {
        this.rating_date = rating_date;
    }

    public List<ReviewProImageList> getReviewProImageLists() {
        return reviewProImageLists;
    }

    public void setReviewProImageLists(List<ReviewProImageList> reviewProImageLists) {
        this.reviewProImageLists = reviewProImageLists;
    }
}
