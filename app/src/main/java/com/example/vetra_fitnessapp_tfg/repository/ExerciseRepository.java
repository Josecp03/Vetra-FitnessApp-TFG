package com.example.vetra_fitnessapp_tfg.repository;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.network.ExerciseApiService;
import com.example.vetra_fitnessapp_tfg.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

public class ExerciseRepository {
    private final ExerciseApiService api = RetrofitClient.getService();

    public Call<List<String>> fetchMuscleGroups() {
        return api.getBodyPartList();
    }

    public Call<List<Exercise>> fetchExercisesByGroup(String group) {
        return api.getExercisesByGroup(group);
    }

    public Call<List<Exercise>> fetchPopularExercises() {
        return api.getAllExercises();
    }
}
