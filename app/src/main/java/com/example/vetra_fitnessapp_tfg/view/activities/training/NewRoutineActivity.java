package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityNewRoutineBinding;
import com.example.vetra_fitnessapp_tfg.databinding.ActivitySignInBinding;
import com.example.vetra_fitnessapp_tfg.databinding.DialogDiscardRoutineBinding;
import com.example.vetra_fitnessapp_tfg.databinding.DialogRecoverPasswordBinding;
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

    private ActivityNewRoutineBinding binding;
    private List<RoutineExercise> exercises = new ArrayList<>();
    private RoutineExerciseAdapter routineAdapter;
    private static final int REQ_SELECT_EX = 1001;

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

        // 2) Botón “Add exercise” → lanzamos selector con startActivityForResult
        binding.buttonAddExercise.setOnClickListener(v -> {
            Intent i = new Intent(this, ExerciseSelectionActivity.class);
            startActivityForResult(i, REQ_SELECT_EX);
            overridePendingTransition(
                    R.anim.slide_in_right_fade,
                    R.anim.slide_out_left_fade
            );
        });

        // 3) Guardar / descartar… (igual que antes)
        binding.buttonSave.setOnClickListener(v -> showSaveRoutineDialog());
        binding.buttonDiscard.setOnClickListener(v -> showDiscardRoutineDialog());


    }

    private void showSaveRoutineDialog() {

        // Inflar el binding del diálogo
        DialogSaveRoutineBinding dialogBinding = DialogSaveRoutineBinding.inflate(getLayoutInflater());

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        // Asignar la vista al diálogo
        bottomSheetDialog.setContentView(dialogBinding.getRoot());

        // Establecer fondo transparente para que los bordes sean redondeados o personalizados
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Listener para el botón de guardar rutina
        dialogBinding.buttonSaveRotineConfirm.setOnClickListener(v -> {

            Prefs.setRoutineInProgress(this, false);
            bottomSheetDialog.dismiss();
            finish();

        });

        // Mostrar el diálogo
        bottomSheetDialog.show();

    }

    private void showDiscardRoutineDialog() {

        // Inflar el binding del diálogo
        DialogDiscardRoutineBinding dialogBinding = DialogDiscardRoutineBinding.inflate(getLayoutInflater());

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        // Asignar la vista al diálogo
        bottomSheetDialog.setContentView(dialogBinding.getRoot());

        // Establecer fondo transparente para que los bordes sean redondeados o personalizados
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Listener para el botón de guardar rutina
        dialogBinding.buttonDiscardRoutineConfirm.setOnClickListener(v -> {


            Prefs.setRoutineInProgress(this, false);
            bottomSheetDialog.dismiss();

            Intent i = new Intent(this, MainActivity.class);

            i.putExtra("fragment_to_open", R.id.navigation_workouts);
            // Limpia el stack si quieres que no queden pantallas anteriores
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(i);

            finish();

        });

        // Mostrar el diálogo
        bottomSheetDialog.show();

    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // NO llamar a super → bloquea el botón “Atrás” mientras estamos creando rutina
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_EX
                && resultCode == RESULT_OK
                && data != null) {

            // recuperamos el Exercise que devolvió ExerciseSelectionActivity
            Exercise ex = (Exercise) data
                    .getSerializableExtra("selected_exercise");
            if (ex != null) {
                // lo envolvemos en RoutineExercise (viene con 1 set ya inicializado)
                exercises.add(new RoutineExercise(ex));
                routineAdapter.notifyItemInserted(exercises.size() - 1);
            }
        }
    }



}