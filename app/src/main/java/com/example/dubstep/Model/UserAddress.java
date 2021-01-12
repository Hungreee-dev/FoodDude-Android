package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class UserAddress {
    String pincode;
    String address1;
    String address2;
    String address3;
    @SerializedName("uid")
    String uid;

    @SerializedName("addressId")
    String addressId;

    @SerializedName("address")
    Address address;

    @SerializedName("message")
    String message;

    @SerializedName("error")
    String error;

    public UserAddress(String uid, Address address) {
        this.uid = uid;
        this.address = address;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public UserAddress(){}

    public UserAddress(String pincode, String address1, String address2, String address3) {
        this.pincode = pincode;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}
