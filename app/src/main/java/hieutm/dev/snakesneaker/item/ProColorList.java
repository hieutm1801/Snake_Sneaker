package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProColorList implements Serializable {

    @SerializedName("color_id")
    private String color_id;

    @SerializedName("color_code")
    private String color_code;

    public ProColorList(String color_id, String color_code) {
        this.color_id = color_id;
        this.color_code = color_code;
    }

    public String getColor_id() {
        return color_id;
    }

    public void setColor_id(String color_id) {
        this.color_id = color_id;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }
}
