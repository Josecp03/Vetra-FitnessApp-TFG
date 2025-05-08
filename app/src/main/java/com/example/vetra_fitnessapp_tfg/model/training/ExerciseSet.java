package com.example.vetra_fitnessapp_tfg.model.training;

import java.io.Serializable;

public class ExerciseSet implements Serializable {

    private int setNumber;
    private int weight;
    private int reps;

    // Constructor vacío necesario para Firestore y Serializable
    public ExerciseSet() { }

    public ExerciseSet(int setNumber, int weight, int reps) {
        this.setNumber = setNumber;
        this.weight    = weight;
        this.reps      = reps;
    }

    public int getSetNumber() {
        return setNumber;
    }
    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getReps() {
        return reps;
    }
    public void setReps(int reps) {
        this.reps = reps;
    }
}
