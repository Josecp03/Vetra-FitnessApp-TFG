package com.example.vetra_fitnessapp_tfg.view.adapters.training;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.VH> {
    private final List<Routine> items;
    public RoutineAdapter(List<Routine> items) { this.items = items; }
    @Override @NonNull public VH onCreateViewHolder(@NonNull ViewGroup p, int i) {
        View v = LayoutInflater.from(p.getContext())
                .inflate(R.layout.item_routine, p, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Routine r = items.get(pos);
        h.tvName.setText(r.getRoutineName());
        h.btnStart.setOnClickListener(v -> {
//            Intent i = new Intent(v.getContext(), RoutineExecutionActivity.class);
//            i.putExtra("routine_id", r.getId());
//            v.getContext().startActivity(i);
        });
    }
    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName;
        MaterialButton btnStart;
        VH(View item) {
            super(item);
            tvName   = item.findViewById(R.id.tvRoutineName);
            btnStart = item.findViewById(R.id.btnStartRoutine);
        }
    }
}
