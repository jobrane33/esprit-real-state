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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;
    private ItemListener itemListener;

    public HomeAdapter(Context context, List<Item> itemList, ItemListener itemListener) {
        this.context = context;
        this.itemList = itemList;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.top_deals, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.price.setText(item.getPrice());
        holder.location.setText(item.getLocation());
        holder.shortDescription.setText(item.getShortDescription());
        Glide.with(context).load(item.getImageResId()).into(holder.backgroundImageView);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateList(List<Item> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView price, location, shortDescription;
        ImageView backgroundImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            price = itemView.findViewById(R.id.price);
            location = itemView.findViewById(R.id.location);
            shortDescription = itemView.findViewById(R.id.short_description);
            backgroundImageView = itemView.findViewById(R.id.bg);

            itemView.setOnClickListener(v -> {
                if (itemListener != null) {
                    itemListener.OnItemPosition(getAdapterPosition());
                }
            });
        }
    }
}
