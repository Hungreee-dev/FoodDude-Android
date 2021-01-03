package com.example.dubstep.Fragment;

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

import com.example.dubstep.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private EditText mUsername;
    private EditText mFullName;
    private EditText mMobileNumber;
    private EditText mEmail;
    private Button UpdateButton;
    DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

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

        FirebaseDatabase.getInstance().getReference("user")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Username = dataSnapshot.child("Username").getValue().toString();
                        String FullName = dataSnapshot.child("fullName").getValue().toString();
                        String Email = dataSnapshot.child("Email").getValue().toString();
                        if(!Email.equals(firebaseAuth.getCurrentUser().getEmail())){
                            FirebaseDatabase.getInstance().getReference()
                                    .child("user")
                                    .child(firebaseAuth.getCurrentUser().getUid())
                                    .child("Email")
                                    .setValue(firebaseAuth.getCurrentUser().getEmail())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mEmail.setText(firebaseAuth.getCurrentUser().getEmail());
                                        }
                                    });

                        }else{
                            mEmail.setText(Email);
                        }
                        String PhoneNumber = dataSnapshot.child("PhoneNumber").getValue().toString();
                        mUsername.setText(Username);
                        mFullName.setText(FullName);
                        mMobileNumber.setText(PhoneNumber);
                        mEmail.setText(Email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference("user")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String Username = dataSnapshot.child("Username").getValue().toString();
                                String FullName = dataSnapshot.child("fullName").getValue().toString();
                                String Email = dataSnapshot.child("Email").getValue().toString();
                                String PhoneNumber = dataSnapshot.child("PhoneNumber").getValue().toString();

                                final String User_Name = mUsername.getText().toString();
                                final String Full_Name = mFullName.getText().toString();
                                final String E_mail = mEmail.getText().toString();
                                final String Phone_Number = mMobileNumber.getText().toString();

                                if (!Full_Name.equals(FullName)) {
                                    FirebaseDatabase.getInstance().getReference("user")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("fullName").setValue(Full_Name);
                                    mFullName.setText(Full_Name);
                                }
                                if (!User_Name.equals(Username)) {
                                    FirebaseDatabase.getInstance().getReference("user")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Username").setValue(User_Name);
                                    mUsername.setText(User_Name);
                                }
                                if (!E_mail.equals(Email)) {
                                    FirebaseDatabase.getInstance().getReference("user")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Email").setValue(E_mail);
                                    mEmail.setText(E_mail);
                                    // [START update_email]
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
                                    FirebaseDatabase.getInstance().getReference("user")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("PhoneNumber").setValue(Phone_Number);
                                    mMobileNumber.setText(Phone_Number);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                Toast.makeText(getContext(),  "Updated",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
