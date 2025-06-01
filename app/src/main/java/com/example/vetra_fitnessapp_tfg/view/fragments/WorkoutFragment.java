package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.databinding.FragmentWorkoutBinding;
import com.example.vetra_fitnessapp_tfg.controller.training.RoutineController;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.example.vetra_fitnessapp_tfg.view.activities.training.NewRoutineActivity;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.RoutineAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento para gestionar las rutinas de entrenamiento del usuario.
 * Muestra la lista de rutinas guardadas y permite crear nuevas rutinas.
 * Incluye validación de límites máximos de rutinas por usuario.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class WorkoutFragment extends Fragment {

    /**
     * Binding para acceder a las vistas del fragmento.
     */
    private FragmentWorkoutBinding binding;

    /**
     * Lista de rutinas del usuario.
     */
    private final List<Routine>     routines   = new ArrayList<>();

    /**
     * Adaptador para mostrar las rutinas en el RecyclerView.
     */
    private RoutineAdapter          adapter;

    /**
     * Controlador para manejar operaciones de rutinas.
     */
    private final RoutineController controller = new RoutineController();

    /**
     * Crea y configura la vista del fragmento.
     *
     * @param inf Inflater para crear la vista
     * @param cont Contenedor padre
     * @param b Estado guardado
     * @return Vista configurada del fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inf, ViewGroup cont, Bundle b) {
        binding = FragmentWorkoutBinding.inflate(inf, cont, false);
        binding.buttonAddRoutine.setOnClickListener(v -> {
            if (routines.size() >= 10) {
                Toast.makeText(
                        requireContext(),
                        "Maximum 10 routines allowed",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                startActivity(
                        new Intent(requireActivity(), NewRoutineActivity.class)
                );
            }
        });
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
