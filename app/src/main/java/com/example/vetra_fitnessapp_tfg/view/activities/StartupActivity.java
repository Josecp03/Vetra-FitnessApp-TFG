package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class StartupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el usuario actual
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Guardar el estado del perfil en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isProfileComplete = prefs.getBoolean("isProfileComplete", false);

        // Iniciar el intent
        Intent intent;

        // Comprobar el estado del usuario
        if (user == null) {

            // Usuario no logueado → SignIn
            intent = new Intent(this, SignInActivity.class);

        } else if (!isProfileComplete) {

            // Logueado pero perfil incompleto → UserSetUp
            intent = new Intent(this, UserSetUpActivity.class);

        } else {

            // Logueado y perfil completo → Main
            intent = new Intent(this, MainActivity.class);
        }

        // Limpiar el back-stack para que no pueda volver a SignIn
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Lanzar el intent
        startActivity(intent);

        // Finalizar la actividad
        finish();

    }
}
