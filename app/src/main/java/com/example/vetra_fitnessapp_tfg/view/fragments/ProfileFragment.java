package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.DialogLogoutBinding;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentProfileBinding;
import com.example.vetra_fitnessapp_tfg.utils.KeyStoreManager;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.example.vetra_fitnessapp_tfg.view.activities.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.material.switchmaterial.SwitchMaterial;


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
    private KeyStoreManager keyStore;
    private boolean notificationsSwitchInitialized = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        keyStore   = new KeyStoreManager();

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
        binding.buttonDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
        binding.profileImage.setOnClickListener(v -> showChangePictureDialog());


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadUserProfile();

        binding.switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!notificationsSwitchInitialized) {
                // Ignorar el primer cambio causado por setChecked() en loadUserProfile()
                return;
            }

            // 1) Guardar en Firestore
            db.collection("users")
                    .document(user.getUid())
                    .update("notifications_enabled", isChecked)
                    .addOnSuccessListener(a -> {
                        String msg = isChecked
                                ? "Notifications enabled"
                                : "Notifications disabled";
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(),
                                    "Error saving notification setting",
                                    Toast.LENGTH_SHORT).show()
                    );

            // 2) Guardar localmente para el Service
            Prefs.setNotificationsEnabled(requireContext(), isChecked);

            // 3) Suscribir / desuscribir de FCM
            String topic = "user_" + user.getUid();
            if (isChecked) {
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(topic)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful())
                                Log.e("ProfileFragment",
                                        "FCM subscribe failed",
                                        task.getException());
                        });
            } else {
                FirebaseMessaging.getInstance()
                        .unsubscribeFromTopic(topic)
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful())
                                Log.e("ProfileFragment",
                                        "FCM unsubscribe failed",
                                        task.getException());
                        });
            }
        });
    }

    private void showDeleteAccountDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        MaterialButton btnConfirm = dialogView.findViewById(R.id.buttonDeleteAccountConfirm);
        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            deleteUserAccountCascade();
        });

        dialog.show();
    }


    private void loadUserProfile() {
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    // 1) Foto de perfil
                    Glide.with(this)
                            .load(doc.getString("profile_photo_url"))
                            .placeholder(R.drawable.ic_profile_picture)
                            .into(binding.profileImage);

                    // 2) Username (desencriptar)
                    String encryptedUser = doc.getString("username");
                    String decryptedUser = keyStore.decrypt(encryptedUser);
                    binding.editTextUserName.setText(decryptedUser != null ? decryptedUser : "");

                    // 3) Edad, peso, altura, calorías
                    Long age     = doc.getLong("age");
                    Double weight= doc.getDouble("weight");
                    Long height  = doc.getLong("height");
                    Long cal     = doc.getLong("user_calories");
                    binding.editTextAge.setText(age     != null ? String.valueOf(age)     : "");
                    binding.editTextWeight.setText(weight!= null ? String.valueOf(weight) : "");
                    binding.editTextHeight.setText(height != null ? String.valueOf(height) : "");
                    binding.editTextCalorieGoal.setText(cal  != null ? String.valueOf(cal)    : "");

                    // 4) Estado del switch de notificaciones (Firestore → UI)
                    Boolean notifEnabled = doc.getBoolean("notifications_enabled");
                    boolean isOn = notifEnabled == null ? true : notifEnabled;
                    binding.switchNotifications.setChecked(isOn);

                    // 5) Guardar estado localmente
                    Prefs.setNotificationsEnabled(requireContext(), isOn);

                    // 6) A partir de ahora, listener responderá solo a cambios manuales
                    notificationsSwitchInitialized = true;
                })
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error reading profile", e);
                    // Aunque falle, liberar el flag para no bloquear futuros cambios
                    notificationsSwitchInitialized = true;
                });
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

        // Encriptar y guardar username
        String plainUser     = binding.editTextUserName.getText().toString().trim();
        String encryptedUser = keyStore.encrypt(plainUser);
        updates.put("username", encryptedUser);

        // Campos restantes
        updates.put("age",           Integer.parseInt(binding.editTextAge.getText().toString().trim()));
        updates.put("height",        Integer.parseInt(binding.editTextHeight.getText().toString().trim()));
        updates.put("weight",        Double.parseDouble(binding.editTextWeight.getText().toString().trim()));
        updates.put("user_calories", Integer.parseInt(binding.editTextCalorieGoal.getText().toString().trim()));

        // Estado de notificaciones
        updates.put("notifications_enabled", binding.switchNotifications.isChecked());

        // Resto de tu lógica de subida de foto y finalizeSave(updates,…)
        if (pendingImageUri != null) {
            // … tu código existente para subir foto …
        } else if (!shouldDeletePhoto) {
            finalizeSave(updates, null);
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
                    if (shouldDeletePhoto && photoRef != null) {
                        photoRef.delete();
                    }
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

    private void deleteProfilePhoto() {
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
        String user = binding.editTextUserName.getText().toString().trim();
        String ageStr = binding.editTextAge.getText().toString().trim();
        String weightStr = binding.editTextWeight.getText().toString().trim();
        String heightStr = binding.editTextHeight.getText().toString().trim();
        String calStr = binding.editTextCalorieGoal.getText().toString().trim();

        if (user.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty()
                || heightStr.isEmpty() || calStr.isEmpty()) {
            Toast.makeText(getContext(), "Please complete all fields before saving", Toast.LENGTH_SHORT).show();
            return false;
        }

        int age = Integer.parseInt(ageStr);
        int height = Integer.parseInt(heightStr);
        double weight = Double.parseDouble(weightStr);
        int calories = Integer.parseInt(calStr);

        if (age < 12 || age > 120) {
            Toast.makeText(getContext(), "Age must be between 12 and 120 years", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (height < 120 || height > 250) {
            Toast.makeText(getContext(), "Height must be between 120 and 250 cm", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (weight < 20 || weight > 300) {
            Toast.makeText(getContext(), "Weight must be between 20 and 300 kg", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (calories < 100 || calories > 30000) {
            Toast.makeText(getContext(), "Calorie goal must be between 100 and 30000", Toast.LENGTH_SHORT).show();
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

    private void deleteUserAccountCascade() {
        ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Deleting your account…");
        progress.setCancelable(false);
        progress.show();

        String uid = user.getUid();
        DocumentReference userDoc = db.collection("users").document(uid);

        // Eliminar subcolecciones y el documento principal
        List<String> subCollections = Arrays.asList("exerciseHistory", "routines");
        List<Task<?>> deleteTasks = new ArrayList<>();

        // Primero, borramos cada documento de cada subcolección
        for (String sub : subCollections) {
            CollectionReference colRef = userDoc.collection(sub);
            Task<QuerySnapshot> queryTask = colRef.get()
                    .addOnSuccessListener(qs -> {
                        for (DocumentSnapshot ds : qs.getDocuments()) {
                            deleteTasks.add(ds.getReference().delete());
                        }
                    });
            deleteTasks.add(queryTask);
        }

        // Cuando todas las tareas de borrado de subcolecciones estén lanzadas:
        Tasks.whenAll(deleteTasks)
                .addOnCompleteListener(t1 -> {
                    // Ahora borramos el documento principal
                    userDoc.delete()
                            .addOnSuccessListener(aVoid -> {
                                // Eliminar Storage
                                deleteUserStorage(uid)
                                        .addOnCompleteListener(t2 -> {
                                            // Eliminar Auth
                                            user.delete()
                                                    .addOnCompleteListener(t3 -> {
                                                        progress.dismiss();
                                                        if (t3.isSuccessful()) {
                                                            Intent intent = new Intent(requireActivity(), SignInActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            requireActivity().finish();
                                                        } else {
                                                            Toast.makeText(getContext(),
                                                                    "Error deleting account", Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                        });
                            })
                            .addOnFailureListener(e -> {
                                progress.dismiss();
                                Toast.makeText(getContext(),
                                        "Error deleting Firestore data" + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                });
    }

    private Task<Void> deleteUserStorage(String uid) {
        // Referencia a la foto de perfil
        StorageReference photoRef = storageRef.child("profile_photos/" + uid + ".jpg");
        // Referencia a la carpeta de usuario
        StorageReference userFolder = storageRef.child("users/" + uid);

        // Borrar foto de perfil
        Task<Void> deletePhoto = photoRef.delete();

        // Listar y borrar el resto de ficheros
        Task<ListResult> listTask = userFolder.listAll();
        Task<Void> deleteContent = listTask.continueWithTask(task -> {
            if (!task.isSuccessful()) throw Objects.requireNonNull(task.getException());
            List<Task<Void>> removes = new ArrayList<>();
            for (StorageReference item : task.getResult().getItems()) {
                removes.add(item.delete());
            }
            return Tasks.whenAll(removes);
        });

        // Esperar a foto + contenido
        return Tasks.whenAll(deletePhoto, deleteContent);
    }





}
