package com.example.vetra_fitnessapp_tfg.model.training;

import java.io.Serializable;
import java.util.List;

/**
 * Modelo que representa un ejercicio de entrenamiento.
 * Contiene toda la información necesaria sobre un ejercicio específico,
 * incluyendo instrucciones, músculos trabajados y equipamiento requerido.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class Exercise implements Serializable {

    /** Identificador único del ejercicio */
    private String id;

    /** Nombre del ejercicio */
    private String name;

    /** Parte del cuerpo que trabaja el ejercicio */
    private String bodyPart;

    /** Equipamiento necesario para realizar el ejercicio */
    private String equipment;

    /** URL del GIF que muestra cómo realizar el ejercicio */
    private String gifUrl;

    /** Músculo principal que trabaja el ejercicio */
    private String target;

    /** Lista de músculos secundarios que trabaja el ejercicio */
    private List<String> secondaryMuscles;

    /** Lista de instrucciones paso a paso para realizar el ejercicio */
    private List<String> instructions;

    /**
     * Constructor completo para crear un ejercicio con todos los datos.
     *
     * @param id Identificador único del ejercicio
     * @param name Nombre del ejercicio
     * @param bodyPart Parte del cuerpo que trabaja
     * @param equipment Equipamiento necesario
     * @param gifUrl URL del GIF demostrativo
     * @param target Músculo principal objetivo
     * @param secondaryMuscles Lista de músculos secundarios
     * @param instructions Lista de instrucciones
     */
    public Exercise(String id, String name, String bodyPart,
                    String equipment, String gifUrl, String target,
                    List<String> secondaryMuscles, List<String> instructions) {
        this.id = id;
        this.name = name;
        this.bodyPart = bodyPart;
        this.equipment = equipment;
        this.gifUrl = gifUrl;
        this.target = target;
        this.secondaryMuscles = secondaryMuscles;
        this.instructions = instructions;
    }

    /**
     * Constructor vacío requerido para la deserialización de Firebase.
     */
    public Exercise() {

    }

    /**
     * Obtiene el identificador único del ejercicio.
     *
     * @return ID del ejercicio
     */
    public String getId() {
        return id;
    }

    /**
     * Establece el identificador único del ejercicio.
     *
     * @param id ID del ejercicio
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del ejercicio.
     *
     * @return Nombre del ejercicio
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del ejercicio.
     *
     * @param name Nombre del ejercicio
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la parte del cuerpo que trabaja el ejercicio.
     *
     * @return Parte del cuerpo
     */
    public String getBodyPart() {
        return bodyPart;
    }

    /**
     * Establece la parte del cuerpo que trabaja el ejercicio.
     *
     * @param bodyPart Parte del cuerpo
     */
    public void setBodyPart(String bodyPart) {
        this.bodyPart = bodyPart;
    }

    /**
     * Obtiene el equipamiento necesario para el ejercicio.
     *
     * @return Equipamiento requerido
     */
    public String getEquipment() {
        return equipment;
    }

    /**
     * Establece el equipamiento necesario para el ejercicio.
     *
     * @param equipment Equipamiento requerido
     */
    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    /**
     * Obtiene la URL del GIF demostrativo del ejercicio.
     *
     * @return URL del GIF
     */
    public String getGifUrl() {
        return gifUrl;
    }

    /**
     * Establece la URL del GIF demostrativo del ejercicio.
     *
     * @param gifUrl URL del GIF
     */
    public void setGifUrl(String gifUrl) {
        this.gifUrl = gifUrl;
    }

    /**
     * Obtiene el músculo principal que trabaja el ejercicio.
     *
     * @return Músculo objetivo principal
     */
    public String getTarget() {
        return target;
    }

    /**
     * Establece el músculo principal que trabaja el ejercicio.
     *
     * @param target Músculo objetivo principal
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Obtiene la lista de músculos secundarios que trabaja el ejercicio.
     *
     * @return Lista de músculos secundarios
     */
    public List<String> getSecondaryMuscles() {
        return secondaryMuscles;
    }

    /**
     * Establece la lista de músculos secundarios que trabaja el ejercicio.
     *
     * @param secondaryMuscles Lista de músculos secundarios
     */
    public void setSecondaryMuscles(List<String> secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }

    /**
     * Obtiene la lista de instrucciones para realizar el ejercicio.
     *
     * @return Lista de instrucciones paso a paso
     */
    public List<String> getInstructions() {
        return instructions;
    }

    /**
     * Establece la lista de instrucciones para realizar el ejercicio.
     *
     * @param instructions Lista de instrucciones paso a paso
     */
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
}
