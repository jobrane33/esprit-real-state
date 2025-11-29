package com.example.realestateapp.repository;

import android.util.Log;

import com.example.realestateapp.model.FavProperty;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoritesRepository {
    private final FirebaseFirestore db;
    private final String userId;

    private static final String TAG = "FavoritesRepo";

    public FavoritesRepository() {
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // --- CREATE ---
    public void addFavorite(FavProperty favProperty) {
        db.collection("users").document(userId)
                .collection("favorites")
                .add(favProperty)
                .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Added favorite with ID: " + documentReference.getId()))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error adding favorite", e));
    }

    // --- READ (with callback for RecyclerView) ---
    public void getFavorites(OnFavoritesLoaded callback) {
        db.collection("users").document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<FavProperty> favorites = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        FavProperty fav = doc.toObject(FavProperty.class);

                        // 🔑 Attach Firestore document ID to the model
                        if (fav != null) {
                            fav.setId(doc.getId()); // NEW
                            favorites.add(fav);
                        }
                    }
                    callback.onLoaded(favorites);
                })
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error getting favorites", e));
    }

    // --- DELETE ---
    public void deleteFavorite(String favoriteId) {
        db.collection("users").document(userId)
                .collection("favorites").document(favoriteId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "Favorite deleted"))
                .addOnFailureListener(e ->
                        Log.w(TAG, "Error deleting favorite", e));
    }

    // --- Callback interface ---
    public interface OnFavoritesLoaded {
        void onLoaded(List<FavProperty> favorites);
    }
}
