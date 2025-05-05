package com.example.vetra_fitnessapp_tfg.controller.training;

import com.example.vetra_fitnessapp_tfg.model.training.Routine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class RoutineController {

    private final FirebaseFirestore db   = FirebaseFirestore.getInstance();
    private final FirebaseAuth      auth = FirebaseAuth.getInstance();

    /**
     * Carga todas las rutinas del usuario logueado y las pasa al callback onSuccess.
     * Si hay error, se imprime la traza.
     */
    public void loadUserRoutines(Consumer<List<Routine>> onSuccess) {
        String uid = auth.getCurrentUser().getUid();

        db.collection("routines")
                .whereEqualTo("user_id", uid)
                .get()
                .addOnSuccessListener(qs -> {
                    List<Routine> lista = new ArrayList<>();
                    for (var doc : qs.getDocuments()) {
                        Routine r = doc.toObject(Routine.class);
                        if (r != null) {
                            // asignar el ID del documento a routineId
                            r.setRoutineId(doc.getId());
                            lista.add(r);
                        }
                    }

                    // Ordenamos por nombre, ignorando mayúsculas,
                    // y dejando los null al final.
                    lista.sort(
                            Comparator.comparing(
                                    Routine::getRoutineName,
                                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                            )
                    );

                    onSuccess.accept(lista);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}
