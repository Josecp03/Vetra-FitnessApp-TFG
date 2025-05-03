package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.example.vetra_fitnessapp_tfg.view.activities.training.NewRoutineActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Prefs.isRoutineInProgress(this)) {
            Intent i = new Intent(this, NewRoutineActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }

        // Obtener el usuario actual de Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Comprobar si no hay usuario logueado
        if (user == null) {

            // Navegar a SignInActivity
            navigateToSignInActivity();
            return;

        }

        // Consultamos Firestore para ver si su perfil ya existe
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(this::handleProfileSnapshot)

                // Si hay algún error, forzamos a completar perfil
                .addOnFailureListener(e -> {
                    navigateToUserSetUpActivity();
                });

    }

    private void handleProfileSnapshot(DocumentSnapshot doc) {

        // Variable para saber si el perfil ya existe
        boolean hasAllFields = doc.exists()
                        && doc.contains("real_name")
                        && doc.contains("last_name")
                        && doc.contains("age")
                        && doc.contains("height")
                        && doc.contains("weight")
                        && doc.contains("user_calories");

        // Comprobar si el perfil tiene todos los campos
        if (hasAllFields) {

            // Redirigir a MainActivity
            navigateToMainActivity();

        } else {

            // Redirigir a UserSetUpActivity
            navigateToUserSetUpActivity();

        }


    }


    private void navigateToSignInActivity() {

        // Crear el intent para navegar a SignInActivity
        Intent intent = new Intent(StartupActivity.this, SignInActivity.class);

        // Limpiar el back-stack para que no pueda volver atrás
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Lanzar el intent
        startActivity(intent);

        // Cerrar esta Activity
        finish();

    }

    private void navigateToUserSetUpActivity() {

        // Crear el intent para navegar a SignInActivity
        Intent intent = new Intent(StartupActivity.this, UserSetUpActivity.class);

        // Limpiar el back-stack para que no pueda volver atrás
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Lanzar el intent
        startActivity(intent);

        // Cerrar esta Activity
        finish();

    }

    private void navigateToMainActivity() {

        // Crear el intent para navegar a SignInActivity
        Intent intent = new Intent(StartupActivity.this, MainActivity.class);

        // Limpiar el back-stack para que no pueda volver atrás
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Lanzar el intent
        startActivity(intent);

        // Cerrar esta Activity
        finish();

    }

}
