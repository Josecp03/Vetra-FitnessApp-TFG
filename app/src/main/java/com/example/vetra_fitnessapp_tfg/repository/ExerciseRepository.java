package com.example.vetra_fitnessapp_tfg.repository;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.network.ExerciseApiService;
import com.example.vetra_fitnessapp_tfg.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;

public class ExerciseRepository {
    private final ExerciseApiService api = RetrofitClient.getService();

    public Call<List<String>> fetchBodyPartList() {
        return api.getBodyPartList();
    }

    public Call<List<String>> fetchTargetList() {
        return api.getTargetList();
    }

    public Call<List<Exercise>> fetchExercisesByGroup(String group) {
        return api.getExercisesByGroup(group);
    }

    public Call<List<Exercise>> fetchPopularExercises(int limit, int offset) {
        return api.getAllExercises(limit, offset);
    }

    public Call<List<String>> fetchEquipmentList() {
        return api.getEquipmentList();
    }

}
