package com.example.realestateapp.screens;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realestateapp.CurrentPropertyData;
import com.example.realestateapp.R;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import java.util.HashMap;
import java.util.Map;
import com.bumptech.glide.Glide;
import com.example.realestateapp.model.Item;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailsActivity extends AppCompatActivity {

    private TextView priceTextView, shortDescriptionTextView, descriptionTextView,
            ownerNameTextView, contactNoTextView, rentSellTextView, locationTextView;
    private ImageView propertyImageView;
    private Button callButton, bookProperty;
    private ImageButton backButton, favButton;

    private FirebaseFirestore db;
    // Wishlist
    private boolean isFavorite = false;

    @SuppressLint({"MissingInflatedId"})
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
        rentSellTextView = findViewById(R.id.rent_sell);
        locationTextView = findViewById(R.id.location);
        propertyImageView = findViewById(R.id.imageView);
        callButton = findViewById(R.id.callButton);
        bookProperty = findViewById(R.id.book_property);
        backButton = findViewById(R.id.back_button);
        favButton = findViewById(R.id.add_to_fav);

        //Intent intent = getIntent();
        Item item = CurrentPropertyData.selectedProperty;
        if (item != null) {
            // Get all data from intent
            String title = item.getTitle(); // fix image
            String location = item.getLocation(); // fix image
            String price = item.getPrice(); // fix image
            String shortdescription = item.getShortDescription(); // fix image
            String imageuri = String.valueOf(item.getImageResId()); // fix image
            String description = item.getDescription(); // fix image
            String contactno = item.getOwnerContact(); // fix image
            String type = item.getType(); // fix image
            String ownername = item.getOwnerName(); // fix image
            String imageBase64 = item.getImageUrl(); // fix image

            // Set data if available
            if (price != null) priceTextView.setText(price);
            if (shortdescription != null) shortDescriptionTextView.setText(shortdescription);
            if (description != null) descriptionTextView.setText(description);
            if (ownername != null) ownerNameTextView.setText("Owner Name: " + ownername);
            if (contactno != null) contactNoTextView.setText("Phone No: " + contactno);
            if (type != null) rentSellTextView.setText(type);
            if (location != null) locationTextView.setText(location);

            // Load image from Base64 or fallback drawable
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                try {
                    byte[] bytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    propertyImageView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    propertyImageView.setImageResource(R.drawable.hom1);
                }
            } else {
                propertyImageView.setImageResource(R.drawable.hom1);
            }

            // Call button
            callButton.setOnClickListener(v -> {
                if (contactno != null && !contactno.isEmpty()) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + contactno));
                    startActivity(dialIntent);
                }
            });

            // Back button
            backButton.setOnClickListener(v -> finish());

            // Wishlist
            checkInitialFavoriteStatus(contactno);

            // Wishlist
            favButton.setOnClickListener(v -> {
                if (isFavorite) {
                    removeFromFavorites(contactno);
                } else {
                    addToFavorites(title, location, price, shortdescription, imageuri, description, contactno, type, ownername);
                }
            });

        } else {
            Toast.makeText(this, "Failed to retrieve property details", Toast.LENGTH_SHORT).show();
            finish();
        }


    }
    // Wishlist
    private void checkInitialFavoriteStatus(String contactno) {
        if (contactno == null) {
            return;
        }
        // We use contactno as ID based on your previous code structure
        db.collection("Favorites").document(contactno).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        isFavorite = true;
                        favButton.setImageResource(R.drawable.baseline_favorite_24); // Filled Heart
                    } else {
                        isFavorite = false;
                        favButton.setImageResource(R.drawable.baseline_favorite_border_24); // Empty Heart
                    }
                });
    }

    // Wishlist
    private void addToFavorites(String title, String location, String price, String shortdescription, String imageuri, String description, String contactno, String type, String ownername) {
        String documentId = contactno; // Using contactno as ID per your existing logic

        Map<String, Object> propertyData = new HashMap<>();
        propertyData.put("title", title);
        propertyData.put("location", location);
        propertyData.put("price", price);
        propertyData.put("shortdescription", shortdescription);
        propertyData.put("imageuri", imageuri);
        propertyData.put("description", description);
        propertyData.put("contactno", contactno);
        propertyData.put("type", type);
        propertyData.put("ownername", ownername);

        db.collection("Favorites")
                .document(documentId)
                .set(propertyData)
                .addOnSuccessListener(aVoid -> {
                    isFavorite = true; // Update state
                    favButton.setImageResource(R.drawable.baseline_favorite_24); // Set Filled Heart
                    Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error adding to favorites", e);
                });
    }
    // Wishlist
    private void removeFromFavorites(String contactno) {
        db.collection("Favorites").document(contactno)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    isFavorite = false; // Update state
                    favButton.setImageResource(R.drawable.baseline_favorite_border_24); // Set Empty Heart
                    Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to remove", Toast.LENGTH_SHORT).show();
                });
    }
}
