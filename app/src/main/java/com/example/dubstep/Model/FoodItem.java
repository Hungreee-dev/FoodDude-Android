package com.example.dubstep.Model;

public class FoodItem {
    int base_price;
    String base_url;
    String name;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    String category;

    public FoodItem(int base_price, String image_url, String name, String category) {
        this.base_price = base_price;
        this.base_url = image_url;
        this.name = name;
        this.category = category;
    }

    public FoodItem(){

    }

    public FoodItem(int price, String name) {
        this.base_price = price;
        this.name = name;
    }

    public int getBase_price() {
        return base_price;
    }

    public void setBase_price(int base_price) {
        this.base_price = base_price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase_url() {
        return base_url;
    }

    public void setBase_url(String image_url) {
        this.base_url = image_url;
    }
}
