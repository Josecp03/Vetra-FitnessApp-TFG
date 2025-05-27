package com.example.vetra_fitnessapp_tfg.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button buttonContinue;
    private Button buttonStopProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        // Impide que al tocar fuera se cierre la Activity
        setFinishOnTouchOutside(false);
        // Quita la flecha de "up" si hay Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        mAuth = FirebaseAuth.getInstance();
        buttonContinue    = findViewById(R.id.buttonContinue);
        buttonStopProcess = findViewById(R.id.buttonStopProcess);

        buttonContinue.setOnClickListener(v -> checkEmailVerified());
        buttonStopProcess.setOnClickListener(v -> showConfirmStopDialog());
    }

    private void checkEmailVerified() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            navigateToSignIn();
            return;
        }
        // Refrescar estado
        user.reload().addOnCompleteListener(t -> {
            if (user.isEmailVerified()) {
                navigateTo(UserSetUpActivity.class);
            } else {
                Toast.makeText(
                        this,
                        "Email not verified yet.\nPlease check your inbox or spam folder.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void showConfirmStopDialog() {
        // Usamos el constructor que permite pasar un estilo
        BottomSheetDialog dialog = new BottomSheetDialog(
                this,
                R.style.BottomSheetDialogTheme // asegúrate de que en tu styles.xml tienes este tema con windowBackground transparente
        );
        dialog.setContentView(R.layout.dialog_confirm_exit_account_creation);

        // Hacemos transparente el fondo del contenedor para respetar las esquinas redondeadas
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Permitimos que al tocar fuera el diálogo se cierre
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        Button btnExit = dialog.findViewById(R.id.buttonExitProcess);
        btnExit.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.delete().addOnCompleteListener(delTask -> {
                    if (delTask.isSuccessful()) {
                        mAuth.signOut();
                        Toast.makeText(
                                this,
                                "Process stopped. Account deleted.",
                                Toast.LENGTH_SHORT
                        ).show();
                        navigateToSignUp();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not delete account: " + delTask.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            } else {
                navigateToSignUp();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // forzar que se quede aquí si no ha verificado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            navigateToSignIn();
        } else if (!user.isEmailVerified()) {
            // nada: permanece en esta pantalla
        } else {
            navigateTo(UserSetUpActivity.class);
        }
    }

    // bloquear botón atrás
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showConfirmStopDialog();
    }

    private void navigateToSignIn() {
        startActivity(new Intent(this, SignInActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void navigateToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void navigateTo(Class<?> cls) {
        startActivity(new Intent(this, cls)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
