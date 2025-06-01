package com.example.vetra_fitnessapp_tfg.view.adapters.training;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

/**
 * Adaptador para mostrar ejercicios durante la ejecución de una rutina.
 * Permite expandir/colapsar ejercicios, editar pesos y repeticiones,
 * y marcar series como completadas. Maneja el estado visual de las
 * series completadas y la persistencia de datos en tiempo real.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class StartRoutineAdapter
        extends RecyclerView.Adapter<StartRoutineAdapter.VH> {

    /**
     * Lista de ejercicios de rutina en ejecución.
     */
    private final List<RoutineExercise> items;

    /**
     * Constructor del adaptador.
     *
     * @param items Lista de ejercicios de rutina en ejecución
     */
    public StartRoutineAdapter(List<RoutineExercise> items) {
        this.items = items;
    }

    /**
     * Crea un ViewHolder para un elemento de ejercicio en ejecución.
     *
     * @param parent ViewGroup padre
     * @param viewType Tipo de vista
     * @return Nuevo ViewHolder configurado
     */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_start_routine_exercise, parent, false);
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
        Glide.with(h.ivThumb.getContext())
                .load(re.getExercise().getGifUrl())
                .circleCrop()
                .into(h.ivThumb);
        h.tvName.setText(re.getExercise().getName());
        boolean expanded = re.isExpanded();
        h.btnDropdown.setImageResource(
                expanded
                        ? R.drawable.ic_drop_down
                        : R.drawable.ic_drop_up
        );
        h.btnDropdown.setOnClickListener(v -> {
            re.setExpanded(!expanded);
            notifyItemChanged(pos);
        });
        h.dividerSets.setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.setsHeader .setVisibility(expanded ? View.VISIBLE : View.GONE);
        h.llSets.removeAllViews();
        if (expanded) {
            h.llSets.setVisibility(View.VISIBLE);

            for (ExerciseSet s : re.getSets()) {
                View row = LayoutInflater.from(h.llSets.getContext())
                        .inflate(R.layout.item_start_routine_set, h.llSets, false);

                TextView tvNum = row.findViewById(R.id.tvSetNumber);
                EditText etW   = row.findViewById(R.id.etWeight);
                EditText etR   = row.findViewById(R.id.etReps);
                ImageView ivC  = row.findViewById(R.id.ivCheck);
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
                boolean done = s.isDone();
                ivC.setSelected(done);
                int initialBg = done
                        ? ContextCompat.getColor(row.getContext(), R.color.green_success)
                        : baseBg;
                row.setBackgroundColor(initialBg);
                ivC.setOnClickListener(v -> {
                    String wText = etW.getText().toString().trim();
                    if (!wText.isEmpty()) {
                        s.setWeight(Integer.parseInt(wText));
                    }
                    String rText = etR.getText().toString().trim();
                    if (!rText.isEmpty()) {
                        s.setReps(Integer.parseInt(rText));
                    }
                    boolean nowDone = !s.isDone();
                    s.setDone(nowDone);
                    ivC.setSelected(nowDone);
                    int newBg = nowDone
                            ? ContextCompat.getColor(row.getContext(), R.color.green_success)
                            : baseBg;
                    row.setBackgroundColor(newBg);
                });

                h.llSets.addView(row);
            }
        } else {
            h.llSets.setVisibility(View.GONE);
        }
        h.headerContainer.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), ExerciseDetailActivity.class);
            i.putExtra("exercise", re.getExercise());
            v.getContext().startActivity(i);
        });
    }

    /**
     * Obtiene el número total de elementos en el adaptador.
     *
     * @return Número de elementos
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder para elementos de ejercicio en ejecución.
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
         * Contenedor del encabezado del ejercicio.
         */
        View         headerContainer;

        /**
         * Botón para expandir/colapsar el ejercicio.
         */
        ImageButton btnDropdown;

        /**
         * Divisor visual entre secciones.
         */
        View         dividerSets;

        /**
         * Encabezado de la tabla de series.
         */
        LinearLayout setsHeader;

        /**
         * Constructor del ViewHolder.
         *
         * @param item Vista del elemento
         */
        VH(View item) {
            super(item);
            ivThumb = item.findViewById(R.id.ivThumb);
            tvName  = item.findViewById(R.id.tvName);
            llSets  = item.findViewById(R.id.llSets);
            headerContainer = item.findViewById(R.id.headerContainer);
            btnDropdown = item.findViewById(R.id.btnDropdown);
            dividerSets   = item.findViewById(R.id.dividerSets);
            setsHeader    = item.findViewById(R.id.setsHeader);
        }
    }
}
