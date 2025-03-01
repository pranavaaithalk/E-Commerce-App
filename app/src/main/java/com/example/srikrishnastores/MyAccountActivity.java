package com.example.srikrishnastores;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class MyAccountActivity extends AppCompatActivity {
    Button additembutton,logoutaccbutton,myordersbutton,myprofilebutton;
    FirebaseUser currentUser;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mauth=FirebaseAuth.getInstance();
        currentUser = mauth.getCurrentUser();
        Toolbar tb=findViewById(R.id.toolbar3);
        setSupportActionBar(tb);
        additembutton=findViewById(R.id.additembutton);
        if(getString(R.string.admin_mail).compareTo(currentUser.getEmail())==0)
        {
            additembutton.setVisibility(View.VISIBLE);
            additembutton.setOnClickListener(v -> {
                startActivity(new Intent(this,AddItemActivity.class));
            });
        }
        else{
            additembutton.setVisibility(View.INVISIBLE);
        }
        myprofilebutton=findViewById(R.id.myprofilebutton);
        myprofilebutton.setOnClickListener(v -> {
            startActivity(new Intent(this,MyProfileActivity.class));
        });
        myordersbutton=findViewById(R.id.myordersbutton);
        myordersbutton.setOnClickListener(v -> {
            startActivity(new Intent(this,MyOrdersActivity.class));
        });
        logoutaccbutton=findViewById(R.id.logoutaccbutton);
        logoutaccbutton.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(MyAccountActivity.this);
            alert.setTitle("Logout");
            alert.setMessage("Are you sure you want to logout?");
            alert.setPositiveButton("Yes", (dialogInterface, i) -> {
                mauth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
            });
            alert.setNegativeButton("No", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            AlertDialog box = alert.create();
            box.show();
        });
    }
}