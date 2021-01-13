package com.example.dubstep.database;

import com.example.dubstep.backendInterface.CartApi;

public class CartDatabase {
    private static CartApi instance;

    public static CartApi getInstance(){
        if(instance == null){
            instance = RetrofitInstanceNode.getRetrofitInstance().create(CartApi.class);
        }
        return instance;
    }
}
