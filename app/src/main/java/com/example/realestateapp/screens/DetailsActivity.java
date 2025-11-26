package com.example.realestateapp.screens;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private TextView priceTextView, shortDescriptionTextView, descriptionTextView, ownerNameTextView,
            contactNoTextView, rentSellTextView, locationTextView;
    private ImageView propertyImageView;
    private Button callButton, bookProperty;
    private ImageButton backButton, favButton;

    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        priceTextView = findViewById(R.id.price);
        shortDescriptionTextView = findViewById(R.id.short_description);
        descriptionTextView = findViewById(R.id.description);
        ownerNameTextView = findViewById(R.id.owner_name);
        contactNoTextView = findViewById(R.id.contact_no);
        propertyImageView = findViewById(R.id.imageView);
        callButton = findViewById(R.id.callButton);
        rentSellTextView = findViewById(R.id.rent_sell);
        favButton = findViewById(R.id.add_to_fav);
        locationTextView = findViewById(R.id.location);
        backButton = findViewById(R.id.back_button);
        bookProperty = findViewById(R.id.book_property);

        // Retrieve passed data from the intent
        Intent intent = getIntent();
        if (intent != null) {
            String location = intent.getStringExtra("location");
            String price = intent.getStringExtra("price");
            String shortdescription = intent.getStringExtra("shortdescription");
            String imageuri = intent.getStringExtra("imageuri");
            String description = intent.getStringExtra("description");
            String contactno = intent.getStringExtra("contactno");
            String type = intent.getStringExtra("type");
            String ownername = intent.getStringExtra("ownername");

            // Set text data
            priceTextView.setText(price);
            shortDescriptionTextView.setText(shortdescription);
            descriptionTextView.setText(description);
            ownerNameTextView.setText("Owner Name:- " + ownername);
            contactNoTextView.setText("Phone No:- " + contactno);
            rentSellTextView.setText(type);
            locationTextView.setText(location);

            // Load image (Firebase URL or fallback to drawable)
            if (imageuri != null && !imageuri.isEmpty()) {
                Glide.with(this)
                        .load(imageuri)
                        .placeholder(R.drawable.hom1)
                        .error(R.drawable.hom1)
                        .centerCrop()
                        .into(propertyImageView);
            } else {
                propertyImageView.setImageResource(R.drawable.hom1);
            }

            // Call Owner
            callButton.setOnClickListener(v -> {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + contactno));
                startActivity(dialIntent);
            });

            // Book Property via email
            bookProperty.setOnClickListener(v -> {
                String subject = "Booking Inquiry for Property at " + locationTextView.getText().toString();
                String content = "I am interested in booking the property. Please provide more details. Thank you.";
                String to_email = "sagar.bawanthade2004@example.com"; // Update with actual email
                sendEmail(subject, content, to_email);
            });

            // Add to Favorites
            favButton.setOnClickListener(v -> addToFavorites(location, price, shortdescription, imageuri, description, contactno, type, ownername));

            // Back button
            backButton.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Failed to retrieve property details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Email intent
    private void sendEmail(String subject, String content, String to_email) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to_email});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose email client"));
    }

    // Add property to Firebase Favorites
    private void addToFavorites(String location, String price, String shortdescription, String imageuri,
                                String description, String contactno, String type, String ownername) {

        db.collection("Favorites")
                .whereEqualTo("location", location)
                .whereEqualTo("price", price)
                .whereEqualTo("shortdescription", shortdescription)
                .whereEqualTo("imageuri", imageuri)
                .whereEqualTo("description", description)
                .whereEqualTo("contactno", contactno)
                .whereEqualTo("type", type)
                .whereEqualTo("ownername", ownername)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Map<String, Object> propertyData = new HashMap<>();
                            propertyData.put("location", location);
                            propertyData.put("price", price);
                            propertyData.put("shortdescription", shortdescription);
                            propertyData.put("imageuri", imageuri);
                            propertyData.put("description", description);
                            propertyData.put("contactno", contactno);
                            propertyData.put("type", type);
                            propertyData.put("ownername", ownername);

                            String documentId = contactno; // unique ID
                            db.collection("Favorites")
                                    .document(documentId)
                                    .set(propertyData)
                                    .addOnSuccessListener(aVoid ->
                                            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Property already added to favorites", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error checking favorites", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
