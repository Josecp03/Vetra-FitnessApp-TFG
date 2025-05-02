package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.controller.training.ExerciseSelectionController;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.ExerciseAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseSelectionActivity extends AppCompatActivity {
    private ExerciseSelectionController controller;
    private ExerciseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_selection);

        // 1) RecyclerView
        RecyclerView rv = findViewById(R.id.rvPopularExercises);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(new ArrayList<>(), exercise -> {
            // aquí añades a la rutina...
        });
        rv.setAdapter(adapter);

        // 2) Controller + llamada
        controller = new ExerciseSelectionController();
        controller.loadPopularExercises(new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> res) {
                Log.d("ExerciseSel", "code=" + res.code() + " body=" + res.body());
                if (res.isSuccessful() && res.body() != null) {
                    adapter.getItems().clear();
                    adapter.getItems().addAll(res.body());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                Log.e("ExerciseSel", "Error loading exercises", t);
            }
        });
    }
}

