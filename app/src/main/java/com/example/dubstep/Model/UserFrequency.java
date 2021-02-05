package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserFrequency {

    @SerializedName("userId")
    String userId;

    @SerializedName("frequency")
    int frequency;

    public String getUserId() {
        return userId;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public UserFrequency(String userId) {
        this.userId = userId;
        frequency = 1;
    }
}
