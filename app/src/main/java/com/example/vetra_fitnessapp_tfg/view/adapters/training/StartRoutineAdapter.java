package com.example.vetra_fitnessapp_tfg.view.adapters.training;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseSet;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;

import java.util.List;

public class StartRoutineAdapter
        extends RecyclerView.Adapter<StartRoutineAdapter.VH> {

    private final List<RoutineExercise> items;

    public StartRoutineAdapter(List<RoutineExercise> items) {
        this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_start_routine_exercise, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RoutineExercise re = items.get(pos);

        // Cabecera: thumbnail + nombre
        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);
        h.tvName.setText(re.getExercise().getName());

        // Limpiamos contenedor de sets
        h.llSets.removeAllViews();

        // Para cada set:
        for (ExerciseSet s : re.getSets()) {
            View row = LayoutInflater.from(h.llSets.getContext())
                    .inflate(R.layout.item_start_routine_set, h.llSets, false);

            TextView tvNum = row.findViewById(R.id.tvSetNumber);
            TextView tvW   = row.findViewById(R.id.tvWeight);
            TextView tvR   = row.findViewById(R.id.tvReps);
            ImageView ivC  = row.findViewById(R.id.ivCheck);

            tvNum.setText(String.valueOf(s.getSetNumber()));
            tvW  .setText(String.valueOf(s.getWeight()));
            tvR  .setText(String.valueOf(s.getReps()));

            // 1) Filas pares fondo blanco
            if (s.getSetNumber() % 2 == 0) {
                row.setBackgroundColor(Color.WHITE);
            } else {
                // impares: fondo transparente (o gris, según tu drawable)
                row.setBackgroundColor(Color.TRANSPARENT);
            }

            // 2) Toggle checkbox + pintar fila de verde_success
            ivC.setOnClickListener(v -> {
                boolean done = ivC.isSelected();
                ivC.setSelected(!done);

                int bg;
                if (!done) {
                    // ahora marcado → verde_success
                    bg = ContextCompat.getColor(
                            row.getContext(), R.color.green_success
                    );
                } else {
                    // desmarcado → volvemos al fondo par/impar
                    bg = (s.getSetNumber() % 2 == 0)
                            ? Color.WHITE
                            : Color.TRANSPARENT;
                }
                row.setBackgroundColor(bg);
            });

            h.llSets.addView(row);
        }
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView   ivThumb;
        TextView    tvName;
        LinearLayout llSets;

        VH(View item) {
            super(item);
            ivThumb = item.findViewById(R.id.ivThumb);
            tvName  = item.findViewById(R.id.tvName);
            llSets  = item.findViewById(R.id.llSets);
        }
    }
}
