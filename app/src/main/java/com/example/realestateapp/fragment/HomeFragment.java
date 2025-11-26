package com.example.realestateapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestateapp.R;
import com.example.realestateapp.adapters.CategoryAdapter;
import com.example.realestateapp.adapters.HomeAdapter;
import com.example.realestateapp.listeners.ItemListener;
import com.example.realestateapp.model.Category;
import com.example.realestateapp.model.Item;
import com.example.realestateapp.screens.DetailsActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView categoryRecyclerView, propertyRecyclerView;
    private CategoryAdapter categoryAdapter;
    private HomeAdapter homeAdapter;
    private List<Category> categoryList;
    private List<Item> allProperties;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.top_deal_RV1);
        propertyRecyclerView = view.findViewById(R.id.top_deal_RV);

        db = FirebaseFirestore.getInstance();
        allProperties = new ArrayList<>();

        setupCategories();
        setupRecyclerView();
        addStaticProperties();    // <-- Add hardcoded drawable properties
        loadPropertiesFromFirestore(); // <-- Fetch Firebase properties

        return view;
    }

    // --- Categories ---
    private void setupCategories() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.villa, "Villa"));
        categoryList.add(new Category(R.drawable.office, "Office"));
        categoryList.add(new Category(R.drawable.shop, "Shop"));
        categoryList.add(new Category(R.drawable.apartment, "Apartment"));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, position -> {
            String selectedCategory = categoryList.get(position).getTitle();
            filterPropertiesByCategory(selectedCategory);
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    // --- RecyclerView & Adapter ---
    private void setupRecyclerView() {
        homeAdapter = new HomeAdapter(getContext(), allProperties, position -> {
            Item clickedItem = allProperties.get(position);

            Intent intent = new Intent(getContext(), DetailsActivity.class);
            intent.putExtra("location", clickedItem.getLocation());
            intent.putExtra("price", clickedItem.getPrice());
            intent.putExtra("shortdescription", clickedItem.getShortDescription());
            intent.putExtra("imageuri", clickedItem.getImageUrl()); // Firebase URL
            intent.putExtra("description", clickedItem.getDescription());
            intent.putExtra("contactno", clickedItem.getOwnerContact());
            intent.putExtra("type", clickedItem.getCategory());
            intent.putExtra("ownername", clickedItem.getOwnerName());

            startActivity(intent);
        });

        propertyRecyclerView.setAdapter(homeAdapter);
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // --- Static properties from drawable ---
    private void addStaticProperties() {
        allProperties.add(new Item(
                "Villa in City Center",
                "City Center",
                "Luxury villa",
                "$500,000",
                "Villa",
                R.drawable.villa,
                "Mr. John Dae",
                "+91-852475935",
                "Welcome to our luxury villa located in the heart of the city."
        ));

        allProperties.add(new Item(
                "Office Space",
                "Downtown",
                "Spacious office",
                "$300,000",
                "Office",
                R.drawable.office,
                "Ms. Jane Doe",
                "+91-987654321",
                "Modern office space suitable for startups and businesses."
        ));

        allProperties.add(new Item(
                "Shop near Market",
                "Market Area",
                "High foot traffic",
                "$200,000",
                "Shop",
                R.drawable.shop,
                "Mr. Sam Smith",
                "+91-123456789",
                "Prime retail shop with excellent visibility near the market."
        ));

        allProperties.add(new Item(
                "Apartment with Balcony",
                "Suburbs",
                "Nice view",
                "$150,000",
                "Apartment",
                R.drawable.apartment,
                "Mrs. Mary Johnson",
                "+91-456789123",
                "Cozy apartment with balcony overlooking scenic views."
        ));

        homeAdapter.updateList(allProperties);
    }

    // --- Firebase Firestore properties ---
    private void loadPropertiesFromFirestore() {
        CollectionReference propertiesRef = db.collection("Properties");
        propertiesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String title = doc.getString("title");
                    String location = doc.getString("location");
                    String shortDescription = doc.getString("shortdescription");
                    String price = doc.getString("price");
                    String category = doc.getString("type"); // or "category"
                    String imageUrl = doc.getString("imageUri");
                    String ownerName = doc.getString("ownername");
                    String ownerContact = doc.getString("contactno");
                    String description = doc.getString("description");

                    Item property = new Item(title, location, shortDescription, price, category,
                            imageUrl, ownerName, ownerContact, description);
                    allProperties.add(property);
                }
                homeAdapter.updateList(allProperties);
            } else {
                Toast.makeText(getContext(), "Failed to load properties", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Filter by category ---
    private void filterPropertiesByCategory(String category) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : allProperties) {
            if (item.getCategory() != null && item.getCategory().equals(category)) {
                filteredList.add(item);
            }
        }
        homeAdapter.updateList(filteredList);
    }
}
