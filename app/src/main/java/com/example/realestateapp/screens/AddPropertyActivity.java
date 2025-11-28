package com.example.realestateapp.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    private String imageBase64;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_property_listing);

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

        db = FirebaseFirestore.getInstance();

        back_button.setOnClickListener(v -> finish());

        // Spinner
        String[] categories = {"Apartment", "Villa", "Office", "Shop"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Pick image
        buttonUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        buttonSubmit.setOnClickListener(v -> submitProperty());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewUploaded.setImageURI(imageUri);
            convertImageToBase64(imageUri);
        }
    }

    private void convertImageToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] bytes = baos.toByteArray();
            imageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            imageBase64 = null;
        }
    }

    private void submitProperty() {
        String location = locationEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String shortDescription = shortDescriptionEditText.getText().toString().trim();
        String ownerName = ownerNameEditText.getText().toString().trim();
        String contactNo = contactNoEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();

        int selectedTypeId = radioGroupType.getCheckedRadioButtonId();
        if (selectedTypeId == -1) {
            Toast.makeText(this, "Please select Type (Sell or Rent)", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedTypeButton = findViewById(selectedTypeId);
        String type = selectedTypeButton.getText().toString();

        String category = spinnerCategory.getSelectedItem().toString();

        if (imageBase64 == null || imageBase64.isEmpty()) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> propertyData = new HashMap<>();
        propertyData.put("title", shortDescription);
        propertyData.put("location", location);
        propertyData.put("type", type);
        propertyData.put("description", description);
        propertyData.put("shortdescription", shortDescription);
        propertyData.put("ownername", ownerName);
        propertyData.put("contactno", contactNo);
        propertyData.put("price", price);
        propertyData.put("category", category);
        propertyData.put("imageBase64", imageBase64);

        db.collection("Properties").add(propertyData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Property added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to add property", Toast.LENGTH_SHORT).show()
                );
    }
}
