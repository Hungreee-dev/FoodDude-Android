package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {

    @SerializedName("message")
    String message;

    @SerializedName("error")
    boolean error;

    @SerializedName("requestHash")
    String requestHash;

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return error;
    }

    public String getRequestHash() {
        return requestHash;
    }
}
