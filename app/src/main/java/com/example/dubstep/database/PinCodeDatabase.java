package com.example.dubstep.database;

import com.example.dubstep.backendInterface.PinCodeInterface;

import retrofit2.Retrofit;

public class PinCodeDatabase {
    private static PinCodeInterface instance;

    public static PinCodeInterface getInstance(){
        if(instance == null){
            Retrofit retrofit = RetrofitInstanceFlask.getRetrofitInstance();
            instance = retrofit.create(PinCodeInterface.class);
        }
        return instance;
    }
}
