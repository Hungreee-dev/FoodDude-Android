package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OrderApi {

    @POST("/api/order/add")
    Call<Order> addOrderItem(@Body Order order);



}
