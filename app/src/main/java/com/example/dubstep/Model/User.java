package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class    User {
    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phoneNumber;

    @SerializedName("email")
    public String email;

    @SerializedName("uid")
    public String uid;

//    cart ,address, ;

    @SerializedName("orders")
    public List<String> orders;

    @SerializedName("error")
    public String error;

    public String getError() {
        return error;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    public List<String> getOrders() {
        return orders;
    }

    public User(String name, String phoneNumber, String email, String uid) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.uid = uid;
    }
}
