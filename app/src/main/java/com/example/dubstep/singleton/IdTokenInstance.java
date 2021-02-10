package com.example.dubstep.singleton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

public class IdTokenInstance {
    private static String token;
    public static String getToken(){
        if(token == null){
            FirebaseAuth
                    .getInstance()
                    .getAccessToken(true)
                    .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                        @Override
                        public void onSuccess(GetTokenResult getTokenResult) {
                            token = getTokenResult.getToken();
                        }
                    });
        }
        return token;
    }

    public static void setToken(String newToken){
        token = newToken;
    }
}
