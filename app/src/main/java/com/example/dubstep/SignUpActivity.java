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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";
    TextInputEditText txtFullName, txtusername, txtemail, txtpassword, txtMobileNumber;
    Button SignUp;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog1;
    boolean fromGoogleSignIn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    public static String NUMBER_EXTRA = "com.example.dubstep.newPhoneNumber";
    public static String EMAIL_EXTRA = "com.example.dubstep.newEmail";
    public static String NAME_EXTRA = "com.example.dubstep.newName";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtemail = (TextInputEditText) findViewById(R.id.EmailEditText1);
        txtFullName = (TextInputEditText) findViewById(R.id.NameEditText1);
        txtusername = (TextInputEditText) findViewById(R.id.UsernameEditText1);
        txtMobileNumber = (TextInputEditText) findViewById(R.id.MobileNumberEditText1);
        txtpassword = (TextInputEditText) findViewById(R.id.PasswordEditText1);
        SignUp = (Button) findViewById(R.id.SignUpButton);
        firebaseAuth = FirebaseAuth.getInstance();
        fromGoogleSignIn = false;
        if (getIntent().hasExtra(NAME_EXTRA)){
            txtFullName.setText(getIntent().getStringExtra(NAME_EXTRA));
            fromGoogleSignIn = true;
        }

        if (getIntent().hasExtra(NUMBER_EXTRA)){
            txtMobileNumber.setText(getIntent().getStringExtra(NUMBER_EXTRA).substring(3,13));
        }
        if (getIntent().hasExtra(EMAIL_EXTRA)){
            txtemail.setText(getIntent().getStringExtra(EMAIL_EXTRA));
        }

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

                verifyDetails(MobileNumber,email);


            }
        });
    }

    private void verifyDetails(String mobileNumber,String email) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = null;
            numberProto = phoneUtil.parse(mobileNumber, "IN");
            checkIfNumberExists(phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164),email);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
    }

    private void checkIfNumberExists(String phoneNo,String email) {
        progressDialog1.show();
        UserDatabase.getInstance().checkUserPhoneName("FoodDude",phoneNo)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()){
                            Log.d("OTP", "onResponse: "+new Gson().toJson(response.code()));
                            if (response.code()==404){
                                if (!fromGoogleSignIn){
                                    checkIfEmailExists(phoneNo,email);
                                } else {

                                }

                            } else if (response.code()==401){
                                Toast.makeText(SignUpActivity.this, "Re - enter your number", Toast.LENGTH_SHORT).show();
                                progressDialog1.dismiss();
                            }
                            return;
                        }
                        if (response.body().getError()==null){
//                            Number exists
                            Toast.makeText(SignUpActivity.this, "Phone number already used", Toast.LENGTH_SHORT).show();
                            progressDialog1.dismiss();

                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, new Gson().toJson(t.getMessage()), Toast.LENGTH_SHORT).show();
                        progressDialog1.dismiss();
                    }
                });
    }

    private void checkIfEmailExists(String phoneNo, String email) {
        UserDatabase.getInstance().checkUserEmail("FoodDude",email)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
                            return;
                        }

                        if (response.code()==404){
//                            email not found signUp
                            createUserWithEmail();
                        }else if (response.code()==401){
                            Toast.makeText(SignUpActivity.this, "Re - enter your details", Toast.LENGTH_SHORT).show();
                            progressDialog1.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, new Gson().toJson(t.getMessage()), Toast.LENGTH_SHORT).show();
                        progressDialog1.dismiss();
                    }
                });
    }

    private void createUserWithEmail() {

        try {
            String email = txtemail.getText().toString().trim();
            String password = txtpassword.getText().toString().trim();
            String fullName = txtFullName.getText().toString();
            String Username = txtusername.getText().toString();
            String MobileNumber;
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = null;
            numberProto = phoneUtil.parse(txtMobileNumber.getText().toString(), "IN");
            MobileNumber = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
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
                                                        MobileNumber.substring(3,13),
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
                                                                    Toast.makeText(SignUpActivity.this, "Email Registration successful", Toast.LENGTH_SHORT).show();
                                                                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(SignUpActivity.this,"Verify email , then login again",Toast.LENGTH_LONG).show();
                                                                            Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                                                                            details.phoneNumber = MobileNumber;
                                                                            intent.putExtra(OtpActivity.OTP_EXTRA,MobileNumber);
                                                                            intent.putExtra(OtpActivity.USER_EXTRA,new Gson().toJson(details));
                                                                            intent.putExtra(OtpActivity.TYPE_EXTRA,0);
                                                                            startActivity(intent);
                                                                            progressDialog1.dismiss();
                                                                            finish();
                                                                        }
                                                                    });
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

        } catch (NumberParseException e) {

            e.printStackTrace();
        }
    }

    private void linkUserWithEmail() {

        try {
            String email = txtemail.getText().toString().trim();
            String password = txtpassword.getText().toString().trim();
            String fullName = txtFullName.getText().toString();
            String Username = txtusername.getText().toString();
            String MobileNumber;
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber numberProto = null;
            numberProto = phoneUtil.parse(txtMobileNumber.getText().toString(), "IN");
            MobileNumber = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
            AuthCredential credential = EmailAuthProvider.getCredential(email,password);
            firebaseAuth.getCurrentUser().linkWithCredential(credential)
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
                                                        MobileNumber.substring(3,13),
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
                                                                    Toast.makeText(SignUpActivity.this, "Email Registration successful", Toast.LENGTH_SHORT).show();
                                                                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(SignUpActivity.this,"Verify email , then login again",Toast.LENGTH_LONG).show();
                                                                            Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                                                                            details.phoneNumber = MobileNumber;
                                                                            intent.putExtra(OtpActivity.OTP_EXTRA,MobileNumber);
                                                                            intent.putExtra(OtpActivity.USER_EXTRA,new Gson().toJson(details));
                                                                            intent.putExtra(OtpActivity.TYPE_EXTRA,0);
                                                                            startActivity(intent);
                                                                            progressDialog1.dismiss();
                                                                            finish();
                                                                        }
                                                                    });
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

        } catch (NumberParseException e) {

            e.printStackTrace();
        }
    }


}
