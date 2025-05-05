package com.example.vetra_fitnessapp_tfg.view.adapters.training;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseSet;
import com.example.vetra_fitnessapp_tfg.model.training.RoutineExercise;
import com.example.vetra_fitnessapp_tfg.view.activities.training.ExerciseDetailActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Objects;

public class RoutineExerciseAdapter extends RecyclerView.Adapter<RoutineExerciseAdapter.VH> {
    private final List<RoutineExercise> items;
    private static final int MAX_SETS = 15;

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
        String rawName     = re.getExercise().getName();
        String displayName = toCamelCase(rawName);

        // 1) Cabecera: imagen + nombre en CamelCase
        h.tvName.setText(displayName);
        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);

        // 2) Limpia contenedor de series
        h.llSets.removeAllViews();

        // 3) Fila de encabezado
        View header = LayoutInflater.from(h.llSets.getContext())
                .inflate(R.layout.item_routine_set, h.llSets, false);
        TextView tvSetH  = header.findViewById(R.id.tvSetNumber);
        TextView tvWgtH  = header.findViewById(R.id.tvWeight);
        TextView tvRepsH = header.findViewById(R.id.tvReps);
        tvSetH.setText("Set");
        tvWgtH.setText("kg");
        tvRepsH.setText("Reps");
        h.llSets.addView(header);

        // 4) Series existentes
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

        // 5) + Add set con límite
        h.btnAddSet.setOnClickListener(v -> {
            if (re.getSets().size() < MAX_SETS) {
                re.addSet();
                notifyItemChanged(pos);
            } else {
                Toast.makeText(
                        h.itemView.getContext(),
                        "Maximum of " + MAX_SETS + " sets allowed.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // 6) Más opciones → diálogo eliminar
        h.btnMore.setOnClickListener(v ->
                showDeleteDialog(h, displayName, pos)
        );
        // 7) Long‑press también abre diálogo eliminar
        h.itemView.setOnLongClickListener(v -> {
            showDeleteDialog(h, displayName, pos);
            return true;
        });

        // 8) Click sobre todo el ítem → abre ExerciseDetailActivity
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(
                    h.itemView.getContext(),
                    ExerciseDetailActivity.class
            );
            i.putExtra("exercise", re.getExercise());
            h.itemView.getContext().startActivity(i);
        });
    }

    @Override public int getItemCount() {
        return items.size();
    }

    /** Convierte texto a CamelCase. */
    private String toCamelCase(String input) {
        String[] parts = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String word = parts[i];
            if (word.isEmpty()) continue;
            String[] hyph = word.split("-");
            for (int j = 0; j < hyph.length; j++) {
                String w = hyph[j];
                sb.append(Character.toUpperCase(w.charAt(0)));
                if (w.length() > 1) sb.append(w.substring(1));
                if (j < hyph.length - 1) sb.append("-");
            }
            if (i < parts.length - 1) sb.append(" ");
        }
        return sb.toString();
    }

    /** Muestra diálogo para confirmar borrado. */
    private void showDeleteDialog(VH h, String displayName, int pos) {
        View dlg = LayoutInflater.from(h.itemView.getContext())
                .inflate(R.layout.dialog_delete_exercise, null);
        BottomSheetDialog sheet = new BottomSheetDialog(
                h.itemView.getContext(),
                R.style.BottomSheetDialogTheme
        );
        sheet.setContentView(dlg);
        Objects.requireNonNull(sheet.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = dlg.findViewById(R.id.textTitleDeleteExercise);
        tvTitle.setText(displayName);

        MaterialButton btnDelete = dlg.findViewById(R.id.buttonDiscardRoutineConfirm);
        btnDelete.setOnClickListener(x -> {
            items.remove(pos);
            notifyItemRemoved(pos);
            sheet.dismiss();
        });

        sheet.show();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView    ivThumb;
        TextView     tvName;
        LinearLayout llSets;
        Button       btnAddSet;
        ImageButton  btnMore;

        VH(View item) {
            super(item);
            ivThumb   = item.findViewById(R.id.ivExerciseThumb);
            tvName    = item.findViewById(R.id.tvExerciseName);
            llSets    = item.findViewById(R.id.llSetsContainer);
            btnAddSet = item.findViewById(R.id.btnAddSet);
            btnMore   = item.findViewById(R.id.btnMore);
        }
    }
}