package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.DialogLogoutBinding;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentProfileBinding;
import com.example.vetra_fitnessapp_tfg.view.activities.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class ProfileFragment extends Fragment {

    private static final int RC_CAMERA = 1001;
    private static final int RC_GALLERY = 1002;

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Uri cameraImageUri;
    private Uri pendingImageUri = null;
    private StorageReference storageRef;
    private boolean shouldDeletePhoto = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Google SignIn
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(requireContext(), googleOptions);

        // Button listeners
        binding.buttonLogOut.setOnClickListener(v -> showLogoutDialog());
        binding.changePictureText.setOnClickListener(v -> showChangePictureDialog());
        binding.buttonSaveChanges.setOnClickListener(v -> saveProfileChanges());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadUserProfile();
    }

    private void loadUserProfile() {
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    Glide.with(this)
                            .load(doc.getString("profile_photo_url"))
                            .placeholder(R.drawable.ic_profile_picture)
                            .into(binding.profileImage);
                    binding.editTextUserName.setText(doc.getString("username"));
                    binding.editTextAge.setText(String.valueOf(doc.getLong("age")));
                    binding.editTextWeight.setText(String.valueOf(doc.getDouble("weight")));
                    binding.editTextHeight.setText(String.valueOf(doc.getLong("height")));
                    binding.editTextCalorieGoal.setText(String.valueOf(doc.getLong("user_calories")));
                })
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error reading profile", e));
    }

    private void showLogoutDialog() {
        DialogLogoutBinding dialogBinding = DialogLogoutBinding.inflate(getLayoutInflater());
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        dialog.setContentView(dialogBinding.getRoot());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialogBinding.buttonLogoutConfirm.setOnClickListener(v -> logout());
        dialog.show();
    }

    private void showChangePictureDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_picture, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        MaterialButton btnCamera = dialogView.findViewById(R.id.buttonTakePhoto);
        btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                requestPermissions(
                        new String[]{android.Manifest.permission.CAMERA}, RC_CAMERA);
            }
        });

        MaterialButton btnGallery = dialogView.findViewById(R.id.buttonSelectGallery);
        btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            String perm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? android.Manifest.permission.READ_MEDIA_IMAGES
                    : android.Manifest.permission.READ_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(requireContext(), perm)
                    == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestPermissions(new String[]{perm}, RC_GALLERY);
            }
        });

        MaterialButton btnDelete = dialogView.findViewById(R.id.buttonDeletePhoto);
        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            deleteProfilePhoto();
        });

        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allGranted = true;
        for (int r : grantResults) {
            if (r != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (requestCode == RC_CAMERA) {
            if (allGranted) {
                openCamera();
            } else if (!shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                Toast.makeText(requireContext(),
                        "Enable app permissions in Settings",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireContext().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else if (requestCode == RC_GALLERY) {
            String perm = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    ? android.Manifest.permission.READ_MEDIA_IMAGES
                    : android.Manifest.permission.READ_EXTERNAL_STORAGE;
            if (allGranted) {
                openGallery();
            } else if (!shouldShowRequestPermissionRationale(perm)) {
                Toast.makeText(requireContext(),
                        "Enable app permissions in Settings",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireContext().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) == null) {
            Toast.makeText(requireContext(), "No camera app found", Toast.LENGTH_SHORT).show();
            return;
        }
        File photoFile;
        try {
            photoFile = createTempImageFile();
        } catch (IOException e) {
            Log.e("ProfileFragment", "Error creating file", e);
            Toast.makeText(requireContext(), "Cannot create image file", Toast.LENGTH_SHORT).show();
            return;
        }
        cameraImageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, RC_CAMERA);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(intent, RC_GALLERY);
        } else {
            Toast.makeText(requireContext(), "No gallery app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CAMERA && resultCode == Activity.RESULT_OK) {
            if (cameraImageUri != null) {
                pendingImageUri = cameraImageUri;
                Glide.with(this)
                        .load(pendingImageUri)
                        .centerCrop()
                        .into(binding.profileImage);
            } else if (data != null && data.getExtras() != null) {
                Bitmap thumb = data.getExtras().getParcelable("data");
                if (thumb != null) {
                    binding.profileImage.setImageBitmap(thumb);
                    pendingImageUri = null;
                }
            }
        } else if (requestCode == RC_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                pendingImageUri = uri;
                Glide.with(this)
                        .load(pendingImageUri)
                        .centerCrop()
                        .into(binding.profileImage);
            }
        }
    }

    private void saveProfileChanges() {
        if (!validateProfileFields()) return;

        Map<String,Object> updates = new HashMap<>();
        updates.put("username", binding.editTextUserName.getText().toString().trim());
        updates.put("age", Integer.parseInt(binding.editTextAge.getText().toString().trim()));
        updates.put("height", Integer.parseInt(binding.editTextHeight.getText().toString().trim()));
        updates.put("weight", Double.parseDouble(binding.editTextWeight.getText().toString().trim()));
        updates.put("user_calories", Integer.parseInt(binding.editTextCalorieGoal.getText().toString().trim()));

        // Caso 1: hay nueva foto
        if (pendingImageUri != null) {
            // Como antes: compressImage(), putBytes(), getDownloadUrl()…
            try {
                byte[] compressed = compressImage(pendingImageUri);
                StorageReference photoRef = storageRef.child("profile_photos/" + user.getUid() + ".jpg");

                Toast.makeText(requireContext(), "Uploading photo...", Toast.LENGTH_SHORT).show();
                binding.buttonSaveChanges.setEnabled(false);

                photoRef.putBytes(compressed)
                        .continueWithTask(task -> {
                            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
                            return photoRef.getDownloadUrl();
                        })
                        .addOnSuccessListener(downloadUri -> {
                            updates.put("profile_photo_url", downloadUri.toString());
                            pendingImageUri = null;
                            shouldDeletePhoto = false;  // ya no eliminar
                            finalizeSave(updates, photoRef);
                        })
                        .addOnFailureListener(e -> {
                            binding.buttonSaveChanges.setEnabled(true);
                            Toast.makeText(requireContext(), "Error uploading photo", Toast.LENGTH_SHORT).show();
                            Log.e("ProfileFragment", "Upload failed", e);
                        });

            } catch (IOException e) {
                Log.e("ProfileFragment", "Error compressing image", e);
                Toast.makeText(requireContext(), "Could not process image", Toast.LENGTH_SHORT).show();
            }

            // Caso 2: ni nueva foto, ni borrado
        } else if (!shouldDeletePhoto) {
            finalizeSave(updates, null);

            // Caso 3: solo borrar foto
        } else {
            updates.put("profile_photo_url", null);
            binding.buttonSaveChanges.setEnabled(false);
            finalizeSave(updates, storageRef.child("profile_photos/" + user.getUid() + ".jpg"));
        }
    }

    private void finalizeSave(Map<String,Object> updates, @Nullable StorageReference photoRef) {
        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Si marca borrado: lo hacemos también en Storage
                    if (shouldDeletePhoto && photoRef != null) {
                        photoRef.delete();
                    }
                    // Si el Fragment sigue activo, actualizamos UI y toast
                    if (isAdded()) {
                        binding.buttonSaveChanges.setEnabled(true);
                        Toast.makeText(getContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
                    }
                    shouldDeletePhoto = false;
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        binding.buttonSaveChanges.setEnabled(true);
                        Toast.makeText(getContext(), "Error saving changes", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("ProfileFragment", "Error updating profile", e);
                });
    }


    private void applyFirestoreUpdates(Map<String, Object> updates) {
        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Toast de "Profile updated!" eliminado
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error updating profile", e);
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void deleteProfilePhoto() {
        // Marcamos para borrar al guardar, actualizamos UI
        shouldDeletePhoto = true;
        pendingImageUri = null;
        binding.profileImage.setImageResource(R.drawable.ic_profile_picture);
        Toast.makeText(requireContext(), "Photo will be deleted when you save changes", Toast.LENGTH_SHORT).show();
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private boolean validateProfileFields() {
        if (binding.editTextUserName.getText().toString().trim().isEmpty()
                || binding.editTextAge.getText().toString().trim().isEmpty()
                || binding.editTextWeight.getText().toString().trim().isEmpty()
                || binding.editTextHeight.getText().toString().trim().isEmpty()
                || binding.editTextCalorieGoal.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please complete all fields before saving", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void logout() {
        mAuth.signOut();
        googleClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        InputStream input = requireContext().getContentResolver().openInputStream(selectedImage);
        ExifInterface ei = new ExifInterface(input);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private byte[] compressImage(Uri uri) throws IOException {
        Bitmap original = MediaStore.Images.Media.getBitmap(
                requireContext().getContentResolver(), uri
        );

        // Aplica corrección de orientación
        Bitmap rotated = rotateImageIfRequired(original, uri);

        int targetWidth = 800;
        int targetHeight = rotated.getHeight() * targetWidth / rotated.getWidth();

        Bitmap scaled = Bitmap.createScaledBitmap(rotated, targetWidth, targetHeight, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        return baos.toByteArray();
    }


    private Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
