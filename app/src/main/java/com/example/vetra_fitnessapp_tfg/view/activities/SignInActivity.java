package com.example.vetra_fitnessapp_tfg.view.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetra_fitnessapp_tfg.R;
import com.google.android.material.card.MaterialCardView;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        // Botón de iniciar sesión
        Button btnSignIn = findViewById(R.id.buttonSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Login pressed", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón de ggogle
        MaterialCardView btnGoogle = findViewById(R.id.buttonGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Google pressed", Toast.LENGTH_SHORT).show();
            }
        });

        // Texto "Forget your password"
        TextView textForgetPassword = findViewById(R.id.textViewForgetPassword);
        textForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Forgot password pressed", Toast.LENGTH_SHORT).show();
            }
        });

        // Texto "Sign up here"
        TextView textSignUp = findViewById(R.id.textViewSignUp);
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Sign up pressed", Toast.LENGTH_SHORT).show();
            }
        });

    }
}