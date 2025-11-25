package com.example.realestateapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.example.realestateapp.listeners.ItemListener;
import com.example.realestateapp.model.Property;
import com.example.realestateapp.screens.ListingsActivity;

import java.util.List;

public class PropertyHomeAdapter extends RecyclerView.Adapter<PropertyHomeAdapter.ViewHolder> {

    private Context context;
    private List<Property> propertyList;
    private ItemListener itemListener;

    public PropertyHomeAdapter(Context context, List<Property> propertyList, ItemListener itemListener) {
        this.context = context;
        this.propertyList = propertyList;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property property = propertyList.get(position);

        holder.propertyName.setText(property.getTitle());
        holder.propertyLocation.setText(property.getLocation());
        holder.propertyPrice.setText(property.getPrice());

        // Load image using Glide
        Glide.with(context).load(property.getImageResId()).into(holder.propertyImage);

        // Click listener on the item
        holder.itemView.setOnClickListener(v -> {
            if (itemListener != null) {
                itemListener.onItemClick(position); // Trigger listener in fragment/activity
            }

            // Optional: open ListingsActivity by category
            Intent intent = new Intent(context, ListingsActivity.class);
            intent.putExtra("category", property.getCategory());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public void updateList(List<Property> newList) {
        propertyList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView propertyName, propertyLocation, propertyPrice;
        ImageView propertyImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyName = itemView.findViewById(R.id.property_title);
            propertyLocation = itemView.findViewById(R.id.property_location);
            propertyPrice = itemView.findViewById(R.id.property_price);
            propertyImage = itemView.findViewById(R.id.property_image);
        }
    }
}
