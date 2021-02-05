package com.example.dubstep.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dubstep.Entity.OrderItem;
import com.example.dubstep.dao.OrderItemDao;

@Database(entities = {OrderItem.class}, version = 1)
public abstract class OrderItemDatabase extends RoomDatabase {
    private static OrderItemDatabase instance;

    public abstract OrderItemDao orderDao();

    public static synchronized OrderItemDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    OrderItemDatabase.class,"order_list")
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return instance;
    }
}
