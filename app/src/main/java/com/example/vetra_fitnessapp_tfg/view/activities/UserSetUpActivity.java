package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityUserSetUpBinding;
import com.example.vetra_fitnessapp_tfg.view.fragments.BodyMetricsFragment;
import com.example.vetra_fitnessapp_tfg.view.fragments.CalorieGoalFragment;
import com.example.vetra_fitnessapp_tfg.view.fragments.PersonalInfoFragment;

public class UserSetUpActivity extends AppCompatActivity {

    // Atributos
    private ActivityUserSetUpBinding binding;
    private int currentStep = 0;
    private final Fragment[] steps = new Fragment[] {
            new PersonalInfoFragment(),
            new BodyMetricsFragment(),
            new CalorieGoalFragment()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSetUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Cargar el primer fragmento
        showStep(currentStep);

        // Llamar al métood para configurar el menú de navegación
        setupNavigationButtons();

    }


    private void setupNavigationButtons() {

        // Listener para el botón de volver
        binding.buttonBack.setOnClickListener(v -> {

            // Comprobar si estamos en el primer fragmento
            if (currentStep == 0) {

                // Crear y lanzar la actividad para navegar a SignInActivity
                startActivity(new Intent(this, SignInActivity.class));

                // Aplicar animación de transición
                overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);

            } else {

                // Actualizar el fragmento actual
                currentStep--;
                showStep(currentStep);

            }
        });

        // Listener para el botón de siguiente
        binding.buttonNext.setOnClickListener(v -> {

            // Comprobar si no estamos en el último fragmento
            if (currentStep < steps.length - 1) {

                // Actualizar el fragmento actual
                currentStep++;
                showStep(currentStep);

            } else {

                // Crear el intent para navegar a MainActivity
                Intent intent = new Intent(this, MainActivity.class);

                // Limpiar el historial de actividades anteriores para que no se pueda volver
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Lanzar el intent
                startActivity(intent);

                // Aplicar animación de transición
                overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);

            }
        });
    }

    private void showStep(int stepIndex) {

        // Cargar el fragmento correspondiente al índice
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, steps[stepIndex])
                .commit();

        // Actualizar el color del primer fragmento
        if (stepIndex >= 0) {
            binding.step1.setBackgroundResource(R.color.white);
        } else {
            binding.step1.setBackgroundResource(R.color.dark_gray);
        }

        // Actualizar el color del segundo fragmento
        if (stepIndex >= 1) {
            binding.step2.setBackgroundResource(R.color.white);
        } else {
            binding.step2.setBackgroundResource(R.color.dark_gray);
        }

        // Actualizar el color del tercer fragmento
        if (stepIndex >= 2) {
            binding.step3.setBackgroundResource(R.color.white);
        } else {
            binding.step3.setBackgroundResource(R.color.dark_gray);
        }

        // Cambiar el texto del botón si es el último paso
        if (stepIndex == steps.length - 1) {
            binding.buttonNext.setText("Finish");
        } else {
            binding.buttonNext.setText("Next");
        }

    }



}