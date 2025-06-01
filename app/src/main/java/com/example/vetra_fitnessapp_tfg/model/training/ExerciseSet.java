package com.example.vetra_fitnessapp_tfg.model.training;

import java.io.Serializable;

/**
 * Modelo que representa una serie individual de un ejercicio.
 * Contiene información sobre el número de serie, peso, repeticiones
 * y estado de completado.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class ExerciseSet implements Serializable {

    /** Número de la serie dentro del ejercicio */
    private int setNumber;

    /** Peso utilizado en esta serie (en kilogramos) */
    private int weight;

    /** Número de repeticiones planificadas o realizadas */
    private int reps;

    /** Indica si la serie ha sido completada */
    private boolean done;

    /**
     * Constructor vacío requerido para la serialización.
     */
    public ExerciseSet() { }

    /**
     * Constructor para crear una serie con valores específicos.
     * Por defecto, la serie se marca como no completada.
     *
     * @param setNumber Número de la serie
     * @param weight Peso a utilizar
     * @param reps Número de repeticiones
     */
    public ExerciseSet(int setNumber, int weight, int reps) {
        this.setNumber = setNumber;
        this.weight = weight;
        this.reps = reps;
        this.done = false;
    }

    /**
     * Obtiene el número de la serie.
     *
     * @return Número de serie
     */
    public int getSetNumber() { return setNumber; }

    /**
     * Establece el número de la serie.
     *
     * @param setNumber Número de serie
     */
    public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

    /**
     * Obtiene el peso utilizado en la serie.
     *
     * @return Peso en kilogramos
     */
    public int getWeight() { return weight; }

    /**
     * Establece el peso utilizado en la serie.
     *
     * @param weight Peso en kilogramos
     */
    public void setWeight(int weight) { this.weight = weight; }

    /**
     * Obtiene el número de repeticiones de la serie.
     *
     * @return Número de repeticiones
     */
    public int getReps() { return reps; }

    /**
     * Establece el número de repeticiones de la serie.
     *
     * @param reps Número de repeticiones
     */
    public void setReps(int reps) { this.reps = reps; }

    /**
     * Verifica si la serie ha sido completada.
     *
     * @return true si la serie está completada, false en caso contrario
     */
    public boolean isDone() { return done; }

    /**
     * Establece el estado de completado de la serie.
     *
     * @param done true para marcar como completada, false para no completada
     */
    public void setDone(boolean done) { this.done = done; }
}
