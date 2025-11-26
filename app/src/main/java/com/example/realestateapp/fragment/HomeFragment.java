package com.example.realestateapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView categoryRecyclerView, propertyRecyclerView;
    private CategoryAdapter categoryAdapter;
    private HomeAdapter homeAdapter;
    private List<Category> categoryList;
    private List<Item> allProperties;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.top_deal_RV1);
        propertyRecyclerView = view.findViewById(R.id.top_deal_RV);

        // --- Categories ---
        categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.villa, "Villa"));
        categoryList.add(new Category(R.drawable.office, "Office"));
        categoryList.add(new Category(R.drawable.shop, "shop"));
        categoryList.add(new Category(R.drawable.apartment, "Apartment"));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, new ItemListener() {
            @Override
            public void onItemClick(int position) {
                String selectedCategory = categoryList.get(position).getTitle();
                filterPropertiesByCategory(selectedCategory);
            }


        });


        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // --- Properties ---
        allProperties = new ArrayList<>();
        // Add properties with all details
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
        allProperties.add(new Item(
                "Villa with Pool",
                "Uptown",
                "Private pool",
                "$700,000",
                "Villa",
                R.drawable.villa,
                "Mr. Alex Brown",
                "+91-789123456",
                "Exclusive villa featuring a private swimming pool and garden."
        ));

        homeAdapter = new HomeAdapter(getContext(), allProperties, position -> {
            Item clickedItem = allProperties.get(position);

            // Open DetailsActivity and pass data
            Intent intent = new Intent(getContext(), DetailsActivity.class);
            intent.putExtra("location", clickedItem.getLocation());
            intent.putExtra("price", clickedItem.getPrice());
            intent.putExtra("shortdescription", clickedItem.getShortDescription());
            intent.putExtra("imageuri", String.valueOf(clickedItem.getImageResId())); // pass drawable ID as string
            intent.putExtra("description", clickedItem.getDescription());
            intent.putExtra("contactno", clickedItem.getOwnerContact());
            intent.putExtra("type", clickedItem.getCategory());
            intent.putExtra("ownername", clickedItem.getOwnerName());

            startActivity(intent);
        });

        propertyRecyclerView.setAdapter(homeAdapter);
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    private void filterPropertiesByCategory(String category) {
        List<Item> filteredList = new ArrayList<>();
        for (Item item : allProperties) {
            if (item.getCategory().equals(category)) {
                filteredList.add(item);
            }
        }
        homeAdapter.updateList(filteredList);
    }
}