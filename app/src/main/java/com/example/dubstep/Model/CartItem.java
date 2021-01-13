package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartItem implements Serializable {
    @SerializedName("name")
    String Name;
    @SerializedName("price")
    int Price;
    @SerializedName("quantity")
    int Quantity;

    String Product_ID;

    @SerializedName("category")
    String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public CartItem() {
    }


    public CartItem(String name, int price, int quantity, String product_ID, String category) {
        Name = name;
        Price = price;
        Quantity = quantity;
        Product_ID = product_ID;
        this.category = category;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(String product_ID) {
        Product_ID = product_ID;
    }
}
