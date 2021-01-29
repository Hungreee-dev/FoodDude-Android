package com.example.dubstep.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubstep.Model.Address;
import com.example.dubstep.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AddressItemAdapter extends ListAdapter<Address, AddressItemAdapter.AddressItemViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onEditButtonClick(Address address);
        void onDeleteButtonClick(Address address);
        void onItemClick(Address address);
    }

    @Override
    public void submitList(@Nullable List<Address> list) {
        super.submitList((list==null||list.isEmpty())?null:new ArrayList<Address>(list));
    }

    public AddressItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Address> DIFF_CALLBACK = new DiffUtil.ItemCallback<Address>() {
        @Override
        public boolean areItemsTheSame(@NonNull Address oldItem, @NonNull Address newItem) {
            if(oldItem.getId().equals(newItem.getId())){
                return true;
            } else if (oldItem.getHouseNumber().equals(newItem.getHouseNumber())){
                return true;
            } else if(oldItem.getLine2().equals(newItem.getLine2())){
                return true;
            } else if(oldItem.getLine1().equals(newItem.getLine1())){
                return true;
            } else {
                return oldItem.getPincode().equals(newItem.getPincode());
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull Address oldItem, @NonNull Address newItem) {
//            if(oldItem.getId().equals(newItem.getId())){
//                return true;
//            } else if (oldItem.getHouseNumber().equals(newItem.getHouseNumber())){
//                return true;
//            } else if(oldItem.getLine2().equals(newItem.getLine2())){
//                return true;
//            } else if(oldItem.getLine1().equals(newItem.getLine1())){
//                return true;
//            } else {
//                return oldItem.getPincode().equals(newItem.getPincode());
//            }
//
            if(!oldItem.getPincode().equals(newItem.getPincode())){
                return false;
            } else if (!oldItem.getLine2().equals(newItem.getLine2())){
                return false;
            } else if(!oldItem.getLine1().equals(newItem.getLine1())){
                return false;
            } else if(!oldItem.getHouseNumber().equals(newItem.getHouseNumber())){
                return false;
            } else {
                return oldItem.getId().equals(newItem.getId());
            }



        }
    } ;

    @NonNull
    @Override
    public AddressItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item,parent,false);
        return new AddressItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressItemViewHolder holder, int position) {
        Address address = getAddressItem(position);
        holder.pincode.setText(address.getPincode());
        holder.address1.setText(address.getLine1());
        holder.address2.setText(address.getLine2());
        holder.houseNo.setText(address.getHouseNumber());
        holder.changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null&& position!=RecyclerView.NO_POSITION){
                    listener.onEditButtonClick(address);
                }
            }
        });

        holder.removeAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null && position != RecyclerView.NO_POSITION){
                    listener.onDeleteButtonClick(address);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null && position != RecyclerView.NO_POSITION){
                    listener.onItemClick(address);
                }
            }
        });
    }

    public Address getAddressItem(int position){
        return getItem(position);
    }

    public class AddressItemViewHolder extends RecyclerView.ViewHolder{
        public TextView houseNo ;
        public TextView pincode ;
        public TextView address1;
        public TextView address2;
        public ImageButton removeAddressButton, changeButton;

        public AddressItemViewHolder(@NonNull View itemView) {
            super(itemView);
            houseNo = itemView.findViewById(R.id.houseNo_textView);
            pincode = itemView.findViewById(R.id.pincode_textview);
            address1 = itemView.findViewById(R.id.address1_textView);
            address2 = itemView.findViewById(R.id.address2_textView);
            changeButton = itemView.findViewById(R.id.change_address_btn);
            removeAddressButton = itemView.findViewById(R.id.remove_address_btn);
        }
    }



    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
