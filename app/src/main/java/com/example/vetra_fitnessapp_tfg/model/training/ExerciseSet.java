package com.example.vetra_fitnessapp_tfg.model.training;

import java.io.Serializable;

/**
 * Representa una serie dentro de la rutina y su estado (hecha o no).
 */
public class ExerciseSet implements Serializable {
    private int setNumber;
    private int weight;
    private int reps;
    private boolean done; // Nueva propiedad

    public ExerciseSet() { }

    public ExerciseSet(int setNumber, int weight, int reps) {
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.done = false;
    }

    public int getSetNumber() { return setNumber; }
    public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }
}