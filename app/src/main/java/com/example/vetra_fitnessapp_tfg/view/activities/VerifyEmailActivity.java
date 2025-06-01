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

/**
 * Actividad para la verificación de email del usuario.
 * Bloquea el acceso a la aplicación hasta que el usuario verifique su email.
 * Proporciona opciones para verificar o cancelar el proceso de registro.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class VerifyEmailActivity extends AppCompatActivity {

    /** Instancia de Firebase Auth para autenticación */
    private FirebaseAuth mAuth;

    /** Botón para continuar después de verificar el email */
    private Button buttonContinue;

    /** Botón para detener el proceso de verificación */
    private Button buttonStopProcess;

    /**
     * Método llamado al crear la actividad.
     * Inicializa Firebase Auth y configura los listeners de los botones.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        setFinishOnTouchOutside(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        mAuth = FirebaseAuth.getInstance();
        buttonContinue    = findViewById(R.id.buttonContinue);
        buttonStopProcess = findViewById(R.id.buttonStopProcess);

        buttonContinue.setOnClickListener(v -> checkEmailVerified());
        buttonStopProcess.setOnClickListener(v -> showConfirmStopDialog());
    }

    /**
     * Verifica si el email del usuario ha sido verificado.
     * Recarga el estado del usuario y navega según el resultado.
     */
    private void checkEmailVerified() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            navigateToSignIn();
            return;
        }
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

    /**
     * Muestra un diálogo de confirmación para detener el proceso de verificación.
     * Permite al usuario cancelar el registro y eliminar la cuenta creada.
     */
    private void showConfirmStopDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(
                this,
                R.style.BottomSheetDialogTheme
        );
        dialog.setContentView(R.layout.dialog_confirm_exit_account_creation);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
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

    /**
     * Método llamado al iniciar la actividad.
     * Verifica el estado del usuario y navega según corresponda.
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            navigateToSignIn();
        } else if (!user.isEmailVerified()) {
        } else {
            navigateTo(UserSetUpActivity.class);
        }
    }

    /**
     * Bloquea el botón de retroceso y muestra el diálogo de confirmación.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showConfirmStopDialog();
    }

    /**
     * Navega a la actividad de inicio de sesión.
     */
    private void navigateToSignIn() {
        startActivity(new Intent(this, SignInActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    /**
     * Navega a la actividad de registro.
     */
    private void navigateToSignUp() {
        startActivity(new Intent(this, SignUpActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    /**
     * Navega a la clase especificada limpiando el back-stack.
     *
     * @param cls Clase de la actividad a la que navegar
     */
    private void navigateTo(Class<?> cls) {
        startActivity(new Intent(this, cls)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
