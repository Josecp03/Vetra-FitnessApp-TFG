package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
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

/**
 * Actividad de inicio que determina el flujo de navegación inicial de la aplicación.
 * Verifica el estado de autenticación, verificación de email, rutinas en progreso
 * y completitud del perfil del usuario para dirigir a la pantalla apropiada.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class StartupActivity extends AppCompatActivity {

    /**
     * Método llamado al crear la actividad.
     * Inicia el proceso de verificación del estado del usuario.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            navigateTo(SignInActivity.class);
            return;
        }
        user.reload().addOnCompleteListener(task -> {
            if (!user.isEmailVerified()) {
                navigateTo(VerifyEmailActivity.class);
            } else {
                initAppFlow();
            }
        });
    }

    /**
     * Continúa con la lógica de flujo de la aplicación después de verificar autenticación.
     * Verifica si hay una rutina en progreso o procede con el flujo normal de perfil.
     */
    private void initAppFlow() {
        // Si estamos en medio de una rutina guardada
        if (Prefs.isRoutineInProgress(this)) {
            String routineId = Prefs.getCurrentRoutineId(this);
            String uid       = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    /**
     * Maneja el documento de rutina obtenido de Firestore.
     * Si existe, reconstruye la rutina y navega a la actividad de entrenamiento.
     *
     * @param doc Documento de Firestore con los datos de la rutina
     */
    private void onRoutineDocument(DocumentSnapshot doc) {
        if (!doc.exists()) {
            Prefs.setRoutineInProgress(this, false);
            Prefs.setCurrentRoutineId(this, null);
            proceedNormalFlow();
            return;
        }

        Routine r = new Routine();
        r.setId(doc.getId());
        r.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        r.setRoutineName(doc.getString("routine_name"));

        List<RoutineExercise> reList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> exMaps =
                (List<Map<String,Object>>) doc.get("exercises");
        if (exMaps != null) {
            for (Map<String,Object> exMap : exMaps) {
                Exercise ex = new Exercise();
                ex.setId((String) exMap.get("exercise_id"));
                ex.setName((String) exMap.get("exercise_name"));
                ex.setGifUrl((String) exMap.get("exercise_photo_url"));
                ex.setTarget((String) exMap.get("muscle"));
                ex.setBodyPart((String) exMap.get("bodyPart"));
                ex.setEquipment((String) exMap.get("equipment"));
                @SuppressWarnings("unchecked")
                List<String> sec = (List<String>) exMap.get("secondaryMuscles");
                ex.setSecondaryMuscles(sec != null ? sec : new ArrayList<>());
                @SuppressWarnings("unchecked")
                List<String> ins = (List<String>) exMap.get("instructions");
                ex.setInstructions(ins != null ? ins : new ArrayList<>());

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

        Intent i = new Intent(this, StartRoutineActivity.class)
                .putExtra(StartRoutineActivity.EXTRA_ROUTINE, r)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    /**
     * Procede con el flujo normal verificando el estado del perfil del usuario.
     */
    private void proceedNormalFlow() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(this::onProfileDocument)
                .addOnFailureListener(e -> {
                    navigateTo(UserSetUpActivity.class);
                });
    }

    /**
     * Maneja el documento de perfil del usuario obtenido de Firestore.
     * Verifica si el perfil está completo para determinar la navegación.
     *
     * @param doc Documento de Firestore con los datos del perfil
     */
    private void onProfileDocument(DocumentSnapshot doc) {
        boolean hasAll = doc.exists()
                && doc.contains("real_name")
                && doc.contains("last_name")
                && doc.contains("age")
                && doc.contains("height")
                && doc.contains("weight")
                && doc.contains("user_calories");

        if (hasAll) {
            navigateTo(MainActivity.class);
        } else {
            navigateTo(UserSetUpActivity.class);
        }
    }

    /**
     * Helper para lanzar una actividad y limpiar el back-stack.
     *
     * @param cls Clase de la actividad a la que navegar
     */
    private void navigateTo(Class<?> cls) {
        Intent i = new Intent(this, cls)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
