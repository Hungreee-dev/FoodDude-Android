package com.example.dubstep.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.dubstep.Entity.OrderItem;
import com.example.dubstep.dao.OrderItemDao;
import com.example.dubstep.database.OrderItemDatabase;

import java.util.List;

public class OrderItemRepository {
    private OrderItemDao orderItemDao;
    private LiveData<List<OrderItem>> orderItemList;
    private LiveData<List<OrderItem>> undeliveredOrderItemList;

    public OrderItemRepository(Application application){
        OrderItemDatabase database = OrderItemDatabase.getInstance(application);
        orderItemDao = database.orderDao();
        orderItemList = orderItemDao.getAllOrderItem();
        undeliveredOrderItemList = orderItemDao.getAllUndeliveredItems();
    }

    public void insert(OrderItem orderItem){
        new InsertOrderItemAsync(orderItemDao).execute(orderItem);
    }

    public void update(OrderItem orderItem){
        new UpdateOrderItemAsync(orderItemDao).execute(orderItem);
    }

    public LiveData<OrderItem> getItem(String orderId){
        return orderItemDao.getItem(orderId);
    }

    public LiveData<OrderItem> ifStatusChanged(String orderId,int orderStatus){
        return orderItemDao.ifStatusChanged(orderId,orderStatus);
    }

    public LiveData<List<OrderItem>> getOrderItemList() {
        return orderItemList;
    }

    public LiveData<List<OrderItem>> getUndeliveredOrderItemList() {
        return undeliveredOrderItemList;
    }

    private static class InsertOrderItemAsync extends AsyncTask<OrderItem,Void,Void>{
        private OrderItemDao orderItemDao;

        public InsertOrderItemAsync(OrderItemDao orderItemDao) {
            this.orderItemDao = orderItemDao;
        }

        @Override
        protected Void doInBackground(OrderItem... orderItems) {
            orderItemDao.insert(orderItems[0]);
            return null;
        }
    }

    private static class UpdateOrderItemAsync extends AsyncTask<OrderItem,Void,Void>{
        private OrderItemDao orderItemDao;

        public UpdateOrderItemAsync(OrderItemDao orderItemDao) {
            this.orderItemDao = orderItemDao;
        }

        @Override
        protected Void doInBackground(OrderItem... orderItems) {
            orderItemDao.update(orderItems[0]);
            return null;
        }
    }

}
