package com.example.realestateapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestateapp.R;
import com.example.realestateapp.model.Property;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private final Context context;
    private final List<Property> propertyList;
    private final FirebaseFirestore db;

    // Interface for update click
    public interface OnUpdateClickListener {
        void onUpdateClick(Property property);
    }

    private OnUpdateClickListener updateClickListener;

    public void setOnUpdateClickListener(OnUpdateClickListener listener) {
        this.updateClickListener = listener;
    }

    public PropertyAdapter(Context context, List<Property> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        holder.txtTitle.setText(property.getTitle() != null ? property.getTitle() : "No Title");
        holder.txtPrice.setText(property.getPrice() != null ? "$" + property.getPrice() : "$0");

        // Load Base64 image
        String imageBase64 = property.getImageBase64();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                byte[] bytes = Base64.decode(imageBase64, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.propertyImage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                holder.propertyImage.setImageResource(R.drawable.hom1);
            }
        } else if (property.getImageResId() != null) {
            holder.propertyImage.setImageResource(property.getImageResId());
        } else {
            holder.propertyImage.setImageResource(R.drawable.hom1);
        }

        // Update button click
        holder.btnUpdate.setOnClickListener(v -> {
            if (updateClickListener != null) {
                updateClickListener.onUpdateClick(property);
            }
        });

        // Delete button click
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                String titleToDelete = propertyList.get(pos).getTitle();
                db.collection("Properties")
                        .whereEqualTo("title", titleToDelete)
                        .get()
                        .addOnSuccessListener(query -> {
                            if (!query.isEmpty()) {
                                String docId = query.getDocuments().get(0).getId();
                                db.collection("Properties").document(docId).delete()
                                        .addOnSuccessListener(aVoid -> {
                                            propertyList.remove(pos);
                                            notifyItemRemoved(pos);
                                            notifyItemRangeChanged(pos, propertyList.size());
                                            Toast.makeText(context, "Property deleted", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show());
                            }
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyList != null ? propertyList.size() : 0;
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImage;
        TextView txtTitle, txtPrice;
        Button btnUpdate, btnDelete;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImage = itemView.findViewById(R.id.property_image);
            txtTitle = itemView.findViewById(R.id.property_title);
            txtPrice = itemView.findViewById(R.id.property_price);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
