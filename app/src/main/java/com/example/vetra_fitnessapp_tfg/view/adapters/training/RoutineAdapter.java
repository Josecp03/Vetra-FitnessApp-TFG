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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adaptador para mostrar rutinas de entrenamiento en un RecyclerView.
 * Permite iniciar rutinas y eliminarlas mediante menús contextuales.
 * Integra con Firebase Firestore para operaciones de eliminación.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.VH> {

    /**
     * Lista de rutinas a mostrar en el adaptador.
     */
    private final List<Routine> items;
    /**
     * Instancia de Firestore para operaciones de base de datos.
     */
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Instancia de autenticación de Firebase.
     */
    private final FirebaseAuth      auth = FirebaseAuth.getInstance();

    /**
     * Constructor del adaptador.
     *
     * @param items Lista de rutinas a mostrar
     */
    public RoutineAdapter(List<Routine> items) {
        this.items = items;
    }

    /**
     * Crea un ViewHolder para un elemento de rutina.
     *
     * @param parent ViewGroup padre
     * @param viewType Tipo de vista
     * @return Nuevo ViewHolder configurado
     */
    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_routine, parent, false);
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
        Routine r = items.get(pos);
        h.tvName.setText(r.getRoutineName());

        h.btnStart.setOnClickListener(v -> {
            Intent i = new Intent(v.getContext(), StartRoutineActivity.class);
            i.putExtra(StartRoutineActivity.EXTRA_ROUTINE, r);
            v.getContext().startActivity(i);
        });

        h.btnMenu.setOnClickListener(v -> showOptionsDialog(h, pos));
        h.itemView.setOnLongClickListener(v -> { showOptionsDialog(h, pos); return true; });
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
     * Muestra el diálogo de opciones para una rutina específica.
     * Actualmente solo incluye la opción de eliminar.
     *
     * @param h ViewHolder del elemento seleccionado
     * @param pos Posición del elemento en la lista
     */
    private void showOptionsDialog(VH h, int pos) {
        if (pos < 0 || pos >= items.size()) {
            return;
        }

        BottomSheetDialog sheet = new BottomSheetDialog(h.itemView.getContext(), R.style.BottomSheetDialogTheme);
        View dlg = LayoutInflater.from(h.itemView.getContext())
                .inflate(R.layout.dialog_delete_routine, null);
        sheet.setContentView(dlg);

        MaterialButton btnDelete = dlg.findViewById(R.id.buttonDeleteRoutineConfirm);
        btnDelete.setOnClickListener(x -> {
            String uid = auth.getCurrentUser().getUid();
            if (pos >= 0 && pos < items.size()) {
                db.collection("users")
                        .document(uid)
                        .collection("routines")
                        .document(items.get(pos).getId())
                        .delete();

                items.remove(pos);
                notifyItemRemoved(pos);
            }
            sheet.dismiss();
        });
        sheet.show();
    }

    /**
     * ViewHolder para elementos de rutina.
     */
    static class VH extends RecyclerView.ViewHolder {
        /**
         * TextView para mostrar el nombre de la rutina.
         */
        TextView       tvName;

        /**
         * Botón para iniciar la rutina.
         */
        MaterialButton btnStart;

        /**
         * Botón de menú para opciones adicionales.
         */
        View           btnMenu;

        /**
         * Constructor del ViewHolder.
         *
         * @param item Vista del elemento
         */
        VH(View item) {
            super(item);
            tvName   = item.findViewById(R.id.tvRoutineName);
            btnStart = item.findViewById(R.id.btnStartRoutine);
            btnMenu  = item.findViewById(R.id.btnMenu);
        }
    }
}
