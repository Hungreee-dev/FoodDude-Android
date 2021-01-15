package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class Billing {
    @SerializedName("baseprice")
    double basePrice;

    @SerializedName("deliveryCharge")
    int deliveryCharge;

    @SerializedName("finalAmount")
    double finalAmount;

    @SerializedName("PromoCode")
    String promocode;

    @SerializedName("paymentMethod")
    String paymentMethod;

    @SerializedName("paymentID")
    String paymentId;

    @SerializedName("orderTime")
    PromoDate orderTime;

    @SerializedName("discount")
    Integer discount;

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(int deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PromoDate getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(PromoDate orderTime) {
        this.orderTime = orderTime;
    }
}
