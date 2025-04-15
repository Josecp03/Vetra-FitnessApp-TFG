package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import androidx.fragment.app.Fragment;
import com.example.vetra_fitnessapp_tfg.R;
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
}