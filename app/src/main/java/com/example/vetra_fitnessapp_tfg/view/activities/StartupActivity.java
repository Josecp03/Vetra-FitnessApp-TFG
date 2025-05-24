package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseSet;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.example.vetra_fitnessapp_tfg.view.activities.training.StartRoutineActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Prefs.isRoutineInProgress(this)) {
            String routineId = Prefs.getCurrentRoutineId(this);
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (routineId != null) {
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .collection("routines")
                        .document(routineId)
                        .get()
                        .addOnSuccessListener(this::onRoutineDocument)
                        .addOnFailureListener(e -> proceedNormalFlow());
                return;
            }
        }
        proceedNormalFlow();
    }

    private void onRoutineDocument(DocumentSnapshot doc) {
        if (!doc.exists()) {
            // si falla, borramos flag y seguimos normal
            Prefs.setRoutineInProgress(this, false);
            Prefs.setCurrentRoutineId(this, null);
            proceedNormalFlow();
            return;
        }

        // Mapeamos snapshot a Routine
        Routine r = new Routine();
        r.setId(doc.getId());
        r.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        r.setRoutineName(doc.getString("routine_name"));

        // ejercicios anidados
        List<RoutineExercise> reList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> exMaps =
                (List<Map<String,Object>>) doc.get("exercises");
        if (exMaps != null) {
            for (Map<String,Object> exMap : exMaps) {
                // Exercise básico
                Exercise ex = new Exercise();
                ex.setId((String) exMap.get("exercise_id"));
                ex.setName((String) exMap.get("exercise_name"));
                ex.setGifUrl((String) exMap.get("exercise_photo_url"));
                ex.setTarget((String) exMap.get("muscle"));
                // extra campos opcionales si los tienes guardados
                ex.setBodyPart((String) exMap.get("bodyPart"));
                ex.setEquipment((String) exMap.get("equipment"));
                @SuppressWarnings("unchecked")
                List<String> sec = (List<String>) exMap.get("secondaryMuscles");
                ex.setSecondaryMuscles(sec != null ? sec : new ArrayList<>());
                @SuppressWarnings("unchecked")
                List<String> ins = (List<String>) exMap.get("instructions");
                ex.setInstructions(ins != null ? ins : new ArrayList<>());

                // RoutineExercise con sus sets
                RoutineExercise re = new RoutineExercise();
                re.setExercise(ex);
                List<ExerciseSet> sets = new ArrayList<>();
                @SuppressWarnings("unchecked")
                List<Map<String,Object>> setsMaps =
                        (List<Map<String,Object>>) exMap.get("sets");
                if (setsMaps != null) {
                    for (Map<String,Object> sm : setsMaps) {
                        int num  = ((Long) sm.get("set_number")).intValue();
                        int w    = ((Long) sm.get("weight")).intValue();
                        int reps = ((Long) sm.get("reps")).intValue();
                        sets.add(new ExerciseSet(num, w, reps));
                    }
                }
                re.setSets(sets);
                reList.add(re);
            }
        }
        r.setExercises(reList);

        // Lanzamos StartRoutineActivity con la rutina cargada
        Intent i = new Intent(this, StartRoutineActivity.class)
                .putExtra(StartRoutineActivity.EXTRA_ROUTINE, r)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void proceedNormalFlow() {
        // usuario Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // ir a sign in
            startActivity(new Intent(this, SignInActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        // perfil en Firestore
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(this::onProfileDocument)
                .addOnFailureListener(e -> {
                    startActivity(new Intent(this, UserSetUpActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                });
    }

    private void onProfileDocument(DocumentSnapshot doc) {
        boolean hasAll = doc.exists()
                && doc.contains("real_name")
                && doc.contains("last_name")
                && doc.contains("age")
                && doc.contains("height")
                && doc.contains("weight")
                && doc.contains("user_calories");

        if (hasAll) {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            startActivity(new Intent(this, UserSetUpActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
        finish();
    }
}
