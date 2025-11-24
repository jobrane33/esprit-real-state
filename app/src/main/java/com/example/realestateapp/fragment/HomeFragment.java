package com.example.realestateapp.fragment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.realestateapp.R;
import com.example.realestateapp.adapters.CategoryAdapter;
import com.example.realestateapp.adapters.HomeAdapter;
import com.example.realestateapp.adapters.PropertyHomeAdapter;
import com.example.realestateapp.listeners.ItemListener;
import com.example.realestateapp.model.Category;
import com.example.realestateapp.model.Item;
import com.example.realestateapp.model.Property;
import com.example.realestateapp.screens.DetailsActivity;
import com.example.realestateapp.screens.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements ItemListener {

    private RecyclerView topDealRV, categoryRecyclerView;
    private HomeAdapter adapter;
    private TextView userName;
    private List<Item> itemList;
    private EditText search;
    private CircleImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        userName = view.findViewById(R.id.user_name);
        profileImage = view.findViewById(R.id.profile_image);
        topDealRV = view.findViewById(R.id.top_deal_RV);
        categoryRecyclerView = view.findViewById(R.id.top_deal_RV1);
        search = view.findViewById(R.id.search);

        // Search click
        search.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });

        // Load Firebase user info
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                userName.setText("Welcome back, " + name);
                                String imageUrl = document.getString("imageUrl");
                                if (imageUrl != null) {
                                    Picasso.get().load(imageUrl).into(profileImage);
                                }
                            }
                        }
                    });
        }

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(R.drawable.villa, "Villa"));
        categories.add(new Category(R.drawable.apartment, "Apartment"));
        categories.add(new Category(R.drawable.apartment, "Shop"));

        categories.add(new Category(R.drawable.office, "Office"));

        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categories);
        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // 2️⃣ Fetch properties from Firestore
        itemList = new ArrayList<>();
        db.collection("Properties").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String location = document.getString("location");
                            String price = document.getString("price");
                            String shortDescription = document.getString("shortdescription");
                            String imageuri = document.getString("imageuri");
                            String ownername = document.getString("ownername");
                            String type = document.getString("type");
                            String contactno = document.getString("contactno");
                            String description = document.getString("description");

                            Item item = new Item(location, price, shortDescription, imageuri, contactno, ownername, type, description);
                            itemList.add(item);
                        }

                        adapter = new HomeAdapter(getContext(), itemList, this);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                        topDealRV.setLayoutManager(linearLayoutManager);
                        topDealRV.setAdapter(adapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void OnItemPosition(int position) {
        Item selectedItem = itemList.get(position);

        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("location", selectedItem.getLocation());
        intent.putExtra("price", selectedItem.getPrice());
        intent.putExtra("shortdescription", selectedItem.getShortDescription());
        intent.putExtra("imageuri", selectedItem.getImageUri());
        intent.putExtra("contactno", selectedItem.getContactNo());
        intent.putExtra("description", selectedItem.getDescription());
        intent.putExtra("type", selectedItem.getType());
        intent.putExtra("ownername", selectedItem.getOwnerName());

        startActivity(intent);
    }
}
