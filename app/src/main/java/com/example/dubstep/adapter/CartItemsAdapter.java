package com.example.dubstep.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.dubstep.Model.CartItem;
import com.example.dubstep.Model.UserCart;
import com.example.dubstep.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.Holders> {

    private Context mContext;
    private List<CartItem> mCartItemList;
    private LayoutInflater mLayoutInflater;

    private OnItemClickListener listener;
    private OnValueChangeListener valueListener;

    public CartItemsAdapter(Context context, List<CartItem> cartItems) {
        mContext = context;
        mCartItemList = cartItems;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Holders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.cart_item_layout, parent, false);
        return new Holders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holders holder, int position) {
        CartItem cartItem = mCartItemList.get(position);
        holder.setCartList(cartItem, position);

        holder.mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemDelete(position);
            }
        });

        holder.mNumberButton.setOnValueChangeListener(
                new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue,
                            int newValue) {
                        valueListener.onQuantityChange(position, newValue);
                    }
                });
    }

    public interface OnItemClickListener {
        void onItemDelete(int position);
    }

    public interface OnValueChangeListener {
        void onQuantityChange(int position, int quantity);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.valueListener = listener;
    }


    public void submitList(List<CartItem> cartItemList) {
        this.mCartItemList = cartItemList;
    }

    @Override
    public int getItemCount() {
        return mCartItemList.size();
    }


    public static class Holders extends RecyclerView.ViewHolder {

        TextView mFoodName, mFoodPrice, mTotalPrice;
        ImageButton mRemove;
        ElegantNumberButton mNumberButton;
        ImageView mItemIcon;

        public Holders(@NonNull View itemView) {
            super(itemView);

            mFoodName = itemView.findViewById(R.id.food_name_textView);
            mFoodPrice = itemView.findViewById(R.id.price_textView);
            mTotalPrice = itemView.findViewById(R.id.total_price_item_text_view);
            mRemove = itemView.findViewById(R.id.remove_btn);
            mNumberButton = itemView.findViewById(R.id.quantity_btns);
            mItemIcon = itemView.findViewById(R.id.item_icon);
        }

        public void setCartList(CartItem cartItem, int position) {
            mFoodName.setText(cartItem.getName());
            String price = "₹" + cartItem.getPrice() + " per item";
            mFoodPrice.setText(price);
            mNumberButton.setNumber(String.valueOf(cartItem.getQuantity()));
            //String category = cartItem.getCategory();
            String productID = cartItem.getName();
            //String category = productID.substring(0, productID.indexOf("_"));
            //mItemIcon.setImageResource(getIcon(category));
            int q = cartItem.getQuantity();
            int bp = Integer.parseInt(cartItem.getPrice());
            int tp = q * bp;
            String totalPrice = "₹" + (q * bp);
            mTotalPrice.setText(totalPrice);


        }

        public static int getIcon(String category) {
            if (category.equalsIgnoreCase("Drinks"))
                return R.drawable.drinks;
            else if (category.equalsIgnoreCase("Main course") || category.equalsIgnoreCase(
                    "Indian"))
                return R.drawable.indian_food;
            else if (category.equalsIgnoreCase("Starters"))
                return R.drawable.starter;
            else if (category.equalsIgnoreCase("Bread"))
                return R.drawable.roti;
            else if (category.equalsIgnoreCase("Chinese"))
                return R.drawable.chinese;
            else
                return R.drawable.biryani;
        }
    }
}
/*public class CartItemsAdapter extends FirebaseRecyclerAdapter<CartItem , CartItemsAdapter.CartItemsViewHolder> {

    private OnItemClickListener listener;
    private OnValueChangeListener valueListener;

    public CartItemsAdapter(@NonNull FirebaseRecyclerOptions<CartItem> options) {
        super(options);
    }
    public CartItemsAdapter()

    @Override
    protected void onBindViewHolder(@NonNull CartItemsViewHolder holder, final int position, @NonNull final CartItem model) {
        holder.mFoodName.setText(model.getName());
        holder.mFoodPrice.setText("₹"+model.getPrice() + " per item");
        holder.mNumberButton.setNumber(String.valueOf(model.getQuantity()));
        //String category = model.getCategory();
        String productID = model.getProduct_ID();
        String category = productID.substring(0, productID.indexOf("_"));
        holder.mItemIcon.setImageResource(getIcon(category));
        int q = Integer.parseInt(model.getQuantity());
        int bp = Integer.parseInt(model.getPrice());
        int tp = q*bp;
        holder.mTotalPrice.setText("₹"+tp);

        holder.mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemDelete(model.getProduct_ID(),position);
            }
        });

        holder.mNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                valueListener.onQuantityChange(model.getProduct_ID(),newValue);
            }
        });

    }

    @NonNull
    @Override
    public CartItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
        CartItemsViewHolder holder = new CartItemsViewHolder(view);

        return holder;
    }

    class CartItemsViewHolder extends RecyclerView.ViewHolder{

        TextView mFoodName;
        TextView mFoodPrice;
        TextView mTotalPrice;
        ImageButton mRemove;
        ElegantNumberButton mNumberButton;
        ImageView mItemIcon;

        public CartItemsViewHolder(@NonNull View itemView) {
            super(itemView);

            mFoodName = itemView.findViewById(R.id.food_name_textView);
            mFoodPrice = itemView.findViewById(R.id.price_textView);
            mTotalPrice = itemView.findViewById(R.id.total_price_item_text_view);
            mRemove = itemView.findViewById(R.id.remove_btn);
            mNumberButton = itemView.findViewById(R.id.quantity_btns);
            mItemIcon = itemView.findViewById(R.id.item_icon);

        }
    }

    public interface OnItemClickListener{
        void onItemDelete(String PID,int position);
    }

    public interface OnValueChangeListener{
        void onQuantityChange(String PID,int quantity);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener){
        this.valueListener = listener;
    }

    public int getIcon(String category){
        if(category.equalsIgnoreCase("Drinks"))
            return R.drawable.drinks;
        else if(category.equalsIgnoreCase("Main course") || category.equalsIgnoreCase("Indian") )
            return R.drawable.indian_food;
        else if(category.equalsIgnoreCase("Starters"))
            return R.drawable.starter;
        else if(category.equalsIgnoreCase("Bread"))
            return R.drawable.roti;
        else if(category.equalsIgnoreCase("Chinese"))
            return R.drawable.chinese;
        else
            return R.drawable.biryani;
    }
}*/
