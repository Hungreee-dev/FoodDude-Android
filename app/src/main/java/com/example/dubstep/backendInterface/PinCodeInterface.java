package com.example.dubstep.backendInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PinCodeInterface {
    @GET("/api/pincode/get")
    Call<List<String>> getPinCodeList();
}
