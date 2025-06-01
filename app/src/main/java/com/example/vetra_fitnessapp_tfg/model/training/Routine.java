package com.example.vetra_fitnessapp_tfg.model.training;

import java.io.Serializable;
import java.util.List;

/**
 * Modelo que representa una rutina de entrenamiento completa.
 * Una rutina contiene múltiples ejercicios con sus respectivas series
 * y pertenece a un usuario específico.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class Routine implements Serializable {

    /** Identificador único de la rutina */
    private String routineId;

    /** ID del usuario propietario de la rutina */
    private String userId;

    /** Nombre descriptivo de la rutina */
    private String routineName;

    /** Lista de ejercicios que componen la rutina */
    private List<RoutineExercise> exercises;

    /**
     * Constructor vacío requerido para la serialización.
     */
    public Routine() { }

    /**
     * Constructor completo para crear una rutina.
     *
     * @param routineId ID único de la rutina
     * @param userId ID del usuario propietario
     * @param routineName Nombre de la rutina
     * @param exercises Lista de ejercicios de la rutina
     */
    public Routine(String routineId, String userId,
                   String routineName,
                   List<RoutineExercise> exercises) {
        this.routineId   = routineId;
        this.userId      = userId;
        this.routineName = routineName;
        this.exercises   = exercises;
    }

    /**
     * Obtiene el ID de la rutina.
     *
     * @return ID de la rutina
     */
    public String getRoutineId() {
        return routineId;
    }

    /**
     * Establece el ID de la rutina.
     *
     * @param routineId ID de la rutina
     */
    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    /**
     * Obtiene el ID del usuario propietario.
     *
     * @return ID del usuario
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Establece el ID del usuario propietario.
     *
     * @param userId ID del usuario
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Obtiene el nombre de la rutina.
     *
     * @return Nombre de la rutina
     */
    public String getRoutineName() {
        return routineName;
    }

    /**
     * Establece el nombre de la rutina.
     *
     * @param routineName Nombre de la rutina
     */
    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    /**
     * Obtiene la lista de ejercicios de la rutina.
     *
     * @return Lista de ejercicios
     */
    public List<RoutineExercise> getExercises() {
        return exercises;
    }

    /**
     * Establece la lista de ejercicios de la rutina.
     *
     * @param exercises Lista de ejercicios
     */
    public void setExercises(List<RoutineExercise> exercises) {
        this.exercises = exercises;
    }

    /**
     * Método alternativo para obtener el ID (compatibilidad).
     *
     * @return ID de la rutina
     */
    public String getId() {
        return routineId;
    }

    /**
     * Método alternativo para establecer el ID (compatibilidad).
     *
     * @param id ID de la rutina
     */
    public void setId(String id) {
        this.routineId = id;
    }
}
