package com.example.dubstep.database;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstanceNode {
    private static Retrofit instance;
    public static Retrofit getRetrofitInstance(){
        if(instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl("https://fooddude-node.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
