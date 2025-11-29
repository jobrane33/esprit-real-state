package com.example.realestateapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestateapp.CurrentPropertyData;
import com.example.realestateapp.R;
import com.example.realestateapp.adapters.CategoryAdapter;
import com.example.realestateapp.adapters.HomeAdapter;
import com.example.realestateapp.model.Category;
import com.example.realestateapp.model.Item;
import com.example.realestateapp.screens.DetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.realestateapp.listeners.ItemListener;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView categoryRecyclerView, propertyRecyclerView;
    private CategoryAdapter categoryAdapter;
    private HomeAdapter homeAdapter;
    private List<Category> categoryList;
    private List<Item> allProperties;
    private List<Item> filteredProperties; // For search
    private FirebaseFirestore db;
    private EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        categoryRecyclerView = view.findViewById(R.id.top_deal_RV1);
        propertyRecyclerView = view.findViewById(R.id.top_deal_RV);
        searchEditText = view.findViewById(R.id.search);

        db = FirebaseFirestore.getInstance();
        allProperties = new ArrayList<>();
        filteredProperties = new ArrayList<>();

        setupCategories();
        setupRecyclerView();
        loadPropertiesFromFirestore();
        setupSearch();

        return view;
    }

    private void setupCategories() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.villa, "Villa"));
        categoryList.add(new Category(R.drawable.office, "Office"));
        categoryList.add(new Category(R.drawable.shop, "Shop"));
        categoryList.add(new Category(R.drawable.apartment, "Apartment"));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, position -> {
            String selectedCategory = categoryList.get(position).getTitle();
            filterByCategory(selectedCategory);
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupRecyclerView() {
        homeAdapter = new HomeAdapter(getContext(), filteredProperties, position -> {

            //1. Get the item
            Item clickedItem = homeAdapter.getItem(position);

            // --- DEBUG LOG ---
            // This will print to Logcat so we know if the click works
            android.util.Log.d("DEBUG_APP", "Clicked: " + clickedItem.getTitle());

            // 2. SAVE DATA (This is the most important line!)
            CurrentPropertyData.selectedProperty = clickedItem;

            // --- DEBUG LOG ---
            // Check if it was actually saved
            if (CurrentPropertyData.selectedProperty != null) {
                android.util.Log.d("DEBUG_APP", "Data saved successfully to helper.");
            } else {
                android.util.Log.e("DEBUG_APP", "CRITICAL ERROR: Data was NOT saved.");
            }

            // 3. Start Activity (Empty Intent)
            Intent intent = new Intent(getContext(), DetailsActivity.class);
            startActivity(intent);
        });

        propertyRecyclerView.setAdapter(homeAdapter);
        propertyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }




    private void addStaticProperties() {
        allProperties.add(new Item("Villa in City Center","City Center","Luxury villa","$500,000",
                "Villa","Sell",null,"Mr. John Dae","+91-852475935",
                "Welcome to our luxury villa located in the heart of the city.", R.drawable.villa));

        allProperties.add(new Item("Office Space","Downtown","Spacious office","$300,000",
                "Office","Rent",null,"Ms. Jane Doe","+91-987654321",
                "Modern office space suitable for startups and businesses.", R.drawable.office));

        filteredProperties.clear();
        filteredProperties.addAll(allProperties);
        homeAdapter.updateList(filteredProperties);
    }

    private void loadPropertiesFromFirestore() {
        db.collection("Properties").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Item property = new Item(
                            doc.getString("title"),
                            doc.getString("location"),
                            doc.getString("shortdescription"),
                            doc.getString("price"),
                            doc.getString("category"),
                            doc.getString("type"),
                            doc.getString("imageBase64"),  // <-- now Base64
                            doc.getString("ownername"),
                            doc.getString("contactno"),
                            doc.getString("description"),
                            0
                    );
                    allProperties.add(property);
                }
                filteredProperties.clear();
                filteredProperties.addAll(allProperties);
                homeAdapter.updateList(filteredProperties);
            } else {
                Toast.makeText(getContext(), "Failed to load properties", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterByCategory(String category) {
        filteredProperties.clear();
        for (Item item : allProperties) {
            if (item.getCategory() != null && item.getCategory().equals(category)) {
                filteredProperties.add(item);
            }
        }
        homeAdapter.updateList(filteredProperties);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                filteredProperties.clear();
                for (Item item : allProperties) {
                    if (item.getTitle() != null && item.getTitle().toLowerCase().contains(query)) {
                        filteredProperties.add(item);
                    }
                }
                homeAdapter.updateList(filteredProperties);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}
