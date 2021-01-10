package com.example.dubstep.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.dubstep.Model.User;
import com.example.dubstep.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private EditText mUsername;
    private EditText mFullName;
    private EditText mMobileNumber;
    private EditText mEmail;
    private Button UpdateButton;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private boolean doUpdate;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        Context context = getActivity();
        SharedPreferences mPrefs = context.getSharedPreferences(
                getString(R.string.shared_prefs_filename) ,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.shared_prefs_user),"");
        User user = gson.fromJson(json,User.class);

        String Username = user.getName();
        String FullName = user.getName();
        String Email = user.getEmail();
        if(!Email.equals(firebaseAuth.getCurrentUser().getEmail())){

            mEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        }else{
            mEmail.setText(Email);
        }
        String PhoneNumber = user.getPhoneNumber();
        mUsername.setText(Username);
        mFullName.setText(FullName);
        mMobileNumber.setText(PhoneNumber);
        mEmail.setText(Email);


        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String User_Name = mUsername.getText().toString();
                final String Full_Name = mFullName.getText().toString();
                final String E_mail = mEmail.getText().toString();
                final String Phone_Number = mMobileNumber.getText().toString();

                if (!Full_Name.equals(FullName)) {
                    doUpdate = true;
                    mFullName.setText(Full_Name);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(Full_Name)
                            .build();

                    firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
                }
                if (!User_Name.equals(Username)) {
                    doUpdate = true;
                    mUsername.setText(User_Name);
                }
                if (!E_mail.equals(Email)) {
                    doUpdate = true;
                    mEmail.setText(E_mail);
                    // [START update_email]
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    user.updateEmail(E_mail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(),  "Email Changed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    // [END update_email]
                }
                if (!Phone_Number.equals(PhoneNumber)){
                   doUpdate = true;
                    mMobileNumber.setText(Phone_Number);
                }

                if(doUpdate){
//                    database update
                    User user = new User(FullName,PhoneNumber,Email,firebaseAuth.getUid());
//                  TODO: apply changes to firestore database by
//                    sending this user to update-details route


//                    save to sharedPrefs
                    SharedPreferences.Editor prefEditor = mPrefs.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(user);
                    prefEditor.putString(getString(R.string.shared_prefs_user),json);
                    prefEditor.apply();
                }
                Toast.makeText(getContext(),  "Updated",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        doUpdate = false;
    }
}
