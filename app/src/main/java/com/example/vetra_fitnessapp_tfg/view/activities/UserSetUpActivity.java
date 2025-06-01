package com.example.vetra_fitnessapp_tfg.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.utils.KeyStoreManager;
import com.example.vetra_fitnessapp_tfg.utils.StepValidator;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityUserSetUpBinding;
import com.example.vetra_fitnessapp_tfg.view.fragments.BodyMetricsFragment;
import com.example.vetra_fitnessapp_tfg.view.fragments.CalorieGoalFragment;
import com.example.vetra_fitnessapp_tfg.view.fragments.PersonalInfoFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Map;
import java.util.HashMap;
import com.google.firebase.firestore.FirebaseFirestore;
import android.net.Uri;

/**
 * Actividad para la configuración inicial del perfil del usuario.
 * Guía al usuario a través de un proceso de 3 pasos para completar
 * su información personal, métricas corporales y objetivo calórico.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class UserSetUpActivity extends AppCompatActivity {

    /** Binding para acceder a las vistas de la actividad */
    private ActivityUserSetUpBinding binding;

    /** Índice del paso actual en el proceso de configuración */
    private int currentStep = 0;

    /** Array de fragmentos que representan cada paso del proceso */
    private final Fragment[] steps = new Fragment[] {
            new PersonalInfoFragment(),
            new BodyMetricsFragment(),
            new CalorieGoalFragment()
    };

    /** Nombre del usuario */
    public String firstName, lastName, gender;

    /** Edad del usuario */
    public int age, height, calorieGoal;

    /** Peso del usuario */
    public double weight;

    /** Gestor de cifrado para datos sensibles */
    private KeyStoreManager keyStore;

    /**
     * Método llamado al crear la actividad.
     * Inicializa el binding, el gestor de cifrado y configura la navegación.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserSetUpBinding.inflate(getLayoutInflater());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(binding.getRoot());

        keyStore = new KeyStoreManager();
        showStep(currentStep);
        setupNavigationButtons();

    }

    /**
     * Configura los listeners de los botones de navegación (Atrás y Siguiente).
     */
    private void setupNavigationButtons() {

        binding.buttonBack.setOnClickListener(v -> {
            if (currentStep > 0) {
                currentStep--;
                showStep(currentStep);
            }
        });

        binding.buttonNext.setOnClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (f instanceof StepValidator) {
                boolean ok = ((StepValidator) f).validateFields();
                if (!ok) {
                    return;
                }
            }
            saveStepData(currentStep);
            if (currentStep < steps.length - 1) {
                currentStep++;
                showStep(currentStep);
            } else {
                saveAllToFirebase();
            }
        });
    }

    /**
     * Muestra el fragmento correspondiente al paso especificado.
     * Actualiza la UI de indicadores de progreso y el texto del botón.
     *
     * @param stepIndex Índice del paso a mostrar (0-2)
     */
    @SuppressLint("SetTextI18n")
    private void showStep(int stepIndex) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, steps[stepIndex])
                .commit();
        if (stepIndex == 0) {
            binding.buttonBack.setVisibility(View.GONE);
        } else {
            binding.buttonBack.setVisibility(View.VISIBLE);
        }
        if (stepIndex >= 0) {
            binding.step1.setBackgroundResource(R.color.white);
        } else {
            binding.step1.setBackgroundResource(R.color.dark_gray);
        }
        if (stepIndex >= 1) {
            binding.step2.setBackgroundResource(R.color.white);
        } else {
            binding.step2.setBackgroundResource(R.color.dark_gray);
        }
        if (stepIndex >= 2) {
            binding.step3.setBackgroundResource(R.color.white);
        } else {
            binding.step3.setBackgroundResource(R.color.dark_gray);
        }
        if (stepIndex == steps.length - 1) {
            binding.buttonNext.setText("Finish");
        } else {
            binding.buttonNext.setText("Next");
        }
    }

    /**
     * Guarda los datos del paso actual en las variables de instancia.
     *
     * @param stepIndex Índice del paso cuyos datos se van a guardar
     */
    private void saveStepData(int stepIndex) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
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

    /**
     * Guarda todos los datos recopilados en Firebase Firestore.
     * Cifra los datos sensibles antes de almacenarlos y navega a MainActivity al completarse.
     */
    private void saveAllToFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Uri photoUri = user.getPhotoUrl();
        String photoUrl = (photoUri != null) ? photoUri.toString() : "";
        Map<String, Object> datos = new HashMap<>();
        datos.put("email", keyStore.encrypt(user.getEmail()));
        datos.put("username", keyStore.encrypt(user.getDisplayName()));
        datos.put("real_name", keyStore.encrypt(firstName));
        datos.put("last_name", keyStore.encrypt(lastName));
        datos.put("age", age);
        datos.put("gender", keyStore.encrypt(gender));
        datos.put("height", height);
        datos.put("weight", weight);
        datos.put("user_calories", calorieGoal);
        datos.put("profile_photo_url", photoUrl);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .set(datos)
                .addOnSuccessListener(unused -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);
                })
                .addOnFailureListener(e -> {
                    Log.e("UserSetUpActivity", "Error al guardar los datos en Firebase", e);
                });

    }
}
