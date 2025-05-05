package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityNewRoutineBinding;
import com.example.vetra_fitnessapp_tfg.databinding.DialogDiscardRoutineBinding;
import com.example.vetra_fitnessapp_tfg.databinding.DialogSaveRoutineBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.RoutineExerciseAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewRoutineActivity extends AppCompatActivity {

    private static final int REQ_SELECT_EX    = 1001;
    private static final int MAX_EXERCISES    = 20;

    private ActivityNewRoutineBinding binding;
    private final List<RoutineExercise> exercises = new ArrayList<>();
    private RoutineExerciseAdapter routineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Marcamos que estamos creando rutina
        Prefs.setRoutineInProgress(this, true);

        // RecyclerView de rutina
        binding.rvRoutineExercises.setLayoutManager(new LinearLayoutManager(this));
        routineAdapter = new RoutineExerciseAdapter(exercises);
        binding.rvRoutineExercises.setAdapter(routineAdapter);

        // Botón “Add exercise”
        binding.buttonAddExercise.setOnClickListener(v -> {
            if (exercises.size() >= MAX_EXERCISES) {
                Toast.makeText(this,
                        "Maximum of " + MAX_EXERCISES + " exercises allowed.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(NewRoutineActivity.this, ExerciseSelectionActivity.class);
            startActivityForResult(i, REQ_SELECT_EX);
            overridePendingTransition(
                    R.anim.slide_in_right_fade,
                    R.anim.slide_out_left_fade
            );
        });

        // Botón “Save”
        binding.buttonSave.setOnClickListener(v -> {
            String name = binding.editTextText.getText()
                    .toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this,
                        "Please enter a name for your routine",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            showSaveRoutineDialog();
        });

        // Botón “Discard”
        binding.buttonDiscard.setOnClickListener(v -> showDiscardRoutineDialog());
    }

    private void showSaveRoutineDialog() {
        DialogSaveRoutineBinding dialogBinding =
                DialogSaveRoutineBinding.inflate(getLayoutInflater());
        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        Objects.requireNonNull(bottomSheetDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        dialogBinding.buttonSaveRotineConfirm.setOnClickListener(v -> {
            Prefs.setRoutineInProgress(this, false);
            bottomSheetDialog.dismiss();
            finish();
        });

        bottomSheetDialog.show();
    }

    private void showDiscardRoutineDialog() {
        DialogDiscardRoutineBinding dialogBinding =
                DialogDiscardRoutineBinding.inflate(getLayoutInflater());
        BottomSheetDialog bottomSheetDialog =
                new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        Objects.requireNonNull(bottomSheetDialog.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        dialogBinding.buttonDiscardRoutineConfirm.setOnClickListener(v -> {
            Prefs.setRoutineInProgress(this, false);
            bottomSheetDialog.dismiss();
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("fragment_to_open", R.id.navigation_workouts);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });

        bottomSheetDialog.show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Bloqueamos el botón Atrás mientras creamos rutina
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_EX
                && resultCode == RESULT_OK
                && data != null) {

            if (exercises.size() >= MAX_EXERCISES) {
                Toast.makeText(this,
                        "You can add up to " + MAX_EXERCISES + " exercises",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Exercise ex = (Exercise) data.getSerializableExtra("selected_exercise");
            if (ex != null) {
                exercises.add(new RoutineExercise(ex));
                routineAdapter.notifyItemInserted(exercises.size() - 1);
            }
        }
    }
}
