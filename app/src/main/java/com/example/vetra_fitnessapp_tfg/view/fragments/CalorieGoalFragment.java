package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentCalorieGoalBinding;

public class CalorieGoalFragment extends Fragment {

    private FragmentCalorieGoalBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalorieGoalBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    /** Llamado desde la Activity al pulsar NEXT */
    public int getCalorieGoalValue() {
        return Integer.parseInt(binding.editTextCalories.getText().toString().trim());
    }


}