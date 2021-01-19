package com.example.dubstep.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dubstep.Model.Order;
import com.example.dubstep.Model.User;
import com.example.dubstep.OrderDetailsActivity;
import com.example.dubstep.R;
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

public class OrderFragment extends Fragment {

    private FirebaseUser mUser;
    private List<String> orderIdList;
    private OrderAdapter orderAdapter;
    private RecyclerView orderRecycler;
    private List<Order> orderList;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static String orderDetailsIntent= "com.example.dubstep.orderDetailsActivity";

    public OrderFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderAdapter = new OrderAdapter(getActivity().getApplicationContext());

        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order order) {
                Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                String json = new Gson().toJson(order);
                intent.putExtra(orderDetailsIntent,json);
                startActivity(intent);
            }
        });

        orderIdList = new ArrayList<String>();
        orderList = new ArrayList<>();

        fetchUserData(mUser.getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_orders, container, false);
//        show Terms and Services of Application
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        orderRecycler = view.findViewById(R.id.order_recyclerview);
        orderRecycler.setHasFixedSize(true);
        orderRecycler.setLayoutManager(layoutManager);
        orderRecycler.setAdapter(orderAdapter);
        swipeRefreshLayout = view.findViewById(R.id.swipeToRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUserData(mUser.getUid());
            }
        });
    }

    private void fetchUserData(String uid) {
        UserDatabase.getInstance().getUser(uid, IdTokenInstance.getToken()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getActivity(), "Network Error Reloading!!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
//                    recreate fragment
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
                Toast.makeText(getActivity(), "Some orders were not fetched successfully \n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                noOrderPresent(true);
            }
        });
    }

    private void fetchOrderData() {
        if (orderIdList==null){
            Toast.makeText(getActivity(), "No order placed", Toast.LENGTH_SHORT).show();
            noOrderPresent(true);
            return;
        } else {
            noOrderPresent(false);
            for (String orderId:orderIdList){
                OrderDatabase.getInstance().getOrderFromId(orderId).enqueue(new Callback<Order>() {
                    @Override
                    public void onResponse(Call<Order> call, Response<Order> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(getActivity(), "Some orders were not fetched successfully", Toast.LENGTH_SHORT).show();
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
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<Order> call, Throwable t) {
                        Toast.makeText(getActivity(), "Some orders were not fetched successfully \n " +t.getMessage(), Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

        }
    }

    private void noOrderPresent(boolean state) {
        swipeRefreshLayout.setRefreshing(false);
        if (state){
            getView().findViewById(R.id.order_empty_view).setVisibility(View.VISIBLE);
            orderRecycler.setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.order_empty_view).setVisibility(View.GONE);
            orderRecycler.setVisibility(View.VISIBLE);
        }


    }
}
