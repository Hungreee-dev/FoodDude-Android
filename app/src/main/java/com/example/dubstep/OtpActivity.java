package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dubstep.Model.Result;
import com.example.dubstep.Model.User;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    Button verify_btn;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    public String phoneNo;
    String verificationCodeBySystem;
    TextInputEditText[] otpEditText;
    User user;
    public static String OTP_EXTRA = "com.example.dubstep.phoneNumber";
    public static String USER_EXTRA = "com.example.dubstep.userDetails";
    public static String TYPE_EXTRA = "com.example.dubstep.typeOfActivity";
    private int type;
//    TYPE_EXTRA -> 0 ->  from SignUpActivity
//    TYPE_EXTRA -> 1 ->  from LoginActivity
//    TYPE_EXTRA -> 2 ->  from ProfileActivity

    FirebaseAuth firebaseAuth;
    private MaterialButton resendOtpButton;
    private CountDownTimer myTimer;

    private TextWatcher myTextWatcher(TextInputEditText currEditText){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count==1) {
                    switch (currEditText.getId()) {
                        case R.id.inputEditOtp1:
                            currEditText.clearFocus();
                            otpEditText[1].requestFocus();
                            otpEditText[1].setCursorVisible(true);
                            break;
                        case R.id.inputEditOtp2:
                            currEditText.clearFocus();
                            otpEditText[2].requestFocus();
                            otpEditText[2].setCursorVisible(true);
                            break;
                        case R.id.inputEditOtp3:
                            currEditText.clearFocus();
                            otpEditText[3].requestFocus();
                            otpEditText[3].setCursorVisible(true);
                            break;
                        case R.id.inputEditOtp4:
                            currEditText.clearFocus();
                            otpEditText[4].requestFocus();
                            otpEditText[4].setCursorVisible(true);
                            break;
                        case R.id.inputEditOtp5:
                            currEditText.clearFocus();
                            otpEditText[5].requestFocus();
                            otpEditText[5].setCursorVisible(true);
                            break;
                        case R.id.inputEditOtp6:
                            currEditText.clearFocus();
//                        go for submit otp
                            break;
                    }
                } else {
//                    if(currEditText.getText().length()>0){
//                        currEditText.setText("");
//                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        otpEditText = new TextInputEditText[6];
        otpEditText[0] = findViewById(R.id.inputEditOtp1);
        otpEditText[1] = findViewById(R.id.inputEditOtp2);
        otpEditText[2] = findViewById(R.id.inputEditOtp3);
        otpEditText[3] = findViewById(R.id.inputEditOtp4);
        otpEditText[4] = findViewById(R.id.inputEditOtp5);
        otpEditText[5] = findViewById(R.id.inputEditOtp6);

        otpEditText[0].addTextChangedListener(myTextWatcher(otpEditText[0]));
        otpEditText[1].addTextChangedListener(myTextWatcher(otpEditText[1]));
        otpEditText[2].addTextChangedListener(myTextWatcher(otpEditText[2]));
        otpEditText[3].addTextChangedListener(myTextWatcher(otpEditText[3]));
        otpEditText[4].addTextChangedListener(myTextWatcher(otpEditText[4]));
        otpEditText[5].addTextChangedListener(myTextWatcher(otpEditText[5]));

        resendOtpButton = findViewById(R.id.resend_otp_button);
        verify_btn = findViewById(R.id.verify_btn);
        progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        progressDialog.setCancelable(false);

        if (getIntent().hasExtra(OTP_EXTRA)){
            phoneNo = getIntent().getStringExtra(OTP_EXTRA);
        } else {
            finish();
        }

        if (getIntent().hasExtra(TYPE_EXTRA)){
            type = getIntent().getIntExtra(TYPE_EXTRA,-1);
        }
        if (type == 0 || type == 2){
            if (getIntent().hasExtra(USER_EXTRA)){
                String userString = getIntent().getStringExtra(USER_EXTRA);
                user = new Gson().fromJson(userString,User.class);
            } else {
                finish();
            }
        } else if (type == -1){
            Toast.makeText(this, "Error in sending otp", Toast.LENGTH_SHORT).show();
            finish();
        }


        sendVerificationCodeToUser(phoneNo);


        //manually handling OTP verification
        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder code = new StringBuilder();
                for (TextInputEditText inputEditText: otpEditText){
                    if (inputEditText.getText() == null || inputEditText.getText().toString().trim().equals("") ){
                        inputEditText.requestFocus();
                        inputEditText.setError("Can't be null");
                        return;
                    } else {
                        code.append(inputEditText.getText());
                    }
                }
                progressDialog.show();
                verifyCode(code.toString());
            }
        });

        resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCodeToUser(phoneNo);
            }
        });
    }


    private void sendVerificationCodeToUser(String phoneNo)  {
        verify_btn.setEnabled(false);
        resendOtpButton.setEnabled(false);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            PhoneNumber numberProto = phoneUtil.parse(phoneNo, "IN");
            this.phoneNo = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    this.phoneNo, // Phone number to verify
                    60, // Timeout duration
                    TimeUnit.SECONDS, // Unit of timeout
                    TaskExecutors.MAIN_THREAD, // Activity (for callback binding)
                    mCallbacks); // OnVerificationStateChangedCallbacks
        } catch (Exception e) {
            // Wrong format
            Toast.makeText(OtpActivity.this,  e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verify_btn.setEnabled(true);
                    progressDialog.dismiss();
                    startTimerForResend();
                    //Get the code in global variable
                    verificationCodeBySystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    progressDialog.dismiss();
                    Toast.makeText(OtpActivity.this,  e.getMessage(),Toast.LENGTH_LONG).show();
                }
            };

    private void startTimerForResend() {
        myTimer =  new CountDownTimer(60000,1000){

            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("timer", "onTick: "+millisUntilFinished/1000);
                resendOtpButton.setText( String.format("00:%d",millisUntilFinished/1000) );
            }

            @Override
            public void onFinish() {
                resendOtpButton.setText("RESEND");
                resendOtpButton.setEnabled(true);
            }
        };
        myTimer.start();
    }

    private void verifyCode(String codeByUser) {
        Log.d("code", "verifyCode: "+codeByUser);
        setOtp(codeByUser);

        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
            progressDialog.show();
            switch (type){
                case 0:
                    signInTheUserByCredentialsFirstTime(credential);
                    break;
                case 1:
                    signInTheUserByCredentialsNormal(credential);
                    break;
                case 2:
                    updateNumberOfUser(credential);
                    break;
                default:
                    Toast.makeText(this, "Cant figure out mode of otp activity", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        } catch (Exception e){
            Toast.makeText(this, "There is a problem with the verification/credential information you provided", Toast.LENGTH_SHORT).show();
        }


    }

    private void updateNumberOfUser(PhoneAuthCredential credential) {
//        firebaseAuth.getCurrentUser().updatePhoneNumber(credential)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                    }
//                });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().unlink("phone")
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            updateNumberInDatabase();
                                        } else {
                                            Toast.makeText(OtpActivity.this, "Unable to add this no.", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OtpActivity.this, "Unable to remove previous no.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
        ;

        firebaseAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

            }
        });
    }

    private void updateNumberInDatabase() {
        user.phoneNumber = phoneNo.substring(3,13);
        user.uid = firebaseAuth.getUid();
        UserDatabase.getInstance().updateUser(user,IdTokenInstance.getToken()).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()){
                    Toast.makeText(OtpActivity.this, "User's data updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(OtpActivity.this, "User's data not updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(OtpActivity.this, "User's data not updated due to some error", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void signInTheUserByCredentialsNormal(PhoneAuthCredential credential) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
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
                                startActivity(new Intent(OtpActivity.this, UserVerifyActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }

                        } else {
                            Toast.makeText(OtpActivity.this,  "Login Failed or User not Available",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void setOtp(String code) {
        Log.d("code", "verifyCode otp: "+code);
        for (int i=0; i <6 ; i++){
            otpEditText[i].setText(String.valueOf(code.charAt(i)));
        }
    }

    private void signInTheUserByCredentialsFirstTime(PhoneAuthCredential credential) {

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(OtpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Log.d("otp", "onComplete: sign of user with no");
                            Toast.makeText(OtpActivity.this, "Verified", Toast.LENGTH_SHORT).show();
                            firebaseAuth.getAccessToken(true)
                                    .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                        @Override
                                        public void onSuccess(GetTokenResult getTokenResult) {
                                            Log.d("otp", "onSuccess of token set: ");
                                            IdTokenInstance.setToken(getTokenResult.getToken());
                                            updateNameNumber(IdTokenInstance.getToken());
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(OtpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateNameNumber(String token) {
        UserDatabase.getInstance().setUserPhoneName(user, token)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (!response.isSuccessful()){
//                            some error in updating
                            Toast.makeText(OtpActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            return;
                        }

                        if (response.body().getError()==null){
                            progressDialog.dismiss();
//                            updated successfuly

                            firebaseAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    boolean verified = firebaseAuth.getCurrentUser().isEmailVerified();
                                    Log.d("otp", "onSuccess: phone number updated");
                                    Log.d("otp", "email verified: "+verified);
                                    if (verified){
                                        checkUserData(firebaseAuth.getUid());
                                    } else{
                                        startActivity(new Intent(OtpActivity.this, UserVerifyActivity.class));
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Log.d("otp", "onResponse: "+response.body().getError());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
//                        some error in updating
                        Toast.makeText(OtpActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void checkUserData(String uid) {
        Log.d("otp", "before get user");
        UserDatabase.getInstance().getUser(uid, IdTokenInstance.getToken())
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        progressDialog.dismiss();
                        if(!response.isSuccessful()){
                            Toast.makeText(OtpActivity.this, new Gson().toJson(response.body()), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        User user = response.body();
                        Context context = OtpActivity.this;
                        SharedPreferences mPrefs = context.getSharedPreferences(
                                getString(R.string.shared_prefs_filename) ,MODE_PRIVATE);
                        SharedPreferences.Editor prefEditor = mPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(user);

                        prefEditor.putString(getString(R.string.shared_prefs_user),json);
                        prefEditor.apply();
                        startActivity(new Intent(OtpActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(OtpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myTimer!=null){
            myTimer.cancel();
        }

    }
}
