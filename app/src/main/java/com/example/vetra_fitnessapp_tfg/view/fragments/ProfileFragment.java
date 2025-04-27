package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.DialogLogoutBinding;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentProfileBinding;
import com.example.vetra_fitnessapp_tfg.utils.Permission;
import com.example.vetra_fitnessapp_tfg.view.activities.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.app.Activity;



public class ProfileFragment extends Fragment {

    private static final int RC_CAMERA = 1001;
    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Permission cameraPermission;
    private Uri cameraImageUri;
    private StorageReference storageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        cameraPermission = new Permission(getActivity(), new String[]{android.Manifest.permission.CAMERA} , RC_CAMERA);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Inicializa GoogleSignInClient
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(requireContext(), googleOptions);

        // Listener para el botón de logut
        binding.buttonLogOut.setOnClickListener(v -> showLogoutDialog());

        // Listener para el botón de cambiar foto
        binding.changePictureText.setOnClickListener(v -> showChangePictureDialog());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Leer perfil de Firestore
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {

                    // Mostrar los datos del usuario en la interfaz
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

                // Manejar errores
                .addOnFailureListener(e -> {
                    Log.e("ProfileFragment", "Error leyendo perfil", e);
                });

        // Listener para guardar los cambios
        binding.buttonSaveChanges.setOnClickListener(v -> {

            // Comprobar que los campos no estén vacíos
            if (!validateProfileFields()) {
                return;
            }

            // Guardar en variables los datos de la interfaz
            String newUserName = binding.editTextUserName.getText().toString().trim();
            String ageStr = binding.editTextAge.getText().toString().trim();
            String heightStr = binding.editTextHeight.getText().toString().trim();
            String weightStr = binding.editTextWeight.getText().toString().trim();
            String caloriesStr = binding.editTextCalorieGoal.getText().toString().trim();

            // Preparar el Map con los campos a actualizar
            Map<String,Object> updates = new HashMap<>();
            updates.put("username", newUserName);
            updates.put("age", Integer.parseInt(ageStr));
            updates.put("height", Integer.parseInt(heightStr));
            updates.put("weight", Double.parseDouble(weightStr));
            updates.put("user_calories", Integer.parseInt(caloriesStr));

            // Lanzar la actualización en Firestore
            db.collection("users")
                    .document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {

                        // Mostrar mensaje de confirmación
                        Toast.makeText(getContext(), "Profile updated!", Toast.LENGTH_SHORT).show();

                    })

                    // Manejar errores
                    .addOnFailureListener(e -> {
                        Log.e("ProfileFragment", "Error actualizando perfil", e);
                    });

        });

    }

    private void showLogoutDialog() {

        // Inflar el binding del diálogo
        DialogLogoutBinding dialogBinding = DialogLogoutBinding.inflate(getLayoutInflater());

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme
        );

        // Asignar la vista al diálogo
        bottomSheetDialog.setContentView(dialogBinding.getRoot());

        // Ajustar fondo transparente
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Referenciar al botón Log out dentro del diálogo
        dialogBinding.buttonLogoutConfirm.setOnClickListener(v -> {

            // Lógica para cerrar sesión
            logout();

        });

        // Mostrar el diálogo
        bottomSheetDialog.show();

    }


    private void showChangePictureDialog() {

        // Inflar la vista del diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_picture, null);

        // Crear el BottomSheetDialog
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        // Asignar la vista inflada al diálogo
        dialog.setContentView(dialogView);

        // Ajustar fondo transparente
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Acciones del diálogo
        MaterialButton buttonTakePhoto = dialogView.findViewById(R.id.buttonTakePhoto);
        buttonTakePhoto.setOnClickListener(v -> {
            dialog.dismiss();

            // Comprobar si ya tiene le permiso
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                cameraPermission.requestPermission();
            }

        });

        MaterialButton buttonSelectGallery = dialogView.findViewById(R.id.buttonSelectGallery);
        buttonSelectGallery.setOnClickListener(v -> dialog.dismiss());

        MaterialButton buttonInsertLink = dialogView.findViewById(R.id.buttonInsertLink);
        buttonInsertLink.setOnClickListener(v -> {
            dialog.dismiss();
            showExternalLinkDialog();
        });

        MaterialButton buttonGenerateAi = dialogView.findViewById(R.id.buttonGenerateAi);
        buttonGenerateAi.setOnClickListener(v -> {
            dialog.dismiss();
            showPromptAIDialog();
        });

        MaterialButton buttonDeletePhoto = dialogView.findViewById(R.id.buttonDeletePhoto);
        buttonDeletePhoto.setOnClickListener(v -> dialog.dismiss());

        // Mostrar el diálogo
        dialog.show();

    }

    private void showExternalLinkDialog() {

        // Inflar la vista personalizada del diálogo desde el layout XML
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.dialog_external_link, null);

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        // Asignar la vista inflada al diálogo
        bottomSheetDialog.setContentView(dialogView);

        // Establecer fondo transparente para que los bordes sean redondeados o personalizados
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Mostrar el diálogo
        bottomSheetDialog.show();

        // Obtener la vista del bottom sheet
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        // Forzar que ocupe toda la altura de la pantalla
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    private void showPromptAIDialog() {

        // Inflar la vista personalizada del diálogo desde el layout XML
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.dialog_ai, null);

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);

        // Asignar la vista inflada al diálogo
        bottomSheetDialog.setContentView(dialogView);

        // Establecer fondo transparente para que los bordes sean redondeados o personalizados
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Mostrar el diálogo
        bottomSheetDialog.show();

        // Obtener la vista del bottom sheet
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        // Forzar que ocupe toda la altura de la pantalla
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    private void logout() {

        // Cerrar sesión de Firebase
        mAuth.signOut();

        // Cerrar sesión de Google
        googleClient.signOut().addOnCompleteListener(requireActivity(), task -> {

            // Cerrar la actividad y volver a la pantalla de inicio de sesión
            Intent intent = new Intent(requireActivity(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();

        });

    }

    private boolean validateProfileFields() {

        // Guardar en variables los datos de la interfaz
        String fn = binding.editTextUserName.getText().toString().trim();
        String age = binding.editTextAge.getText().toString().trim();
        String weight = binding.editTextWeight.getText().toString().trim();
        String height = binding.editTextHeight.getText().toString().trim();
        String calories = binding.editTextCalorieGoal.getText().toString().trim();

        // Comprobar si los campos están vacíos
        if (fn.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty() || calories.isEmpty()) {

            // Mostrar mensaje de error al usuario
            Toast.makeText(getContext(), "Please complete all fields before saving", Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permission.handlePermissionsResult(requireActivity(), requestCode, permissions, grantResults);

        // Si era la petición de cámara y el usuario concedió todos los permisos, abrimos cámara
        if (requestCode == RC_CAMERA) {
            boolean allGranted = true;
            for (int r : grantResults) {
                if (r != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                openCamera();
            }
        }

    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile;
        try {
            photoFile = createTempImageFile();
        } catch (IOException e) {
            Log.e("ProfileFragment", "Error creando archivo de imagen", e);
            return;
        }

        cameraImageUri = FileProvider.getUriForFile(requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                photoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(cameraIntent, RC_CAMERA);
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CAMERA && resultCode == Activity.RESULT_OK && cameraImageUri != null) {
            uploadImageToFirebase(cameraImageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageToFirebase(Uri uri) {
        StorageReference photoRef = storageRef.child("profile_photos/" + user.getUid() + ".jpg");
        photoRef.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return photoRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String url = task.getResult().toString();
                        updateProfilePhotoUrl(url);
                    } else {
                        Toast.makeText(getContext(), "Error subiendo foto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfilePhotoUrl(String url) {
        Map<String,Object> upd = new HashMap<>();
        upd.put("profile_photo_url", url);
        db.collection("users").document(user.getUid())
                .update(upd)
                .addOnSuccessListener(a -> {
                    Glide.with(this).load(url).into(binding.profileImage);
                    Toast.makeText(getContext(), "Foto actualizada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error guardando URL", Toast.LENGTH_SHORT).show();
                });
    }

}