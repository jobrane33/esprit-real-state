package com.example.realestateapp.screens;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.example.realestateapp.R;
import com.example.realestateapp.adapters.PropertyAdapter;
import com.example.realestateapp.model.Property;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListPropertiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private ArrayList<Property> propertyList;
    private FirebaseFirestore db;

    // Activity Result Launcher to handle update results
    private final ActivityResultLauncher<Intent> updatePropertyLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Reload the list when returning from update
                    loadProperties();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_properties);

        recyclerView = findViewById(R.id.recyclerViewProperties);
        propertyList = new ArrayList<>();
        adapter = new PropertyAdapter(this, propertyList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Set update click listener
        adapter.setOnUpdateClickListener(property -> {
            Intent intent = new Intent(this, UpdatePropertyActivity.class);
            intent.putExtra("propertyTitle", property.getTitle());
            updatePropertyLauncher.launch(intent);
        });

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
                            String imageBase64 = doc.getString("imageBase64");

                            propertyList.add(new Property(title, location, price, category, imageBase64));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load properties", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
