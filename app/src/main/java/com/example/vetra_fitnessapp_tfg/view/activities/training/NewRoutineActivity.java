package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRoutineActivity extends AppCompatActivity {
    private ActivityNewRoutineBinding binding;
    private final List<RoutineExercise> exercises = new ArrayList<>();
    private RoutineExerciseAdapter routineAdapter;
    private static final int REQ_SELECT_EX = 1001;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Marcamos que estamos creando rutina
        Prefs.setRoutineInProgress(this, true);

        // RecyclerView de rutina
        binding.rvRoutineExercises.setLayoutManager(new LinearLayoutManager(this));
        routineAdapter = new RoutineExerciseAdapter(exercises);
        binding.rvRoutineExercises.setAdapter(routineAdapter);

        // Botón “Add exercise”
        binding.buttonAddExercise.setOnClickListener(v -> {
            if (exercises.size() >= 20) {
                Toast.makeText(this, "You can add up to 20 exercises", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ExerciseSelectionActivity.class);
            startActivityForResult(i, REQ_SELECT_EX);
            overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);
        });

        // Guardar / descartar
        binding.buttonSave.setOnClickListener(v -> showSaveRoutineDialog());
        binding.buttonDiscard.setOnClickListener(v -> showDiscardRoutineDialog());
    }

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

            // Construir documento
            Map<String,Object> routine = new HashMap<>();
            routine.put("user_id", auth.getCurrentUser().getUid());
            routine.put("routine_name", name);

            List<Map<String,Object>> exList = new ArrayList<>();
            for (RoutineExercise re : exercises) {
                Map<String,Object> exMap = new HashMap<>();
                exMap.put("exercise_id", re.getExercise().getId());
                exMap.put("exercise_name", toCamelCase(re.getExercise().getName()));
                exMap.put("exercise_photo_url", re.getExercise().getGifUrl());
                exMap.put("muscle", toCamelCase(re.getExercise().getTarget()));

                List<Map<String,Object>> setsMap = new ArrayList<>();
                for (ExerciseSet s : re.getSets()) {
                    Map<String,Object> sMap = new HashMap<>();
                    sMap.put("set_number", s.getSetNumber());
                    sMap.put("weight", s.getWeight());
                    sMap.put("reps", s.getReps());
                    setsMap.add(sMap);
                }
                exMap.put("sets", setsMap);
                exList.add(exMap);
            }
            routine.put("exercises", exList);

            // Guardar en Firestore
            db.collection("routines")
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

    private void showDiscardRoutineDialog() {
        // igual que antes…
        Prefs.setRoutineInProgress(this, false);
        finish();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // bloquea atrás mientras creas
    }

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
