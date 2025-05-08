package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.databinding.ActivityStartRoutineBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.StartRoutineAdapter;

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

        // 1) Recupero el objeto completo
        routine = (Routine) getIntent().getSerializableExtra(EXTRA_ROUTINE);
        if (routine == null) {
            Toast.makeText(this, "Routine data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2) Toolbar
        binding.tvRoutineTitle.setText(routine.getRoutineName());
        binding.btnFinishRoutine.setOnClickListener(v -> finish());

        // 3) RecyclerView
        adapter = new StartRoutineAdapter(routine.getExercises());
        binding.rvStartRoutine.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStartRoutine.setAdapter(adapter);
    }
}
