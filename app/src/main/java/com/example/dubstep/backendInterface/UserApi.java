package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Result;
import com.example.dubstep.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    @POST("/api/users/new")
    Call<Result> addUser(@Body User user, @Header("authorization") String authorizationToken);

    @FormUrlEncoded
    @POST("/api/users/get-user")
    Call<User> getUser(@Field("uid") String uid, @Header("authorization") String authorizationToken);

    @FormUrlEncoded
    @POST("/api/users/add-order-id")
    Call<User> addOrderId(@Field("uid") String uid,@Field("orderId") String orderId, @Header("authorization") String authorizationToken);
}
