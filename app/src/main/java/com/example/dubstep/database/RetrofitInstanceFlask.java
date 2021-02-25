package com.example.dubstep.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstanceFlask {
    private static Retrofit instance;
    public static Retrofit getRetrofitInstance(){
        if(instance == null){
            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            instance = new Retrofit.Builder()
                    .baseUrl("https://fooddude.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .client(okHttpClient)
                    .build();
        }
        return instance;
    }


}
