package com.example.dubstep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dubstep.singleton.IdTokenInstance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class Splash_Screen extends AppCompatActivity {

    TextView dubstep, dabba;
    ImageView logo;
    private FirebaseAuth mAuth;

    private static int SPLASH_SCREEN = 2500; //2.5 secs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        dubstep =findViewById(R.id.dubstepText);
        dabba = findViewById(R.id.dabbaText);
        logo = findViewById(R.id.logoImage);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if(currentUser != null) {
                    if (currentUser.isEmailVerified()){
                        if(Splash_Screen.this
                                .getSharedPreferences(
                                        getString(R.string.shared_prefs_filename),MODE_PRIVATE
                                )
                                .contains(getString(R.string.shared_prefs_user))){
                            mAuth.getAccessToken(true)
                                    .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                        @Override
                                        public void onSuccess(GetTokenResult getTokenResult) {
                                            IdTokenInstance.setToken(getTokenResult.getToken());
                                            Intent intent=new Intent(Splash_Screen.this, MainActivity.class);
                                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                                    makeSceneTransitionAnimation(Splash_Screen.this, (View)logo, "imageMain");
                                            startActivity(intent, options.toBundle());
                                            finish();

                                        }
                                    });
                        }else{
                            Intent intent=new Intent(Splash_Screen.this, LoginActivity.class);
                            ActivityOptionsCompat options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation(Splash_Screen.this, (View)logo, "imageMain");
                            startActivity(intent, options.toBundle());
                            finish();
                        }
                    } else {
                        Intent intent=new Intent(Splash_Screen.this, LoginActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(Splash_Screen.this, (View)logo, "imageMain");
                        startActivity(intent, options.toBundle());
                        finish();
                    }
                } else {
                    Intent intent=new Intent(Splash_Screen.this, LoginActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(Splash_Screen.this, (View)logo, "imageMain");
                    startActivity(intent, options.toBundle());
                    finish();

                }
            }

        },SPLASH_SCREEN);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.splashanimation);
        dubstep.startAnimation(myanim);
    }
}
