package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class PromoDate {
    @SerializedName("day")
    int day;

    @SerializedName("month")
    int month;

    @SerializedName("timestamp")
    long timestamp;

    @SerializedName("year")
    int year;

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getYear() {
        return year;
    }

    public PromoDate(long timestamp){
        this.timestamp = timestamp;
    }
}
