package com.example.vetra_fitnessapp_tfg.controller.training;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.repository.ExerciseRepository;
import java.util.List;
import retrofit2.Callback;

public class ExerciseSelectionController {
    private final ExerciseRepository repo = new ExerciseRepository();

    public void loadMuscleGroups(Callback<List<String>> cb) {
        repo.fetchMuscleGroups().enqueue(cb);
    }

    public void loadExercisesByGroup(String group, Callback<List<Exercise>> cb) {
        repo.fetchExercisesByGroup(group).enqueue(cb);
    }

    public void loadPopularExercises(Callback<List<Exercise>> cb) {
        repo.fetchPopularExercises().enqueue(cb);
    }
}
