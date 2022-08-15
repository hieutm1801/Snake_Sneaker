package hieutm.dev.snakesneaker.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CountryList implements Serializable {

    @SerializedName("country_name")
    private String country_name;

    public CountryList(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }
}
