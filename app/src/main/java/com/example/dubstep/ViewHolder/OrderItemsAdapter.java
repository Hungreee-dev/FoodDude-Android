package com.example.dubstep.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubstep.Model.OrderItem;
import com.example.dubstep.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class OrderItemsAdapter extends FirebaseRecyclerAdapter<OrderItem, OrderItemsAdapter.OrderHolder> {

    private OnItemClickListener listener;

    public OrderItemsAdapter(@NonNull FirebaseRecyclerOptions<OrderItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHolder holder, int position, @NonNull final OrderItem model)
    {
        holder.mobile_no.setText(model.getPhone_Number());
        holder.cart_size.setText(model.getCartTotalAmount());
        holder.latitude.setText(Double.toString(model.getLatitude()));
        holder.longitude.setText(Double.toString(model.getLongitude()));

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(model.getLatitude(),model.getLongitude(), model.getPhone_Number(), model.getCustomer_UID(), model.getStatus(),model.getCartTotalAmount());
            }
        });
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,
                parent, false);
        OrderHolder holder = new OrderHolder(v);
        return holder;

    }

    class OrderHolder extends RecyclerView.ViewHolder {
        TextView mobile_no;
        TextView cart_size;
        TextView latitude;
        TextView longitude;
        ImageButton accept;

        public OrderHolder(@NonNull View itemView) {
            super(itemView);
            mobile_no = itemView.findViewById(R.id.text_view_mob);
            cart_size = itemView.findViewById(R.id.text_view_cart);
            latitude = itemView.findViewById(R.id.text_view_Latitude);
            longitude = itemView.findViewById(R.id.text_view_Longitude);
            accept = itemView.findViewById(R.id.button_send);

        }
    }

    public interface OnItemClickListener{
        void onItemClick(double latitude, double longitude, String phone, String cust_uid, String status,String amount);
    }
    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

}

