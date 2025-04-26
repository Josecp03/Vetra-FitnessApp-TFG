package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.bumptech.glide.Glide;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Permission cameraPermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        cameraPermission = new Permission(getActivity(), new String[]{android.Manifest.permission.CAMERA} , 1);

        // Inicializa FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

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
            cameraPermission.requestPermission();
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
    }


}