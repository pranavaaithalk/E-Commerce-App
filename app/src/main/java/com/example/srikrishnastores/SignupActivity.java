package com.example.srikrishnastores;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private DatabaseReference userDatabaseRef;
    private static final int REQ_ONE_TAP = 100;
    EditText signupemail,signuppassword;
    String email,password;
    Button signupbutton;
    TextView transfertologin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        signupemail=findViewById(R.id.signupemail);
        signuppassword=findViewById(R.id.signuppassword);
        signupbutton=findViewById(R.id.signupbutton);
        transfertologin=findViewById(R.id.transfertologin);
        signupbutton.setOnClickListener(view -> {
            email=signupemail.getText().toString();
            password=signuppassword.getText().toString();
            if(email.isEmpty()||password.isEmpty())
            {
                Toast.makeText(this,"Some Fiels are Missing!",Toast.LENGTH_SHORT).show();
            }
            else{
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            currentUser = mAuth.getCurrentUser();
                            String userId = currentUser.getUid();
                            createUserProfile(userId,email);
                            Toast.makeText(SignupActivity.this,"Registration Successful!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                        }
                        else{
                            Toast.makeText(SignupActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        transfertologin.setOnClickListener(view -> {
            startActivity(new Intent(this,LoginActivity.class));
        });

    }
    private void createUserProfile(String userId, String email) {
        UserProfile userProfile = new UserProfile(email); // Only email field initialized
        userDatabaseRef.child(userId).setValue(userProfile);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {
        if(user!=null)
        {
            startActivity(new Intent(this,MainActivity.class));
        }
    }
}