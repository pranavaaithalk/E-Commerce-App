package com.example.srikrishnastores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Orders> cartList;
    private Context context;

    public OrderAdapter(List<Orders> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders item = cartList.get(position);
        holder.itemName.setText("Order ID: "+item.getOrderId());
        holder.itemName.setOnClickListener(view -> {
            Intent i=new Intent(context, OrderdetailActivity.class);
            i.putExtra("orderid",item.getOrderId());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.ordersidtv);
        }
    }
}
