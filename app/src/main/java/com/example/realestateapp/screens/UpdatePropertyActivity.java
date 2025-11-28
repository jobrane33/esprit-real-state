package com.example.realestateapp.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.example.realestateapp.model.Property;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UpdatePropertyActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextLocation, editTextPrice,
            editTextDescription;
    private Spinner spinnerCategory;
    private RadioGroup radioGroupType;
    private ImageView imageView;
    private Button buttonUpdate;

    private FirebaseFirestore db;
    private Property currentProperty;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_property);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextDescription = findViewById(R.id.editTextDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        radioGroupType = findViewById(R.id.radioGroupType);
        imageView = findViewById(R.id.imageViewUpdated);

        buttonUpdate = findViewById(R.id.buttonUpdateProperty);

        db = FirebaseFirestore.getInstance();

        String propertyTitle = getIntent().getStringExtra("propertyTitle");
        if (propertyTitle != null) prefillProperty(propertyTitle);

        buttonUpdate.setOnClickListener(v -> updateProperty());
    }

    private void prefillProperty(String title) {
        db.collection("Properties").whereEqualTo("title", title)
                .get().addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        currentProperty = doc.toObject(Property.class);
                        currentProperty.setTitle(doc.getString("title")); // keep title as ID key

                        editTextTitle.setText(currentProperty.getTitle());
                        editTextLocation.setText(currentProperty.getLocation());
                        editTextPrice.setText(currentProperty.getPrice());
                        editTextDescription.setText(currentProperty.getCategory()); // example, map properly

                        if (currentProperty.getImageUrl() != null && !currentProperty.getImageUrl().isEmpty()) {
                            Glide.with(this).load(currentProperty.getImageUrl()).into(imageView);
                        } else if (currentProperty.getImageResId() != null) {
                            imageView.setImageResource(currentProperty.getImageResId());
                        }

                    } else {
                        Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void updateProperty() {
        if (currentProperty == null) return;

        currentProperty.setTitle(editTextTitle.getText().toString());
        currentProperty.setLocation(editTextLocation.getText().toString());
        currentProperty.setPrice(editTextPrice.getText().toString());
        currentProperty.setCategory(editTextDescription.getText().toString()); // example

        db.collection("Properties")
                .whereEqualTo("title", currentProperty.getTitle())
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        String docId = query.getDocuments().get(0).getId();
                        db.collection("Properties").document(docId).set(currentProperty)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Property updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}
