package com.example.dubstep;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.dubstep.Model.KeyRazorpay;
import com.example.dubstep.Model.Order;
import com.example.dubstep.Model.User;
import com.example.dubstep.Model.UserCart;
import com.example.dubstep.database.CartDatabase;
import com.example.dubstep.database.OrderDatabase;
import com.example.dubstep.database.PaymentDatabase;
import com.example.dubstep.database.UserDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThankYouActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private static final String TAG = "ThankYouActivity";
    Button continueButton;
   // private ImageView thankYouImage;
//    LottieAnimationView animationView;
    private TextView orderStatus;
    private View bgView;
    private LottieAnimationView successAnimation, failAnimation;
    public static String ORDER_EXTRA = "com.example.dubstep.orderDetails";
    private Order order;
    User user;
    String mUid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        successAnimation = findViewById(R.id.success_animation);
        failAnimation = findViewById(R.id.fail_animation);
        bgView= findViewById(R.id.bg_view);
//        animationView = findViewById(R.id.animation_order_completed);
        continueButton = findViewById(R.id.ContinueButton);
        orderStatus = findViewById(R.id.final_order_result_textview);
        progressDialog = new ProgressDialog(this);

        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        progressDialog.setCancelable(false);

//        animationView.setVisibility(View.INVISIBLE);
        successAnimation.setVisibility(View.INVISIBLE);
        continueButton.setVisibility(View.INVISIBLE);
        orderStatus.setVisibility(View.INVISIBLE);
