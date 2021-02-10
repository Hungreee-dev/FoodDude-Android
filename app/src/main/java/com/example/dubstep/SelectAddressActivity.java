package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dubstep.Model.Address;
import com.example.dubstep.Model.UserAddress;
import com.example.dubstep.adapter.AddressItemAdapter;
import com.example.dubstep.adapter.FoodItemAdapter;
import com.example.dubstep.database.AddressDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.example.dubstep.singleton.OrderDetails;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectAddressActivity extends AppCompatActivity {

    public static final int EDIT_ADDRESS_CODE=1;

    TextView emptyAddressTextView;
    public ExtendedFloatingActionButton addAddress;
    public ExtendedFloatingActionButton continueOrderBtn;
    private ProgressDialog progressDialog;
    private RecyclerView addressRecycler;
    private FirebaseUser mUser;
    AddressItemAdapter adapter;
    List<Address>  addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_address);
        emptyAddressTextView = findViewById(R.id.address_empty_textView);
        addressRecycler = findViewById(R.id.address_recycler_view);
        addressList = new ArrayList<Address>();

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        addAddress = findViewById(R.id.set_address_btn);

        continueOrderBtn = findViewById(R.id.continueOrder);

        progressDialog = new ProgressDialog(SelectAddressActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );

//  1. check if exist in sharedPrefs
//  2. then fetch from backend
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        addressRecycler.setLayoutManager(layoutManager);
        adapter = new AddressItemAdapter();
        adapter.setOnItemClickListener(new AddressItemAdapter.OnItemClickListener() {
            @Override
            public void onEditButtonClick(Address address) {
//                edit address
                editAddress(address);
            }

            @Override
            public void onDeleteButtonClick(Address address) {
                deleteAddress(address);
            }

            @Override
            public void onItemClick(Address address) {
//                go to promocode activity
//                add continue order functionality to each address
                OrderDetails orderDetails = OrderDetails.getInstance();
                orderDetails.setDeliveryAddress(address);
                Intent intent = new Intent(SelectAddressActivity.this,ReferralActivity.class);
                startActivity(intent);
            }
        });

    }

    private void editAddress(Address address) {
        String json = new Gson().toJson(address);
        Intent intent = new Intent(SelectAddressActivity.this, AddAddressActivity.class);
        intent.putExtra(AddAddressActivity.EXTRA_ADDRESS,json);
        startActivityForResult(intent,EDIT_ADDRESS_CODE);

    }

    private void deleteAddress(Address address) {
        Log.d("address", "deleteAddress: "+new Gson().toJson(address));
        AddressDatabase.getInstance().removeAddress(mUser.getUid(),address.getId(),IdTokenInstance.getToken())
                .enqueue(new Callback<Address>() {
                    @Override
                    public void onResponse(Call<Address> call, Response<Address> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(SelectAddressActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }
                        if (response.body().getMessage()!=null){
                            addressList.removeIf(address1 -> address1.getId().equals(address.getId()));
                            Log.d("address", "deleteAddress: "+new Gson().toJson(addressList));
                            if(addressList.isEmpty()){
                                adapter.submitList(null);
                            } else {
                                adapter.submitList(addressList);
                            }
                        }
                        if (addressList.isEmpty()){
                            emptyAddressTextView.setVisibility(View.VISIBLE);
                            addressRecycler.setVisibility(View.INVISIBLE);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Address> call, Throwable t) {
                        Toast.makeText(SelectAddressActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("address", "onResume: called");
        showAddress();
    }

    private void showAddress() {
        Log.d("address", "showAddress: "+Log.getStackTraceString(new Throwable()));
        AddressDatabase.getInstance().getAllAddress(mUser.getUid(), IdTokenInstance.getToken())
            .enqueue(new Callback<List<Address>>() {
                @Override
                public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(SelectAddressActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }
                    addressList = new ArrayList<>();
                    adapter.submitList(addressList);
                    addressList.addAll(response.body()) ;
                    if (addressList.isEmpty()){
//                        show no address found
                        emptyAddressTextView.setVisibility(View.VISIBLE);
                        addressRecycler.setVisibility(View.INVISIBLE);
                    } else {
                        emptyAddressTextView.setVisibility(View.INVISIBLE);
                        addressRecycler.setVisibility(View.VISIBLE);
                        adapter.submitList(addressList);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<List<Address>> call, Throwable t) {
                    Toast.makeText(SelectAddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

        addressRecycler.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void changeAddress(View view) {
        Intent intent = new Intent(SelectAddressActivity.this, AddAddressActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EDIT_ADDRESS_CODE&&resultCode==RESULT_OK){
//            we could use this technique to decrease the call to server &
//            to fetch all record on each edit request on showAddress() on onResume() method
        }
    }
}