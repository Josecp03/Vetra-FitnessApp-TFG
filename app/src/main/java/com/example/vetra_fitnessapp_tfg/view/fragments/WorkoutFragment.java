package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.databinding.FragmentWorkoutBinding;
import com.example.vetra_fitnessapp_tfg.controller.training.RoutineController;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.example.vetra_fitnessapp_tfg.view.activities.training.NewRoutineActivity;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.RoutineAdapter;

import java.util.ArrayList;
import java.util.List;

public class WorkoutFragment extends Fragment {
    private FragmentWorkoutBinding binding;
    private final List<Routine>     routines   = new ArrayList<>();
    private RoutineAdapter          adapter;
    private final RoutineController controller = new RoutineController();

    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup cont, Bundle b) {
        binding = FragmentWorkoutBinding.inflate(inf, cont, false);

        binding.buttonAddRoutine.setOnClickListener(v ->
                startActivity(new Intent(requireActivity(), NewRoutineActivity.class))
        );

        adapter = new RoutineAdapter(routines);
        binding.rvMyRoutines.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMyRoutines.setAdapter(adapter);

        controller.loadUserRoutines(list -> {
            routines.clear();
            routines.addAll(list);
            adapter.notifyDataSetChanged();
        });

        return binding.getRoot();
    }
}
