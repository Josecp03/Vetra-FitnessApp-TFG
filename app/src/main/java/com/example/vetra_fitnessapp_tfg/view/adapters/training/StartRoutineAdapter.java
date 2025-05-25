package com.example.vetra_fitnessapp_tfg.view.adapters.training;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.vetra_fitnessapp_tfg.view.activities.training.ExerciseDetailActivity;

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

        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);
        h.tvName.setText(re.getExercise().getName());

        h.llSets.removeAllViews();

        for (ExerciseSet s : re.getSets()) {
            View row = LayoutInflater.from(h.llSets.getContext())
                    .inflate(R.layout.item_start_routine_set, h.llSets, false);

            TextView tvNum   = row.findViewById(R.id.tvSetNumber);
            EditText etW     = row.findViewById(R.id.etWeight);
            EditText etR     = row.findViewById(R.id.etReps);
            ImageView ivC    = row.findViewById(R.id.ivCheck);

            tvNum.setText(String.valueOf(s.getSetNumber()));
            etW.setHint(String.valueOf(s.getWeight()));
            etW.setText("");
            etR.setHint(String.valueOf(s.getReps()));
            etR.setText("");

            etW.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String text = etW.getText().toString();
                    s.setWeight(text.isEmpty() ? 0 : Integer.parseInt(text));
                }
            });
            etR.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    String text = etR.getText().toString();
                    s.setReps(text.isEmpty() ? 0 : Integer.parseInt(text));
                }
            });

            int baseBg = (s.getSetNumber() % 2 == 0)
                    ? Color.WHITE
                    : Color.TRANSPARENT;
            row.setBackgroundColor(baseBg);

            ivC.setOnClickListener(v -> {
                boolean done = ivC.isSelected();
                ivC.setSelected(!done);
                s.setDone(!done);

                int bg = s.isDone()
                        ? ContextCompat.getColor(row.getContext(), R.color.green_success)
                        : baseBg;
                row.setBackgroundColor(bg);
            });

            h.llSets.addView(row);
        }

        h.headerContainer.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ExerciseDetailActivity.class);
            i.putExtra("exercise", re.getExercise());
            v.getContext().startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView    ivThumb;
        TextView     tvName;
        LinearLayout llSets;
        View         headerContainer;

        VH(View item) {
            super(item);
            ivThumb = item.findViewById(R.id.ivThumb);
            tvName  = item.findViewById(R.id.tvName);
            llSets  = item.findViewById(R.id.llSets);
            headerContainer = item.findViewById(R.id.headerContainer);
        }
    }
}
