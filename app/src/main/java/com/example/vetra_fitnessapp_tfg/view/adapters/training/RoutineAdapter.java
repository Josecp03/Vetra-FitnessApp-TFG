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
import com.example.vetra_fitnessapp_tfg.view.activities.training.StartRoutineActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.VH> {
    private final List<Routine> items;

    public RoutineAdapter(List<Routine> items) {
        this.items = items;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Routine r = items.get(pos);
        h.tvName.setText(r.getRoutineName());
        h.btnStart.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), StartRoutineActivity.class);
            // aquí paso el objeto entero
            i.putExtra(StartRoutineActivity.EXTRA_ROUTINE, r);
            v.getContext().startActivity(i);
        });
    }

    @Override public int getItemCount() {
        return items.size();
    }

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
