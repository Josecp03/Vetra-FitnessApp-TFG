package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vetra_fitnessapp_tfg.utils.StepValidator;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentPersonalInfoBinding;

public class PersonalInfoFragment extends Fragment implements StepValidator {

    private FragmentPersonalInfoBinding binding;
    private String selectedGender = "man";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Establecer un género por defecto
        selectGender("man");

        // Llamar a una función para actuaizar la vista según el género seleccionado
        binding.optionMan.setOnClickListener(v -> selectGender("man"));
        binding.optionWoman.setOnClickListener(v -> selectGender("woman"));

        return view;
    }

    private void selectGender(String gender) {

        // Comprobar el género que fue seleccionado
        if (gender.equals("man")) {

            // Hacer visible la imagen de seleccionado en man
            binding.checkMan.setVisibility(View.VISIBLE);

            // Ocultar la imagen de seleccionado en woman
            binding.checkWoman.setVisibility(View.GONE);

            // Actualizar variable selectgender
            selectedGender = "man";

        } else if (gender.equals("woman")) {

            // Ocultar la imagen de seleccionado e woman
            binding.checkMan.setVisibility(View.GONE);

            // Hacer visible la imagen de seleccionado en woman
            binding.checkWoman.setVisibility(View.VISIBLE);

            // Actualizar variable selectgender
            selectedGender = "woman";

        }

    }

    @Override
    public boolean validateFields() {
        String fn = binding.editTextFirstName.getText().toString().trim();
        String ln = binding.editTextSecondName.getText().toString().trim();
        String ageStr = binding.editTextAge.getText().toString().trim();

        if (fn.isEmpty() || ln.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(getContext(), "Please complete all fields to continue", Toast.LENGTH_SHORT).show();
            return false;
        }

        Integer age = getAgeValue();
        if (age == null || age < 12) {
            Toast.makeText(getContext(), "You must be at least 12 years old", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public String getFirstName() {
        return binding.editTextFirstName.getText().toString().trim();
    }

    public String getLastName() {
        return binding.editTextSecondName.getText().toString().trim();
    }

    public Integer getAgeValue() {
        try {
            return Integer.parseInt(binding.editTextAge.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getGender() {
        return selectedGender;
    }

}