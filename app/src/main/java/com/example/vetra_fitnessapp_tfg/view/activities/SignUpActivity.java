package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Listener para el botón de volver
        binding.buttonBack.setOnClickListener(v -> {

            // Crear el intent para navegar a SignInActivity
            Intent i = new Intent(SignUpActivity.this, SignInActivity.class);

            // Lanzar el intent
            startActivity(i);

            // Aplicar animación de transición
            overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);

        });

        // Listener para el botón de SignUp
        binding.buttonSignUp.setOnClickListener(v ->{

            // Crear el intent para navegar a UserSetUpActivity
            Intent i = new Intent(SignUpActivity.this, UserSetUpActivity.class);

            // Lanzar el intent
            startActivity(i);

            // Aplicar animación de transición
            overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);

        });

    }
}