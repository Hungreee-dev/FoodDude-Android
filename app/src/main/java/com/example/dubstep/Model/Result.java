package com.example.dubstep.Model;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("success")
    private String success;

    @SerializedName("error")
    private String error;

    @SerializedName("message")
    private String message;

    public String getSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }


}
