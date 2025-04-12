package com.example.vetra_fitnessapp_tfg.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.vetra_fitnessapp_tfg.databinding.ActivitySignInBinding;


import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    // Atributos
    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Listener para el botón de SignIn
        binding.buttonSignIn.setOnClickListener(v -> {
            Toast.makeText(SignInActivity.this, "Login pressed", Toast.LENGTH_SHORT).show();
        });

        // Listener para el botón de Google
        binding.buttonGoogle.setOnClickListener(v -> {
            Toast.makeText(SignInActivity.this, "Google pressed", Toast.LENGTH_SHORT).show();
        });

        // Listener para el texto "Forget your password?"
        binding.textViewForgetPassword.setOnClickListener(v -> showForgotPasswordDialog());

        // Listener para el texto "Sign up here"
        binding.textViewSignUp.setOnClickListener(v -> {

            // Crear intent para navegar a SignUpActivity
            Intent i = new Intent(SignInActivity.this, SignUpActivity.class);

            // Lanzar el intent
            startActivity(i);

            // Aplicar animación de transición
            overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);

        });

    }


    private void showForgotPasswordDialog() {

        // Inflar la vista personalizada del diálogo desde el layout XML
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.dialog_recover_password, null);

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

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