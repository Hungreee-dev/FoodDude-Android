package com.example.dubstep.database;

import com.example.dubstep.backendInterface.UserApi;

public class UserDatabase {
    private static UserApi instance;
    public static UserApi getInstance(){
        if (instance == null){
            instance = RetrofitInstanceNode.getRetrofitInstance()
                    .create(UserApi.class);
        }
        return instance;
    }
}
