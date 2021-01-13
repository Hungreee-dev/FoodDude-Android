package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dubstep.Model.CartItem;
import com.example.dubstep.Model.UserCart;
import com.example.dubstep.adapter.CartItemsAdapter;
import com.example.dubstep.database.CartDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//       1. change place order button to Continue button that takes to new activity
//       2. Create a new activity where we have a editext and submit button
//       3. Editext for promocode and submit button for placing order
//       4. Submit Button should open a popup(Dialog) that askes for final confirmation
//       5. On yes send the message to whatsapp.

public class CartMainActivity extends AppCompatActivity {


    CartItemsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MaterialButton mplaceOrder;
    private TextView mPriceTotal;
    private TextView mCartTotal;
    private String myOrderMessage;
    private TextView mDelivery, emptyCartMesage;
    private DatabaseReference userref;
    private DatabaseReference mCartRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private double TotalPrice;
    private List<CartItem> mCartItemList;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_main);

        firebaseAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference("user").child(firebaseAuth.getCurrentUser().getUid().toString());
        mCartRef = FirebaseDatabase.getInstance().getReference("Cart").child(firebaseAuth.getCurrentUser().getUid().toString());

        mplaceOrder = findViewById(R.id.btn_place_order);
        emptyCartMesage = findViewById(R.id.empty_cart_text_view);

        setUpRecycler();
        mplaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartMainActivity.this, SelectAddressActivity.class);
                intent.putExtra("message",createMessage());
                intent.putExtra("wanumber","+916371830551");
                intent.putExtra("cartTotal", String.valueOf(TotalPrice));
                startActivity(intent);
//
//
            }
        });

        progressDialog = new ProgressDialog(CartMainActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

    }

    private String createMessage() {
        return myOrderMessage;
    }

    private void setUpTotals() {

        mPriceTotal = findViewById(R.id.total_price_text_view);
        mCartTotal = findViewById(R.id.cart_total_textView);
        mDelivery = findViewById(R.id.DdeliveryTextView);

        int cartTotal = 0;
//        double discount = 0;

        for (CartItem cartItem : mCartItemList){
          cartTotal += cartItem.getQuantity() * Integer.parseInt(cartItem.getPrice());
        }
        mCartTotal.setText(cartTotal);
        mDelivery.setText("50");
        mPriceTotal.setText(cartTotal+50);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void setUpRecycler() {

        recyclerView = findViewById(R.id.cart_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mCartItemList = new ArrayList<>();
        adapter = new CartItemsAdapter(CartMainActivity.this,mCartItemList);

        CartDatabase.getInstance().getAllCartItems(firebaseAuth.getUid(), IdTokenInstance.getToken())
                .enqueue(new Callback<List<CartItem>>() {
                    @Override
                    public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(CartMainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mCartItemList.addAll(response.body()) ;

//                        change inside of adapter to take CartItem class
                        adapter.submitList(mCartItemList);
                        if (mCartItemList.isEmpty()){
                            //show no address found
                            emptyCartMesage.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        } else {
                            emptyCartMesage.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.submitList(mCartItemList);
                            setUpTotals();
                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<List<CartItem>> call, Throwable t) {
                        Toast.makeText(CartMainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        adapter.setOnValueChangeListener(new CartItemsAdapter.OnValueChangeListener() {
            @Override
            public void onQuantityChange(UserCart userCart, int quantity) {
                userCart.getCartItem().setQuantity(quantity);
                CartDatabase.getInstance().editCartItem(userCart, IdTokenInstance.getToken())
                        .enqueue(new Callback<UserCart>() {
                            @Override
                            public void onResponse(Call<UserCart> call,
                                    Response<UserCart> response) {
                                if (!response.isSuccessful()){
                                    Toast.makeText(CartMainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(CartMainActivity.this, "Quantity Updated", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<UserCart> call, Throwable t) {
                                Toast.makeText(CartMainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        adapter.setOnItemClickListener(new CartItemsAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(UserCart userCart) {
                userCart.getCartItem().setQuantity(0);
                CartDatabase.getInstance().editCartItem(userCart, IdTokenInstance.getToken())
                        .enqueue(new Callback<UserCart>() {
                            @Override
                            public void onResponse(Call<UserCart> call,
                                    Response<UserCart> response) {
                                if (!response.isSuccessful()){
                                    Toast.makeText(CartMainActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(CartMainActivity.this, "Quantity Updated", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<UserCart> call, Throwable t) {
                                Toast.makeText(CartMainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        recyclerView.setAdapter(adapter);

    }
}
