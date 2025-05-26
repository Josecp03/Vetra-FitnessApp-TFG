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

    public void loadUserRoutines(Consumer<List<Routine>> onSuccess) {
        String uid = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("routines")
                .get()
                .addOnSuccessListener(qs -> {
                    List<Routine> lista = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : qs) {
                        Routine r = new Routine();
                        r.setId(doc.getId());
                        r.setUserId(uid);
                        r.setRoutineName(doc.getString("routine_name"));

                        List<RoutineExercise> reList = new ArrayList<>();
                        @SuppressWarnings("unchecked")
                        List<Map<String,Object>> exMaps = (List<Map<String,Object>>) doc.get("exercises");

                        if (exMaps != null) {
                            for (Map<String,Object> exMap : exMaps) {
                                String exId   = (String) exMap.get("exercise_id");
                                String exName = (String) exMap.get("exercise_name");
                                String url    = (String) exMap.get("exercise_photo_url");
                                String muscle = (String) exMap.get("muscle");

                                Exercise ex = new Exercise();
                                ex.setId(exId);
                                ex.setName(exName);
                                ex.setGifUrl(url);
                                ex.setTarget(muscle);

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

                                @SuppressWarnings("unchecked")
                                List<Map<String,Object>> setsMaps = (List<Map<String,Object>>) exMap.get("sets");
                                List<ExerciseSet> sets = new ArrayList<>();
                                if (setsMaps != null) {
                                    for (Map<String,Object> sm : setsMaps) {
                                        int num   = ((Long) sm.get("set_number")).intValue();
                                        int w     = ((Long) sm.get("weight")).intValue();
                                        int reps  = ((Long) sm.get("reps")).intValue();
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

                    lista.sort((a,b) -> a.getRoutineName().compareToIgnoreCase(b.getRoutineName()));
                    onSuccess.accept(lista);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
