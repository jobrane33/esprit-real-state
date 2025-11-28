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
import com.example.realestateapp.R;

public class DetailsActivity extends AppCompatActivity {

    private TextView priceTextView, shortDescriptionTextView, descriptionTextView,
            ownerNameTextView, contactNoTextView, rentSellTextView, locationTextView;
    private ImageView propertyImageView;
    private Button callButton, bookProperty;
    private ImageButton backButton, favButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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

        Intent intent = getIntent();
        if (intent != null) {
            // Get all data from intent
            String location = intent.getStringExtra("location");
            String price = intent.getStringExtra("price");
            String shortdescription = intent.getStringExtra("shortdescription");
            String description = intent.getStringExtra("description");
            String contactno = intent.getStringExtra("contactno");
            String type = intent.getStringExtra("type");
            String ownername = intent.getStringExtra("ownername");
            String imageBase64 = intent.getStringExtra("imageBase64");

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

        } else {
            Toast.makeText(this, "Failed to retrieve property details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
