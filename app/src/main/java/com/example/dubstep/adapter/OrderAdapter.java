package com.example.dubstep.adapter;

import static com.example.dubstep.R.*;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubstep.Model.Order;
import com.example.dubstep.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class OrderAdapter extends ListAdapter<Order, OrderAdapter.OrderViewHolder> {
    private OnItemClickListener listener;

    Context mContext;

    public OrderAdapter(Context context) {
        super(DIFF_CALLBACK);
        mContext = context;
    }

    public OrderAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Order> DIFF_CALLBACK = new DiffUtil.ItemCallback<Order>() {
        @Override
        public boolean areItemsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            return oldItem.getOrderId().equals(newItem.getOrderId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Order oldItem, @NonNull Order newItem) {
            if (oldItem.getOrderStatus() != newItem.getOrderStatus()){
                return false;
            } else if (oldItem.getBilling().getOrderTime().getTimestamp() != newItem.getBilling().getOrderTime().getTimestamp()){
                return false;
            } else {
                return oldItem.getBilling().getFinalAmount() == newItem.getBilling().getFinalAmount();
            }
        }
    };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout.order_item_1,parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = getOrderItem(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US);
        Calendar cal  = Calendar.getInstance();
        cal.setTimeInMillis(order.getBilling().getOrderTime().getTimestamp());
        holder.orderTimeTextview.setText(sdf.format(cal.getTime()));

        holder.orderIdTextview.setText(order.getOrderId());
        holder.orderAmountTextview.setText("\u20B9 "+order.getBilling().getFinalAmount());
        String status;
        int colorInt;
        int statusIcon = drawable.ic_cooking_time;
        switch (order.getOrderStatus()){
            case 0:
                status = "Processing";
                colorInt = mContext.getColor(color.processing);
                statusIcon = drawable.ic_cooking_time;
                break;
            case 1:
                status = "Delivering";
                colorInt = mContext.getColor(color.delivering);
                statusIcon = drawable.ic_delivery_bike;
                break;
            case 2:
                status = "Delivered";
                colorInt = mContext.getColor(color.delivered);
                statusIcon = drawable.tick;
                break;
            default:
                status = "";
                colorInt = mContext.getColor(color.grey);
                break;

        }

        holder.orderStatusTextview.setText(status);
        holder.statusImage.setImageResource(statusIcon);
        holder.orderStatusTextview.setTextColor(colorInt);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null && position!=RecyclerView.NO_POSITION){
                    listener.onItemClick(order);
                }
            }
        });

    }

    public Order getOrderItem(int position){
        return getItem(position);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        public TextView orderIdTextview;
        public TextView orderTimeTextview;
        public TextView orderStatusTextview;
        public TextView orderAmountTextview;
        public ImageView statusImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderAmountTextview = itemView.findViewById(id.order_amount_textview);
            orderIdTextview = itemView.findViewById(id.order_id_textview);
            orderStatusTextview = itemView.findViewById(id.order_status_textview);
            orderTimeTextview = itemView.findViewById(id.order_time_textview);
            statusImage = itemView.findViewById(id.status_image);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Order order);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}
