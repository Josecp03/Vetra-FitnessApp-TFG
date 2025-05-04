package com.example.vetra_fitnessapp_tfg.model.training;

import java.util.ArrayList;
import java.util.List;

public class RoutineExercise {

    private Exercise exercise;
    private List<ExerciseSet> sets = new ArrayList<>();

    public RoutineExercise(Exercise exercise) {
        this.exercise = exercise;
        sets.add(new ExerciseSet(1, 0, 0));
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<ExerciseSet> getSets() {
        return sets;
    }

    public void setSets(List<ExerciseSet> sets) {
        this.sets = sets;
    }

    public void addSet() {
        int next = sets.size()+1;
        sets.add(new ExerciseSet(next, 0, 0));
    }

}
