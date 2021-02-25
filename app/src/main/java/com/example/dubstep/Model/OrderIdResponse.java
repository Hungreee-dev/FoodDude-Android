package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class OrderIdResponse {
    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
