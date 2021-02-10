package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Menu implements Serializable {
    @SerializedName("Availability")
    private String availability;
    @SerializedName("Category")
    private String category;
    @SerializedName("ImgLink")
    private String imgLink;
    @SerializedName("Name")
    private String name;
    @SerializedName("Price")
    private String price;
    @SerializedName("Veg")
    private String veg;

    public String getAvailability() {
        return availability;
    }

    public String getCategory() {
        return category;
    }

    public String getImgLink() {
        return imgLink;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getVeg() {
        return veg;
    }
}
