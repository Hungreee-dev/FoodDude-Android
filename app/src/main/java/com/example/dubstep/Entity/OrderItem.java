package com.example.dubstep.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_list")
public class OrderItem {

    @PrimaryKey
    @NonNull
    private String orderId;
    private Long timestamp;
    private int status;
    private double totalAmount;

    public OrderItem(String orderId, Long timestamp, int status, double totalAmount) {
        this.orderId = orderId;
        this.timestamp = timestamp;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
