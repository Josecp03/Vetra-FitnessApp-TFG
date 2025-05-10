package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityStartRoutineBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.example.vetra_fitnessapp_tfg.view.activities.StartupActivity;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.StartRoutineAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class StartRoutineActivity extends AppCompatActivity {
    public static final String EXTRA_ROUTINE = "extra_routine";

    private ActivityStartRoutineBinding binding;
    private Routine routine;
    private StartRoutineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) recuperar y marcar progreso
        routine = (Routine) getIntent().getSerializableExtra(EXTRA_ROUTINE);
        if (routine == null) {
            // si no viene, volvemos al launcher
            startActivity(new Intent(this, StartupActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }
        // guardo el ID y marco en progreso
        Prefs.setCurrentRoutineId(this, routine.getId());
        Prefs.setRoutineInProgress(this, true);

        binding = ActivityStartRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // título
        binding.tvRoutineTitle.setText(routine.getRoutineName());

        // Discard
        binding.btnDiscardRoutine.setOnClickListener(v -> {
            View dlg = getLayoutInflater().inflate(
                    R.layout.dialog_discard_execution_routine, null);
            BottomSheetDialog sheet = new BottomSheetDialog(
                    this, R.style.BottomSheetDialogTheme);
            sheet.setContentView(dlg);

            MaterialButton btnYes = dlg.findViewById(R.id.buttonConfirmDiscard);
            btnYes.setOnClickListener(x -> {
                Prefs.setRoutineInProgress(this, false);
                Prefs.setCurrentRoutineId(this, null);
                sheet.dismiss();
                // En lugar de finish(), lanzamos MainActivity:
                Intent i = new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            });

            sheet.show();
        });

        // Finish
        binding.btnFinishRoutine.setOnClickListener(v -> {
            View dlg = getLayoutInflater().inflate(
                    R.layout.dialog_finish_routine, null);
            BottomSheetDialog sheet = new BottomSheetDialog(
                    this, R.style.BottomSheetDialogTheme);
            sheet.setContentView(dlg);

            MaterialButton btnYes = dlg.findViewById(R.id.buttonConfirmFinish);
            btnYes.setOnClickListener(x -> {
                Prefs.setRoutineInProgress(this, false);
                Prefs.setCurrentRoutineId(this, null);
                sheet.dismiss();
                // Redirigimos también a MainActivity
                Intent i = new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            });

            sheet.show();
        });

        // RecyclerView
        adapter = new StartRoutineAdapter(routine.getExercises());
        binding.rvStartRoutine.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStartRoutine.setAdapter(adapter);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // bloqueado mientras haya rutina en progreso
    }
}
