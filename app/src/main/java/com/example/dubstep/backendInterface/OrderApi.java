package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Order;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderApi {

    @POST("/api/order/add")
    Call<Order> addOrderItem(@Body Order order);

    @GET("/api/order/get")
    Call<Order> getOrderFromId(@Query("id")String orderId);

}
