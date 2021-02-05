package com.example.dubstep.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubstep.Model.GlideApp;
import com.example.dubstep.Model.FoodClass;
import com.example.dubstep.R;
import com.google.android.material.card.MaterialCardView;

public class FoodClassAdapter extends ListAdapter<FoodClass, FoodClassAdapter.FoodClassViewHolder> {

    private OnItemClickListener listener;

    public FoodClassAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<FoodClass> DIFF_CALLBACK = new DiffUtil.ItemCallback<FoodClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull FoodClass oldItem, @NonNull FoodClass newItem) {
            return oldItem.getCategory().equals(newItem.getCategory());
        }

        @Override
        public boolean areContentsTheSame(@NonNull FoodClass oldItem, @NonNull FoodClass newItem) {
            return oldItem.getCategory().equals(newItem.getCategory());
        }
    };

    @NonNull
    @Override
    public FoodClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_class_layout,parent,false);
        return new FoodClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodClassViewHolder holder, final int position) {
        final FoodClass model = getFoodClassAt(position);
        Log.d("count", "onBindViewHolder: "+model.getMenuList().size());
        holder.foodClassTextView.setText(model.getCategory());
        GlideApp.with( holder.foodClassImageView.getContext() )
                .load(model.getImageLink())
                .centerCrop()
                .placeholder(R.drawable.ic_food_dude_icon)
                .into(holder.foodClassImageView);
    }

    public FoodClass getFoodClassAt(int position){
        return getItem(position);
    }

    public class FoodClassViewHolder extends RecyclerView.ViewHolder {
        public TextView foodClassTextView;
        public MaterialCardView foodClassCardView;
        public ImageView foodClassImageView;
        public FoodClassViewHolder(@NonNull View itemView) {
            super(itemView);
            foodClassTextView = itemView.findViewById(R.id.food_class_textview);
            foodClassCardView = itemView.findViewById(R.id.food_class_cardview);
            foodClassImageView = itemView.findViewById(R.id.imageViewFoodClass);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener!=null && position!=RecyclerView.NO_POSITION){
                        listener.onItemClick(getFoodClassAt(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(FoodClass foodClass);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
