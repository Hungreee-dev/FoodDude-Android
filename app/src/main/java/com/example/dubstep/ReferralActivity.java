package com.example.dubstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dubstep.Model.Order;
import com.example.dubstep.Model.PromoDate;
import com.example.dubstep.Model.Promocode;
import com.example.dubstep.Model.UserCart;
import com.example.dubstep.Model.UserFrequency;
import com.example.dubstep.database.CartDatabase;
import com.example.dubstep.database.OrderDatabase;
import com.example.dubstep.database.PromocodeDatabase;
import com.example.dubstep.singleton.IdTokenInstance;
import com.example.dubstep.singleton.OrderDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReferralActivity extends AppCompatActivity {
    EditText referral;
    Button placeOrder;
    TextView promoCodeText;
    TextView cartTotal;
    TextView totalPrice;
    Double totalDiscountPrice;
    boolean promoUsed;
    String currPromo;
    ProgressDialog progressDialog;
    TextView discountOnPromo;
    long count;
    List<Promocode> promocodeList;
    private String mUid;
    OrderDetails orderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
//        fetch cart details
        mUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referral = findViewById(R.id.edit_text_referral);
        placeOrder = findViewById(R.id.button_place_order);
        promoCodeText = findViewById(R.id.promocode_dicount_text);
        cartTotal = findViewById(R.id.cart_total_without_promo);
        totalPrice = findViewById(R.id.cart_total_with_promo);
        discountOnPromo = findViewById(R.id.dicount_on_promo_textview);
        promocodeList = new ArrayList<Promocode>();
        orderDetails = OrderDetails.getInstance();
        double cartTotalPrice = orderDetails.getBillingDetails().getFinalAmount();
        totalDiscountPrice = cartTotalPrice;
        cartTotal.setText(String.format("Cart Price : ₹ %s",cartTotalPrice));
        totalPrice.setText(String.format("Total Price : ₹ %s",totalDiscountPrice));
        discountOnPromo.setVisibility(View.GONE);
        promoUsed = false;
        progressDialog = new ProgressDialog(this);
    }

    public void btnPlaceOrder(View view) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popUpView = inflater.inflate(R.layout.activity_confirm, null);

