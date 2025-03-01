package com.example.srikrishnastores;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<StoreItem> cartItemList;
    private DatabaseReference cartRef,userRef;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private FirebaseAuth mAuth;
    private int totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar tb=findViewById(R.id.toolbar4);
        setSupportActionBar(tb);
        recyclerView = findViewById(R.id.recyclerViewCart);
        totalPriceTextView = findViewById(R.id.totalPriceTextView3);
        checkoutButton = findViewById(R.id.checkoutButton);
        mAuth = FirebaseAuth.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, this, price -> {
            totalPriceTextView.setText("Total: ₹" + totalAmount);
        });

        recyclerView.setAdapter(cartAdapter);

        String userId = mAuth.getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("Carts").child(userId);
        loadCartItems();

        checkoutButton.setOnClickListener(v -> {
            if(totalAmount>0) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String address = snapshot.child("address").getValue(String.class);
                            if(address==null){
                                Toast.makeText(CartActivity.this,"Update Profile to Continue!",Toast.LENGTH_SHORT).show();
                            }else {

                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Checkout");
                                builder.setMessage("Please Confirm Before Checkout!");

                                LinearLayout layout = new LinearLayout(CartActivity.this);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                layout.setPadding(40, 20, 40, 20);

                                TextView add = new TextView(CartActivity.this);
                                add.setText("Address: " + (address != null ? address : "Not available"));
                                add.setPadding(10, 0, 10, 10);

                                TextView tot = new TextView(CartActivity.this);
                                tot.setText("Total Amount: ₹" + totalAmount);
                                tot.setPadding(10, 0, 10, 10);

                                TextView pmode = new TextView(CartActivity.this);
                                pmode.setText("Payment Mode: Cash On Delivery");
                                pmode.setPadding(10, 0, 10, 10);

                                layout.addView(add);
                                layout.addView(tot);
                                layout.addView(pmode);

                                builder.setView(layout);

                                builder.setPositiveButton("Yes", (dialog, which) -> {
                                    ordercomplete(cartItemList);
                                    Toast.makeText(CartActivity.this, "Checkout Successful!", Toast.LENGTH_SHORT).show();
                                    cartRef.removeValue();
                                    cartItemList.clear();
                                    cartAdapter.notifyDataSetChanged();
                                    totalPriceTextView.setText("Total: ₹0");
                                });

                                builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        } else {
                            Toast.makeText(CartActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CartActivity.this, "Failed to fetch user details!", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(CartActivity.this, "Cart is Empty!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ordercomplete(List<StoreItem> items) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(userId);

        String orderId = ordersRef.push().getKey();
        if (orderId == null) {
            Toast.makeText(this, "Error generating order ID!", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference orderRef = ordersRef.child(orderId);
        for (StoreItem item : items) {
            orderRef.child("items").child(item.getId()).setValue(item)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Order_Completion", "Item " + item.getName() + " ordered");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to complete order!", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItemList.clear();
                int newTotalAmount = 0;

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    StoreItem item = itemSnapshot.getValue(StoreItem.class);
                    if (item != null) {
                        item.setId(itemSnapshot.getKey());
                        cartItemList.add(item);
                        newTotalAmount += item.getPrice();
                    }
                }

                cartAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                totalAmount = newTotalAmount;
                totalPriceTextView.setText("Total: ₹" + totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Failed to load items", Toast.LENGTH_SHORT).show();
            }
        });
    }

}