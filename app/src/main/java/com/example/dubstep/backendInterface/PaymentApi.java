package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.KeyRazorpay;
import com.example.dubstep.Model.OrderIdResponse;
import com.example.dubstep.Model.PaymentRequest;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PaymentApi {

    @FormUrlEncoded
    @POST("/api/payment/get-request-hash")
    Call<PaymentRequest> getRequestHash(@Field("uid") String uid,
                                        @Field("txnid") String txnid,
                                        @Field("amount") String amount,
                                        @Field("productinfo") String productInfo,
                                        @Field("firstname") String name,
                                        @Field("email") String email,
                                        @Header("authorization") String authorizationHeader);

    @FormUrlEncoded
    @POST("/api/payment/get-payment-key")
    Call<KeyRazorpay> getRazorpayKey(@Field("uid")String uid, @Header("authorization")String authorizationHeader);

    @FormUrlEncoded
    @POST("/api/payment/razorpay")
    Call<OrderIdResponse> getOrderId(@Field("uid")String uid,
                                     @Field("price")Double price,
                                     @Field("type")int type,
                                     @Header("authorization")String authorizationHeader);


}
