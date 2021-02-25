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

//    Created a constructor cause flask doesn't accepts url-encoded data on json
    public Order(String userId){
        this.userId = userId;
    }

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

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public Billing getBilling() {
        return billing;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }
}
