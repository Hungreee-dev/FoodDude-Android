package com.example.dubstep.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.dubstep.CartMainActivity;
import com.example.dubstep.FoodItemActivity;
import com.example.dubstep.Model.FoodClass;
import com.example.dubstep.Model.Menu;
import com.example.dubstep.R;
import com.example.dubstep.adapter.FoodClassAdapter;
import com.example.dubstep.database.MenuDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    // TODO: 1. manage this fragment lifecycle properly
//          showing loading dialog on each call rather than just once when initialised
//       2. Insert ImageView in each element in recycler view for item image
    FirebaseAuth firebaseAuth;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    FoodClassAdapter adapter;

    private ImageButton mCartButton;
    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        firebaseAuth = FirebaseAuth.getInstance();

        adapter = new FoodClassAdapter();
    }

    private void fetchFoodItems(){
        MenuDatabase.getInstance().getMenuList().enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                if(!response.isSuccessful()){
                    Log.d("OnFailure", "onFailure: Unable to fetch "+response.message());
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                List<Menu> menuList = response.body();
                List<FoodClass> foodClasses = new ArrayList<FoodClass>();
                for (Menu menu:menuList){
                    boolean found = false;
                    if(foodClasses.isEmpty()){
//                        foodclass list empty so added one right away
                        FoodClass foodClass = new FoodClass();
                        foodClass.setCategory(menu.getCategory());
                        foodClass.addMenuItem(menu);
                        foodClass.setImageLink(menu.getImgLink());
                        foodClasses.add(foodClass);
                    } else {
//                        checked for category in each member of foodClass list
                        for (FoodClass foodClass: foodClasses){
                            if(menu.getCategory().equals(foodClass.getCategory())){
                                foodClass.addMenuItem(menu);
                                found = true;
                                break;
                            }
                        }

                        if(!found){
                            FoodClass foodClass = new FoodClass();
                            foodClass.setCategory(menu.getCategory());
                            foodClass.addMenuItem(menu);
                            foodClass.setImageLink(menu.getImgLink());
                            foodClasses.add(foodClass);
                        }
                    }
                }
                adapter.submitList(foodClasses);
                progressDialog.dismiss();
                adapter.setOnItemClickListener(new FoodClassAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(FoodClass foodClass) {
                        Intent intent = new Intent(getContext(), FoodItemActivity.class);
                        intent.putExtra("category", foodClass.getCategory());
                        ArrayList<Menu> menus = new ArrayList<Menu>();
                        menus.addAll(foodClass.getMenuList());
                        intent.putExtra("menu_list", menus);
                        startActivity(intent);
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {
                Log.d("OnFailure", "onFailure: Unable to fetch "+t.getMessage());
                swipeRefreshLayout.setRefreshing(false);
                getActivity().recreate();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mCartButton = view.findViewById(R.id.cart_btn);
        recyclerView = view.findViewById(R.id.main_recyclerview);
        swipeRefreshLayout = view.findViewById(R.id.food_class_swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFoodItems();
            }
        });
        recyclerView.setHasFixedSize(true);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

        layoutManager = new LinearLayoutManager(getContext());
        //layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CartMainActivity.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        fetchFoodItems();

    }

    @Override
    public void onStart() {
        super.onStart();


    }

}
