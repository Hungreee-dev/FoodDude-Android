package com.example.dubstep.database;

import com.example.dubstep.backendInterface.MenuApiInterface;

import retrofit2.Retrofit;

public class MenuDatabase {
    private static MenuApiInterface instance;

    public static MenuApiInterface getInstance(){
        if(instance == null){
            Retrofit retrofit = RetrofitInstanceFlask.getRetrofitInstance();
            instance = retrofit.create(MenuApiInterface.class);
        }
        return instance;
    }
}
