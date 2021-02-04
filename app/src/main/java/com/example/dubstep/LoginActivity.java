package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dubstep.Model.Result;
import com.example.dubstep.Model.User;
import com.example.dubstep.ViewHolder.OtpActivity;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText txtemail, txtpassword;
    private TextInputLayout inputLayoutPhone;
    private TextInputEditText inputEditTextPhone;
    private Button btn_login;
    private boolean email = false;
    private Button btn_login_otp;
    private boolean otp = true;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
//    private SignInButton GoogleSignInButton;
//    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 1;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtemail = (EditText) findViewById(R.id.EmailLoginEditText);
        txtpassword = (EditText) findViewById(R.id.PasswordLoginEditText);
        btn_login = (Button) findViewById(R.id.LoginButton);
        btn_login_otp = findViewById(R.id.LoginOtpButton);
        inputEditTextPhone = findViewById(R.id.loginTextInputEditText);
        inputLayoutPhone = findViewById(R.id.loginTextInputLayout);

        inputEditTextPhone.addTextChangedListener(myWatcher());
//        GoogleSignInButton = (SignInButton) findViewById(R.id.GoogleSignInButton);

        firebaseAuth = FirebaseAuth.getInstance();
        MaterialButton forgotPasswordBtn = findViewById(R.id.forgot_password_btn);
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,PasswordResetActivity.class));
            }
        });

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        progressDialog.dismiss();

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();

//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

//        GoogleSignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                GSignIn();
//            }
//        });

//        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser mFireBaseUser = firebaseAuth.getCurrentUser();
//                if (mFireBaseUser != null){
//                    //FirebaseAuth.getInstance().signOut();
//                    //mGoogleSignInClient.signOut();
//                    //Toast.makeText(LoginActivity.this,  "You are logged in",Toast.LENGTH_LONG).show();
//                }
//                else {
//                    //Toast.makeText(LoginActivity.this,  "Please Log in",Toast.LENGTH_LONG).show();
//                }
//            }
//        };

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email){
                    inputEditTextPhone.setText("");
                    inputLayoutPhone.setVisibility(View.GONE);
                    txtpassword.setVisibility(View.VISIBLE);
                    txtemail.setVisibility(View.VISIBLE);
                    email = true;
                    otp = false;
                    inputLayoutPhone.setClickable(true);
                    return;
                }
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );

                String email = txtemail.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this,  "Please Enter Email",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this,  "Please Enter Password",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    boolean verified = firebaseAuth.getCurrentUser().isEmailVerified();
                                    String uid = firebaseAuth.getUid();
                                    if (verified){
                                        firebaseAuth.getAccessToken(true)
                                                .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                                    @Override
                                                    public void onSuccess(GetTokenResult getTokenResult) {
                                                        IdTokenInstance.setToken(getTokenResult.getToken());
                                                        checkUserData(uid);
                                                    }
                                                });

                                    } else{
                                        startActivity(new Intent(LoginActivity.this, UserVerifyActivity.class));
                                        finish();
                                    }

                                } else {

                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this,  "Login Failed or User not Available",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                                    intent.putExtra(SignUpActivity.EMAIL_EXTRA,email);
                                    startActivity(intent);


                                }

                            }
                        });

            }
        });

        btn_login_otp.setOnClickListener(mylistner());
    }

    private TextWatcher myWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("regex", "onTextChanged: "+s.length());
                if (s.length()!=10){
//                    inputLayoutPhone.setError("Enter Complete Mobile No.");
                } else {
                    Pattern pattern = Pattern.compile("^\\d+");
                    Matcher m = pattern.matcher(s.toString());
                    Log.d("regex", "onTextChanged: "+m.matches());
                    if (!m.matches()){
                        inputLayoutPhone.setError("Mobile no needs to be only digits");
                    } else {
                        inputLayoutPhone.setError(null);
                        btn_login_otp.setClickable(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private View.OnClickListener mylistner() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (otp){
                    if (inputEditTextPhone.getText()==null){
                        inputEditTextPhone.setError("Mobile No. can't be empty");
                        return;
                    }
                    if (inputEditTextPhone.getText().length()!=10){
                        inputLayoutPhone.setError("Enter Complete Mobile No.");
                        return;
                    }
//                    start new activity

                    try {
                        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                        Phonenumber.PhoneNumber numberProto = null;
                        numberProto = phoneUtil.parse(inputEditTextPhone.getText().toString(), "IN");
                        checkIfNumberExists(phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164));
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }


                } else {
                    inputLayoutPhone.setVisibility(View.VISIBLE);
                    txtemail.setText("");
                    txtpassword.setText("");
                    txtemail.setVisibility(View.GONE);
                    txtpassword.setVisibility(View.GONE);
                    otp = true;
                    email = false;
                }
            }
        };
    }

    private void checkUserData(String uid) {
        UserDatabase.getInstance().getUser(uid,IdTokenInstance.getToken())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(LoginActivity.this, new Gson().toJson(response.body()), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }
                        User user = response.body();
                        Context context = LoginActivity.this;
                        SharedPreferences mPrefs = context.getSharedPreferences(
                                getString(R.string.shared_prefs_filename) ,MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = mPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(user);

                        prefEditor.putString(getString(R.string.shared_prefs_user),json);
                        prefEditor.apply();
                        progressDialog.dismiss();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        firebaseAuth.addAuthStateListener(mAuthStateListener);

//        FirebaseUser user = firebaseAuth.getCurrentUser();

    }

    public void btn_register(View view) {

        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }

//    public void GSignIn(){
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(LoginActivity.this,  "Login Failed or User not Available",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            boolean newuser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (newuser) {
                                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                            }
                            else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)); //change later based on rider/customer
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this,  "Login Failed or User not Available",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void checkIfNumberExists(String phoneNo) {
        progressDialog.show();
        UserDatabase.getInstance().checkUserPhoneName("FoodDude",phoneNo)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()){
                            Log.d("OTP", "onResponse: "+new Gson().toJson(response.code()));
                            if (response.code()==404){
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Phone number not found,\n Please register first", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                                intent.putExtra(SignUpActivity.NUMBER_EXTRA,phoneNo);
                                startActivity(intent);
                            } else if (response.code()==401){
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Re - enter your number", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }
                        if (response.body().getError()==null){
//                            Number exists
                            progressDialog.dismiss();
                            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                            intent.putExtra(OtpActivity.OTP_EXTRA,inputEditTextPhone.getText().toString());
                            intent.putExtra(OtpActivity.TYPE_EXTRA,1);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, new Gson().toJson(t.getMessage()), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
