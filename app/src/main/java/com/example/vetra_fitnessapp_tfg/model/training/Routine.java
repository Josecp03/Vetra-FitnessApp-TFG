package com.example.vetra_fitnessapp_tfg.model.training;

import java.io.Serializable;
import java.util.List;

public class Routine implements Serializable {

    private String routineId;
    private String userId;
    private String routineName;
    private List<RoutineExercise> exercises;

    public Routine() { }

    public Routine(String routineId, String userId,
                   String routineName,
                   List<RoutineExercise> exercises) {
        this.routineId   = routineId;
        this.userId      = userId;
        this.routineName = routineName;
        this.exercises   = exercises;
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

    public String getRoutineName() {
        return routineName;
    }
    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public List<RoutineExercise> getExercises() {
        return exercises;
    }
    public void setExercises(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }

    // alias para comodidad
    public String getId() {
        return routineId;
    }
    public void setId(String id) {
        this.routineId = id;
    }
}
