// view/adapters/training/RoutineExerciseAdapter.java
package com.example.vetra_fitnessapp_tfg.view.adapters.training;

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

public class RoutineExerciseAdapter
        extends RecyclerView.Adapter<RoutineExerciseAdapter.VH> {

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

        // nombre + thumb
        h.tvName.setText(re.getExercise().getName());
        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);

        // cascada: limpias y vuelves a inflar cada set
        h.llSets.removeAllViews();
        for (ExerciseSet set : re.getSets()) {
            View row = LayoutInflater.from(h.llSets.getContext())
                    .inflate(R.layout.item_routine_set, h.llSets, false);
            ((TextView)row.findViewById(R.id.tvSetNumber))
                    .setText(String.valueOf(set.getSetNumber()));
            ((TextView)row.findViewById(R.id.tvWeight))
                    .setText(String.valueOf(set.getWeight()));
            ((TextView)row.findViewById(R.id.tvReps))
                    .setText(String.valueOf(set.getReps()));
            h.llSets.addView(row);
        }

        // añadir nueva serie
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
            ivThumb   = item.findViewById(R.id.ivExerciseThumb);
            tvName    = item.findViewById(R.id.tvExerciseName);
            llSets    = item.findViewById(R.id.llSetsContainer);
            btnAddSet = item.findViewById(R.id.btnAddSet);
        }
    }
}
