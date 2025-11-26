package com.example.realestateapp.screens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestateapp.R;
import com.example.realestateapp.adapters.FavAdapter;
import com.example.realestateapp.model.FavProperty;
import com.example.realestateapp.repository.FavoritesRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavAdapter favAdapter;
    private FavoritesRepository favoritesRepo;
    private List<FavProperty> favList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        recyclerView = findViewById(R.id.recyclerFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        favList = new ArrayList<>();
        favAdapter = new FavAdapter(this, favList);
        recyclerView.setAdapter(favAdapter);

        favoritesRepo = new FavoritesRepository();

        // Load favorites from Firestore
        loadFavorites();
    }

    private void loadFavorites() {
        favoritesRepo.getFavoritesSnapshot(querySnapshot -> {
            favList.clear();
            for (DocumentSnapshot doc : querySnapshot) {
                FavProperty fav = doc.toObject(FavProperty.class);
                favList.add(fav);
            }
            favAdapter.notifyDataSetChanged();
        });
    }
}
