package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityUserSetUpBinding;

public class UserSetUpActivity extends AppCompatActivity {

    private ActivityUserSetUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSetUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Listener para el botón de volver
        binding.buttonBack.setOnClickListener(v -> {

            // Crear el intent para navegar a SignInActivity
            Intent i = new Intent(UserSetUpActivity.this, SignUpActivity.class);

            // Lanzar el intent
            startActivity(i);

            // Aplicar animación de transición
            overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);

        });


    }
}