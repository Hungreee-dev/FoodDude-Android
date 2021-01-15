package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Promocode {

    @SerializedName("Category")
    int category;

    @SerializedName("Description")
    String description;

    @SerializedName("Expiry")
    PromoDate expiry;

    @SerializedName("Max_usage")
    int maxUsage;

    @SerializedName("Max_usage_per_user")
    int maxUsagePerUser;

    @SerializedName("Percentage")
    int percentage;

    @SerializedName("Start Time")
    PromoDate startTime;

    @SerializedName("code")
    String code;

    @SerializedName("user_frequency")
    List<UserFrequency> userFrequencyList;

    public List<UserFrequency> getUserFrequencyList() {
        return userFrequencyList;
    }

    public void setUserFrequencyList(List<UserFrequency> userFrequencyList) {
        this.userFrequencyList = userFrequencyList;
    }

    public int getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public PromoDate getExpiry() {
        return expiry;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public int getMaxUsagePerUser() {
        return maxUsagePerUser;
    }

    public int getPercentage() {
        return percentage;
    }

    public PromoDate getStartTime() {
        return startTime;
    }

    public String getCode() {
        return code;
    }
}
