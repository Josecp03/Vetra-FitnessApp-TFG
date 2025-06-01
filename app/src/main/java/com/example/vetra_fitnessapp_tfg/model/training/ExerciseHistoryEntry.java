package com.example.vetra_fitnessapp_tfg.model.training;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.List;

/**
 * Modelo que representa una entrada en el historial de ejercicios del usuario.
 * Almacena información sobre un ejercicio realizado en una fecha específica,
 * incluyendo todas las series completadas.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class ExerciseHistoryEntry implements Serializable {

    /** ID del usuario que realizó el ejercicio */
    @PropertyName("user_id")
    private String userId;

    /** ID del ejercicio realizado */
    @PropertyName("exercise_id")
    private String exerciseId;

    /** Nombre del ejercicio realizado */
    @PropertyName("exercise_name")
    private String exerciseName;

    /** URL de la foto/GIF del ejercicio */
    @PropertyName("exercise_photo_url")
    private String exercisePhotoUrl;

    /** Fecha en que se realizó el ejercicio (formato YYYY-MM-DD) */
    @PropertyName("date")
    private String date; // YYYY-MM-DD

    /** Lista de series realizadas en este ejercicio */
    @PropertyName("sets")
    private List<SetRecord> sets;

    /**
     * Constructor vacío requerido para la deserialización de Firebase.
     */
    public ExerciseHistoryEntry() { }

    /**
     * Constructor completo para crear una entrada de historial.
     *
     * @param userId ID del usuario
     * @param exerciseId ID del ejercicio
     * @param exerciseName Nombre del ejercicio
     * @param exercisePhotoUrl URL de la foto del ejercicio
     * @param date Fecha de realización
     * @param sets Lista de series realizadas
     */
    public ExerciseHistoryEntry(
            String userId,
            String exerciseId,
            String exerciseName,
            String exercisePhotoUrl,
            String date,
            List<SetRecord> sets
    ) {
        this.userId           = userId;
        this.exerciseId       = exerciseId;
        this.exerciseName     = exerciseName;
        this.exercisePhotoUrl = exercisePhotoUrl;
        this.date             = date;
        this.sets             = sets;
    }

    /**
     * Obtiene el ID del usuario.
     *
     * @return ID del usuario
     */
    @PropertyName("user_id")
    public String getUserId() { return userId; }

    /**
     * Establece el ID del usuario.
     *
     * @param userId ID del usuario
     */
    @PropertyName("user_id")
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Obtiene el ID del ejercicio.
     *
     * @return ID del ejercicio
     */
    @PropertyName("exercise_id")
    public String getExerciseId() { return exerciseId; }

    /**
     * Establece el ID del ejercicio.
     *
     * @param exerciseId ID del ejercicio
     */
    @PropertyName("exercise_id")
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }

    /**
     * Obtiene el nombre del ejercicio.
     *
     * @return Nombre del ejercicio
     */
    @PropertyName("exercise_name")
    public String getExerciseName() { return exerciseName; }

    /**
     * Establece el nombre del ejercicio.
     *
     * @param exerciseName Nombre del ejercicio
     */
    @PropertyName("exercise_name")
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    /**
     * Obtiene la URL de la foto del ejercicio.
     *
     * @return URL de la foto
     */
    @PropertyName("exercise_photo_url")
    public String getExercisePhotoUrl() { return exercisePhotoUrl; }

    /**
     * Establece la URL de la foto del ejercicio.
     *
     * @param exercisePhotoUrl URL de la foto
     */
    @PropertyName("exercise_photo_url")
    public void setExercisePhotoUrl(String exercisePhotoUrl) { this.exercisePhotoUrl = exercisePhotoUrl; }

    /**
     * Obtiene la fecha de realización del ejercicio.
     *
     * @return Fecha en formato YYYY-MM-DD
     */
    @PropertyName("date")
    public String getDate() { return date; }

    /**
     * Establece la fecha de realización del ejercicio.
     *
     * @param date Fecha en formato YYYY-MM-DD
     */
    @PropertyName("date")
    public void setDate(String date) { this.date = date; }

    /**
     * Obtiene la lista de series realizadas.
     *
     * @return Lista de registros de series
     */
    @PropertyName("sets")
    public List<SetRecord> getSets() { return sets; }

    /**
     * Establece la lista de series realizadas.
     *
     * @param sets Lista de registros de series
     */
    @PropertyName("sets")
    public void setSets(List<SetRecord> sets) { this.sets = sets; }

    /**
     * Clase interna que representa el registro de una serie individual.
     * Contiene información sobre peso, repeticiones y número de serie.
     */
    public static class SetRecord implements Serializable {

        /** Número de la serie */
        @PropertyName("set_number")
        private int setNumber;

        /** Peso utilizado en la serie */
        @PropertyName("weight")
        private int weight;

        /** Número de repeticiones realizadas */
        @PropertyName("reps")
        private int reps;

        /**
         * Constructor vacío requerido para la deserialización de Firebase.
         */
        public SetRecord() { }

        /**
         * Constructor para crear un registro de serie.
         *
         * @param setNumber Número de la serie
         * @param weight Peso utilizado
         * @param reps Repeticiones realizadas
         */
        public SetRecord(int setNumber, int weight, int reps) {
            this.setNumber = setNumber;
            this.weight    = weight;
            this.reps      = reps;
        }

        /**
         * Obtiene el número de la serie.
         *
         * @return Número de serie
         */
        @PropertyName("set_number")
        public int getSetNumber() { return setNumber; }

        /**
         * Establece el número de la serie.
         *
         * @param setNumber Número de serie
         */
        @PropertyName("set_number")
        public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

        /**
         * Obtiene el peso utilizado en la serie.
         *
         * @return Peso en kilogramos
         */
        @PropertyName("weight")
        public int getWeight() { return weight; }

        /**
         * Establece el peso utilizado en la serie.
         *
         * @param weight Peso en kilogramos
         */
        @PropertyName("weight")
        public void setWeight(int weight) { this.weight = weight; }

        /**
         * Obtiene el número de repeticiones realizadas.
         *
         * @return Número de repeticiones
         */
        @PropertyName("reps")
        public int getReps() { return reps; }

        /**
         * Establece el número de repeticiones realizadas.
         *
         * @param reps Número de repeticiones
         */
        @PropertyName("reps")
        public void setReps(int reps) { this.reps = reps; }
    }
}
