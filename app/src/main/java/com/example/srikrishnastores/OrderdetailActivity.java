package com.example.srikrishnastores;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderdetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderdetailAdapter cartAdapter;
    private List<StoreItem> cartItemList;
    private DatabaseReference cartRef;
    static String orderid;
    private TextView totalPriceTextView,orderidtv;
    private FirebaseAuth mAuth;
    private int totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_orderdetail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar tb = findViewById(R.id.toolbar5);
        setSupportActionBar(tb);
        recyclerView = findViewById(R.id.recyclerViewdet);
        totalPriceTextView = findViewById(R.id.totalPriceTextView3);
        orderidtv = findViewById(R.id.orderidtv);
        orderid = getIntent().getStringExtra("orderid");
        orderidtv.setText("Order Id: " + orderid);
        mAuth = FirebaseAuth.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();
        cartAdapter = new OrderdetailAdapter(cartItemList, this);
        recyclerView.setAdapter(cartAdapter);
        String userId = mAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);
        loadCartItems();
    }
    private void loadCartItems() {
        cartRef.child(orderid).child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemList.clear();
                totalAmount = 0;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    StoreItem item = itemSnapshot.getValue(StoreItem.class);
                    if (item != null) {
                        item.setId(itemSnapshot.getKey());
                        cartItemList.add(item);
                        totalAmount += item.getPrice();
                    }
                }
                cartAdapter.notifyDataSetChanged();
                totalPriceTextView.setText("Total: ₹" + totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderdetailActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}