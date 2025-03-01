package com.example.srikrishnastores;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText signupemail,signuppassword;
    String email,password;
    Button signupbutton;
    TextView transfertosignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        signupemail=findViewById(R.id.loginemail);
        signuppassword=findViewById(R.id.loginpassword);
        signupbutton=findViewById(R.id.loginbutton);
        transfertosignup=findViewById(R.id.transfertosignup);
        signupbutton.setOnClickListener(view -> {
            email=signupemail.getText().toString();
            password=signuppassword.getText().toString();
            if(email.isEmpty()||password.isEmpty())
            {
                Toast.makeText(this,"Some Fiels are Missing!",Toast.LENGTH_SHORT).show();
            }
            else if(Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this,"Login Successful!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"Error: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {
                Toast.makeText(this,"Invalid Email!",Toast.LENGTH_SHORT).show();
            }
        });
        transfertosignup.setOnClickListener(view -> {
            startActivity(new Intent(this,SignupActivity.class));
        });

    }
}