package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dubstep.Model.CartInfo;
import com.example.dubstep.Model.CartItem;
import com.example.dubstep.Model.FoodItem;
import com.example.dubstep.Model.GlideApp;
import com.example.dubstep.Model.Menu;
import com.example.dubstep.Model.UserCart;
import com.example.dubstep.ViewHolder.FoodClassViewHolder;
import com.example.dubstep.ViewHolder.FoodItemViewHolder;
import com.example.dubstep.adapter.FoodItemAdapter;
import com.example.dubstep.database.CartDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodItemActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String base_name;
    DatabaseReference cartref;
    StorageReference storageRef;
    FirebaseAuth firebaseAuth;

    private FloatingActionButton mCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item);

//        get category name
        base_name = getIntent().getStringExtra("category");

//        get menu items
        ArrayList<Menu> menuList = (ArrayList<Menu>) getIntent().getSerializableExtra("menu_list");
        Log.d("Count on items", "onCreate: "+menuList.size());


//        storage ref for images
        storageRef = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        cartref = FirebaseDatabase.getInstance().getReference("Cart");
        TextView foodItemBaseName = findViewById(R.id.food_item_base_name);
        foodItemBaseName.setText(base_name);
        setFloatingButtonAction();
        setRecyclerView(menuList);



    }

    private void setRecyclerView(ArrayList<Menu> items) {
        recyclerView = findViewById(R.id.food_item_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
//        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>().setQuery(foodref, FoodItem.class).build();
        FoodItemAdapter adapter = new FoodItemAdapter();
        adapter.submitList(items);

        adapter.setOnItemClickListener(new FoodItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Menu menu) {
                Log.d("cart add", "onItemClick: add menu item to cart" + menu.getName());
//                just so cart would work
                CartItem cartItem = new CartItem(menu.getName(),menu.getPrice(),1);
                addToCart(cartItem);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void addToCart(CartItem addedItem) {
        UserCart userCart = new UserCart(firebaseAuth.getUid(), addedItem);
        CartDatabase.getInstance().addCartItem(userCart, IdTokenInstance.getToken())
                .enqueue(new Callback<UserCart>() {
                    @Override
                    public void onResponse(Call<UserCart> call, Response<UserCart> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(FoodItemActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(FoodItemActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<UserCart> call, Throwable t) {
                        Toast.makeText(FoodItemActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setFloatingButtonAction() {
        mCartButton = findViewById(R.id.cart_btn_food_item);
        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CartMainActivity.class);
                startActivity(intent);
            }
        });
    }
}