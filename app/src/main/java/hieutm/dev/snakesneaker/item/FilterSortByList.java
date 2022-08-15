package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FilterSortByList implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("sort")
    private String sort;

    @SerializedName("selected")
    private boolean isSelect;

    public FilterSortByList(String title, String sort, boolean isSelect) {
        this.title = title;
        this.sort = sort;
        this.isSelect = isSelect;
    }

    public String getTitle() {
        return title;
    }

    public String getSort() {
        return sort;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
