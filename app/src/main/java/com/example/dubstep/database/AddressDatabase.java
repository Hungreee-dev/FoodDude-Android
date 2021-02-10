package com.example.dubstep.database;

import com.example.dubstep.backendInterface.AddressApi;

public class AddressDatabase {
    private static AddressApi instance;

    public static AddressApi getInstance(){
        if(instance == null){
            instance = RetrofitInstanceNode.getRetrofitInstance().create(AddressApi.class);
        }
        return instance;
    }
}
