package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.dubstep.Fragment.AboutUsFragment;
import com.example.dubstep.Fragment.ContactUsFragment;
import com.example.dubstep.Fragment.HomeFragment;
import com.example.dubstep.Fragment.OrderFragment;
import com.example.dubstep.Fragment.ProfileFragment;
import com.example.dubstep.Fragment.TermsAndConditionFragment;
import com.example.dubstep.Interface.ItemClickListener;
import com.example.dubstep.Model.FoodItem;
import com.example.dubstep.Model.User;
import com.example.dubstep.ViewHolder.FoodItemViewHolder;
import com.example.dubstep.viewmodel.OrderItemViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth firebaseAuth;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ImageButton drawerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        toolbar = findViewById(R.id.main_toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerButton = findViewById(R.id.drawer_btn);

        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        ){

        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        greetingUserHeader();

        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void greetingUserHeader() {
        SharedPreferences mPrefs = MainActivity.this.getSharedPreferences(getString(R.string.shared_prefs_filename),MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(getString(R.string.shared_prefs_user),"");
        User user = gson.fromJson(json,User.class);

        TextView userNameTextView = navigationView.getHeaderView(0).findViewById(R.id.userNameTextView);
        userNameTextView.setText(String.format("Hi!\n%s", user.name));

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_home:
                if(navigationView.getMenu().findItem(R.id.nav_home).isChecked()){
                    break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new HomeFragment())
                        .commit();
                break;

            case R.id.profile_nav:
                if(navigationView.getMenu().findItem(R.id.profile_nav).isChecked()){
                    break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,new ProfileFragment())
                        .commit();
                break;
            case R.id.nav_cart:
                Intent intent = new Intent(this, CartMainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_order:
//                Intent orderIntent = new Intent(this, OrdersActivity.class);
//                startActivity(orderIntent);
                if(navigationView.getMenu().findItem(R.id.nav_order).isChecked()){
                    break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,new OrderFragment())
                        .commit();
                break;
            case R.id.log_out:
                if (navigationView.getMenu().findItem(R.id.nav_order).isChecked()){
                    getSupportFragmentManager()
                            .popBackStack();

                }
//                mGoogleSignInClient.signOut();
                mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                        new ViewModelProvider(MainActivity.this,
                                ViewModelProvider.AndroidViewModelFactory.getInstance(MainActivity.this.getApplication())
                        ).get(OrderItemViewModel.class).deleteAllItemsAndLogout();

                    }
                });
            case R.id.terms_condition:
                if (navigationView.getMenu().findItem(R.id.terms_condition).isChecked()){
                    break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,new TermsAndConditionFragment())
                        .commit();
                break;
            case R.id.about_us:
                if (navigationView.getMenu().findItem(R.id.about_us).isChecked()){
                    break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,new AboutUsFragment())
                        .commit();
                break;
            case R.id.contact_us:
                if (navigationView.getMenu().findItem(R.id.contact_us).isChecked()){
                    break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,new ContactUsFragment())
                        .commit();
                break;

        }
        navigationView.setCheckedItem(item);

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;

    }

    public void onBackPressed() {
        int item = navigationView.getCheckedItem().getItemId();
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } if(item == R.id.profile_nav||item == R.id.nav_order || item == R.id.terms_condition){
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            getSupportFragmentManager().popBackStack();
            navigationView.setCheckedItem(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }




}
