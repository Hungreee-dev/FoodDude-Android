package com.example.dubstep.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dubstep.R;
import com.google.android.material.card.MaterialCardView;

public class FoodClassViewHolder extends RecyclerView.ViewHolder {
    public TextView foodClassTextView;
    public MaterialCardView foodClassCardView;
    public ImageView foodClassImageView;
    public FoodClassViewHolder(@NonNull View itemView) {
        super(itemView);
        foodClassTextView = itemView.findViewById(R.id.food_class_textview);
        foodClassCardView = itemView.findViewById(R.id.food_class_cardview);
        foodClassImageView = itemView.findViewById(R.id.imageViewFoodClass);
    }
}
