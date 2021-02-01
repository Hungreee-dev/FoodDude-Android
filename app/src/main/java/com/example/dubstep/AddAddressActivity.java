package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dubstep.Model.Address;
import com.example.dubstep.Model.User;
import com.example.dubstep.Model.UserAddress;
import com.example.dubstep.backendInterface.AddressApi;
import com.example.dubstep.database.AddressDatabase;
import com.example.dubstep.database.PinCodeDatabase;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddAddressActivity extends AppCompatActivity {

    public final static String EXTRA_ADDRESS=
            "com.example.dubstep.extra_address";

    EditText pincodeEditText;
    EditText houseNoEditText;
    EditText address1EditText;
    EditText address2EditText;
    EditText cityEditText;
    EditText stateEditText;
    TextView pincodeNotFound;
    FirebaseUser mUser;
    FirebaseDatabase mDatabase;
    String pincode;
    private ProgressDialog progressDialog;
    List<String> mPincode;
    Address oldAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ExtendedFloatingActionButton btn = findViewById(R.id.add_address_button);
        pincodeEditText = findViewById(R.id.pincode_editText);
        houseNoEditText = findViewById(R.id.houseNo_editText);
        address1EditText = findViewById(R.id.address1_editText);
        address2EditText = findViewById(R.id.address2_editText);
        cityEditText = findViewById(R.id.city_editText);
        stateEditText = findViewById(R.id.state_editText);
        pincodeNotFound = findViewById(R.id.pincode_not_found_textview);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mPincode = new ArrayList<String>();

        progressDialog = new ProgressDialog(AddAddressActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        pincodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pincodeNotFound.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if(getIntent().hasExtra(EXTRA_ADDRESS)) {
            setAddress();
        } else {
            progressDialog.dismiss();
            btn.setText("ADD");
        }
    }

    private void setAddress(){
//        get address from intent

            String json = getIntent().getStringExtra(EXTRA_ADDRESS);
            if(json==null){
                setResult(RESULT_CANCELED);
                finish();
            }
            oldAddress = new Gson().fromJson(json,Address.class);
            pincodeEditText.setText(oldAddress.getPincode());
            houseNoEditText.setText(oldAddress.getHouseNumber());
            address1EditText.setText(oldAddress.getLine1());
            address2EditText.setText(oldAddress.getLine2());
            cityEditText.setText(oldAddress.getCity());
            stateEditText.setText(oldAddress.getState());
            progressDialog.dismiss();
    }

    public void addAddress(View view) {
        progressDialog.show();
        pincodeNotFound.setVisibility(View.INVISIBLE);
        pincode = (pincodeEditText.getText().toString()!=null)?pincodeEditText.getText().toString():"";
        if(pincode.equals("")){
            Toast.makeText(this,"Pincode can't be blank",Toast.LENGTH_SHORT).show();
        } else {
//         1. Check pincode present or not
            if(mPincode.isEmpty()){
                PinCodeDatabase.getInstance().getPinCodeList().enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                        if (!response.isSuccessful()) {
                            Log.d("OnFailure", "onFailure: Unable to fetch " + response.message());
                            return;
                        } else {
                            mPincode = response.body();
                            checkPincode();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<String>> call, Throwable t) {
                        Log.d("OnFailure", "onFailure: Unable to fetch "+t.getMessage());
                    }

                });
            } else {
                checkPincode();
            }

        }

    }

    private void checkPincode() {
        if (mPincode.contains(pincode)) {
            if(houseNoEditText.getText().toString() == null || houseNoEditText.getText().toString().trim().equals("")){
                Log.d("address", "checkPincode: "+houseNoEditText.getText().toString().trim());
                Toast.makeText(this, "House No can't be empty", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            if(address1EditText.getText().toString() == null || address1EditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "Address Line 1 can't be empty", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            if(address2EditText.getText().toString() == null || address2EditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "Address Line 2 can't be empty", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            if(cityEditText.getText().toString() == null || cityEditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "City's name can't be empty", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            if(stateEditText.getText().toString() == null || stateEditText.getText().toString().trim().equals("")){
                Toast.makeText(this, "State's name can't be empty", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                return;
            }
            String houseNo = houseNoEditText.getText().toString().trim();
            String address1 = address1EditText.getText().toString().trim();
            String address2 = address2EditText.getText().toString().trim();
            String city = cityEditText.getText().toString().trim();
            String state = stateEditText.getText().toString().trim();
//              2. Add that address to user_address database under user uid
            Address address = new Address(pincode,houseNo,address1,address2,city,state);

            if(getIntent().hasExtra(EXTRA_ADDRESS)){
                editAddress(address);
            } else {
                AddressDatabase.getInstance().addAddress(new UserAddress(mUser.getUid(),address), IdTokenInstance.getToken())
                        .enqueue(new Callback<UserAddress>() {
                            @Override
                            public void onResponse(Call<UserAddress> call, Response<UserAddress> response) {
                                if(!response.isSuccessful()){
                                    Toast.makeText(AddAddressActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                UserAddress addressResponse = response.body();
                                if(addressResponse.getError()!=null){
                                    Toast.makeText(AddAddressActivity.this, addressResponse.getError(), Toast.LENGTH_SHORT).show();
                                } else if(addressResponse.getMessage()!=null){
//                            message added to database successfully
                                    Toast.makeText(AddAddressActivity.this, "Address Added Successfully", Toast.LENGTH_SHORT).show();
                                    onComplete();
                                }
                            }

                            @Override
                            public void onFailure(Call<UserAddress> call, Throwable t) {
                                Toast.makeText(AddAddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }


        } else {
            pincodeNotFound.setVisibility(View.VISIBLE);
        }
        progressDialog.dismiss();
    }

    private void editAddress(Address address) {
        UserAddress userAddress = new UserAddress(mUser.getUid(),address);
        userAddress.setAddressId(oldAddress.getId());
        AddressDatabase.getInstance().editAddress(userAddress,IdTokenInstance.getToken())
                .enqueue(new Callback<Address>() {
                    @Override
                    public void onResponse(Call<Address> call, Response<Address> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(AddAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(AddAddressActivity.this, "Address Added Successfully", Toast.LENGTH_SHORT).show();
                        onComplete();
                    }

                    @Override
                    public void onFailure(Call<Address> call, Throwable t) {
                        Toast.makeText(AddAddressActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void onComplete(){
        progressDialog.dismiss();
        finish();
    }
}