package com.example.dubstep.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.dubstep.CartMainActivity;
import com.example.dubstep.FoodItemActivity;
import com.example.dubstep.Interface.ItemClickListener;
import com.example.dubstep.LoginActivity;
import com.example.dubstep.MainActivity;
import com.example.dubstep.Model.FoodClass;
import com.example.dubstep.Model.FoodItem;
import com.example.dubstep.Model.GlideApp;
import com.example.dubstep.Model.Menu;
import com.example.dubstep.R;
import com.example.dubstep.ViewHolder.FoodClassViewHolder;
import com.example.dubstep.ViewHolder.FoodItemViewHolder;
import com.example.dubstep.adapter.FoodClassAdapter;
import com.example.dubstep.database.MenuDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    // TODO: 1. manage this fragment lifecycle properly
//          showing loading dialog on each call rather than just once when initialised
//       2. Insert ImageView in each element in recycler view for item image
    FirebaseAuth firebaseAuth;
    private DatabaseReference userref;
    private DatabaseReference foodref;
    private DatabaseReference cartref;
    private StorageReference firebaseRef;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
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
        firebaseRef = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        adapter = new FoodClassAdapter();

        MenuDatabase.getInstance().getMenuList().enqueue(new Callback<List<Menu>>() {
            @Override
            public void onResponse(Call<List<Menu>> call, Response<List<Menu>> response) {
                if(!response.isSuccessful()){
                    Log.d("OnFailure", "onFailure: Unable to fetch "+response.message());
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
            }

            @Override
            public void onFailure(Call<List<Menu>> call, Throwable t) {
                Log.d("OnFailure", "onFailure: Unable to fetch "+t.getMessage());
                getActivity().recreate();
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mCartButton = view.findViewById(R.id.cart_btn);
        recyclerView = view.findViewById(R.id.main_recyclerview);
        recyclerView.setHasFixedSize(true);

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

    }

    @Override
    public void onStart() {
        super.onStart();


    }

}
