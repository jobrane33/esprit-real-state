package com.example.realestateapp.screens;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.realestateapp.R;
import com.example.realestateapp.adapters.PropertyAdapter;
import com.example.realestateapp.model.Property;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import android.widget.Toast;
import java.util.ArrayList;

public class ListPropertiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private ArrayList<Property> propertyList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_properties);

        recyclerView = findViewById(R.id.recyclerViewProperties);
        db = FirebaseFirestore.getInstance();
        propertyList = new ArrayList<>();
        adapter = new PropertyAdapter(this, propertyList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadProperties();
    }

    private void loadProperties() {
        db.collection("Properties").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        propertyList.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String title = doc.getString("title");
                            String category = doc.getString("category");
                            String type = doc.getString("type");
                            String price = doc.getString("price");
                            String location = doc.getString("location");
                            String imageUrl = doc.getString("imageUri");

                            propertyList.add(new Property(title, category, type, price, location, imageUrl));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load properties", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
