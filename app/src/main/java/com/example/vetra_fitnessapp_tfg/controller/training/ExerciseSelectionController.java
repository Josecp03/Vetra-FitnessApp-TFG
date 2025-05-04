package com.example.vetra_fitnessapp_tfg.controller.training;

import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.repository.ExerciseRepository;
import java.util.List;
import retrofit2.Callback;

public class ExerciseSelectionController {
    private final ExerciseRepository repo = new ExerciseRepository();

    public void loadExercisesByGroup(String group, Callback<List<Exercise>> cb) {
        repo.fetchExercisesByGroup(group).enqueue(cb);
    }

    public void loadTargetList(Callback<List<String>> cb) {
        repo.fetchTargetList().enqueue(cb);
    }

    public void loadPopularExercises(int limit, int offset, Callback<List<Exercise>> cb) {
        repo.fetchPopularExercises(limit, offset).enqueue(cb);
    }

}
