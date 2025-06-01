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

/**
 * Fragmento para capturar la información personal básica del usuario.
 * Incluye nombre, apellido, edad y selección de género.
 * Forma parte del proceso de configuración inicial de la aplicación.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class PersonalInfoFragment extends Fragment implements StepValidator {

    /**
     * Binding para acceder a las vistas del fragmento.
     */
    private FragmentPersonalInfoBinding binding;

    /**
     * Género seleccionado por el usuario.
     */
    private String selectedGender = "man";

    /**
     * Crea y configura la vista del fragmento.
     *
     * @param inflater Inflater para crear la vista
     * @param container Contenedor padre
     * @param savedInstanceState Estado guardado
     * @return Vista configurada del fragmento
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        selectGender("man");
        binding.optionMan.setOnClickListener(v -> selectGender("man"));
        binding.optionWoman.setOnClickListener(v -> selectGender("woman"));
        return view;
    }

    /**
     * Actualiza la interfaz según el género seleccionado.
     *
     * @param gender Género seleccionado ("man" o "woman")
     */
    private void selectGender(String gender) {
        if (gender.equals("man")) {
            binding.checkMan.setVisibility(View.VISIBLE);
            binding.checkWoman.setVisibility(View.GONE);
            selectedGender = "man";
        } else if (gender.equals("woman")) {
            binding.checkMan.setVisibility(View.GONE);
            binding.checkWoman.setVisibility(View.VISIBLE);
            selectedGender = "woman";
        }
    }

    /**
     * Valida que todos los campos estén completos y la edad sea válida.
     *
     * @return true si todos los campos son válidos, false en caso contrario
     */
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

    /**
     * Obtiene el nombre ingresado por el usuario.
     *
     * @return Nombre del usuario
     */
    public String getFirstName() {
        return binding.editTextFirstName.getText().toString().trim();
    }

    /**
     * Obtiene el apellido ingresado por el usuario.
     *
     * @return Apellido del usuario
     */
    public String getLastName() {
        return binding.editTextSecondName.getText().toString().trim();
    }

    /**
     * Obtiene la edad ingresada por el usuario.
     *
     * @return Edad del usuario, o null si no es válida
     */
    public Integer getAgeValue() {
        try {
            return Integer.parseInt(binding.editTextAge.getText().toString().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Obtiene el género seleccionado por el usuario.
     *
     * @return Género seleccionado ("man" o "woman")
     */
    public String getGender() {
        return selectedGender;
    }

}
