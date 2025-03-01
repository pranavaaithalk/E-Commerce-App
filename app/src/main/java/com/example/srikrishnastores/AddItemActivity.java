package com.example.srikrishnastores;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName, editTextPrice;
    private Spinner spinnerCategory;
    private ImageView imageView;
    private Button btnSelectImage, btnUploadItem;
    private Uri imageUri;
    private String selectedCategory; // To store the selected category

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("StoreItems");
        storageReference = FirebaseStorage.getInstance().getReference("item_images");

        // Initialize UI Components
        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageView = findViewById(R.id.imageView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadItem = findViewById(R.id.btnUploadItem);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        // Populate Spinner with Categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.Category,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Get selected category
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "Uncategorized"; // Default value
            }
        });

        // Select Image from Gallery
        btnSelectImage.setOnClickListener(v -> openFileChooser());

        // Upload Item Data
        btnUploadItem.setOnClickListener(v -> {
            if (validateInputs()) {
                uploadImage();
            }
        });
    }

    // Open Image Picker
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri); // Show Image in ImageView
        }
    }

    // Validate Inputs
    private boolean validateInputs() {
        if (editTextName.getText().toString().trim().isEmpty() ||
                editTextPrice.getText().toString().trim().isEmpty() ||
                imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Upload Image to Firebase Storage
    private void uploadImage() {
        progressDialog.show();

        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    uploadItemToDatabase(imageUrl); // Upload item details with image URL
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Image Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Upload Item Details to Firebase Realtime Database
    private void uploadItemToDatabase(String imageUrl) {
        String itemId = databaseReference.push().getKey(); // Generate unique ID

        Map<String, Object> item = new HashMap<>();
        item.put("name", editTextName.getText().toString().trim());
        item.put("price", Integer.parseInt(editTextPrice.getText().toString().trim()));
        item.put("imageUrl", imageUrl);
        item.put("category", selectedCategory); // Get selected category

        if (itemId != null) {
            databaseReference.child(itemId).setValue(item)
                    .addOnSuccessListener(unused -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Item Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Database Upload Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Clear Fields After Upload
    private void clearFields() {
        editTextName.setText("");
        editTextPrice.setText("");
        spinnerCategory.setSelection(0); // Reset Spinner
        imageView.setImageResource(android.R.color.transparent);
        imageUri = null;
    }
}