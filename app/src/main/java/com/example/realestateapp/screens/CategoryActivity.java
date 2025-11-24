package com.example.realestateapp.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestateapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CategoryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText categoryTitle;
    private ImageView categoryImageView;
    private Button chooseImageBtn, addCategoryBtn;
    private ProgressBar progressBar;

    private Uri imageUri;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryTitle = findViewById(R.id.category_title);
        categoryImageView = findViewById(R.id.category_image);
        chooseImageBtn = findViewById(R.id.btn_choose_image);
        addCategoryBtn = findViewById(R.id.btn_add_category);
        progressBar = findViewById(R.id.progress_bar);

        db = FirebaseFirestore.getInstance();

        chooseImageBtn.setOnClickListener(v -> openFileChooser());
        addCategoryBtn.setOnClickListener(v -> addCategory());
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Category Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri originalUri = data.getData();
            imageUri = compressAndSaveImage(originalUri);
            categoryImageView.setImageURI(imageUri);
        }
    }

    // Compress and resize image before saving to internal storage
    private Uri compressAndSaveImage(Uri originalUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(originalUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            // Resize
            int maxSize = 800;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float ratio = (float) width / height;
            if (ratio > 1) {
                width = maxSize;
                height = (int) (width / ratio);
            } else {
                height = maxSize;
                width = (int) (height * ratio);
            }
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

            // Save to internal storage
            File file = new File(getFilesDir(), "category_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
            fos.flush();
            fos.close();

            return Uri.fromFile(file);

        } catch (Exception e) {
            e.printStackTrace();
            return originalUri; // fallback if compression fails
        }
    }

    private void addCategory() {
        String title = categoryTitle.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            categoryTitle.setError("Title required");
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Save local image path in Firestore
        Map<String, Object> category = new HashMap<>();
        category.put("title", title);
        category.put("imagePath", imageUri.getPath());

        db.collection("Category")
                .add(category)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CategoryActivity.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                    categoryTitle.setText("");
                    categoryImageView.setImageResource(android.R.color.transparent);
                    imageUri = null;
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(CategoryActivity.this, "Error adding category", Toast.LENGTH_SHORT).show();
                });
    }
}
