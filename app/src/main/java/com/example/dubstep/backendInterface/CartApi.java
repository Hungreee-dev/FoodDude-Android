package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Address;
import com.example.dubstep.Model.CartItem;
import com.example.dubstep.Model.UserAddress;
import com.example.dubstep.Model.UserCart;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CartApi {
    @POST("api/users/cart/add")
    Call<UserCart> addCartItem(@Body UserCart cartItem, @Header("authorization") String authorizationHeader);

    @FormUrlEncoded
    @POST("api/users/cart/get")
    Call<List<CartItem>> getAllCartItems(@Field("uid") String uid, @Header("authorization") String authorizationHeader);

    @POST("api/users/cart/edit-item")
    Call<UserCart> editCartItem(@Body UserCart cartItem, @Header("authorization") String authorizationHeader);
}
