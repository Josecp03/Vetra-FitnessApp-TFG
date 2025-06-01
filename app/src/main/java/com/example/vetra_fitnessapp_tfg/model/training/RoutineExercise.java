package com.example.vetra_fitnessapp_tfg.model.training;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa un ejercicio dentro de una rutina específica.
 * Combina la información del ejercicio base con las series planificadas
 * y el estado de expansión en la interfaz de usuario.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class RoutineExercise implements Serializable {

    /** Ejercicio base con toda su información */
    private Exercise exercise;

    /** Lista de series planificadas para este ejercicio en la rutina */
    private List<ExerciseSet> sets = new ArrayList<>();

    /** Estado de expansión en la UI (expandido/colapsado) */
    private boolean expanded = true;

    /**
     * Constructor vacío requerido para la serialización.
     */
    public RoutineExercise() { }

    /**
     * Constructor que crea un ejercicio de rutina con una serie inicial.
     * Por defecto se crea con una serie vacía (peso y repeticiones en 0).
     *
     * @param exercise Ejercicio base a incluir en la rutina
     */
    public RoutineExercise(Exercise exercise) {
        this.exercise = exercise;
        sets.add(new ExerciseSet(1, 0, 0));
    }

    /**
     * Obtiene el ejercicio base.
     *
     * @return Ejercicio asociado
     */
    public Exercise getExercise() {
        return exercise;
    }

    /**
     * Establece el ejercicio base.
     *
     * @param exercise Ejercicio a asociar
     */
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    /**
     * Obtiene la lista de series del ejercicio.
     *
     * @return Lista de series
     */
    public List<ExerciseSet> getSets() {
        return sets;
    }

    /**
     * Establece la lista de series del ejercicio.
     *
     * @param sets Lista de series
     */
    public void setSets(List<ExerciseSet> sets) {
        this.sets = sets;
    }

    /**
     * Añade una nueva serie al ejercicio.
     * La nueva serie se numera automáticamente como la siguiente disponible.
     */
    public void addSet() {
        int next = sets.size() + 1;
        sets.add(new ExerciseSet(next, 0, 0));
    }

    /**
     * Verifica si el ejercicio está expandido en la UI.
     *
     * @return true si está expandido, false si está colapsado
     */
    public boolean isExpanded() { return expanded; }

    /**
     * Establece el estado de expansión del ejercicio en la UI.
     *
     * @param expanded true para expandir, false para colapsar
     */
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}
