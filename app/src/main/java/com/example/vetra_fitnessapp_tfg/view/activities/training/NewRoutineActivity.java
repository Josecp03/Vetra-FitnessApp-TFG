package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityNewRoutineBinding;
import com.example.vetra_fitnessapp_tfg.databinding.ActivitySignInBinding;

public class NewRoutineActivity extends AppCompatActivity {

    private ActivityNewRoutineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonAddExercise.setOnClickListener(v -> {

            // Crear intent para navegar a ExerciseSelectionActivity
            Intent i = new Intent(NewRoutineActivity.this, ExerciseSelectionActivity.class);

            // Lanzar el intent
            startActivity(i);

            // Aplicar animación de transición
            overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);

        });

    }
}