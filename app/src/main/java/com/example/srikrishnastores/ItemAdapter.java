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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private Context context;
    private List<StoreItem> itemList;

    public ItemAdapter(Context context, List<StoreItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemscard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StoreItem item = itemList.get(position);

        // Set item name & price
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("₹" + item.getPrice());

        // Load image using Picasso
        Picasso.get().load(item.getImageUrl()).into(holder.itemImage);

        holder.addToCartButton.setOnClickListener(v -> {
            StoreItem itemm = itemList.get(position);

            // Set item name & price
            holder.itemName.setText(itemm.getName());
            holder.itemPrice.setText("₹" + itemm.getPrice());

            // Load image using Picasso
            Picasso.get().load(itemm.getImageUrl()).into(holder.itemImage);

            // Add to Cart Button Click Listener
            holder.addToCartButton.setOnClickListener(vi -> {
                addToCart(itemm);
            });
        });
    }

    private void addToCart(StoreItem item) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Please log in to add items to the cart.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);

        // Store cart item under user's cart
        cartRef.child(item.getId()).setValue(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Item added to cart!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to add item to cart!", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemimageincard);
            itemName = itemView.findViewById(R.id.itemnameincard);
            itemPrice = itemView.findViewById(R.id.itempriceincard);
            addToCartButton = itemView.findViewById(R.id.adcbutton_incard);
        }
    }
}

