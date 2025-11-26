package com.example.realestateapp.screens;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realestateapp.R;
import com.example.realestateapp.model.Property;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class UpdatePropertyActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextLocation, editTextPrice,
            editTextType, editTextCategory, editTextDescription;
    private Button buttonUpdateProperty;

    private FirebaseFirestore db;
    private DocumentReference currentDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_property);

        db = FirebaseFirestore.getInstance();

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextType = findViewById(R.id.editTextType);
        editTextCategory = findViewById(R.id.editTextCategory);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonUpdateProperty = findViewById(R.id.buttonUpdateProperty);

        // Get property title from intent
        String propertyTitle = getIntent().getStringExtra("propertyTitle");
        if (propertyTitle != null) {
            prefillProperty(propertyTitle);
        }

        buttonUpdateProperty.setOnClickListener(v -> updateProperty());
    }

    private void prefillProperty(String title) {
        db.collection("Properties")
                .whereEqualTo("title", title)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        QueryDocumentSnapshot doc = (QueryDocumentSnapshot) querySnapshot.getDocuments().get(0);
                        currentDocRef = doc.getReference();

                        editTextTitle.setText(doc.getString("title"));
                        editTextLocation.setText(doc.getString("location"));
                        editTextPrice.setText(doc.getString("price"));
                        editTextType.setText(doc.getString("type"));
                        editTextCategory.setText(doc.getString("category"));
                        editTextDescription.setText(doc.getString("description"));
                    } else {
                        Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error loading property", Toast.LENGTH_SHORT).show());
    }

    private void updateProperty() {
        if (currentDocRef == null) return;

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("title", editTextTitle.getText().toString());
        updatedData.put("location", editTextLocation.getText().toString());
        updatedData.put("price", editTextPrice.getText().toString());
        updatedData.put("type", editTextType.getText().toString());
        updatedData.put("category", editTextCategory.getText().toString());
        updatedData.put("description", editTextDescription.getText().toString());

        currentDocRef.set(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Property updated!", Toast.LENGTH_SHORT).show();
                    finish(); // go back to previous screen
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}
