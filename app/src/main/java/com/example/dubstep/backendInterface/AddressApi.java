package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Address;
import com.example.dubstep.Model.UserAddress;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AddressApi {

    @POST("api/users/address/add")
    Call<UserAddress> addAddress(@Body UserAddress address, @Header("authorization") String authorizationHeader);

    @FormUrlEncoded
    @POST("api/users/address/get-all")
    Call<List<Address>> getAllAddress(@Field("uid") String uid, @Header("authorization") String authorizationHeader);

    @FormUrlEncoded
    @POST("api/users/address/remove")
    Call<Address> removeAddress(@Field("uid") String uid, @Field("addressId") String id,@Header("authorization") String authorizationHeader);

    @POST("api/users/address/edit")
    Call<Address> editAddress(@Body UserAddress userAddress,@Header("authorization") String authorizationHeader);
}
