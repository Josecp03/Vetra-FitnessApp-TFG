package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityNewRoutineBinding;
import com.example.vetra_fitnessapp_tfg.databinding.DialogSaveRoutineBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseSet;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.RoutineExerciseAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Actividad para crear una nueva rutina de entrenamiento.
 * Permite añadir ejercicios, configurar series y guardar la rutina completa.
 * Incluye validaciones de límites y diálogos de confirmación para descarte.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class NewRoutineActivity extends AppCompatActivity {

    /**
     * Binding para acceder a las vistas del layout de la actividad.
     */
    private ActivityNewRoutineBinding binding;

    /**
     * Lista de ejercicios añadidos a la rutina.
     */
    private final List<RoutineExercise> exercises = new ArrayList<>();

    /**
     * Adaptador para mostrar los ejercicios de la rutina.
     */
    private RoutineExerciseAdapter routineAdapter;

    /**
     * Código de solicitud para la selección de ejercicios.
     */
    private static final int REQ_SELECT_EX = 1001;

    /**
     * Instancia de Firestore para operaciones de base de datos.
     */
    private FirebaseFirestore db;

    /**
     * Instancia de autenticación de Firebase.
     */
    private FirebaseAuth auth;

    /**
     * Método llamado al crear la actividad.
     * Inicializa Firebase, configura el RecyclerView y establece los listeners.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Prefs.setRoutineInProgress(this, true);

        binding.rvRoutineExercises.setLayoutManager(new LinearLayoutManager(this));
        routineAdapter = new RoutineExerciseAdapter(exercises);
        binding.rvRoutineExercises.setAdapter(routineAdapter);

        binding.buttonAddExercise.setOnClickListener(v -> {
            if (exercises.size() >= 20) {
                Toast.makeText(this, "You can add up to 20 exercises", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ExerciseSelectionActivity.class);
            startActivityForResult(i, REQ_SELECT_EX);
            overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);
        });

        binding.buttonSave.setOnClickListener(v -> {
            if (exercises.isEmpty()) {
                Toast.makeText(
                        this,
                        "Add at least 1 exercise",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }
            showSaveRoutineDialog();
        });
        binding.buttonDiscard.setOnClickListener(v -> showDiscardRoutineDialog());
    }

    /**
     * Muestra el diálogo de confirmación para descartar la rutina.
     * Permite al usuario cancelar la creación y limpiar el estado de progreso.
     */
    private void showDiscardRoutineDialog() {
        // Inflamos el layout de tu dialog_discard_routine.xml
        View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_discard_routine, null, false);

        BottomSheetDialog bottomSheet = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme);
        bottomSheet.setContentView(dialogView);
        Objects.requireNonNull(bottomSheet.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        // Botón de confirmar descarte
        MaterialButton btnConfirm = dialogView.findViewById(
                R.id.buttonDiscardRoutineConfirm);
        btnConfirm.setOnClickListener(v -> {
            Prefs.setRoutineInProgress(this, false);
            bottomSheet.dismiss();
            finish();
        });

        // Opcional: permitir cancelar tocando fuera o con back
        bottomSheet.setCancelable(true);
        bottomSheet.show();
    }

    /**
     * Muestra el diálogo para guardar la rutina con un nombre específico.
     * Valida el nombre y guarda la rutina completa en Firestore.
     */
    private void showSaveRoutineDialog() {
        DialogSaveRoutineBinding dialogBinding = DialogSaveRoutineBinding.inflate(getLayoutInflater());
        BottomSheetDialog bottomSheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheet.setContentView(dialogBinding.getRoot());
        bottomSheet.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialogBinding.buttonSaveRotineConfirm.setOnClickListener(v -> {
            String name = binding.editTextText.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a routine name", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = auth.getCurrentUser().getUid();
            Map<String,Object> routine = new HashMap<>();
            routine.put("routine_name", name);

            List<Map<String,Object>> exList = new ArrayList<>();
            for (RoutineExercise re : exercises) {
                Map<String,Object> exMap = new HashMap<>();
                exMap.put("exercise_id", re.getExercise().getId());
                exMap.put("exercise_name", toCamelCase(re.getExercise().getName()));
                exMap.put("exercise_photo_url", re.getExercise().getGifUrl());
                exMap.put("muscle", toCamelCase(re.getExercise().getTarget()));

                exMap.put("bodyPart",        re.getExercise().getBodyPart());
                exMap.put("equipment",       re.getExercise().getEquipment());
                exMap.put("secondaryMuscles", re.getExercise().getSecondaryMuscles());
                exMap.put("instructions",    re.getExercise().getInstructions());

                List<Map<String,Object>> setsMap = new ArrayList<>();
                for (ExerciseSet s : re.getSets()) {
                    Map<String,Object> sMap = new HashMap<>();
                    sMap.put("set_number", s.getSetNumber());
                    sMap.put("weight",     s.getWeight());
                    sMap.put("reps",       s.getReps());
                    setsMap.add(sMap);
                }
                exMap.put("sets", setsMap);
                exList.add(exMap);
            }
            routine.put("exercises", exList);

            db.collection("users")
                    .document(uid)
                    .collection("routines")
                    .add(routine)
                    .addOnSuccessListener(docRef -> {
                        Prefs.setRoutineInProgress(this, false);
                        bottomSheet.dismiss();
                        Intent i = new Intent(this, MainActivity.class)
                                .putExtra("fragment_to_open", R.id.navigation_workouts)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving routine", Toast.LENGTH_SHORT).show()
                    );
        });

        bottomSheet.show();
    }

    /**
     * Maneja el botón de retroceso del sistema.
     * Muestra el diálogo de descarte en lugar de cerrar directamente.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showDiscardRoutineDialog();
    }

    /**
     * Maneja el resultado de actividades iniciadas para obtener resultados.
     *
     * @param requestCode Código de la solicitud
     * @param resultCode Código del resultado
     * @param data Datos devueltos por la actividad
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_EX
                && resultCode == RESULT_OK
                && data != null) {
            Exercise ex = (Exercise) data.getSerializableExtra("selected_exercise");
            if (ex != null) {
                exercises.add(new RoutineExercise(ex));
                routineAdapter.notifyItemInserted(exercises.size() - 1);
            }
        }
    }

    /**
     * Convierte una cadena de texto a formato CamelCase.
     * Capitaliza la primera letra de cada palabra.
     *
     * @param s Cadena de texto a convertir
     * @return Cadena convertida a CamelCase
     */
    private String toCamelCase(String s) {
        StringBuilder out = new StringBuilder();
        for (String w : s.split("\\s+")) {
            if (w.isEmpty()) continue;
            out.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1))
                    .append(" ");
        }
        return out.toString().trim();
    }
}
