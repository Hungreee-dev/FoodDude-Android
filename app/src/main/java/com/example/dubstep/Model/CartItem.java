package com.example.dubstep.Model;

public class CartItem {
    String Name;
    String Price;
    String Quantity;
    String Product_ID;
    String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }



    public CartItem() {
    }


    public CartItem(String name, String price, String quantity, String product_ID, String category) {
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

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getProduct_ID() {
        return Product_ID;
    }

    public void setProduct_ID(String product_ID) {
        Product_ID = product_ID;
    }
}
