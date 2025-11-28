package com.example.realestateapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.example.realestateapp.model.FavProperty;
import com.example.realestateapp.repository.FavoritesRepository; // NEW
import com.example.realestateapp.screens.DetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private Context context;
    private List<FavProperty> favorites;
    private FavoritesRepository favoritesRepo; // NEW

    public FavAdapter(Context context, List<FavProperty> favorites) {
        this.context = context;
        this.favorites = favorites;
        this.favoritesRepo = new FavoritesRepository(); // NEW
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }





    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavProperty fav = favorites.get(position);
        holder.title.setText(fav.getTitle());
        holder.location.setText(fav.getLocation());
        holder.price.setText(fav.getPrice());

        // LOG
        //android.util.Log.d("FavAdapter", "Loading Image for " + fav.getTitle() + ": " + fav.getImageuri());

        // Image
        String imageUriString = fav.getImageuri();

        try {
            // 1. Try to convert the string to a number (Resource ID)
            int resourceId = Integer.parseInt(imageUriString);

            // If successful, load it as an Integer
            Glide.with(context)
                    .load(resourceId)
                    .placeholder(R.drawable.villa)
                    .into(holder.image);

        } catch (NumberFormatException | NullPointerException e) {
            // 2. If it's NOT a number (it's a real HTTP URL or null), load as String
            Glide.with(context)
                    .load(imageUriString)
                    .placeholder(R.drawable.villa)
                    .into(holder.image);
        }


        holder.delete.setOnClickListener(v -> {
            // 1. Check for valid ID before deleting
            if (fav.getContactno() != null) {
                deleteFromFirestore(fav.getContactno(), holder.getAdapterPosition());
            } else {
                Toast.makeText(context, "Error: Item ID missing", Toast.LENGTH_SHORT).show();
            }
        });
        // --- FEATURE 2: SHARE PROPERTY (New) ---
        holder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Check out this amazing property: " + fav.getTitle() + "\nPrice: " + fav.getPrice() + "\nLocation: " + fav.getLocation();
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Real Estate App Property");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // --- FEATURE 3: CLICK ROW TO OPEN DETAILS (New) ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            // Pass all data back to details so it populates correctly
            intent.putExtra("title", fav.getTitle());
            intent.putExtra("price", fav.getPrice());
            intent.putExtra("location", fav.getLocation());
            intent.putExtra("shortdescription", fav.getShortDescription());
            intent.putExtra("imageuri", fav.getImageuri());
            intent.putExtra("description", fav.getDescription());
            intent.putExtra("contactno", fav.getContactno());
            intent.putExtra("type", fav.getType());
            intent.putExtra("ownername", fav.getOwnername());
            context.startActivity(intent);
        });
    }

    private void deleteFromFirestore(String documentId, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Favorites").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // 2. Remove from the local list so it disappears immediately
                    if (position != RecyclerView.NO_POSITION && position < favorites.size()) {
                        favorites.remove(position);
                        notifyItemRemoved(position);
                        // Optional: Notify range changed to update position indexes
                        notifyItemRangeChanged(position, favorites.size());
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, price;
        ImageView image, delete, share;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.favorite_title);
            location = itemView.findViewById(R.id.favorite_location);
            price = itemView.findViewById(R.id.favorite_price);
            image = itemView.findViewById(R.id.favorite_image);
            delete = itemView.findViewById(R.id.favorite_delete);

            share = itemView.findViewById(R.id.favorite_share);
        }
    }

}
