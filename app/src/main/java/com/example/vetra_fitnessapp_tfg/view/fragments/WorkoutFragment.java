package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentWorkoutBinding;
import com.example.vetra_fitnessapp_tfg.view.activities.training.NewRoutineActivity;

public class WorkoutFragment extends Fragment {

    private FragmentWorkoutBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.buttonAddRoutine.setOnClickListener(v->{

            // Crear intent para navegar a NewRoutineActivity
            Intent intent = new Intent(requireActivity(), NewRoutineActivity.class);

            // Lanzar el intent
            startActivity(intent);

            // Aplicar animación de transición
            requireActivity().overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);

        });

        return view;

    }
}