//        create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popUpView, width, height);


        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popUpView.findViewById(R.id.button_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popUpView.findViewById(R.id.button_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageManager pm = getPackageManager();
                progressDialog.show();
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.getWindow().setBackgroundDrawableResource(
                        android.R.color.transparent
                );
                try {
//                    TODO:  1. Delete the cart
//                           2. Add That deleted cart information to order class
//                           2. Send that order class to backend route
                    deleteCart();
                    if(currPromo==null){
                        orderDetails.getBillingDetails().setDiscount(null);
                    }
                    orderDetails.getBillingDetails().setPaymentMethod("CASH");
                    orderDetails.getBillingDetails().setPaymentId("1234");
                    orderDetails.getBillingDetails().setOrderTime(new PromoDate(Calendar.getInstance().getTimeInMillis()));
                    String id = mUid+orderDetails.getBillingDetails().getOrderTime().getTimestamp();
                    Order order = new Order(
                            id,
                            mUid,
                            orderDetails.getCartItems(),
                            orderDetails.getBillingDetails(),
                            0,
                            orderDetails.getDeliveryAddress()
                    );

//                    function to create an order
                    createOrder(order);



                } catch (Exception e){
                    Toast.makeText(getBaseContext(),"Due to some error order was not completed .\n Please order again",Toast.LENGTH_SHORT).show();

                }



            }
        });


    }

    private void createOrder(Order order) {

        try {
            OrderDatabase.getInstance().addOrderItem(order).enqueue(new Callback<Order>() {
                @Override
                public void onResponse(Call<Order> call, Response<Order> response) {
                    if (!response.isSuccessful()){
                        Toast.makeText(ReferralActivity.this, "Due to some error order was not created", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        return;
                    }

                    if (response.body().getSuccess()!=null){
//                    Order was successful
                        Intent intent = new Intent(ReferralActivity.this,OrdersActivity.class);
                        progressDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<Order> call, Throwable t) {
                    Toast.makeText(ReferralActivity.this, "Error occured \n "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        } catch (NullPointerException e){
            Toast.makeText(this, "Null pointer exception occured", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }

    private void deleteCart() {
        CartDatabase.getInstance().deleteCart(mUid, IdTokenInstance.getToken())
                .enqueue(new Callback<UserCart>() {
                    @Override
                    public void onResponse(Call<UserCart> call, Response<UserCart> response) {
                        if (!response.isSuccessful()){
                            Toast.makeText(ReferralActivity.this, "Due to some issue cart was not cleared", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (response.body().getMessage()==null){
                            Toast.makeText(ReferralActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserCart> call, Throwable t) {
                        Toast.makeText(ReferralActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void applyPromo(View view) {
//        search if promocode exists
        if(referral.getText().toString().equals("")){
            Toast.makeText(this,"Enter Some Promocode to check",Toast.LENGTH_SHORT).show();
            discountOnPromo.setVisibility(View.GONE);
            return;
        }
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
        promoCodeText.setVisibility(View.INVISIBLE);
        discountOnPromo.setVisibility(View.GONE);
        checkPromoCode(referral.getText().toString());

    }

    private void checkPromoCode(String promocode) {
        Log.d("promocode", "checkPromoCode: "+promocode);
        if (promocodeList.isEmpty()) {
            PromocodeDatabase.getInstance().getAllPromo()
                    .enqueue(new Callback<List<Promocode>>() {
                        @Override
                        public void onResponse(Call<List<Promocode>> call, Response<List<Promocode>> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(ReferralActivity.this, "Some error occured while fetching details", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                return;
                            }
                            promocodeList = response.body();
                            Log.d("Promocode", new Gson().toJson(promocodeList.get(0)));
                            for (Promocode currPromocode : promocodeList) {
                                if (currPromocode.getCode().equals(promocode)) {
                                    setPromocode(currPromocode);
                                    progressDialog.dismiss();
                                    return;
                                }
                            }
                            currPromo = null;
                            setPromocode(null);
                            progressDialog.dismiss();

                        }

                        @Override
                        public void onFailure(Call<List<Promocode>> call, Throwable t) {
                            Toast.makeText(ReferralActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
        } else {
            for (Promocode currPromocode : promocodeList) {
                if (currPromocode.getCode().equals(promocode)) {
                    setPromocode(currPromocode);
                    progressDialog.dismiss();
                    return;
                }
            }

            setPromocode(null);
            progressDialog.dismiss();
        }

    }

    private void setPromocode(Promocode currPromocode) {
        int discountPercentage = 0;
        if(currPromocode == null){
//            set Promocode not found
            promoCodeText.setVisibility(View.VISIBLE);
            discountOnPromo.setVisibility(View.GONE);
            promoCodeText.setText("No Promocode Found");

        } else {
//            1. check category
//            2. on updatePromocode() apply discount
            int category = currPromocode.getCategory();
            switch (category){
//            category 1 unlimited -> no need for further check
                case 1:
                    discountPercentage = currPromocode.getPercentage();
                    updatePromocode(currPromocode);
                    break;

//            category 2 Max limit per users -> get the frequency of that user
                case 2:
                    if(rejectForDate(currPromocode.getStartTime(),currPromocode.getExpiry())){
                        return;
                    }
                    if(currPromocode.getUserFrequencyList()!=null){
                        for (UserFrequency freq:currPromocode.getUserFrequencyList()){
                            if(freq.getUserId().equals(mUid)) {
                                if(freq.getFrequency()>=currPromocode.getMaxUsagePerUser()){
//                                    usage limit exhausted
                                    promoCodeText.setVisibility(View.VISIBLE);
                                    discountOnPromo.setVisibility(View.GONE);
                                    promoCodeText.setText("You have exhausted this promocode usage limit");
                                    return;
                                } else {
                                    discountPercentage = currPromocode.getPercentage();
//                                    add 1 to  user frequency
                                    freq.setFrequency(freq.getFrequency()+1);
                                    updatePromocode(currPromocode);
                                    return;
                                }
                            }
                        }

//                        that means user was not found create a new one then
                        UserFrequency frequency = new UserFrequency(mUid);
                        currPromocode.getUserFrequencyList().add(frequency);
                    } else {
//                        user frequency list was empty
                        UserFrequency frequency = new UserFrequency(mUid);
                        currPromocode.setUserFrequencyList(new ArrayList<>());
                        currPromocode.getUserFrequencyList().add(frequency);
                        updatePromocode(currPromocode);
                        return;
                    }

                    break;
                case 3:
//            category 3 max limit -> check max limit is exhausted or not
                    boolean found = false;
                    if (currPromocode.getUserFrequencyList()!=null){

                        if(currPromocode.getMaxUsage() > currPromocode.getUserFrequencyList().size()) {
                            for (UserFrequency freq : currPromocode.getUserFrequencyList()) {
                                if (freq.getUserId().equals(mUid)) {
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                UserFrequency frequency = new UserFrequency(mUid);
                                currPromocode.getUserFrequencyList().add(frequency);
                                updatePromocode(currPromocode);
                                return;
                            }
                        } else {
                            promoCodeText.setVisibility(View.VISIBLE);
                            discountOnPromo.setVisibility(View.GONE);
                            promoCodeText.setText("Promocode exhauted");
                            return;
                        }
                    } else {
                        UserFrequency frequency = new UserFrequency(mUid);
                        currPromocode.setUserFrequencyList(new ArrayList<UserFrequency>());
                        currPromocode.getUserFrequencyList().add(frequency);
                        updatePromocode(currPromocode);
                        return;
                    }
                    break;
                default:
                    break;
            }



        }
    }

    private void updatePromocode(Promocode promocode){
        currPromo = promocode.getCode();
        promoCodeText.setVisibility(View.VISIBLE);
        promoCodeText.setText("Promocode applied \n Discount % :  "+String.valueOf(promocode.getPercentage())+"%");
        discountOnPromo.setVisibility(View.VISIBLE);
        double discountedAmount = orderDetails.getBillingDetails().getFinalAmount();
        Log.d("discount", "updatePromocode: "+totalDiscountPrice);
        discountedAmount = ((double) promocode.getPercentage() / 100.0) * totalDiscountPrice;
        Log.d("discount", discountedAmount+"");
        orderDetails.getBillingDetails().setFinalAmount((int) discountedAmount);
        orderDetails.getBillingDetails().setDiscount(promocode.getPercentage());
        orderDetails.getBillingDetails().setPromocode(promocode.getCode());
        cartTotal.setText("Cart total : \u20B9 "+totalDiscountPrice);
        discountOnPromo.setText("Promocode Discount : - \u20B9 " + discountedAmount );
        totalPrice.setText("Total Amount \u20B9 "+ (totalDiscountPrice-discountedAmount) );

    }

    private boolean rejectForDate(PromoDate startTime, PromoDate expiry) {
        if(!checkForDate(startTime,expiry)){
            promoCodeText.setVisibility(View.VISIBLE);
            discountOnPromo.setVisibility(View.GONE);
            promoCodeText.setText("Promocode expired or not started yet");
            return true;
         } else {
            return false;
        }

    }

    private boolean checkForDate(PromoDate start, PromoDate expiry){
        Date date = new Date();
        Calendar myCal = Calendar.getInstance();
        myCal.set(start.getYear(),start.getMonth(),start.getDay());
        Date startDate = myCal.getTime();
        myCal.set(expiry.getYear(),expiry.getMonth(),expiry.getDay());
        Date expiryDate = myCal.getTime();
//        true means inside expiry and start date time
//        false means outside expiry and start date time
        if(date.after(startDate)){
//            alright
            return date.before(expiryDate);
        }

        return false;
    }

}