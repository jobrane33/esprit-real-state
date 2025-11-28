package com.example.realestateapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import com.bumptech.glide.signature.ObjectKey;
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
import com.example.realestateapp.screens.ListPropertiesActivity;
import com.example.realestateapp.screens.LoginActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private CircleImageView userProfile;
    private EditText userName, userEmail;
    private AppCompatButton updateButton, signOutButton  ;

    private Button addProperty;


    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    private String userId;

    // Firebase
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

        // Inside onViewCreated() method
        storageRef = FirebaseStorage.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userProfile = view.findViewById(R.id.profile_image);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);
        updateButton = view.findViewById(R.id.update_button);
        signOutButton = view.findViewById(R.id.sign_out);
        addProperty = view.findViewById(R.id.add_property);
        AppCompatButton changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        addProperty.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddPropertyActivity.class);
            startActivity(intent);
        });
        Button listPropertiesBtn = view.findViewById(R.id.btn_list_properties);
        listPropertiesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListPropertiesActivity.class);
            startActivity(intent);
        });
        // Fetch user data from Firestore
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
                    if (imageUrl != null) {
                        // Load profile image using Glide
                        Glide.with(requireContext()).load(imageUrl).into(userProfile);
                    }

                } else {
                    // Handle the case where the document doesn't exist
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Handle failure
                Log.e("AccountFragment", "Error fetching user data", e);
                Toast.makeText(getActivity(), "Failed to fetch user data", Toast.LENGTH_SHORT).show();
            });
        }

        // Set click listener for profile image to choose image
        userProfile.setOnClickListener(v -> chooseImage());

        // Update user data on button click
        updateButton.setOnClickListener(v -> updateUserData());

        // Set click listener for sign out button
        signOutButton.setOnClickListener(v -> signOut());

        loadLocalProfileImage();
        // Set click listener for add property button
        //addProperty.setOnClickListener(v -> {
        //Intent intent = new Intent(getActivity(), AddPropertyActivity.class);
        //startActivity(intent);
        //});




    }



    private void signOut() {
        mAuth.signOut();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        // Finish the current activity to prevent the user from navigating back
        getActivity().finish();

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
    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);

        EditText oldPasswordInput = dialogView.findViewById(R.id.old_password);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password);
        EditText confirmPasswordInput = dialogView.findViewById(R.id.confirm_password);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Change Password")
                .setView(dialogView)
                .setPositiveButton("Change", (dialog, which) -> {
                    String oldPassword = oldPasswordInput.getText().toString().trim();
                    String newPassword = newPasswordInput.getText().toString().trim();
                    String confirmPassword = confirmPasswordInput.getText().toString().trim();

                    if (newPassword.isEmpty() || confirmPassword.isEmpty() || oldPassword.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    changeUserPassword(oldPassword, newPassword);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void changeUserPassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "No logged in user", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate user before changing password
        String email = user.getEmail();
        if (email == null) {
            Toast.makeText(getContext(), "User email not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get credential for re-authentication
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {
                    // Re-authentication successful, now update password
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to update password: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateUserData() {
        String newName = userName.getText().toString().trim();
        String newEmail = userEmail.getText().toString().trim();

        DocumentReference userRef = db.collection("users").document(userId);
        Map<String, Object> newData = new HashMap<>();
        newData.put("name", newName);
        newData.put("email", newEmail);

        if (imageUri != null) {
            // Define filename
            String filename = userId + "_profile2.jpg";

            // Delete existing file if exists
            File oldFile = new File(requireContext().getFilesDir(), filename);
            if (oldFile.exists()) {
                boolean deleted = oldFile.delete();
                if (!deleted) {
                    Log.w("AccountFragment", "Old profile image not deleted");
                }
            }

            // Save new image locally
            String savedImagePath = saveImageToInternalStorage(imageUri, filename);

            if (savedImagePath != null) {
                newData.put("localImagePath", savedImagePath);

                userRef.set(newData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("AccountFragment", "Error updating Firestore data", e);
                            Toast.makeText(getActivity(), "Failed to update profile data", Toast.LENGTH_SHORT).show();
                        });
                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to save image locally", Toast.LENGTH_SHORT).show();
            }
        } else {
            userRef.set(newData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("AccountFragment", "Error updating Firestore data", e);
                        Toast.makeText(getActivity(), "Failed to update profile data", Toast.LENGTH_SHORT).show();
                    });
            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLocalProfileImage() {
        String filename = userId + "_profile2.jpg";
        File file = new File(requireContext().getFilesDir(), filename);
        if (file.exists()) {
            // Load the image from the file into your CircleImageView
            long lastModified = file.lastModified();
            Glide.with(requireContext())
                    .load(file)
                    .signature(new ObjectKey(String.valueOf(lastModified)))
                    .into(userProfile);
        }
    }

    private String saveImageToInternalStorage(Uri imageUri, String filename) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            File file = new File(requireContext().getFilesDir(), filename);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
