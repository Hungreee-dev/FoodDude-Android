package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dubstep.Model.FoodItem;
import com.example.dubstep.Model.GlideApp;
import com.example.dubstep.ViewHolder.FoodClassViewHolder;
import com.example.dubstep.ViewHolder.FoodItemViewHolder;
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

import java.util.HashMap;

public class FoodItemActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    String base_name;
    DatabaseReference cartref;
    StorageReference storageRef;

    private FloatingActionButton mCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item);
        base_name = getIntent().getStringExtra("base_name");
        String index = getIntent().getStringExtra("index");
        DatabaseReference items = FirebaseDatabase.getInstance()
                .getReference()
                .child("food_menu")
                .child(index)
                .child("items");
        storageRef = FirebaseStorage.getInstance().getReference();
        cartref = FirebaseDatabase.getInstance().getReference("Cart");
        TextView foodItemBaseName = findViewById(R.id.food_item_base_name);
        foodItemBaseName.setText(base_name);
        setFloatingButtonAction();
        setRecyclerView(items);



    }

    private void setRecyclerView(DatabaseReference items) {
        recyclerView = findViewById(R.id.food_item_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);
//        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>().setQuery(foodref, FoodItem.class).build();
        FirebaseRecyclerOptions<FoodItem> options = new FirebaseRecyclerOptions.Builder<FoodItem>().setQuery(items,FoodItem.class).build();
        FirebaseRecyclerAdapter<FoodItem, FoodItemViewHolder> adapter =
                new FirebaseRecyclerAdapter<FoodItem, FoodItemViewHolder>(options) {
                    @NonNull
                    @Override
                    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(FoodItemActivity.this).inflate(R.layout.food_item_layout_2,parent,false);
                        FoodItemViewHolder holder = new FoodItemViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final FoodItemViewHolder holder, final int position, @NonNull final FoodItem model) {
                        holder.mFoodItemName.setText(model.getName());
                        model.setCategory(base_name);
                        holder.mFoodItemPrice.setText("Price: \u20B9 " + model.getBase_price());
                        holder.mAddToCart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.mAddToCart.setEnabled(false);
                                addToCart(model,position);
                                //Toast.makeText(MainActivity.this,getRef(position).getKey(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        String childName;
                        if(model.getBase_url()!=null){
                            childName = model.getBase_url();
                        } else {
                            childName = String.format("%s/%s.*",base_name.toLowerCase(),base_name.toLowerCase());
                        }
                        GlideApp.with(FoodItemActivity.this)
                                .load(storageRef.child(childName))
                                .centerCrop()
                                .placeholder(R.drawable.splash_drawable)
                                .into(holder.foodItemImageView);
                    }
                    };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void addToCart(FoodItem addedItem,int position) {
        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("Name", addedItem.getName());
        cartMap.put("Price", String.valueOf(addedItem.getBase_price()));
        cartMap.put("Quantity", "1");
        cartMap.put("Product_ID", base_name+"_"+position);
        cartMap.put("Category" , addedItem.getCategory());

        cartref.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Products")
                .child(String.valueOf(cartMap.get("Product_ID")))
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FoodItemActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
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