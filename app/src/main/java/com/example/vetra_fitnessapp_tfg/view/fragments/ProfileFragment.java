package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
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

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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
        buttonTakePhoto.setOnClickListener(v -> dialog.dismiss());

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


}