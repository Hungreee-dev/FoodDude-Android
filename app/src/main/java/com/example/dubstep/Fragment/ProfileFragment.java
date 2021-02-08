package com.example.dubstep.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dubstep.LoginActivity;
import com.example.dubstep.Model.Result;
import com.example.dubstep.Model.User;
import com.example.dubstep.R;
import com.example.dubstep.ViewHolder.OtpActivity;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private TextInputEditText mUsername;
    private TextInputEditText mFullName;
    private TextInputEditText mMobileNumber;
    private TextInputEditText mEmail;
    private Button UpdateButton;
    private FirebaseAuth firebaseAuth;
    private boolean doUpdate;
    String newUsername = "";
    String newFullname = "";
    String newEmail = "";
    String newPhoneNumber = "";
    boolean changeUsername = false;
    boolean changeFullname = false;
    boolean changeEmail = false;
    boolean changePhoneNumber = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        mUsername = view.findViewById(R.id.mUsername);
        mFullName = view.findViewById(R.id.mFullName);
        mMobileNumber = view.findViewById(R.id.mMobileNumber);
        mEmail = view.findViewById(R.id.mEmail);
        UpdateButton = view.findViewById(R.id.UpdateButton);

        Context context = getActivity();
        SharedPreferences mPrefs = context.getSharedPreferences(
                getString(R.string.shared_prefs_filename) ,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.shared_prefs_user),"");
        User user = gson.fromJson(json,User.class);

        String Username = user.getName();
        String FullName = user.getName();
        String Email = user.getEmail();
        String PhoneNumber =  user.getPhoneNumber();
        if(!Email.equals(firebaseAuth.getCurrentUser().getEmail())){

            mEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        }else{
            mEmail.setText(Email);
        }

        mUsername.setText(Username);
        mFullName.setText(FullName);
        mMobileNumber.setText(PhoneNumber);
        mEmail.setText(Email);

        Log.d("profile", "onViewCreated: "+json);
        mMobileNumber.addTextChangedListener(myTextWatcher());


        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newUsername = mUsername.getText().toString();
                newFullname = mFullName.getText().toString();
                newEmail = mEmail.getText().toString();
                newPhoneNumber = mMobileNumber.getText().toString();

                if (!newPhoneNumber.equals(PhoneNumber)){
                    Log.d("profile", "onNumberChanged ");
                    changePhoneNumber = true;
                }

                if (!newFullname.equals(FullName)) {
                    changeFullname = true;
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newFullname)
                            .build();

                    firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
                }
                if (!newUsername.equals(Username)) {
                    changeUsername = true;
                }
                if (!newEmail.equals(Email)) {
                    changeEmail = true;
                    // [START update_email]
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.updateEmail(newEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity().getApplicationContext(),  "Email Changed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    // [END update_email]
                }


                if(changePhoneNumber || changeEmail || changeUsername || changeFullname){
//                    database update
                    User user = new User(newFullname,newPhoneNumber,newEmail,firebaseAuth.getUid());


//                    save to sharedPrefs
                    SharedPreferences.Editor prefEditor = mPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(user);
                    prefEditor.putString(getString(R.string.shared_prefs_user),json);
                    prefEditor.apply();
                    if (changePhoneNumber){
                        updatePhoneNumber(newPhoneNumber,user);
                    } else {
                        Log.d("profile", "onNotUpdatingNumber ");
                        updateUserInFirestore(user);
                    }

//                    In the last reset all boolean to false and newVariable to ""
                    Toast.makeText(getContext(),  "Updated",Toast.LENGTH_SHORT).show();
                    changePhoneNumber = false;
                    changeFullname = false;
                    changeUsername = false;
                    changeEmail = false;
                    newPhoneNumber = "";
                    newEmail = "";
                    newUsername = "";
                    newFullname = "";
                }

            }
        });
    }


    private void updateUserInFirestore(User user) {
        user.uid = firebaseAuth.getUid();
            UserDatabase.getInstance().updateUser(user, IdTokenInstance.getToken()).enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    if (response.isSuccessful()){
                        Toast.makeText(getActivity().getApplicationContext(), "User's data updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "User's data not updated", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), "User's data not updated due to some error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void updatePhoneNumber(String newPhoneNumber,User user) {

        Log.d("profile", "onUpdatingNumber " + new Gson().toJson(firebaseAuth.getCurrentUser().getProviderData()));
        Intent intent = new Intent(getActivity(), OtpActivity.class);
        intent.putExtra(OtpActivity.OTP_EXTRA,newPhoneNumber);
        intent.putExtra(OtpActivity.USER_EXTRA,new Gson().toJson(user));
        intent.putExtra(OtpActivity.TYPE_EXTRA,2);
        startActivity(intent);

    }

    private TextWatcher myTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10){
                    UpdateButton.setClickable(true);
                    mMobileNumber.setError(null);
                } else {
                    UpdateButton.setClickable(false);
                    mMobileNumber.setError("Number is not 10 digits");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        doUpdate = false;
    }
}
