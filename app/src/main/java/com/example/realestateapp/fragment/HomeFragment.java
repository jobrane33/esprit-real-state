package com.example.realestateapp.fragment;

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

        // Categories
        categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.villa, "Villa"));
        categoryList.add(new Category(R.drawable.office, "Office"));
        categoryList.add(new Category(R.drawable.shop, "Shop"));
        categoryList.add(new Category(R.drawable.apartment, "Appartment"));

        categoryAdapter = new CategoryAdapter(getContext(), categoryList, position -> {
            String selectedCategory = categoryList.get(position).getTitle();
            filterPropertiesByCategory(selectedCategory);
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Properties
        allProperties = new ArrayList<>();
        allProperties.add(new Item("Villa in City Center", "City Center", "Luxury villa", "500000", "Villa", R.drawable.villa));
        allProperties.add(new Item("Office Space", "Downtown", "Spacious office", "300000", "Office", R.drawable.office));
        allProperties.add(new Item("Shop near Market", "Market Area", "High foot traffic", "200000", "Shop", R.drawable.shop));
        allProperties.add(new Item("Apartment with Balcony", "Suburbs", "Nice view", "150000", "Appartment", R.drawable.apartment));
        allProperties.add(new Item("Villa with Pool", "Uptown", "Private pool", "700000", "Villa", R.drawable.villa));

        homeAdapter = new HomeAdapter(getContext(), allProperties, position -> {
            // clic sur une propriété (détails / modifier / supprimer)
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
