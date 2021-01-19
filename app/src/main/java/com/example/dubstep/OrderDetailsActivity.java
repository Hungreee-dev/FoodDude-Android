package com.example.dubstep;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dubstep.Fragment.OrderFragment;
import com.example.dubstep.Model.CartItem;
import com.example.dubstep.Model.Order;
import com.example.dubstep.adapter.OrderAdapter;
import com.example.dubstep.singleton.OrderDetails;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

    private TextView orderId;
    private TextView timeOfOrder;
    private TextView addressLine1;
    private TextView addressLine2;
    private TextView pincode;
    private TextView totalPrice;
    private TableLayout orderItems;
    private Chip orderStatus;
    private Order order;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_item_details);
        Log.d("details", getIntent().getStringExtra(OrderFragment.orderDetailsIntent));
//        orderid
        orderId = findViewById(R.id.textview_order_id);
//        time of order
        timeOfOrder = findViewById(R.id.textview_time_of_order);
//        address 1
        addressLine1 = findViewById(R.id.textview_person_details_address1);
//        address 2
        addressLine2 = findViewById(R.id.textview_person_details_address2);
//        pincode
        pincode = findViewById(R.id.textview_person_details_pincode);
//
//        Order Details
//        item name   price  quantity   price*quantity
        orderItems = findViewById(R.id.table_layout_orders_details);
//
        totalPrice = findViewById(R.id.textview_total_price_orders);
//        Order Status
//        chip   (0,1,2)
          orderStatus = findViewById(R.id.chip_status);

          order = new Gson().fromJson(getIntent().getStringExtra(OrderFragment.orderDetailsIntent),Order.class);

          setData();
    }

    private void setData() {
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                Color.BLACK,
                Color.RED,
                Color.GREEN,
                Color.BLUE
        };

        ColorStateList myList = new ColorStateList(states, colors);
        orderId.setText(order.getOrderId());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss", Locale.US);
        Calendar cal  = Calendar.getInstance();
        cal.setTimeInMillis(order.getBilling().getOrderTime().getTimestamp());
        timeOfOrder.setText(sdf.format(cal.getTime()));
        addressLine1.setText(order.getDeliveryAddress().getLine1());
        addressLine2.setText(order.getDeliveryAddress().getLine2());
        pincode.setText(order.getDeliveryAddress().getPincode());
        totalPrice.setText("\u20B9 "+order.getBilling().getFinalAmount());
        for (CartItem item: order.getCartItems()){
            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row_orders,null);
            ((TextView)row.findViewById(R.id.textview_orders_details)).setText(item.getName());
            ((TextView)row.findViewById(R.id.textview_orders_price)).setText("\u20B9 "+item.getPrice());
            ((TextView)row.findViewById(R.id.textview_orders_quantity)).setText(String.valueOf(item.getQuantity()));
            double price = Double.parseDouble(item.getPrice())*item.getQuantity();
            ((TextView)row.findViewById(R.id.textview_orders_total_price)).setText("\u20B9 "+price);
            orderItems.addView(row);
        }

        TableRow delivery = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row_orders,null);
        ((TextView)delivery.findViewById(R.id.textview_x)).setVisibility(View.INVISIBLE);
        ((TextView)delivery.findViewById(R.id.textview_orders_price)).setVisibility(View.INVISIBLE);
        ((TextView)delivery.findViewById(R.id.textview_orders_details)).setText("Delivery Charge");
        ((TextView)delivery.findViewById(R.id.textview_orders_quantity)).setVisibility(View.INVISIBLE);
        ((TextView)delivery.findViewById(R.id.textview_orders_total_price)).setText(" \u20B9 "+ order.getBilling().getDeliveryCharge());
        orderItems.addView(delivery);

        if (order.getBilling().getPromocode()!=null){
            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.table_row_orders,null);
            ((TextView)row.findViewById(R.id.textview_x)).setVisibility(View.INVISIBLE);
            ((TextView)row.findViewById(R.id.textview_orders_price)).setVisibility(View.INVISIBLE);
            ((TextView)row.findViewById(R.id.textview_orders_details)).setText(order.getBilling().getPromocode());
            ((TextView)row.findViewById(R.id.textview_orders_quantity)).setText(String.valueOf(order.getBilling().getDiscount())+"%");
            double price = order.getBilling().getDiscount()/100.0 * order.getBilling().getFinalAmount();
            ((TextView)row.findViewById(R.id.textview_orders_total_price)).setText("-\u20B9 "+price);
            orderItems.addView(row);
        }
        switch (order.getOrderStatus()){
            case 0:
                orderStatus.setText("Processing");
                orderStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),R.color.colorPending)));
                break;
            case 1:
                orderStatus.setText("Delivering");
                orderStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),R.color.colorAccent)));
                break;
            case 2:
                orderStatus.setText("Delivered");
                orderStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(getBaseContext(),R.color.colorDelivered)));
                break;
        }
    }
}
