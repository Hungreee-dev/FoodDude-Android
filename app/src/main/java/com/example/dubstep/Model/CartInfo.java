package com.example.dubstep.Model;

public class CartInfo {
    double CartItemsTotal;
    double CartTotal;
    double Delivery;

    public CartInfo() {
    }

    public CartInfo(double cartItemsTotal, double cartTotal, double delivery) {
        CartItemsTotal = cartItemsTotal;
        CartTotal = cartTotal;
        Delivery = delivery;
    }

    public double getCartItemsTotal() {
        return CartItemsTotal;
    }

    public void setCartItemsTotal(double cartItemsTotal) {
        CartItemsTotal = cartItemsTotal;
    }

    public double getCartTotal() {
        return CartTotal;
    }

    public void setCartTotal(double cartTotal) {
        CartTotal = cartTotal;
    }

    public double getDelivery() {
        return Delivery;
    }

    public void setDelivery(double delivery) {
        Delivery = delivery;
    }
}
