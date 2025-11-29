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
import com.example.realestateapp.CurrentPropertyData;
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

        // ---------------------------------------------------------
        // FIX 1: HANDLE BASE64 IMAGES CORRECTLY
        // ---------------------------------------------------------
        String imageUriString = fav.getImageuri();

        if (imageUriString != null && !imageUriString.isEmpty()) {
            try {
                // CASE A: Try to parse as a Resource ID (e.g. "213123085")
                int resourceId = Integer.parseInt(imageUriString);

                Glide.with(context)
                        .load(resourceId)
                        .placeholder(R.drawable.villa)
                        .into(holder.image);

            } catch (NumberFormatException e) {
                // CASE B: It is NOT a number. It is likely a Base64 String.
                try {
                    // Clean string just in case
                    String cleanBase64 = imageUriString.trim();

                    // Decode Base64 to Bitmap
                    byte[] decodedString = android.util.Base64.decode(cleanBase64, android.util.Base64.DEFAULT);
                    android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    // Load Bitmap into Glide
                    Glide.with(context)
                            .load(decodedByte)
                            .placeholder(R.drawable.villa)
                            .into(holder.image);

                } catch (Exception ex) {
                    // CASE C: It failed to decode (corrupted or normal URL).
                    // Try loading as standard string URL as a last resort.
                    Glide.with(context)
                            .load(imageUriString)
                            .placeholder(R.drawable.villa)
                            .into(holder.image);
                }
            }
        } else {
            // String is null/empty
            holder.image.setImageResource(R.drawable.villa);
        }
        // ---------------------------------------------------------
        // END OF IMAGE FIX
        // ---------------------------------------------------------


        // Delete Button
        holder.delete.setOnClickListener(v -> {
            if (fav.getContactno() != null) {
                deleteFromFirestore(fav.getContactno(), holder.getAdapterPosition());
            } else {
                Toast.makeText(context, "Error: Item ID missing", Toast.LENGTH_SHORT).show();
            }
        });

        // Share Button
        holder.share.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = "Check out this amazing property: " + fav.getTitle() + "\nPrice: " + fav.getPrice() + "\nLocation: " + fav.getLocation();
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Real Estate App Property");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // ---------------------------------------------------------
        // FIX 2: PREVENT CRASH ON CLICK (Use Helper)
        // ---------------------------------------------------------
        holder.itemView.setOnClickListener(v -> {
            // We must convert 'FavProperty' to 'Item' to use our Helper
            // assuming 'Item' is the class expected by CurrentPropertyData

            // 1. Create a temporary Item object
            // (Make sure to import com.example.realestateapp.models.Item)
            com.example.realestateapp.model.Item tempItem = new com.example.realestateapp.model.Item();

            // 2. Manually copy the data over
            tempItem.setTitle(fav.getTitle());
            tempItem.setPrice(fav.getPrice());
            tempItem.setLocation(fav.getLocation());
            tempItem.setShortDescription(fav.getShortDescription());
            tempItem.setDescription(fav.getDescription());
            tempItem.setOwnerContact(fav.getContactno());
            tempItem.setType(fav.getType());
            tempItem.setOwnerName(fav.getOwnername());

            // IMPORTANT: Pass the image string (Base64 or ID) to the Item's ImageUrl field
            // DetailsActivity looks at 'getImageUrl' for the base64 string.
            tempItem.setImageUrl(fav.getImageuri());

            // 3. Save to the Static Helper
            CurrentPropertyData.selectedProperty = tempItem;

            // 4. Open Activity (NO EXTRAS to avoid crash)
            Intent intent = new Intent(context, DetailsActivity.class);
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