//        animationView.setAnimation("failed.json");


        mUid = FirebaseAuth.getInstance().getUid();

        if (getIntent().hasExtra(ORDER_EXTRA)) {
            String orderString = getIntent().getStringExtra(ORDER_EXTRA);
            order = new Gson().fromJson(orderString, Order.class);
        } else {
            Toast.makeText(this, "Please select payment method again", Toast.LENGTH_SHORT).show();
            finish();
        }
        fetchUserData();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (continueButton.getText().equals("Retry Payment")){
                    progressDialog.show();
                    fetchUserData();
                    return;
                }
                startActivity(
                        new Intent(ThankYouActivity.this, MainActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                );
            }
        });
    }

    private void fetchUserData() {
        Log.d(TAG, "fetchUserData: 1");
        if (this.getSharedPreferences(getString(R.string.shared_prefs_filename), MODE_PRIVATE)
                .contains(getString(R.string.shared_prefs_user))) {
            Log.d(TAG, "fetchUserData: 2");
            SharedPreferences mPrefs = this.getSharedPreferences(getString(R.string.shared_prefs_filename),MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString(getString(R.string.shared_prefs_user),"");
            user = gson.fromJson(json,User.class);
            Log.d(TAG, "fetchUserData: "+order.getBilling().getPaymentMethod());
            if (order.getBilling().getPaymentMethod().equals("ONLINE")) {
                onlinePaymentProcedure();
            } else if (order.getBilling().getPaymentMethod().equals("CASH")) {
                offlinePaymentProcedure();
            } else {
                Toast.makeText(this, "Please select payment method again", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            UserDatabase.getInstance().getUser(mUid,IdTokenInstance.getToken()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(ThankYouActivity.this, "Unable to fetch user details", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    Log.d(TAG, "onResponse: 3");
                    if (response.body()!=null){
                        user = response.body();
                        if (order.getBilling().getPaymentMethod().equals("ONLINE")) {
                            onlinePaymentProcedure();
                        } else if (order.getBilling().getPaymentMethod().equals("CASH")) {
                            offlinePaymentProcedure();
                        } else {
                            Toast.makeText(ThankYouActivity.this, "Please select payment method again", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(ThankYouActivity.this, "Unable to fetch user details, "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

        private void offlinePaymentProcedure () {
            createOrder(order);
        }

        private void onlinePaymentProcedure () {
            Log.d(TAG, "onlinePaymentProcedure: ");
            PaymentDatabase.getInstance().getRazorpayKey(mUid, IdTokenInstance.getToken())
                    .enqueue(new Callback<KeyRazorpay>() {
                        @Override
                        public void onResponse(Call<KeyRazorpay> call, Response<KeyRazorpay> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(ThankYouActivity.this, "Some network error, Try again!!", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }

                            assert response.body() != null;
                            if (!response.body().isError()) {
                                String key = response.body().getKey();
                                Log.d(TAG, "onResponse: "+key);
                                startCheckout(key);
                            }
                        }

                        @Override
                        public void onFailure(Call<KeyRazorpay> call, Throwable t) {
                            Toast.makeText(ThankYouActivity.this, "Some network error," + t.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }

        private void startCheckout (String key){
            Log.d(TAG, "startCheckout: ");
            /**
             * Instantiate Checkout
             */
            Checkout checkout = new Checkout();
            checkout.setKeyID(key);
            /**
             * Set your logo here
             */
            checkout.setImage(R.drawable.logo);

            /**
             * Reference to current activity
             */
            final Activity activity = this;

            /**
             * Pass your payment options to the Razorpay Checkout as a JSONObject
             */
            try {
                JSONObject options = new JSONObject();

                options.put("name", user.getName());
                options.put("description", "Reference No. #123456");
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//                options.put("order_id", order.getOrderId());//from response of step 3.
                options.put("theme.color", "#3399cc");
                options.put("currency", "INR");
                options.put("amount", order.getBilling().getFinalAmount()*100);//pass amount in currency subunits
                options.put("prefill.email", user.getEmail());
                options.put("prefill.contact", user.getPhoneNumber());
                checkout.open(activity, options);

            } catch (Exception e) {
                Log.e(TAG, "Error in starting Razorpay Checkout", e);
            }
        }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Log.d(TAG, "onPaymentSuccess: "+s);
        Log.d(TAG, new Gson().toJson(paymentData));
        order.getBilling().setPaymentId(paymentData.getPaymentId());
        createOrder(order);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d(TAG, "onPaymentError: "+i);
        Log.d(TAG, "onPaymentError: "+s);
        Log.d(TAG, new Gson().toJson(paymentData));
        showAsFailed(s);
    }

    private void createOrder(Order order) {

        try {
            OrderDatabase.getInstance().addOrderItem(order).enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(ThankYouActivity.this, "Due to some error order was not created", Toast.LENGTH_SHORT).show();
                        showAsFailed("Due to some error order was not created");
                        progressDialog.dismiss();
                        return;
                    }

                    if (response.body().getSuccess()!=null){
//                    Order was successful
//                        add the order id to user database
                        UserDatabase.getInstance().addOrderId(mUid,order.getOrderId(),IdTokenInstance.getToken())
                                .enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(Call<User> call, Response<User> response) {
                                       if (response.isSuccessful()){
                                           deleteCart();
                                           return;
                                       }
                                        showAsFailed("Due to some error order was not created");
                                    }

                                    @Override
                                    public void onFailure(Call<User> call, Throwable t) {
                                        Toast.makeText(ThankYouActivity.this, "Error occured \n "+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        showAsFailed(t.getMessage());
                                    }
                                });

                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Toast.makeText(ThankYouActivity.this, "Error occured \n "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    showAsFailed(t.getMessage());
                }
            });
        } catch (NullPointerException e){
            Toast.makeText(this, "Null pointer exception occured", Toast.LENGTH_SHORT).show();
            showAsFailed("Some error occured");
            progressDialog.dismiss();
        }


    }

    private void showAsFailed(String error) {
        progressDialog.dismiss();
//        animationView.setVisibility(View.VISIBLE);
        successAnimation.setVisibility(View.INVISIBLE);
       // successAnimation.playAnimation();
        continueButton.setVisibility(View.VISIBLE);
        orderStatus.setVisibility(View.VISIBLE);
        failAnimation.setVisibility(View.VISIBLE);
        findViewById(R.id.thank_you).setVisibility(View.INVISIBLE);
        bgView.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.orangeLight));
        orderStatus.setText(String.format("Order Failed \n %s", error));
//        animationView.setAnimation("failed.json");
       // failAnimation.setImageDrawable(getDrawable(R.drawable.no_orders));
        continueButton.setText("Retry Payment");
    }

    private void deleteCart() {
        CartDatabase.getInstance().deleteCart(mUid, IdTokenInstance.getToken())
                .enqueue(new Callback<UserCart>() {
                    @Override
                    public void onResponse(Call<UserCart> call, Response<UserCart> response) {
                        showAsSuccess();
                        if (!response.isSuccessful()){
                            Toast.makeText(ThankYouActivity.this, "Due to some issue cart was not cleared", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (response.body().getMessage()==null){
                            Toast.makeText(ThankYouActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserCart> call, Throwable t) {
                        Toast.makeText(ThankYouActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        showAsSuccess();
                    }
                });
    }

    private void showAsSuccess() {
        progressDialog.dismiss();
//        animationView.setVisibility(View.VISIBLE);
        successAnimation.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.VISIBLE);
        orderStatus.setVisibility(View.VISIBLE);
        orderStatus.setText("Order Successful");
//        animationView.enableMergePathsForKitKatAndAbove(true);
//        animationView.setAnimation("success.json");
        ///successAnimation.setImageDrawable(getDrawable(R.drawable.tick));
        continueButton.setText("Continue Ordering");
    }
}
