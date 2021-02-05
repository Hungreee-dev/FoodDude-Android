package com.example.dubstep.database;

import com.example.dubstep.backendInterface.PaymentApi;

public class PaymentDatabase {
    private static PaymentApi instance;

    public static synchronized PaymentApi getInstance(){
        if (instance == null){
            instance = RetrofitInstanceNode.getRetrofitInstance()
                    .create(PaymentApi.class);
        }
        return instance;
    }
}
