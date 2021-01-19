package com.example.dubstep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.dubstep.Model.Order;
import com.example.dubstep.Model.User;
import com.example.dubstep.adapter.OrderAdapter;
import com.example.dubstep.database.OrderDatabase;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private List<String> orderIdList;
    private OrderAdapter orderAdapter;
    private RecyclerView orderRecycler;
    private List<Order> orderList;
    public static String orderDetailsIntent= "com.example.dubstep.orderDetailsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
//        1. get user's data
//        2, from that get list of orderId data
//        3. Pass that orderId list to make api call on each id
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderAdapter = new OrderAdapter(getApplicationContext());
        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order order) {
                Intent intent = new Intent(OrdersActivity.this,OrderDetailsActivity.class);
                String json = new Gson().toJson(order);
                intent.putExtra(orderDetailsIntent,json);
                startActivity(intent);
            }
        });

        orderRecycler = findViewById(R.id.order_recyclerview);
        orderRecycler.setHasFixedSize(true);
        orderRecycler.setLayoutManager(layoutManager);
        orderRecycler.setAdapter(orderAdapter);


        orderIdList = new ArrayList<String>();
        orderList = new ArrayList<>();
        fetchUserData(mUser.getUid());
    }

    private void fetchUserData(String uid) {
        UserDatabase.getInstance().getUser(uid, IdTokenInstance.getToken()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(OrdersActivity.this, "Network Error Reloading!!", Toast.LENGTH_SHORT).show();
                    recreate();
                    return;
                }

                User user = response.body();
                Log.d("order", "onResponse: ");
                if(user.getOrders()!=null){
                    Log.d("order", "onResponse: "+user.getOrders());
                    orderIdList.addAll(user.getOrders());
                    Log.d("order", "onResponse: "+orderIdList);
//                    fetch order data of each order items
                    fetchOrderData();
                } else {
                    noOrderPresent(true);
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(OrdersActivity.this, "Some orders were not fetched successfully \n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                noOrderPresent(true);
            }
        });
    }

    private void fetchOrderData() {
        if (orderIdList==null){
            Toast.makeText(this, "No order placed", Toast.LENGTH_SHORT).show();
            noOrderPresent(true);
            return;
        } else {
            noOrderPresent(false);
            for (String orderId:orderIdList){
                OrderDatabase.getInstance().getOrderFromId(orderId).enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(OrdersActivity.this, "Some orders were not fetched successfully", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("order", "onResponse: "+response.body());
                        orderList.add(response.body());
                        orderList.sort(new Comparator<Order>() {
                            @Override
                            public int compare(Order o1, Order o2) {
                                return Long.compare(o2.getBilling().getOrderTime().getTimestamp(), o1.getBilling().getOrderTime().getTimestamp());
                            }
                        });
                        orderAdapter.submitList(new ArrayList<>(orderList));
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        Toast.makeText(OrdersActivity.this, "Some orders were not fetched successfully \n " +t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    }

    private void noOrderPresent(boolean state) {
        if (state){
            findViewById(R.id.order_empty_view).setVisibility(View.VISIBLE);
            orderRecycler.setVisibility(View.GONE);
        } else {
            findViewById(R.id.order_empty_view).setVisibility(View.GONE);
            orderRecycler.setVisibility(View.VISIBLE);
        }


    }
}