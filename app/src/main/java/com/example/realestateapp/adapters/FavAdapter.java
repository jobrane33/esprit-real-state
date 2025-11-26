package com.example.realestateapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.example.realestateapp.model.FavProperty;
import com.example.realestateapp.repository.FavoritesRepository; // NEW

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

        holder.title.setText(fav.getDescription());
        holder.location.setText(fav.getLocation());
        holder.price.setText(fav.getPrice());

        Glide.with(context).load(fav.getFavImageUrl()).into(holder.image);

        // --- NEW: Delete favorite ---
        holder.delete.setOnClickListener(v -> {
            if (fav.getId() != null) { // make sure FavProperty has an id field
                favoritesRepo.deleteFavorite(fav.getId());
            }
            favorites.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, price;
        ImageView image, delete; // NEW

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.favorite_title);
            location = itemView.findViewById(R.id.favorite_location);
            price = itemView.findViewById(R.id.favorite_price);
            image = itemView.findViewById(R.id.favorite_image);
            delete = itemView.findViewById(R.id.favorite_delete); // NEW
        }
    }
}
