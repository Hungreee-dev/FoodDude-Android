package com.example.dubstep.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstanceNode {
    private static Retrofit instance;
    public static Retrofit getRetrofitInstance(){
        if(instance == null){

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            instance = new Retrofit.Builder()
//                    .baseUrl("https://fooddude-node.herokuapp.com/")
                    .baseUrl("http://192.168.43.234:3030")
                    .addConverterFactory(GsonConverterFactory.create())
//                    .client(okHttpClient)
                    .build();
        }
        return instance;
    }
}
