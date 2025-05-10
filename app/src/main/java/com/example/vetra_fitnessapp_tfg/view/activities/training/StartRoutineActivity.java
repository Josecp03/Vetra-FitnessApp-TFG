package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityStartRoutineBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
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
        binding = ActivityStartRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1) Recuperar rutina
        routine = (Routine) getIntent().getSerializableExtra(EXTRA_ROUTINE);
        if (routine == null) {
            Toast.makeText(this, "Routine data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) Toolbar
        binding.tvRoutineTitle.setText(routine.getRoutineName());

        // 2a) Discard → mostrar diálogo de confirmación
        binding.btnDiscardRoutine.setOnClickListener(v -> {
            View dlg = getLayoutInflater()
                    .inflate(R.layout.dialog_discard_execution_routine, null);
            BottomSheetDialog sheet = new BottomSheetDialog(
                    this, R.style.BottomSheetDialogTheme
            );
            sheet.setContentView(dlg);
            sheet.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            MaterialButton btnConfirm = dlg.findViewById(R.id.buttonConfirmDiscard);
            btnConfirm.setOnClickListener(x -> {
                sheet.dismiss();
                finish();  // aquí descartas la rutina
            });

            sheet.show();
        });

        // 2b) Finish → por ahora sale directamente (más adelante mostraremos diálogo)
        binding.btnFinishRoutine.setOnClickListener(v -> {
            View dlg = getLayoutInflater()
                    .inflate(R.layout.dialog_finish_routine, null);
            BottomSheetDialog sheet = new BottomSheetDialog(
                    this, R.style.BottomSheetDialogTheme
            );
            sheet.setContentView(dlg);
            sheet.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            MaterialButton btnConfirm = dlg.findViewById(R.id.buttonConfirmFinish);
            btnConfirm.setOnClickListener(x -> {
                sheet.dismiss();
                // aquí guardarías los datos si hiciera falta…
                finish();
            });

            sheet.show();
        });

        // 3) RecyclerView
        adapter = new StartRoutineAdapter(routine.getExercises());
        binding.rvStartRoutine.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStartRoutine.setAdapter(adapter);
    }
}
