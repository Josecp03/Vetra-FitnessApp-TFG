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

/**
 * Adaptador para mostrar ejercicios dentro de una rutina en creación.
 * Maneja la expansión/colapso de ejercicios, gestión de series,
 * y eliminación de ejercicios de la rutina. Incluye validaciones
 * de límites máximos de series.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class RoutineExerciseAdapter extends RecyclerView.Adapter<RoutineExerciseAdapter.VH> {
    /**
     * Lista de ejercicios de rutina a mostrar.
     */
    private final List<RoutineExercise> items;
    /**
     * Número máximo de series permitidas por ejercicio.
     */
    private static final int MAX_SETS = 15;

    /**
     * Constructor del adaptador.
     *
     * @param items Lista de ejercicios de rutina a mostrar
     */
    public RoutineExerciseAdapter(List<RoutineExercise> items) {
        this.items = items;
    }

    /**
     * Crea un ViewHolder para un elemento de ejercicio de rutina.
     *
     * @param parent ViewGroup padre
     * @param viewType Tipo de vista
     * @return Nuevo ViewHolder configurado
     */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine_exercise, parent, false);
        return new VH(v);
    }

    /**
     * Vincula datos a un ViewHolder específico.
     *
     * @param h ViewHolder a configurar
     * @param pos Posición del elemento en la lista
     */
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RoutineExercise re = items.get(pos);
        String displayName = toCamelCase(re.getExercise().getName());
        h.tvName.setText(displayName);
        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);
        boolean expanded = re.isExpanded();
        h.btnDropdown.setImageResource(
                expanded
                        ? R.drawable.ic_drop_down
                        : R.drawable.ic_drop_up
        );
        h.btnDropdown.setOnClickListener(v -> {
            re.setExpanded(!re.isExpanded());
            notifyItemChanged(pos);
        });
        h.llSets.removeAllViews();
        if (expanded) {
            h.divider.setVisibility(View.VISIBLE);
            h.llSets.setVisibility(View.VISIBLE);
            h.btnAddSet.setVisibility(View.VISIBLE);
            h.btnRemoveSet.setVisibility(View.VISIBLE);
            View header = LayoutInflater.from(h.llSets.getContext())
                    .inflate(R.layout.item_routine_set, h.llSets, false);
            ((TextView) header.findViewById(R.id.tvSetNumber)).setText("Set");
            ((TextView) header.findViewById(R.id.tvWeight))   .setText("kg");
            ((TextView) header.findViewById(R.id.tvReps))     .setText("Reps");
            h.llSets.addView(header);
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
            h.divider.setVisibility(View.GONE);
            h.llSets.setVisibility(View.GONE);
            h.btnAddSet.setVisibility(View.GONE);
            h.btnRemoveSet.setVisibility(View.GONE);
        }
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
        h.btnRemoveSet.setOnClickListener(v -> {
            if (re.getSets().size() > 1) {
                re.getSets().remove(re.getSets().size() - 1);
                notifyItemChanged(pos);
            }
        });
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

    /**
     * Obtiene el número total de elementos en el adaptador.
     *
     * @return Número de elementos
     */
    @Override public int getItemCount() {
        return items.size();
    }

    /**
     * Convierte texto a formato CamelCase manejando guiones.
     *
     * @param input Texto de entrada a convertir
     * @return Texto convertido a CamelCase
     */
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

    /**
     * Muestra el diálogo de confirmación para eliminar un ejercicio.
     *
     * @param h ViewHolder del elemento
     * @param displayName Nombre del ejercicio a mostrar
     * @param pos Posición del ejercicio en la lista
     */
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

    /**
     * ViewHolder para elementos de ejercicio de rutina.
     */
    static class VH extends RecyclerView.ViewHolder {
        /**
         * ImageView para la miniatura del ejercicio.
         */
        ImageView    ivThumb;

        /**
         * TextView para el nombre del ejercicio.
         */
        TextView     tvName;

        /**
         * Contenedor para las series del ejercicio.
         */
        LinearLayout llSets;

        /**
         * Botones para añadir y quitar series.
         */
        Button       btnAddSet;
        Button       btnRemoveSet;

        /**
         * Botones de menú y expansión.
         */
        ImageButton  btnMore;
        ImageButton  btnDropdown;

        /**
         * Divisor visual entre secciones.
         */
        View         divider;

        /**
         * Constructor del ViewHolder.
         *
         * @param item Vista del elemento
         */
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
