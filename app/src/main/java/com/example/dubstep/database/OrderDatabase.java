package com.example.dubstep.database;

import com.example.dubstep.backendInterface.OrderApi;

import retrofit2.Retrofit;

public class OrderDatabase {

    private static OrderApi instance;

    public static OrderApi getInstance(){
        if (instance == null){
            instance = RetrofitInstanceFlask.getRetrofitInstance()
                    .create(OrderApi.class);
        }
        return instance;
    }

}
