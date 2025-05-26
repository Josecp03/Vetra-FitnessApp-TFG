package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vetra_fitnessapp_tfg.utils.StepValidator;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentBodyMetricsBinding;


public class BodyMetricsFragment extends Fragment implements StepValidator {

    private FragmentBodyMetricsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBodyMetricsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public boolean validateFields() {
        String h = binding.editTextHeight.getText().toString().trim();
        String w = binding.editTextWeight.getText().toString().trim();

        if (h.isEmpty() || w.isEmpty()) {
            Toast.makeText(getContext(), "Please complete all fields to continue", Toast.LENGTH_SHORT).show();
            return false;
        }

        int height = Integer.parseInt(h);
        double weight = Double.parseDouble(w);

        if (height < 120) {
            Toast.makeText(getContext(), "Minimum height is 120 cm", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (height > 250) {
            Toast.makeText(getContext(), "Please enter a reasonable height (≤250 cm)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (weight < 20) {
            Toast.makeText(getContext(), "Minimum weight is 20 kg", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (weight > 300) {
            Toast.makeText(getContext(), "Please enter a reasonable weight (≤300 kg)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public int getHeightValue() {
        return Integer.parseInt(binding.editTextHeight.getText().toString().trim());
    }

    public double getWeightValue() {
        return Double.parseDouble(binding.editTextWeight.getText().toString().trim());
    }

}