package com.example.vetra_fitnessapp_tfg.view.adapters.training;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    public interface OnAddClickListener {
        void onAdd(Exercise exercise);
    }

    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    private final List<Exercise> items;
    private final OnAddClickListener addListener;
    private final OnItemClickListener itemListener;

    public ExerciseAdapter(List<Exercise> items,
                           OnAddClickListener addListener,
                           OnItemClickListener itemListener) {
        this.items = items;
        this.addListener = addListener;
        this.itemListener = itemListener;
    }

    public List<Exercise> getItems() {
        return items;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_popular_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Exercise ex = items.get(pos);
        h.tvName.setText(toCamelCase(ex.getName()));
        h.tvTarget.setText(toCamelCase(ex.getTarget()));
        Glide.with(h.ivExercise.getContext())
                .load(ex.getGifUrl())
                .circleCrop()
                .into(h.ivExercise);

        h.btnAdd.setOnClickListener(v -> addListener.onAdd(ex));
        h.itemView.setOnClickListener(v -> itemListener.onItemClick(ex));
    }

    @Override public int getItemCount() { return items.size(); }

    private String toCamelCase(String s) {
        StringBuilder out = new StringBuilder();
        for (String w : s.split("\\s+")) {
            if (w.isEmpty()) continue;
            out.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1))
                    .append(" ");
        }
        return out.toString().trim();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExercise;
        TextView tvName, tvTarget;
        ImageButton btnAdd;
        ViewHolder(View itemView) {
            super(itemView);
            ivExercise = itemView.findViewById(R.id.ivExercise);
            tvName     = itemView.findViewById(R.id.tvExerciseName);
            tvTarget   = itemView.findViewById(R.id.tvExerciseTarget);
            btnAdd     = itemView.findViewById(R.id.btnAddExercise);
        }
    }
}
