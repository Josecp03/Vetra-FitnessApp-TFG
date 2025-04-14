package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vetra_fitnessapp_tfg.databinding.FragmentBodyMetricsBinding;


public class BodyMetricsFragment extends Fragment {

    private FragmentBodyMetricsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBodyMetricsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();



        return view;
    }
}