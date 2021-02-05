package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dubstep.Model.Result;
import com.example.dubstep.Model.User;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText txtFullName, txtusername, txtemail, txtpassword, txtMobileNumber;
    Button SignUp;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtemail = (EditText) findViewById(R.id.EmailEditText);
        txtFullName = (EditText) findViewById(R.id.NameEditText);
        txtusername = (EditText) findViewById(R.id.UsernameEditText);
        txtMobileNumber = (EditText) findViewById(R.id.MobileNumberEditText);
        txtpassword = (EditText) findViewById(R.id.PasswordEditText);
        SignUp = (Button) findViewById(R.id.SignUpButton);
        firebaseAuth = FirebaseAuth.getInstance();

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog1 = new ProgressDialog(SignUpActivity.this);
                progressDialog1.show();
                progressDialog1.setContentView(R.layout.progress_dialog);
                progressDialog1.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                final String email = txtemail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();
                final String fullName = txtFullName.getText().toString();
                final String Username = txtusername.getText().toString();
                final String MobileNumber = txtMobileNumber.getText().toString();

                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Full Name",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(Username)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Username",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(MobileNumber)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Username",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Email",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this,  "Please Enter Password",Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                    return;
                }


                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    FirebaseAuth.getInstance().getAccessToken(true)
                                            .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                                @Override
                                                public void onSuccess(GetTokenResult getTokenResult) {
                                                    IdTokenInstance.setToken(getTokenResult.getToken());

                                                    final User details = new User(
                                                            fullName,
                                                            MobileNumber,
                                                            email,
                                                            firebaseUser.getUid()
                                                    );
                                                    UserDatabase.getInstance().addUser(details, IdTokenInstance.getToken())
                                                            .enqueue(new Callback<Result>() {
                                                                @Override
                                                                public void onResponse(Call<Result> call, Response<Result> response) {
                                                                    if (!response.isSuccessful()){
                                                                        Toast.makeText(SignUpActivity.this, response.body().getError() + "Try again", Toast.LENGTH_SHORT).show();
                                                                        return;
                                                                    }if (response.body().getMessage()!=null){
//                                                                        User created successfully
                                                                        Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                                                        firebaseUser.sendEmailVerification();
                                                                        Toast.makeText(SignUpActivity.this,"Verify email , then login again",Toast.LENGTH_LONG).show();
                                                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                                        progressDialog1.dismiss();
                                                                        finish();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFailure(Call<Result> call, Throwable t) {
                                                                    Toast.makeText(SignUpActivity.this, "Some error occured ! \n Please try again" +t.getMessage(), Toast.LENGTH_SHORT).show();

                                                                }
                                                            });
                                                }
                                            });




                                } else {

                                    Toast.makeText(SignUpActivity.this, "Authentication failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog1.dismiss();

                                }


                            }
                        });


            }
        });
    }
}
