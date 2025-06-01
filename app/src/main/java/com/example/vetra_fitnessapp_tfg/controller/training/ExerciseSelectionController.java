package com.example.vetra_fitnessapp_tfg.controller.training;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.repository.ExerciseRepository;
import java.util.List;
import retrofit2.Callback;

/**
 * Controlador para la selección de ejercicios.
 * Maneja la lógica de negocio para obtener ejercicios desde la API externa
 * a través del repositorio correspondiente.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class ExerciseSelectionController {

    /** Repositorio para acceder a los datos de ejercicios */
    private final ExerciseRepository repo = new ExerciseRepository();

    /**
     * Carga la lista de músculos objetivo disponibles.
     *
     * @param cb Callback para manejar la respuesta asíncrona
     */
    public void loadTargetList(Callback<List<String>> cb) {
        repo.fetchTargetList().enqueue(cb);
    }

    /**
     * Carga ejercicios populares con paginación.
     *
     * @param limit Número máximo de ejercicios a cargar
     * @param offset Desplazamiento para la paginación
     * @param cb Callback para manejar la respuesta asíncrona
     */
    public void loadPopularExercises(int limit, int offset, Callback<List<Exercise>> cb) {
        repo.fetchPopularExercises(limit, offset).enqueue(cb);
    }

    /**
     * Carga la lista de equipamiento disponible.
     *
     * @param cb Callback para manejar la respuesta asíncrona
     */
    public void loadEquipmentList(Callback<List<String>> cb) {
        repo.fetchEquipmentList().enqueue(cb);
    }

    /**
     * Carga ejercicios filtrados por músculo objetivo específico.
     *
     * @param target Músculo objetivo a filtrar
     * @param limit Número máximo de ejercicios a cargar
     * @param offset Desplazamiento para la paginación
     * @param cb Callback para manejar la respuesta asíncrona
     */
    public void loadExercisesByTarget(String target, int limit, int offset, Callback<List<Exercise>> cb) {
        repo.fetchExercisesByTarget(target, limit, offset).enqueue(cb);
    }

    /**
     * Carga ejercicios filtrados por tipo de equipamiento.
     *
     * @param type Tipo de equipamiento a filtrar
     * @param limit Número máximo de ejercicios a cargar
     * @param offset Desplazamiento para la paginación
     * @param cb Callback para manejar la respuesta asíncrona
     */
    public void loadExercisesByEquipment(String type, int limit, int offset, Callback<List<Exercise>> cb) {
        repo.fetchExercisesByEquipment(type, limit, offset).enqueue(cb);
    }

    /**
     * Carga ejercicios filtrados por nombre.
     *
     * @param name Nombre del ejercicio a buscar
     * @param limit Número máximo de ejercicios a cargar
     * @param offset Desplazamiento para la paginación
     * @param cb Callback para manejar la respuesta asíncrona
     */
    public void loadExercisesByName(String name, int limit, int offset, Callback<List<Exercise>> cb) {
        repo.fetchExercisesByName(name, limit, offset).enqueue(cb);
    }
}
