package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderApi {

    @POST("/api/order/add")
    Call<Order> addOrderItem(@Body Order order);

    @POST("/api/order/user")
    Call<List<Order>> getOrderFromId(@Body Order order);

}
