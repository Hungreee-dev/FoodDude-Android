package com.example.dubstep.database;

import com.example.dubstep.backendInterface.PromocodeApi;

import retrofit2.Retrofit;

public class PromocodeDatabase {
    private static PromocodeApi instance;

    public static PromocodeApi getInstance(){
        if (instance == null){
            Retrofit retrofit = RetrofitInstanceFlask.getRetrofitInstance();
            instance = retrofit.create(PromocodeApi.class);
        }
        return instance;
    }

}
