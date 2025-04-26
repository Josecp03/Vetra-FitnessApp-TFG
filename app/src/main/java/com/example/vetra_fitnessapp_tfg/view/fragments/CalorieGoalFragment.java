package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vetra_fitnessapp_tfg.StepValidator;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentCalorieGoalBinding;

public class CalorieGoalFragment extends Fragment implements StepValidator {

    private FragmentCalorieGoalBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalorieGoalBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    /** Llamado desde la Activity al pulsar NEXT */
    public int getCalorieGoalValue() {
        return Integer.parseInt(binding.editTextCalories.getText().toString().trim());
    }

    @Override
    public boolean validateFields() {

        // Guardar los datos en variables temporales
        String c = binding.editTextCalories.getText().toString().trim();

        // Comprobar que los campos no estén vacíos
        if (c.isEmpty()) {
            Toast.makeText(getContext(), "Please complete all fields to continue", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}