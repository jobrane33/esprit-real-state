package com.example.realestateapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestateapp.R;
import com.example.realestateapp.model.Property;
import com.example.realestateapp.screens.AddPropertyActivity;
import com.example.realestateapp.screens.UpdatePropertyActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    Context context;
    ArrayList<Property> list;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PropertyAdapter(Context context, ArrayList<Property> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Property p = list.get(position);
        holder.title.setText(p.getTitle());

        // DELETE using title (because there is no id)
        holder.btnDelete.setOnClickListener(v -> {
            db.collection("Properties").whereEqualTo("title", p.getTitle())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            doc.getReference().delete();
                        }
                        Toast.makeText(context, "Property deleted", Toast.LENGTH_SHORT).show();
                        ((AppCompatActivity)context).finish(); // go back to Home
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                    );
        });

        // UPDATE using title as identifier
        holder.btnUpdate.setOnClickListener(v -> {
            Intent i = new Intent(context, UpdatePropertyActivity.class);
            i.putExtra("propertyTitle", p.getTitle()); // send title for prefill
            context.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        Button btnDelete, btnUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}
