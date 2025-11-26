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
import com.example.realestateapp.model.Item;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private Context context;
    private List<Item> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public HomeAdapter(Context context, List<Item> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Item item = items.get(position);
        holder.titleText.setText(item.getTitle());
        holder.priceText.setText(item.getPrice());
        holder.locationText.setText(item.getLocation());

        // Load image: either drawable or Firebase URL
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.hom1)
                    .error(R.drawable.hom1)
                    .centerCrop()
                    .into(holder.imageView);
        } else if (item.getImageResId() != null) {
            holder.imageView.setImageResource(item.getImageResId());
        } else {
            holder.imageView.setImageResource(R.drawable.hom1);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Get item at position
    public Item getItem(int position) {
        return items.get(position);
    }

    // Update list for filtering / loading
    public void updateList(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText, priceText, locationText;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.property_image);
            titleText = itemView.findViewById(R.id.property_title);
            priceText = itemView.findViewById(R.id.property_price);
            locationText = itemView.findViewById(R.id.property_location);
        }
    }
}
