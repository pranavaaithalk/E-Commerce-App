package com.example.srikrishnastores;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class MyProfileActivity extends AppCompatActivity {
    private EditText profileName, profileEmail, profilePhone, profileAddress;
    private Button updateProfileButton;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        Toolbar tb=findViewById(R.id.toolbar2);
        setSupportActionBar(tb);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        profileAddress = findViewById(R.id.profileAddress);
        updateProfileButton = findViewById(R.id.updateProfileButton);

        profileEmail.setEnabled(false); // Email cannot be changed
        // Fetch user details from Firebase
        loadUserProfile();
        updateProfileButton.setOnClickListener(v -> updateUserProfile());
    }
    private void loadUserProfile() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    profileName.setText(name);
                    profileEmail.setText(email);
                    profilePhone.setText(phone);
                    profileAddress.setText(address);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MyProfileActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String name = profileName.getText().toString().trim();
        String phone = profilePhone.getText().toString().trim();
        String address = profileAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a HashMap to update the values
        databaseReference.child("name").setValue(name);
        databaseReference.child("phone").setValue(phone);
        databaseReference.child("address").setValue(address)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MyProfileActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(MyProfileActivity.this, "Failed to update profile.", Toast.LENGTH_SHORT).show());
    }
}