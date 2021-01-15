package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    @SerializedName("id")
    String orderId;
//    userId+timeoforder

    @SerializedName("userId")
    String userId;

    @SerializedName("items")
    List<CartItem> cartItems;

    @SerializedName("billing")
    Billing billing;

    @SerializedName("orderStatus")
    int orderStatus;

    @SerializedName("Address")
    Address deliveryAddress;

    public Order(String orderId, String userId, List<CartItem> cartItems, Billing billing, int orderStatus, Address deliveryAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.cartItems = cartItems;
        this.billing = billing;
        this.orderStatus = orderStatus;
        this.deliveryAddress = deliveryAddress;
    }

    @SerializedName("success")
    String success;

    public String getSuccess() {
        return success;
    }
}
