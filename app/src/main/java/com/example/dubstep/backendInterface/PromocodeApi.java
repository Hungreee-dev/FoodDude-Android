package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Promocode;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PromocodeApi {

    @GET("/api/promocode/get")
    Call<List<Promocode>> getAllPromo();

    @POST("/api/promocode/add")
    Call<Promocode> addPromo();

}
