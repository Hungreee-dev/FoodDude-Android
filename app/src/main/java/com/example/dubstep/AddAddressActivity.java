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

import com.example.dubstep.Model.UserAddress;
import com.example.dubstep.database.PinCodeDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AddAddressActivity extends AppCompatActivity {

    EditText pincodeEditText;
    EditText address1EditText;
    EditText address2EditText;
    EditText address3EditText;
    TextView pincodeNotFound;
    FirebaseUser mUser;
    FirebaseDatabase mDatabase;
    DatabaseReference mPincode;
    String pincode;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        pincodeEditText = findViewById(R.id.pincode_editText);
        address1EditText = findViewById(R.id.address1_editText);
        address2EditText = findViewById(R.id.address2_editText);
        address3EditText = findViewById(R.id.address3_editText);
        pincodeNotFound = findViewById(R.id.pincode_not_found_textview);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mPincode = mDatabase.getReference().child("pincode");
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
        setAddress();
    }

    private void setAddress(){
        mDatabase.getReference().child("user_address").child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    UserAddress address = snapshot.getValue(UserAddress.class);
                    pincodeEditText.setText(address.getPincode());
                    address1EditText.setText(address.getAddress1());
                    address2EditText.setText(address.getAddress2());
                    address3EditText.setText(address.getAddress3());
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void addAddress(View view) {
        progressDialog.show();
        pincodeNotFound.setVisibility(View.INVISIBLE);
        pincode = (pincodeEditText.getText().toString()!=null)?pincodeEditText.getText().toString():"";
        if(pincode.equals("")){
            Toast.makeText(this,"Pincode can't be blank",Toast.LENGTH_SHORT).show();
        } else {
//         1. Check pincode present or not
            PinCodeDatabase.getInstance().getPinCodeList().enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (!response.isSuccessful()) {
                        Log.d("OnFailure", "onFailure: Unable to fetch " + response.message());
                        return;
                    } else {
                        List<String> mPinCodes = response.body();
                        if (mPinCodes.contains(pincode)) {
                            String address1 = (address1EditText.getText().toString() != null) ? address1EditText.getText().toString() : "";
                            String address2 = (address2EditText.getText().toString() != null) ? address2EditText.getText().toString() : "";
                            String address3 = (address3EditText.getText().toString() != null) ? address3EditText.getText().toString() : "";
//              2. Add that address to user_address database under user uid
                            UserAddress address = new UserAddress(pincode, address1, address2, address3);
                            FirebaseDatabase.getInstance().getReference()
                                    .child("user_address")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(address).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    onComplete();
                                }
                            });
                        } else {
                            pincodeNotFound.setVisibility(View.VISIBLE);
                        }
                        progressDialog.dismiss();
                    }
                    }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    Log.d("OnFailure", "onFailure: Unable to fetch "+t.getMessage());
                }

            });
        }

    }


    public void onComplete(){
        finish();
    }
}