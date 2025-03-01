package com.example.srikrishnastores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<StoreItem> cartList;
    private Context context;
    private OnItemRemovedListener onItemRemovedListener; // Listener for price update

    // Interface to notify CartActivity
    public interface OnItemRemovedListener {
        void onItemRemoved(int price);
    }

    public CartAdapter(List<StoreItem> cartList, Context context, OnItemRemovedListener listener) {
        this.cartList = cartList;
        this.context = context;
        this.onItemRemovedListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        StoreItem item = cartList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("₹" + item.getPrice());
        Picasso.get().load(item.getImageUrl()).into(holder.itemImage);

        holder.removeButton.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);

            if (item.getId() != null) {
                cartRef.child(item.getId()).removeValue().addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show();

                    if (onItemRemovedListener != null) {
                        onItemRemovedListener.onItemRemoved(item.getPrice());
                    }

                    // Ensure valid position before removing
                    if (position >= 0 && position < cartList.size()) {
                        cartList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            } else {
                Toast.makeText(context, "Error: Item ID is null", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        ImageView itemImage;
        Button removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cartItemNamedet);
            itemPrice = itemView.findViewById(R.id.cartItemPricedet);
            itemImage = itemView.findViewById(R.id.cartItemImagedet);
            removeButton = itemView.findViewById(R.id.removeItemButton);
        }
    }
}
