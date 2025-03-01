package com.example.srikrishnastores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderdetailAdapter extends RecyclerView.Adapter<OrderdetailAdapter.CartViewHolder> {
    private List<StoreItem> cartList;
    private Context context;

    public OrderdetailAdapter(List<StoreItem> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        StoreItem item = cartList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("₹" + item.getPrice());
        Picasso.get().load(item.getImageUrl()).into(holder.itemImage);

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        ImageView itemImage;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cartItemNamedet);
            itemPrice = itemView.findViewById(R.id.cartItemPricedet);
            itemImage = itemView.findViewById(R.id.cartItemImagedet);
        }
    }
}
