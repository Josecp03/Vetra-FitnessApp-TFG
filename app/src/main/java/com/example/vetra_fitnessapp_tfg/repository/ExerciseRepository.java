package com.example.vetra_fitnessapp_tfg.repository;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.network.ExerciseApiService;
import com.example.vetra_fitnessapp_tfg.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

/**
 * Repositorio para el acceso a datos de ejercicios.
 * Actúa como capa de abstracción entre los controladores y la API externa,
 * proporcionando métodos para obtener diferentes tipos de datos de ejercicios.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class ExerciseRepository {

    /** Servicio de API configurado para realizar las llamadas HTTP */
    private final ExerciseApiService api = RetrofitClient.getService();

    /**
     * Obtiene la lista de todas las partes del cuerpo disponibles.
     *
     * @return Call para ejecutar la solicitud de partes del cuerpo
     */
    public Call<List<String>> fetchBodyPartList() {
        return api.getBodyPartList();
    }

    /**
     * Obtiene la lista de todos los músculos objetivo disponibles.
     *
     * @return Call para ejecutar la solicitud de músculos objetivo
     */
    public Call<List<String>> fetchTargetList() {
        return api.getTargetList();
    }

    /**
     * Obtiene ejercicios filtrados por grupo muscular específico.
     *
     * @param group Nombre del grupo muscular a filtrar
     * @return Call para ejecutar la solicitud de ejercicios por grupo
     */
    public Call<List<Exercise>> fetchExercisesByGroup(String group) {
        return api.getExercisesByGroup(group);
    }

    /**
     * Obtiene ejercicios populares con paginación.
     *
     * @param limit Número máximo de ejercicios a obtener
     * @param offset Desplazamiento para la paginación
     * @return Call para ejecutar la solicitud de ejercicios populares
     */
    public Call<List<Exercise>> fetchPopularExercises(int limit, int offset) {
        return api.getAllExercises(limit, offset);
    }

    /**
     * Obtiene la lista de todo el equipamiento disponible.
     *
     * @return Call para ejecutar la solicitud de equipamiento
     */
    public Call<List<String>> fetchEquipmentList() {
        return api.getEquipmentList();
    }

    /**
     * Obtiene ejercicios filtrados por músculo objetivo específico.
     *
     * @param target Nombre del músculo objetivo
     * @param limit Número máximo de ejercicios a obtener
     * @param offset Desplazamiento para la paginación
     * @return Call para ejecutar la solicitud de ejercicios por músculo
     */
    public Call<List<Exercise>> fetchExercisesByTarget(String target, int limit, int offset) {
        return api.getExercisesByTarget(target, limit, offset);
    }

    /**
     * Obtiene ejercicios filtrados por tipo de equipamiento específico.
     *
     * @param type Nombre del tipo de equipamiento
     * @param limit Número máximo de ejercicios a obtener
     * @param offset Desplazamiento para la paginación
     * @return Call para ejecutar la solicitud de ejercicios por equipamiento
     */
    public Call<List<Exercise>> fetchExercisesByEquipment(String type, int limit, int offset) {
        return api.getExercisesByEquipment(type, limit, offset);
    }

    /**
     * Busca ejercicios por nombre específico.
     *
     * @param name Nombre del ejercicio a buscar
     * @param limit Número máximo de ejercicios a obtener
     * @param offset Desplazamiento para la paginación
     * @return Call para ejecutar la solicitud de búsqueda por nombre
     */
    public Call<List<Exercise>> fetchExercisesByName(String name, int limit, int offset) {
        return api.getExercisesByName(name, limit, offset);
    }
}
