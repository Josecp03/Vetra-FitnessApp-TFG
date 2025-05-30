package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityStartRoutineBinding;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseHistoryEntry.SetRecord;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseSet;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.example.vetra_fitnessapp_tfg.view.activities.StartupActivity;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.StartRoutineAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartRoutineActivity extends AppCompatActivity {
    public static final String EXTRA_ROUTINE = "extra_routine";

    private ActivityStartRoutineBinding binding;
    private Routine routine;
    private StartRoutineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routine = (Routine) getIntent().getSerializableExtra(EXTRA_ROUTINE);
        if (routine == null) {
            startActivity(new Intent(this, StartupActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }
        Prefs.setCurrentRoutineId(this, routine.getId());
        Prefs.setRoutineInProgress(this, true);

        binding = ActivityStartRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvRoutineTitle.setText(routine.getRoutineName());

        binding.btnDiscardRoutine.setOnClickListener(v -> showDiscardDialog());

        binding.btnFinishRoutine.setOnClickListener(v -> showFinishDialog());

        adapter = new StartRoutineAdapter(routine.getExercises());
        binding.rvStartRoutine.setLayoutManager(new LinearLayoutManager(this));
        binding.rvStartRoutine.setAdapter(adapter);
    }

    private void showDiscardDialog() {
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
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });
        sheet.show();
    }

    @SuppressLint("SetTextI18n")
    private void showFinishDialog() {
        // 1) Detectar series incompletas
        boolean hasIncomplete = false;
        // 2) Detectar series "completadas" pero con peso y reps a 0
        boolean hasZeroCompleted = false;

        for (RoutineExercise re : routine.getExercises()) {
            for (ExerciseSet s : re.getSets()) {
                if (!s.isDone()) {
                    hasIncomplete = true;
                }
                // si está marcada como hecha pero sin peso y reps
                if (s.isDone() && s.getWeight() == 0 && s.getReps() == 0) {
                    hasZeroCompleted = true;
                }
                if (hasIncomplete && hasZeroCompleted) break;
            }
            if (hasIncomplete && hasZeroCompleted) break;
        }

        View dlg = getLayoutInflater().inflate(
                R.layout.dialog_finish_routine, null);
        BottomSheetDialog sheet = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme);
        sheet.setContentView(dlg);

        TextView tvMsg = dlg.findViewById(R.id.textMessageFinishRoutine);

        // Si hay series incompletas O series "vacias" completadas, advertimos
        if (hasIncomplete || hasZeroCompleted) {
            tvMsg.setText(
                    "⚠️🚨 You have incomplete or zero-valued sets. " +
                            "Are you sure you want to finish the routine? " +
                            "Incomplete or empty sets will not be saved in your history."
            );
        } else {
            tvMsg.setText(
                    getString(R.string.finish_routine_restarting_later_means_starting_from_the_beginning)
            );
        }

        MaterialButton btnYes = dlg.findViewById(R.id.buttonConfirmFinish);
        btnYes.setOnClickListener(x -> {
            saveHistory();
            Prefs.setRoutineInProgress(this, false);
            Prefs.setCurrentRoutineId(this, null);
            sheet.dismiss();
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        });

        sheet.show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showDiscardDialog();
    }

    private void saveHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String today = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? LocalDate.now().toString()
                : "";

        for (RoutineExercise re : routine.getExercises()) {
            List<SetRecord> doneSets = new ArrayList<>();
            for (ExerciseSet s : re.getSets()) {
                if (s.isDone()) {
                    doneSets.add(new SetRecord(
                            s.getSetNumber(),
                            s.getWeight(),
                            s.getReps()
                    ));
                }
            }
            if (doneSets.isEmpty()) continue;

            Map<String,Object> entry = new HashMap<>();
            entry.put("exercise_id",        re.getExercise().getId());
            entry.put("exercise_name",      re.getExercise().getName());
            entry.put("exercise_photo_url", re.getExercise().getGifUrl());
            entry.put("date",               today);

            List<Map<String,Object>> setsList = new ArrayList<>();
            for (SetRecord r : doneSets) {
                Map<String,Object> m = new HashMap<>();
                m.put("set_number", r.getSetNumber());
                m.put("weight",     r.getWeight());
                m.put("reps",       r.getReps());
                setsList.add(m);
            }
            entry.put("sets", setsList);

            db.collection("users")
                    .document(uid)
                    .collection("exerciseHistory")
                    .add(entry)
                    .addOnSuccessListener(docRef ->
                            Log.d("StartRoutineActivity", "History ID=" + docRef.getId())
                    )
                    .addOnFailureListener(Throwable::printStackTrace);
        }
    }
}
