package com.example.realestateapp.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.example.realestateapp.model.Property;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

public class UpdatePropertyActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextLocation, editTextPrice, editTextDescription;
    private Spinner spinnerCategory;
    private RadioGroup radioGroupType;
    private ImageView imageView;
    private Button buttonUpdate;

    private FirebaseFirestore db;
    private Property currentProperty;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

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

        // Setup spinner values
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.categories_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Image picker setup
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageView.setImageBitmap(bitmap);

                            // Convert to Base64 and save in currentProperty
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                            byte[] imageBytes = baos.toByteArray();
                            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            if (currentProperty != null) {
                                currentProperty.setImageBase64(encodedImage);
                                currentProperty.setImageUrl(null); // clear URL if new image
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

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
                        currentProperty.setTitle(doc.getString("title"));

                        editTextTitle.setText(currentProperty.getTitle());
                        editTextLocation.setText(currentProperty.getLocation());
                        editTextPrice.setText(currentProperty.getPrice());
                        editTextDescription.setText(currentProperty.getShortdescription());

                        // Spinner selection
                        if (currentProperty.getCategory() != null) {
                            ArrayAdapter adapter = (ArrayAdapter) spinnerCategory.getAdapter();
                            int spinnerPosition = adapter.getPosition(currentProperty.getCategory());
                            spinnerCategory.setSelection(spinnerPosition);
                        }

                        // Radio button selection
                        if (currentProperty.getType() != null) {
                            if (currentProperty.getType().equalsIgnoreCase("Sell")) {
                                radioGroupType.check(R.id.radioSell);
                            } else if (currentProperty.getType().equalsIgnoreCase("Rent")) {
                                radioGroupType.check(R.id.radioRent);
                            }
                        }

                        // Load image
                        if (currentProperty.getImageUrl() != null && !currentProperty.getImageUrl().isEmpty()) {
                            Glide.with(this).load(currentProperty.getImageUrl()).into(imageView);
                        } else if (currentProperty.getImageBase64() != null && !currentProperty.getImageBase64().isEmpty()) {
                            byte[] bytes = Base64.decode(currentProperty.getImageBase64(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
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
        currentProperty.setShortdescription(editTextDescription.getText().toString());
        currentProperty.setCategory(Objects.requireNonNull(spinnerCategory.getSelectedItem()).toString());

        int selectedTypeId = radioGroupType.getCheckedRadioButtonId();
        if (selectedTypeId != -1) {
            RadioButton selectedRadio = findViewById(selectedTypeId);
            currentProperty.setType(selectedRadio.getText().toString());
        }

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
