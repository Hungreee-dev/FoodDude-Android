package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Promocode;
import com.example.dubstep.Model.PromocodeResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PromocodeApi {

//    @GET("/api/promocode/get")
//    Call<List<Promocode>> getAllPromo();
//
//    @POST("/api/promocode/add")
//    Call<Promocode> addPromo();

    @FormUrlEncoded
    @POST("/api/promocode/get-promocode-details")
    Call<PromocodeResult> getPromo(@Field("uid") String uid,
                                   @Field("promocode")String promocode,
                                   @Header("authorization")String authorizationHeader);

    @FormUrlEncoded
    @POST("/api/promocode/check-promocode")
    Call<PromocodeResult> checkPromo(@Field("uid") String uid,
                                   @Field("promocode")String promocode,
                                   @Header("authorization")String authorizationHeader);

    @FormUrlEncoded
    @POST("/api/promocode/avail-promocode")
    Call<PromocodeResult> availPromo(@Field("uid") String uid,
                                     @Field("promocode")String promocode,
                                     @Field("discount") double discount,
                                     @Header("authorization")String authorizationHeader);

}
