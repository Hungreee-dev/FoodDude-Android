package com.example.dubstep.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubstep.FoodItemActivity;
import com.example.dubstep.Model.FoodClass;
import com.example.dubstep.Model.FoodItem;
import com.example.dubstep.Model.GlideApp;
import com.example.dubstep.Model.Menu;
import com.example.dubstep.R;
import com.example.dubstep.ViewHolder.FoodItemViewHolder;
import com.google.android.material.button.MaterialButton;

public class FoodItemAdapter extends ListAdapter<Menu, FoodItemAdapter.FoodItemViewHolder> {
    private OnItemClickListener listener;

    public FoodItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Menu> DIFF_CALLBACK = new DiffUtil.ItemCallback<Menu>() {
        @Override
        public boolean areItemsTheSame(@NonNull Menu oldItem, @NonNull Menu newItem) {
            return oldItem.getName().equals(newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Menu oldItem, @NonNull Menu newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };


    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_layout_2,parent,false);
        FoodItemViewHolder holder = new FoodItemViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        Menu model = getFoodItemAt(position);
        holder.mFoodItemName.setText(model.getName());
        holder.mFoodItemPrice.setText("Price: \u20B9 " + model.getPrice());
        if(model.getVeg().equals("0")){
            String uri = "@drawable/ic_veg_icon";
            holder.vegNonNegMark.setImageDrawable(holder.foodItemImageView
                        .getContext()
                        .getResources().getDrawable(R.drawable.ic_veg_icon));
        }
        holder.mAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mAddToCart.setEnabled(false);
//                Add that item to users cart
//              addToCart(model);
            }
        });

        GlideApp.with(holder.foodItemImageView.getContext())
                .load(model.getImgLink())
                .centerCrop()
                .placeholder(R.drawable.splash_drawable)
                .into(holder.foodItemImageView);
    }

    public Menu getFoodItemAt(int position){
        return getItem(position);
    }

    public class FoodItemViewHolder extends RecyclerView.ViewHolder {

        public TextView mFoodItemName;
        //public ImageView mFoodImage;
        public TextView mFoodItemPrice;
        public MaterialButton mAddToCart;
        public ImageView foodItemImageView;
        public ImageView vegNonNegMark;

        public FoodItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mFoodItemName = (TextView) itemView.findViewById(R.id.food_name);
            mFoodItemPrice = (TextView) itemView.findViewById(R.id.food_price);
            //mFoodImage = (ImageView) itemView.findViewById(R.id.food_image);
            mAddToCart = (MaterialButton) itemView.findViewById(R.id.addtocart_btn);
            foodItemImageView = itemView.findViewById(R.id.food_item_imageView);
            vegNonNegMark = itemView.findViewById(R.id.veg_non_veg_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getFoodItemAt(position));
                    }
                }
            });
        }

    }

    public interface OnItemClickListener{
        void onItemClick(Menu menu);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }

}
