package com.example.srikrishnastores;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mauth;
    FirebaseUser currentUser;
    Toolbar toolbar;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private List<StoreItem> itemList;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mauth=FirebaseAuth.getInstance();
        currentUser = mauth.getCurrentUser();
        FirebaseUser currentUser = mauth.getCurrentUser();
        updateUI(currentUser);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("StoreItems");
        initializecategory();
        fetchItemsFromDatabase();

    }

    private void initializecategory() {
        String[] categories = getResources().getStringArray(R.array.Category);
        LinearLayout categoryLayout = findViewById(R.id.mainpagecategoryscroll);
        TextView tView = new TextView(this);
        tView.setText("All");
        tView.setTextSize(18);
        tView.setPadding(10, 10, 30, 10);
        tView.setOnClickListener(view -> {
            fetchItemsFromDatabase();
        });
        categoryLayout.addView(tView);
        for (String category : categories) {
            TextView textView = new TextView(this);
            textView.setText(category);
            textView.setTextSize(18);
            textView.setPadding(10, 10, 30, 10);
            textView.setOnClickListener(view -> {
                String selectedCategory = ((TextView) view).getText().toString();
                fetchItemsByCategory(selectedCategory);
            });
            categoryLayout.addView(textView);
        }


    }
    private void fetchItemsByCategory(String selectedCategory) {
        databaseRef.orderByChild("category").equalTo(selectedCategory)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            StoreItem item = itemSnapshot.getValue(StoreItem.class);
                            if (item != null) {
                                item.setId(itemSnapshot.getKey());
                                itemList.add(item);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }


    private void fetchItemsFromDatabase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    StoreItem item = itemSnapshot.getValue(StoreItem.class);
                    if (item != null) {
                        item.setId(itemSnapshot.getKey());
                        itemList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to read data", error.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainpageopt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.opt1)
        {
            startActivity(new Intent(this,MyAccountActivity.class));
        }else if(id==R.id.opt2){
            startActivity(new Intent(this,AboutusActivity.class));
        } else if(id==R.id.opt3)
        {
            AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setMessage("Are you sure you want to Logout?");
            alert.setPositiveButton("Yes", (dialogInterface, i) -> {
                mauth.signOut();
                startActivity(new Intent(this,LoginActivity.class));
            });
            alert.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog box= alert.create();
            box.show();

        } else if(id==R.id.action_cart) {
            startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI(FirebaseUser user) {
        if(user==null)
        {
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}