package com.example.vetra_fitnessapp_tfg.view.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        // Botón de iniciar sesión
        Button btnSignIn = findViewById(R.id.buttonSendRecovery);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Login pressed", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón de Google
        MaterialCardView btnGoogle = findViewById(R.id.buttonGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Google pressed", Toast.LENGTH_SHORT).show();
            }
        });

        // Texto "Forget your password"
        TextView textForgetPassword = findViewById(R.id.textViewForgetPassword);
        textForgetPassword.setOnClickListener(v -> showForgotPasswordDialog());


        // Texto "Sign up here"
        TextView textSignUp = findViewById(R.id.textViewSignUp);
        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignInActivity.this, "Sign up pressed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void showForgotPasswordDialog() {
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.dialog_recover_password, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(dialogView);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.show();

        // Forzar altura máxima
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }

    }

}