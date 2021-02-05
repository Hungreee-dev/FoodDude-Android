package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class KeyRazorpay {

    @SerializedName("error")
    boolean error;

    @SerializedName("key")
    String key;

    public boolean isError() {
        return error;
    }

    public String getKey() {
        return key;
    }
}
