package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class UserCart {

    @SerializedName("uid")
    String uid;

    @SerializedName("item")
    CartItem cartItem;

    @SerializedName("message")
    String message;

    @SerializedName("error")
    String error;


    public UserCart(String uid, CartItem cartItem) {
        this.uid = uid;
        this.cartItem = cartItem;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }


}
