package com.example.vetra_fitnessapp_tfg.view.adapters.training;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseSet;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;

import java.util.List;

public class RoutineExerciseAdapter extends RecyclerView.Adapter<RoutineExerciseAdapter.VH> {
    private final List<RoutineExercise> items;

    public RoutineExerciseAdapter(List<RoutineExercise> items) {
        this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine_exercise, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RoutineExercise re = items.get(pos);

        // Cabecera: imagen + nombre
        h.tvName.setText(re.getExercise().getName());
        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);

        // Limpiamos contenedor de series
        h.llSets.removeAllViews();

        // --- Fila de encabezado Set / kg / Reps ---
        View header = LayoutInflater.from(h.llSets.getContext())
                .inflate(R.layout.item_routine_set, h.llSets, false);
        TextView tvSetH = header.findViewById(R.id.tvSetNumber);
        TextView tvWgtH = header.findViewById(R.id.tvWeight);
        TextView tvRepsH= header.findViewById(R.id.tvReps);
        tvSetH.setText("Set");
        tvWgtH.setText("kg");
        tvRepsH.setText("Reps");
        // fondo transparente para header
        header.setBackgroundColor(Color.TRANSPARENT);
        h.llSets.addView(header);

        // Inflar cada set existente
        for (ExerciseSet set : re.getSets()) {
            View row = LayoutInflater.from(h.llSets.getContext())
                    .inflate(R.layout.item_routine_set, h.llSets, false);
            TextView tvNum = row.findViewById(R.id.tvSetNumber);
            TextView tvW   = row.findViewById(R.id.tvWeight);
            TextView tvR   = row.findViewById(R.id.tvReps);
            tvNum.setText(String.valueOf(set.getSetNumber()));
            tvW.setText(String.valueOf(set.getWeight()));
            tvR.setText(String.valueOf(set.getReps()));

            // Fila par → fondo blanco
            if (set.getSetNumber() % 2 == 0) {
                row.setBackgroundColor(Color.WHITE);
            }
            h.llSets.addView(row);
        }

        // Botón "+ Add set"
        h.btnAddSet.setOnClickListener(v -> {
            re.addSet();
            notifyItemChanged(pos);
        });
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName;
        LinearLayout llSets;
        Button btnAddSet;

        VH(View item) {
            super(item);
            ivThumb = item.findViewById(R.id.ivExerciseThumb);
            tvName  = item.findViewById(R.id.tvExerciseName);
            llSets  = item.findViewById(R.id.llSetsContainer);
            btnAddSet = item.findViewById(R.id.btnAddSet);
        }
    }
}