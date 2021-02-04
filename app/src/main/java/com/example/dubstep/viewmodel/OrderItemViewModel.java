package com.example.dubstep.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dubstep.Entity.OrderItem;
import com.example.dubstep.repository.OrderItemRepository;

import java.util.List;

public class OrderItemViewModel extends AndroidViewModel {
    private OrderItemRepository repository;
    private LiveData<List<OrderItem>> allOrders;
    private LiveData<List<OrderItem>> allUndeliveredOrders;

    public OrderItemViewModel(@NonNull Application application) {
        super(application);
        repository = new OrderItemRepository(application);
        allOrders = repository.getOrderItemList();
        allUndeliveredOrders = repository.getUndeliveredOrderItemList();
    }

    public void insert(OrderItem orderItem){
        repository.insert(orderItem);
    }

    public void update(OrderItem orderItem){
        repository.update(orderItem);
    }

    public void deleteAllItemsAndLogout(){
        repository.deleteAllRecordAndLogout();
    }

    public LiveData<List<OrderItem>> getAllOrders() {
        return allOrders;
    }

    public LiveData<List<OrderItem>> getAllUndeliveredOrders() {
        return allUndeliveredOrders;
    }

    public LiveData<OrderItem> getItem(String orderId){
        return repository.getItem(orderId);
    }

    public LiveData<OrderItem> ifStatusChanged(String orderId,int orderStatus){
        return repository.ifStatusChanged(orderId,orderStatus);
    }
}
