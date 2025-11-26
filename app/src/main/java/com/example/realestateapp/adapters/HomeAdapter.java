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
import com.example.realestateapp.listeners.ItemListener;
import com.example.realestateapp.model.Item;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private Context context;
    private List<Item> itemList;
    private ItemListener listener;

    public HomeAdapter(Context context, List<Item> itemList, ItemListener listener) {
        this.context = context;
        this.itemList = itemList;
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
        Item item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.location.setText(item.getLocation());
        holder.price.setText(item.getPrice());

        // Load image from Firebase URL or drawable fallback
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.hom1)
                    .error(R.drawable.hom1)
                    .into(holder.image);
        } else if (item.getImageResId() != null) {
            holder.image.setImageResource(item.getImageResId());
        } else {
            holder.image.setImageResource(R.drawable.hom1);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateList(List<Item> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }

    static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView title, location, price;
        ImageView image;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.property_title);
            location = itemView.findViewById(R.id.property_location);
            price = itemView.findViewById(R.id.property_price);
            image = itemView.findViewById(R.id.property_image);
        }
    }
}
