package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class PromocodeResult {


    @SerializedName("error")
    boolean error;

    @SerializedName("message")
    String message;

    @SerializedName("promocode")
    Promocode promocode;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public Promocode getPromocode() {
        return promocode;
    }
}
