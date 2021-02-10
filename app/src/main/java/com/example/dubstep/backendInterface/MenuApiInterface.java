package com.example.dubstep.backendInterface;

import com.example.dubstep.Model.Menu;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MenuApiInterface {
    @GET("/api/menu/get")
    Call<List<Menu>> getMenuList();


}
