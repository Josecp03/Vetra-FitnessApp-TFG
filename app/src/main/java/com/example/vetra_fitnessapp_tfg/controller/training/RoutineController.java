package com.example.vetra_fitnessapp_tfg.controller.training;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseSet;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RoutineController {

    private final FirebaseFirestore db   = FirebaseFirestore.getInstance();
    private final FirebaseAuth      auth = FirebaseAuth.getInstance();

    /**
     * Carga todas las rutinas del usuario logueado y las pasa al callback onSuccess.
     */
    public void loadUserRoutines(Consumer<List<Routine>> onSuccess) {
        String uid = auth.getCurrentUser().getUid();

        db.collection("routines")
                .whereEqualTo("user_id", uid)
                .get()
                .addOnSuccessListener(qs -> {
                    List<Routine> lista = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : qs) {
                        // 1) Cabecera de la rutina
                        Routine r = new Routine();
                        r.setId(doc.getId());
                        r.setUserId(doc.getString("user_id"));
                        r.setRoutineName(doc.getString("routine_name"));

                        // 2) Leer lista de ejercicios anidada
                        List<RoutineExercise> reList = new ArrayList<>();
                        @SuppressWarnings("unchecked")
                        List<Map<String,Object>> exMaps =
                                (List<Map<String,Object>>) doc.get("exercises");

                        if (exMaps != null) {
                            for (Map<String,Object> exMap : exMaps) {
                                // Construir el objeto Exercise
                                String exId   = (String) exMap.get("exercise_id");
                                String exName = (String) exMap.get("exercise_name");
                                String url    = (String) exMap.get("exercise_photo_url");
                                String muscle = (String) exMap.get("muscle");

                                Exercise ex = new Exercise();
                                ex.setId(exId);
                                ex.setName(exName);
                                ex.setGifUrl(url);
                                ex.setTarget(muscle);

                                // Construir el RoutineExercise
                                RoutineExercise re = new RoutineExercise();
                                re.setExercise(ex);

                                // Leer sus sets
                                @SuppressWarnings("unchecked")
                                List<Map<String,Object>> setsMaps =
                                        (List<Map<String,Object>>) exMap.get("sets");
                                List<ExerciseSet> sets = new ArrayList<>();
                                if (setsMaps != null) {
                                    for (Map<String,Object> sm : setsMaps) {
                                        // Firestore numéricos vienen como Long
                                        int num   = ((Long)    sm.get("set_number")).intValue();
                                        int w     = ((Long)    sm.get("weight")).intValue();
                                        int reps  = ((Long)    sm.get("reps")).intValue();
                                        sets.add(new ExerciseSet(num, w, reps));
                                    }
                                }
                                re.setSets(sets);

                                reList.add(re);
                            }
                        }

                        r.setExercises(reList);
                        lista.add(r);
                    }

                    // Si quieres ordenarlas:
                    lista.sort((a,b) ->
                            a.getRoutineName()
                                    .compareToIgnoreCase(b.getRoutineName())
                    );

                    onSuccess.accept(lista);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
