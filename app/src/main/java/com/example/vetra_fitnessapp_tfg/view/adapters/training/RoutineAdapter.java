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

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.VH> {
    private final List<Routine> items;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth      auth = FirebaseAuth.getInstance();


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
            i.putExtra(StartRoutineActivity.EXTRA_ROUTINE, r);
            v.getContext().startActivity(i);
        });

        h.btnMenu.setOnClickListener(v -> showOptionsDialog(h, pos));
        h.itemView.setOnLongClickListener(v -> { showOptionsDialog(h, pos); return true; });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void showOptionsDialog(VH h, int pos) {
        BottomSheetDialog sheet = new BottomSheetDialog(h.itemView.getContext(), R.style.BottomSheetDialogTheme);
        View dlg = LayoutInflater.from(h.itemView.getContext())
                .inflate(R.layout.dialog_delete_routine, null);
        sheet.setContentView(dlg);
        MaterialButton btnDelete = dlg.findViewById(R.id.buttonDeleteRoutineConfirm);
        btnDelete.setOnClickListener(x -> {
            String uid = auth.getCurrentUser().getUid();
            db.collection("users")
                    .document(uid)
                    .collection("routines")
                    .document(items.get(pos).getId())
                    .delete();
            items.remove(pos);
            notifyItemRemoved(pos);
            sheet.dismiss();
        });
        sheet.show();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView       tvName;
        MaterialButton btnStart;
        View           btnMenu;

        VH(View item) {
            super(item);
            tvName   = item.findViewById(R.id.tvRoutineName);
            btnStart = item.findViewById(R.id.btnStartRoutine);
            btnMenu  = item.findViewById(R.id.btnMenu);
        }
    }
}
