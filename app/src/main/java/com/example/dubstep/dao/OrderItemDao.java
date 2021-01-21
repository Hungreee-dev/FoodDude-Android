package com.example.dubstep.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dubstep.Entity.OrderItem;

import java.util.List;

@Dao
public interface OrderItemDao {

    @Insert
    void insert(OrderItem orderItem);

    @Update
    void update(OrderItem orderItem);

    @Query("SELECT * FROM order_list WHERE orderId=:orderId")
    LiveData<OrderItem> getItem(String orderId);

    @Query("SELECT * FROM order_list WHERE orderId=:orderId AND status!=:orderStatus")
    LiveData<OrderItem> ifStatusChanged(String orderId,int orderStatus);

    @Query("SELECT * FROM order_list ORDER BY timestamp DESC")
    LiveData<List<OrderItem>> getAllOrderItem();

    @Query("SELECT * FROM order_list WHERE status!=2 ORDER BY timestamp DESC")
    LiveData<List<OrderItem>> getAllUndeliveredItems();

}
