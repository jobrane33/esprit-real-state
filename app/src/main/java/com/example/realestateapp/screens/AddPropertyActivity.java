package com.example.realestateapp.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestateapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText locationEditText, descriptionEditText, shortDescriptionEditText,
            ownerNameEditText, contactNoEditText, priceEditText;
    private RadioGroup radioGroupType;
    private Spinner spinnerCategory;
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
        descriptionEditText = findViewById(R.id.property_description);
        shortDescriptionEditText = findViewById(R.id.property_shortdescription);
        ownerNameEditText = findViewById(R.id.property_ownername);
        contactNoEditText = findViewById(R.id.property_contactno);
        priceEditText = findViewById(R.id.property_price);
        radioGroupType = findViewById(R.id.radioGroupType);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        imageViewUploaded = findViewById(R.id.imageViewUploaded);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        back_button = findViewById(R.id.back_button);

        // Null check for debugging
        if (spinnerCategory == null) {
            throw new RuntimeException("spinnerCategory is null! Check layout file name and ID.");
        }

        // --- Firestore ---
        db = FirebaseFirestore.getInstance();

        // --- Back button ---
        back_button.setOnClickListener(v -> finish());

        // --- Setup Spinner ---
        String[] categories = {"Home", "Villa", "Flat", "Shop"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // --- Select image from gallery ---
        buttonUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // --- Submit property ---
        buttonSubmit.setOnClickListener(v -> submitProperty());
    }

    private void submitProperty() {
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String shortDescription = shortDescriptionEditText.getText().toString().trim();
        String ownerName = ownerNameEditText.getText().toString().trim();
        String contactNo = contactNoEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();

        // --- Get Type from RadioGroup ---
        int selectedTypeId = radioGroupType.getCheckedRadioButtonId();
        if (selectedTypeId == -1) {
            Toast.makeText(this, "Please select Type (Sell or Rent)", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedTypeButton = findViewById(selectedTypeId);
        String type = selectedTypeButton.getText().toString();

        // --- Get Category from Spinner ---
        String category = spinnerCategory.getSelectedItem().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Save to Firestore ---
        Map<String, Object> propertyData = new HashMap<>();
        propertyData.put("location", location);
        propertyData.put("type", type);
        propertyData.put("description", description);
        propertyData.put("shortdescription", shortDescription);
        propertyData.put("ownername", ownerName);
        propertyData.put("contactno", contactNo);
        propertyData.put("price", price);
        propertyData.put("category", category);
        propertyData.put("imageUri", imageUri.toString());

        db.collection("Properties").add(propertyData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Property added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddPropertyActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add property", Toast.LENGTH_SHORT).show()
                );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewUploaded.setImageURI(imageUri);
        }
    }
}
