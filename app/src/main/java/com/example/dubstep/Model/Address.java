package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("pincode")
    private String pincode;
    @SerializedName("houseNumber")
    private String houseNumber;
    @SerializedName("line1")
    private String line1;
    @SerializedName("line2")
    private String line2;
    @SerializedName("city")
    private String city;
    @SerializedName("state")
    private String state;
    @SerializedName("message")
    private String message;
    @SerializedName("error")
    private String error;

    public Address(String pincode, String houseNumber, String line1, String line2, String city, String state) {
        this.pincode = pincode;
        this.houseNumber = houseNumber;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPincode() {
        return pincode;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }


    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }
}
