package com.example.vetra_fitnessapp_tfg.network;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interfaz de servicio para la API de ejercicios usando Retrofit.
 * Define todos los endpoints disponibles para obtener información sobre ejercicios
 * desde la API externa de ExerciseDB.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public interface ExerciseApiService {

    /**
     * Obtiene la lista de todas las partes del cuerpo disponibles.
     *
     * @return Call con lista de nombres de partes del cuerpo
     */
    @GET("exercises/bodyPartList")
    Call<List<String>> getBodyPartList();

    /**
     * Obtiene la lista de todos los músculos objetivo disponibles.
     *
     * @return Call con lista de nombres de músculos objetivo
     */
    @GET("exercises/targetList")
    Call<List<String>> getTargetList();

    /**
     * Obtiene la lista de todo el equipamiento disponible.
     *
     * @return Call con lista de nombres de equipamiento
     */
    @GET("exercises/equipmentList")
    Call<List<String>> getEquipmentList();

    /**
     * Obtiene ejercicios filtrados por parte del cuerpo específica.
     *
     * @param group Nombre de la parte del cuerpo a filtrar
     * @return Call con lista de ejercicios para esa parte del cuerpo
     */
    @GET("exercises/bodyPart/{group}")
    Call<List<Exercise>> getExercisesByGroup(@Path("group") String group);

    /**
     * Obtiene todos los ejercicios con paginación.
     *
     * @param limit Número máximo de ejercicios a retornar
     * @param offset Número de ejercicios a omitir (para paginación)
     * @return Call con lista paginada de ejercicios
     */
    @GET("exercises")
    Call<List<Exercise>> getAllExercises(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    /**
     * Obtiene ejercicios filtrados por músculo objetivo específico.
     *
     * @param target Nombre del músculo objetivo
     * @param limit Número máximo de ejercicios a retornar
     * @param offset Número de ejercicios a omitir (para paginación)
     * @return Call con lista de ejercicios para ese músculo
     */
    @GET("exercises/target/{target}")
    Call<List<Exercise>> getExercisesByTarget(
            @Path("target") String target,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    /**
     * Obtiene ejercicios filtrados por tipo de equipamiento específico.
     *
     * @param type Nombre del tipo de equipamiento
     * @param limit Número máximo de ejercicios a retornar
     * @param offset Número de ejercicios a omitir (para paginación)
     * @return Call con lista de ejercicios para ese equipamiento
     */
    @GET("exercises/equipment/{type}")
    Call<List<Exercise>> getExercisesByEquipment(
            @Path("type") String type,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    /**
     * Busca ejercicios por nombre específico.
     *
     * @param name Nombre del ejercicio a buscar
     * @param limit Número máximo de ejercicios a retornar
     * @param offset Número de ejercicios a omitir (para paginación)
     * @return Call con lista de ejercicios que coinciden con el nombre
     */
    @GET("exercises/name/{name}")
    Call<List<Exercise>> getExercisesByName(
            @Path("name") String name,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
