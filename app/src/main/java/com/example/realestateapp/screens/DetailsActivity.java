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

        priceTextView = findViewById(R.id.price);
        shortDescriptionTextView = findViewById(R.id.rent_sell);
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

        Intent intent = getIntent();
        if (intent != null) {
            String location = intent.getStringExtra("location");
            String price = intent.getStringExtra("price");
            String shortdescription = intent.getStringExtra("shortdescription");
            String imageuri = intent.getStringExtra("imageuri");
            String description = intent.getStringExtra("description");
            String contactno = intent.getStringExtra("contactno");
            String type = intent.getStringExtra("type"); // Correct
            String ownername = intent.getStringExtra("ownername");

            priceTextView.setText(price);
            shortDescriptionTextView.setText(shortdescription);
            descriptionTextView.setText(description);
            ownerNameTextView.setText("Owner Name: " + ownername);
            contactNoTextView.setText("Phone No: " + contactno);
            rentSellTextView.setText(type);
            locationTextView.setText(location);

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

            callButton.setOnClickListener(v -> {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + contactno));
                startActivity(dialIntent);
            });

            backButton.setOnClickListener(v -> finish());
        } else {
            Toast.makeText(this, "Failed to retrieve property details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
