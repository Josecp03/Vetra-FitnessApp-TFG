package com.example.vetra_fitnessapp_tfg.network;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ExerciseApiService {
    @GET("exercises/bodyPartList")
    Call<List<String>> getBodyPartList();

    @GET("exercises/targetList")
    Call<List<String>> getTargetList();

    @GET("exercises/equipmentList")
    Call<List<String>> getEquipmentList();

    @GET("exercises/bodyPart/{group}")
    Call<List<Exercise>> getExercisesByGroup(@Path("group") String group);

    @GET("exercises")
    Call<List<Exercise>> getAllExercises(
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
