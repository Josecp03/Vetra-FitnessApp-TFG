package com.example.vetra_fitnessapp_tfg.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.StepValidator;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityUserSetUpBinding;
import com.example.vetra_fitnessapp_tfg.view.fragments.BodyMetricsFragment;
import com.example.vetra_fitnessapp_tfg.view.fragments.CalorieGoalFragment;
import com.example.vetra_fitnessapp_tfg.view.fragments.PersonalInfoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Map;
import java.util.HashMap;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserSetUpActivity extends AppCompatActivity {

    // Atributos
    private ActivityUserSetUpBinding binding;
    private int currentStep = 0;
    private final Fragment[] steps = new Fragment[] {
            new PersonalInfoFragment(),
            new BodyMetricsFragment(),
            new CalorieGoalFragment()
    };

    // Datos temporales
    public String firstName, lastName, gender;
    public int age, height, calorieGoal;
    public double weight;

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

            // Solo retrocedemos si no estamos en el primer paso
            if (currentStep > 0) {

                // Bajamos un paso y mostramos el fragmento anterior
                currentStep--;
                showStep(currentStep);

            }

        });

        // Listener para el botón de siguiente
        binding.buttonNext.setOnClickListener(v -> {

            // Buscar el fragmento actual
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

            // Comprobar si el fragmento actual implementa StepValidator
            if (f instanceof StepValidator) {

                // Si lo hace, llamar a la función correspodniente
                boolean ok = ((StepValidator) f).validateFields();

                // Si no pasa la validación, no guardamos ni avanzamos
                if (!ok) {
                    return;
                }

            }

            // Guardamos los datos del paso actual antes de cambiar
            saveStepData(currentStep);

            // Comprobar si no estamos en el último fragmento
            if (currentStep < steps.length - 1) {

                // Actualizar el fragmento actual
                currentStep++;
                showStep(currentStep);

            } else {

                // Guardar los datos en Firebase
                saveAllToFirebase();

            }

        });
    }

    @SuppressLint("SetTextI18n")
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

    private void saveStepData(int stepIndex) {

        // Obtener los datos del fragmento actual
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        // Switch para guardar los datos en los atributos temporales
        switch (stepIndex) {
            case 0:
                PersonalInfoFragment p = (PersonalInfoFragment) f;
                firstName = p.getFirstName();
                lastName  = p.getLastName();
                age = p.getAgeValue();
                gender = p.getGender();
                break;
            case 1:
                BodyMetricsFragment b = (BodyMetricsFragment) f;
                height = b.getHeightValue();
                weight = b.getWeightValue();
                break;
            case 2:
                CalorieGoalFragment c = (CalorieGoalFragment) f;
                calorieGoal = c.getCalorieGoalValue();
                break;
        }
    }


    private void saveAllToFirebase() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> datos = new HashMap<>();

        datos.put("username", user.getDisplayName());
        datos.put("email", user.getEmail());
        datos.put("real_name", firstName);
        datos.put("last_name", lastName);
        datos.put("gender", gender);
        datos.put("age", age);
        datos.put("height", height);
        datos.put("weight", weight);
        datos.put("user_calories", calorieGoal);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .set(datos)
                .addOnSuccessListener(unused -> {

                    // Guardar el estado del perfil en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    prefs.edit().putBoolean("isProfileComplete", true).apply();

                    // Crear el intent para navegar a MainActivity
                    Intent intent = new Intent(this, MainActivity.class);

                    // Limpiar el historial de actividades anteriores para que no se pueda volver
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // Lanzar el intent
                    startActivity(intent);

                    // Aplicar animación de transición
                    overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);

                })

                .addOnFailureListener(e -> {
                    Log.e("UserSetUpActivity", "Error al guardar los datos en Firebase", e);
                });

    }

}