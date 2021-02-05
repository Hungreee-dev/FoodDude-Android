package com.example.dubstep.Fragment;

import android.app.ProgressDialog;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dubstep.Entity.OrderItem;
import com.example.dubstep.Model.Order;
import com.example.dubstep.Model.User;
import com.example.dubstep.OrderDetailsActivity;
import com.example.dubstep.R;
import com.example.dubstep.adapter.OrderAdapter;
import com.example.dubstep.database.OrderDatabase;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.example.dubstep.viewmodel.OrderItemViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {

    private static final String TAG = "Order";
    private FirebaseUser mUser;
    private List<String> orderIdList;
    private OrderAdapter orderAdapter;
    private RecyclerView orderRecycler;
    private List<Order> orderList;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static String orderDetailsIntent= "com.example.dubstep.orderDetailsActivity.orderId";
    private OrderItemViewModel orderItemViewModel;
    private List<OrderItem> orderItems ;
    private List<OrderItem> undeliveredOrderItems;
    private final Object lock = new Object();
    private Integer fetchedOrder = 0;
    private Integer TotalOrders = -1;
    private ProgressDialog progressDialog;

//    use this boolean to search for orderId just once on fragment creation
    boolean firstTime;

    public OrderFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        firstTime = true;
        orderAdapter = new OrderAdapter(getActivity().getApplicationContext());

        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OrderItem orderItem) {
                Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
                intent.putExtra(orderDetailsIntent,orderItem.getOrderId());
                startActivity(intent);
            }
        });

        orderIdList = new ArrayList<String>();
        undeliveredOrderItems = new ArrayList<>();
        orderList = new ArrayList<>();
        orderItemViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())
                ).get(OrderItemViewModel.class);

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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
//        Step 2
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkUndelivered();
            }
        });

            orderItemViewModel.getAllOrders().observe(OrderFragment.this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(List<OrderItem> orderItemsLocal) {
                fetchedOrder = orderItemsLocal.size();
                if (orderItemsLocal!=null && orderItemsLocal.size()>0){
//                    orderItems = new ArrayList<>(orderItemsLocal);
                    if (firstTime){
                        noOrderPresent(false);
                        firstTime = false;
                        checkUndelivered();
                        fetchNewOrderList(mUser.getUid(),orderItemsLocal);
                    }
                    Log.d(TAG, "onChanged: "+orderItemsLocal.size());
                } else {
//                    sqlite database is not created yet so fetch all the data
                    firstTime = false;
                    noOrderPresent(true);
                    fetchUserData(mUser.getUid());
//                    orderItems = new ArrayList<>();
                }

                if (fetchedOrder.equals(TotalOrders)){
                    removeLoader(orderItemsLocal);
                } else {

                }

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        orderItemViewModel.getAllOrders().removeObservers(OrderFragment.this);
    }

    private void fetchNewOrderList(String uid, List<OrderItem> orderItemsLocal) {
        synchronized (lock){
            UserDatabase.getInstance().getUser(uid, IdTokenInstance.getToken()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(getActivity(), "Network Error Reloading!!", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        noOrderPresent(true);
//                    recreate fragment
                        return;
                    }

                    User user = response.body();
                    if(user.getOrders()!=null){
                        TotalOrders = user.getOrders().size();
                        if (TotalOrders.equals(fetchedOrder)){
                            removeLoader(orderItemsLocal);
                        } else{
//                    fetch order data for new item only
                            for (String orderId :
                                    user.getOrders()) {
                                orderItemViewModel.getItem(orderId).observe(OrderFragment.this, new Observer<OrderItem>() {
                                    @Override
                                    public void onChanged(OrderItem orderItem) {
                                        if (orderItem==null){
                                            addOrderDetails(orderId);
                                        }
                                    }
                                });
                            }
                        }

                    } else {
                        removeLoader(null);
                        noOrderPresent(true);
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getActivity(), "Some orders were not fetched successfully \n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    noOrderPresent(true);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void checkUndelivered(){
        final boolean[] checkUndeliveredOnce = {true};
        synchronized (lock){
            orderItemViewModel.getAllUndeliveredOrders().observe(OrderFragment.this, new Observer<List<OrderItem>>() {
                @Override
                public void onChanged(List<OrderItem> orderItems) {
                    if (checkUndeliveredOnce[0] && (orderItems!=null && !orderItems.isEmpty())){
                        checkUndeliveredOnce[0] = false;
                        for (OrderItem orderItem :
                                orderItems) {
                            updateOrderDetails(orderItem.getOrderId());
                        }
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    private void fetchUserData(String uid) {
        UserDatabase.getInstance().getUser(uid, IdTokenInstance.getToken()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getActivity(), "Network Error Reloading!!", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                    noOrderPresent(true);
//                    recreate fragment
                    return;
                }

                User user = response.body();
                if(user.getOrders()!=null){
                    TotalOrders = user.getOrders().size();
                    orderIdList = new ArrayList<>(user.getOrders());
//                    fetch order data of each order items
                    fetchOrderData();
                } else {
                    TotalOrders = 0;
                    removeLoader(null);
                    noOrderPresent(true);
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Some orders were not fetched successfully \n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                noOrderPresent(true);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void removeLoader(List<OrderItem> orderItemsLocal) {
        if (orderItemsLocal!=null && orderItemsLocal.size()>0){
            orderAdapter.submitList(orderItemsLocal);
        } else {
            noOrderPresent(true);
        }
        progressDialog.dismiss();
    }

    private void fetchOrderData() {
        if (orderIdList==null || orderIdList.isEmpty()){
            TotalOrders = 0;
            removeLoader(null);
            Toast.makeText(getActivity(), "No order placed", Toast.LENGTH_SHORT).show();
            noOrderPresent(true);
            swipeRefreshLayout.setRefreshing(false);
        } else {
            for (String orderId:orderIdList){

                addOrderDetails(orderId);

            }
            noOrderPresent(false);


            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void addOrderDetails(String orderId) {
        OrderDatabase.getInstance().getOrderFromId(orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getActivity(), "Some orders were not fetched successfully", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("order", "onAdd: "+response.body());
                Order order = response.body();
                OrderItem orderItem = new OrderItem(
                        order.getOrderId(),
                        order.getBilling().getOrderTime().getTimestamp(),
                        order.getOrderStatus(),
                        order.getBilling().getFinalAmount()
                        );
                fetchedOrder++;
                orderItemViewModel.insert(orderItem);

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Toast.makeText(getActivity(), "Some orders were not fetched successfully \n " +t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateOrderDetails(String orderId) {
        synchronized (lock){
            OrderDatabase.getInstance().getOrderFromId(orderId).enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(getActivity(), "Some orders were not fetched successfully", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Order order = response.body();
                    OrderItem orderItem = new OrderItem(
                            order.getOrderId(),
                            order.getBilling().getOrderTime().getTimestamp(),
                            order.getOrderStatus(),
                            order.getBilling().getFinalAmount()
                    );

                    orderItemViewModel.ifStatusChanged(orderId,orderItem.getStatus()).observe( OrderFragment.this, new Observer<OrderItem>() {
                        int statusNow = orderItem.getStatus();
                        boolean doneOnce = true;
                        @Override
                        public void onChanged(OrderItem oldOrderItem) {
                            if (doneOnce){
                                doneOnce = false;
                                if (oldOrderItem != null){
                                    Log.d("order", "onChanged: item update"+oldOrderItem);
                                    oldOrderItem.setStatus(statusNow);
                                    orderItemViewModel.update(oldOrderItem);
                                }
                            }

                        }
                    });

                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Toast.makeText(getActivity(), "Some orders were not fetched successfully \n " +t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
