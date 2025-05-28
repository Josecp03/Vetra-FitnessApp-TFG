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
        String displayName = toCamelCase(re.getExercise().getName());

        // 1) Cabecera fija
        h.tvName.setText(displayName);
        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);

        // 2) Leemos estado siempre dinámicamente
        boolean expanded = re.isExpanded();

        // 3) Dropdown icon y toggle correcto
        h.btnDropdown.setImageResource(
                expanded
                        ? R.drawable.ic_drop_down
                        : R.drawable.ic_drop_up
        );
        h.btnDropdown.setOnClickListener(v -> {
            // Toggle basado en el estado real
            re.setExpanded(!re.isExpanded());
            notifyItemChanged(pos);
        });

        // 4) Limpiar siempre antes de repoblar
        h.llSets.removeAllViews();

        if (expanded) {
            // 5.a) Mostrar divisor, sets y botones
            h.divider.setVisibility(View.VISIBLE);
            h.llSets.setVisibility(View.VISIBLE);
            h.btnAddSet.setVisibility(View.VISIBLE);
            h.btnRemoveSet.setVisibility(View.VISIBLE);

            // Cabecera de mini-tabla
            View header = LayoutInflater.from(h.llSets.getContext())
                    .inflate(R.layout.item_routine_set, h.llSets, false);
            ((TextView) header.findViewById(R.id.tvSetNumber)).setText("Set");
            ((TextView) header.findViewById(R.id.tvWeight))   .setText("kg");
            ((TextView) header.findViewById(R.id.tvReps))     .setText("Reps");
            h.llSets.addView(header);

            // Filas de sets
            for (ExerciseSet set : re.getSets()) {
                View row = LayoutInflater.from(h.llSets.getContext())
                        .inflate(R.layout.item_routine_set, h.llSets, false);
                ((TextView) row.findViewById(R.id.tvSetNumber))
                        .setText(String.valueOf(set.getSetNumber()));
                ((TextView) row.findViewById(R.id.tvWeight))
                        .setText(String.valueOf(set.getWeight()));
                ((TextView) row.findViewById(R.id.tvReps))
                        .setText(String.valueOf(set.getReps()));
                if (set.getSetNumber() % 2 == 0) {
                    row.setBackgroundColor(Color.WHITE);
                }
                h.llSets.addView(row);
            }
        } else {
            // 5.b) Ocultar todo lo expandible
            h.divider.setVisibility(View.GONE);
            h.llSets.setVisibility(View.GONE);
            h.btnAddSet.setVisibility(View.GONE);
            h.btnRemoveSet.setVisibility(View.GONE);
        }

        // 6) Listeners añadir/quitar
        h.btnAddSet.setOnClickListener(v -> {
            if (re.getSets().size() < MAX_SETS) {
                re.addSet();
                // forzamos rebind para recalcular visibilidad y contenido
                notifyItemChanged(pos);
            } else {
                Toast.makeText(
                        h.itemView.getContext(),
                        "Maximum of " + MAX_SETS + " sets allowed.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        h.btnRemoveSet.setOnClickListener(v -> {
            if (re.getSets().size() > 1) {
                re.getSets().remove(re.getSets().size() - 1);
                notifyItemChanged(pos);
            }
        });

        // 7) Menú y navegación (sin cambios)
        h.btnMore.setOnClickListener(v -> showDeleteDialog(h, displayName, pos));
        h.itemView.setOnLongClickListener(v -> {
            showDeleteDialog(h, displayName, pos);
            return true;
        });
        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(h.itemView.getContext(), ExerciseDetailActivity.class);
            i.putExtra("exercise", re.getExercise());
            h.itemView.getContext().startActivity(i);
        });
    }



    @Override public int getItemCount() {
        return items.size();
    }

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
        Button       btnRemoveSet;
        ImageButton  btnMore;
        ImageButton  btnDropdown;
        View         divider;

        VH(View item) {
            super(item);
            ivThumb      = item.findViewById(R.id.ivExerciseThumb);
            tvName       = item.findViewById(R.id.tvExerciseName);
            llSets       = item.findViewById(R.id.llSetsContainer);
            btnAddSet    = item.findViewById(R.id.btnAddSet);
            btnRemoveSet = item.findViewById(R.id.btnRemoveSet);
            btnMore      = item.findViewById(R.id.btnMore);
            btnDropdown  = item.findViewById(R.id.btnDropdown);
            divider      = item.findViewById(R.id.divider);
        }
    }
}