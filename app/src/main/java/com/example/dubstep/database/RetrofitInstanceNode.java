package com.example.dubstep.database;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstanceNode {
    private static Retrofit instance;
    public static Retrofit getRetrofitInstance(){
        if(instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl("http://192.168.43.234:3030/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
