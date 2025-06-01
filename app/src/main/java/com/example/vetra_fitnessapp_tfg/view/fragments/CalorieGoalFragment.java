package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vetra_fitnessapp_tfg.utils.StepValidator;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentCalorieGoalBinding;

/**
 * Fragmento para establecer el objetivo calórico diario del usuario.
 * Forma parte del proceso de configuración inicial y valida
 * que el objetivo esté dentro de rangos saludables.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class CalorieGoalFragment extends Fragment implements StepValidator {

    private FragmentCalorieGoalBinding binding;

    /**
     * Crea y configura la vista del fragmento.
     *
     * @param inflater Inflater para crear la vista
     * @param container Contenedor padre
     * @param savedInstanceState Estado guardado
     * @return Vista configurada del fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalorieGoalBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    /**
     * Obtiene el objetivo calórico ingresado por el usuario.
     *
     * @return Objetivo calórico diario
     */
    public int getCalorieGoalValue() {
        return Integer.parseInt(binding.editTextCalories.getText().toString().trim());
    }

    /**
     * Valida que el objetivo calórico esté completo y dentro de rangos válidos.
     *
     * @return true si el campo es válido, false en caso contrario
     */
    @Override
    public boolean validateFields() {
        String cStr = binding.editTextCalories.getText().toString().trim();

        if (cStr.isEmpty()) {
            Toast.makeText(getContext(), "Please complete all fields to continue", Toast.LENGTH_SHORT).show();
            return false;
        }

        int calories = Integer.parseInt(cStr);

        if (calories < 100) {
            Toast.makeText(getContext(), "Minimum calorie goal is 100", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (calories > 30000) {
            Toast.makeText(getContext(), "Please enter a reasonable calorie amount (≤30000)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
