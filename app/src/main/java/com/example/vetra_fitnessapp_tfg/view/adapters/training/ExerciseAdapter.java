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

/**
 * Adaptador para mostrar ejercicios en un RecyclerView.
 * Maneja la visualización de ejercicios con imagen, nombre y músculo objetivo.
 * Proporciona callbacks para añadir ejercicios y ver detalles.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    /**
     * Interfaz de callback para manejar clics en el botón de añadir ejercicio.
     */
    public interface OnAddClickListener {
        void onAdd(Exercise exercise);
    }

    /**
     * Interfaz de callback para manejar clics en elementos de ejercicio.
     */
    public interface OnItemClickListener {
        void onItemClick(Exercise exercise);
    }

    /**
     * Lista de ejercicios a mostrar en el adaptador.
     */
    private final List<Exercise> items;

    /**
     * Listener para manejar clics en el botón de añadir.
     */
    private final OnAddClickListener addListener;

    /**
     * Listener para manejar clics en elementos de ejercicio.
     */
    private final OnItemClickListener itemListener;


    /**
     * Constructor del adaptador.
     *
     * @param items Lista de ejercicios a mostrar
     * @param addListener Listener para el botón de añadir
     * @param itemListener Listener para clics en elementos
     */
    public ExerciseAdapter(List<Exercise> items,
                           OnAddClickListener addListener,
                           OnItemClickListener itemListener) {
        this.items = items;
        this.addListener = addListener;
        this.itemListener = itemListener;
    }

    /**
     * Obtiene la lista de ejercicios del adaptador.
     *
     * @return Lista mutable de ejercicios
     */
    public List<Exercise> getItems() {
        return items;
    }

    /**
     * Crea un ViewHolder para un elemento de ejercicio.
     *
     * @param parent ViewGroup padre
     * @param viewType Tipo de vista
     * @return Nuevo ViewHolder configurado
     */
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Vincula datos a un ViewHolder específico.
     *
     * @param h ViewHolder a configurar
     * @param pos Posición del elemento en la lista
     */
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

    /**
     * Convierte texto a formato CamelCase.
     *
     * @param s Texto a convertir
     * @return Texto en CamelCase
     */
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

    /**
     * ViewHolder para elementos de ejercicio.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * ImageView para mostrar la imagen del ejercicio.
         */
        ImageView ivExercise;

        /**
         * TextViews para nombre y músculo objetivo.
         */
        TextView tvName, tvTarget;

        /**
         * Botón para añadir el ejercicio.
         */
        ImageButton btnAdd;

        /**
         * Constructor del ViewHolder.
         *
         * @param itemView Vista del elemento
         */
        ViewHolder(View itemView) {
            super(itemView);
            ivExercise = itemView.findViewById(R.id.ivExercise);
            tvName     = itemView.findViewById(R.id.tvExerciseName);
            tvTarget   = itemView.findViewById(R.id.tvExerciseTarget);
            btnAdd     = itemView.findViewById(R.id.btnAddExercise);
        }
    }

}
