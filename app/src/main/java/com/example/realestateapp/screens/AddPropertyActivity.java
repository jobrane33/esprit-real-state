package com.example.realestateapp.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestateapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText locationEditText, typeEditText, descriptionEditText, shortDescriptionEditText,
            ownerNameEditText, contactNoEditText, priceEditText, categoryEditText;
    private ImageView imageViewUploaded;
    private Button buttonUploadImage, buttonSubmit;
    private ImageButton back_button;

    private Uri imageUri;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_property_listing);

        // --- Initialize views ---
        locationEditText = findViewById(R.id.property_location);
        typeEditText = findViewById(R.id.property_type);
        descriptionEditText = findViewById(R.id.property_description);
        shortDescriptionEditText = findViewById(R.id.property_shortdescription);
        ownerNameEditText = findViewById(R.id.property_ownername);
        contactNoEditText = findViewById(R.id.property_contactno);
        priceEditText = findViewById(R.id.property_price);
        categoryEditText = findViewById(R.id.property_category);
        imageViewUploaded = findViewById(R.id.imageViewUploaded);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        back_button = findViewById(R.id.back_button);

        // --- Firestore ---
        db = FirebaseFirestore.getInstance();

        // --- Back button ---
        back_button.setOnClickListener(v -> finish());

        // --- Select image from gallery ---
        buttonUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // --- Submit property ---
        buttonSubmit.setOnClickListener(v -> {
            String location = locationEditText.getText().toString();
            String type = typeEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String shortDescription = shortDescriptionEditText.getText().toString();
            String ownerName = ownerNameEditText.getText().toString();
            String contactNo = contactNoEditText.getText().toString();
            String price = priceEditText.getText().toString();
            String category = categoryEditText.getText().toString();

            if(imageUri == null){
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create property data
            Map<String, Object> propertyData = new HashMap<>();
            propertyData.put("location", location);
            propertyData.put("type", type);
            propertyData.put("description", description);
            propertyData.put("shortdescription", shortDescription);
            propertyData.put("ownername", ownerName);
            propertyData.put("contactno", contactNo);
            propertyData.put("price", price);
            propertyData.put("category", category);
            propertyData.put("imageUri", imageUri.toString()); // <-- local URI

            // Save to Firestore
            db.collection("Properties").add(propertyData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Property added successfully", Toast.LENGTH_SHORT).show();
                        // Rediriger vers Home
                        Intent intent = new Intent(AddPropertyActivity.this, HomeActivity.class); // ou activity qui contient HomeFragment
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add property", Toast.LENGTH_SHORT).show()
                    );

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewUploaded.setImageURI(imageUri);
        }
    }

    private void clearForm() {
        locationEditText.setText("");
        typeEditText.setText("");
        descriptionEditText.setText("");
        shortDescriptionEditText.setText("");
        ownerNameEditText.setText("");
        contactNoEditText.setText("");
        priceEditText.setText("");
        categoryEditText.setText("");
        imageViewUploaded.setImageDrawable(null);
        imageUri = null;
    }
}
