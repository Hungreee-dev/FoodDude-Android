package com.example.dubstep.singleton;

import com.example.dubstep.Model.Address;
import com.example.dubstep.Model.Billing;
import com.example.dubstep.Model.CartItem;

import java.util.List;

public class OrderDetails {
    public static OrderDetails instance;

    private List<CartItem> cartItems;
    private Address deliveryAddress;
    private Billing billingDetails;


    public static OrderDetails getInstance(){
        if (instance == null){
            instance = new OrderDetails();
        }
        return instance;
    }

    private OrderDetails(){
        billingDetails = new Billing();
    }


    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Billing getBillingDetails() {
        return billingDetails;
    }

    public void setBillingDetails(Billing billingDetails) {
        this.billingDetails = billingDetails;
    }
}
