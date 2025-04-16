package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.DialogChangePictureBinding;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentProfileBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Listener para el botón de logut
        binding.buttonLogOut.setOnClickListener(v -> showLogoutDialog());

        // Listener para el botón de cambiar foto
        binding.changePictureText.setOnClickListener(v -> showChangePictureDialog());

        return view;
    }

    private void showLogoutDialog() {

        // Inflar la vista del diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout, null);

        // Crear el BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
        );

        // Asignar la vista inflada al diálogo
        bottomSheetDialog.setContentView(dialogView);

        // Ajustar fondo transparente
        Objects.requireNonNull(bottomSheetDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        // Referenciar al botón Log out dentro del diálogo
        MaterialButton buttonLogoutConfirm = dialogView.findViewById(R.id.buttonLogoutConfirm);
        buttonLogoutConfirm.setOnClickListener(v -> {

            // Lógica para cerrar sesión

            // Cerrar el diálogo
            bottomSheetDialog.dismiss();
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

}