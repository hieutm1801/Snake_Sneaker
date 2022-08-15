package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReviewProImageList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("image")
    private String image;

    @SerializedName("image_path_thumb")
    private String image_path_thumb;

    public ReviewProImageList(String id, String image, String image_path_thumb) {
        this.id = id;
        this.image = image;
        this.image_path_thumb = image_path_thumb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage_path_thumb() {
        return image_path_thumb;
    }

    public void setImage_path_thumb(String image_path_thumb) {
        this.image_path_thumb = image_path_thumb;
    }
}
