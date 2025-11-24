package com.example.realestateapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.realestateapp.R;
import com.example.realestateapp.screens.AddPropertyActivity;
import com.example.realestateapp.screens.CategoryActivity;
import com.example.realestateapp.screens.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private CircleImageView userProfile;
    private EditText userName, userEmail;
    private AppCompatButton updateButton, signOutButton;
    private Button addProperty, manageCategoriesBtn;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String userId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        userProfile = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        updateButton = view.findViewById(R.id.update_button);
        signOutButton = view.findViewById(R.id.sign_out);
        addProperty = view.findViewById(R.id.add_property);
        manageCategoriesBtn = view.findViewById(R.id.btn_manage_categories);

        fetchUserData();

        // Profile image picker
        userProfile.setOnClickListener(v -> chooseImage());

        // Update user data
        updateButton.setOnClickListener(v -> updateUserData());

        // Sign out
        signOutButton.setOnClickListener(v -> signOut());

        // Add property
        addProperty.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddPropertyActivity.class);
            startActivity(intent);
        });

        // Manage categories
        manageCategoriesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String email = documentSnapshot.getString("email");
                    String imageUrl = documentSnapshot.getString("imageUrl");

                    userName.setText(name);
                    userEmail.setText(email);

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(requireContext()).load(imageUrl).into(userProfile);
                    }
                } else {
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                Log.e("AccountFragment", "Error fetching user data", e);
                Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            userProfile.setImageURI(imageUri);
        }
    }

    private void updateUserData() {
        Map<String, Object> newData = new HashMap<>();
        DocumentReference userRef = db.collection("users").document(userId);

        String newName = userName.getText().toString().trim();
        String newEmail = userEmail.getText().toString().trim();
        if (!newName.isEmpty()) newData.put("name", newName);
        if (!newEmail.isEmpty()) newData.put("email", newEmail);

        if (imageUri != null) {
            StorageReference profileImageRef = storageRef.child("profile_images/" + userId + ".jpg");
            profileImageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                newData.put("imageUrl", uri.toString());
                                userRef.set(newData, SetOptions.merge())
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show());
                            }).addOnFailureListener(e ->
                                    Toast.makeText(getActivity(), "Failed to get image URL", Toast.LENGTH_SHORT).show()))
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show());
        } else {
            userRef.set(newData, SetOptions.merge())
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show());
        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
