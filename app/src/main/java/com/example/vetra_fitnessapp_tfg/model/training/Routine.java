// com/example/vetra_fitnessapp_tfg/model/training/Routine.java
package com.example.vetra_fitnessapp_tfg.model.training;

import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.List;

@IgnoreExtraProperties
public class Routine {

    private String routineId;
    private String userId;

    @PropertyName("routine_name")
    private String routineName;

    private List<RoutineExercise> exercises;

    public Routine() { }

    public Routine(String routineId, String userId,
                   String routineName,
                   List<RoutineExercise> exercises) {
        this.routineId    = routineId;
        this.userId       = userId;
        this.routineName  = routineName;
        this.exercises    = exercises;
    }

    public String getRoutineId() {
        return routineId;
    }
    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("routine_name")
    public String getRoutineName() {
        return routineName;
    }
    @PropertyName("routine_name")
    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public List<RoutineExercise> getExercises() {
        return exercises;
    }
    public void setExercises(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }

    // Alias para conveniencia si en algún sitio usas r.setId(...)
    public String getId() {
        return routineId;
    }
    public void setId(String id) {
        this.routineId = id;
    }
}
