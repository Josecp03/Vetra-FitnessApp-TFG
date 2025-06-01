package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.DialogRecoverPasswordBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.vetra_fitnessapp_tfg.databinding.ActivitySignInBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Objects;

/**
 * Actividad para el inicio de sesión de usuarios.
 * Permite autenticación mediante email/contraseña y Google Sign-In.
 * También proporciona funcionalidad para recuperación de contraseñas.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class SignInActivity extends AppCompatActivity {

    /** Binding para acceder a las vistas de la actividad */
    private ActivitySignInBinding binding;

    /** Tag para logging */
    private static final String TAG = SignInActivity.class.getSimpleName();

    /** Instancia de Firebase Auth para autenticación */
    private FirebaseAuth mAuth;

    /** Cliente de Google Sign-In */
    private GoogleSignInClient googleClient;

    /** Launcher para manejar el resultado de Google Sign-In */
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleGoogleSignInResult
            );

    /**
     * Método llamado al crear la actividad.
     * Inicializa Firebase Auth, configura Google Sign-In y establece los listeners.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Obtiene el ID del cliente de Google
                .requestEmail() // Solicitar el correo electrónico
                .build(); // Construir el objeto GoogleSignInOptions
        googleClient = GoogleSignIn.getClient(this, googleOptions);

        binding.buttonSignIn.setOnClickListener(v -> signInWithEmail());
        binding.buttonGoogle.setOnClickListener(v -> signInWithGoogle());
        binding.textViewForgetPassword.setOnClickListener(v -> showForgotPasswordDialog());
        binding.textViewSignUp.setOnClickListener(v -> navigateToSignUpActivity());

    }

    /**
     * Muestra el diálogo para recuperación de contraseña.
     * Permite al usuario ingresar su email para recibir un enlace de recuperación.
     */
    private void showForgotPasswordDialog() {
        DialogRecoverPasswordBinding dialogBinding = DialogRecoverPasswordBinding.inflate(getLayoutInflater());
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setContentView(dialogBinding.getRoot());
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        dialogBinding.buttonSendMail.setOnClickListener(v -> {
            String email = dialogBinding.editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                return;
            }
            resetPassword(email, bottomSheetDialog);
        });

        bottomSheetDialog.show();
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    /**
     * Envía un email de recuperación de contraseña al usuario.
     *
     * @param email  Email del usuario para enviar el enlace de recuperación
     * @param dialog Diálogo a cerrar si el envío es exitoso
     */
    private void resetPassword(String email, BottomSheetDialog dialog) {
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = !task.getResult().getSignInMethods().isEmpty();

                        if (exists) {
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(this, resetTask -> {
                                        if (resetTask.isSuccessful()) {
                                            FirebaseAuth.getInstance().signOut();
                                            dialog.dismiss();
                                            Toast.makeText(this, "Recovery link sent. Please check your email and log in again with your new password.", Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(SignInActivity.this, SignInActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.e(TAG, "Error sending recovery email", resetTask.getException());
                                            Toast.makeText(this, "Failed to send recovery email: " + resetTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "This email is not associated with any account", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e(TAG, "Error checking email existence", task.getException());
                        Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Inicia el proceso de autenticación con Google.
     */
    private void signInWithGoogle() {
        Intent intent = googleClient.getSignInIntent();
        googleSignInLauncher.launch(intent);
    }

    /**
     * Maneja el resultado del proceso de Google Sign-In.
     *
     * @param result Resultado de la actividad de Google Sign-In
     */
    private void handleGoogleSignInResult(androidx.activity.result.ActivityResult result) {
        if (result.getData() == null) {
            return;
        }
        try {
            GoogleSignInAccount acct = GoogleSignIn
                    .getSignedInAccountFromIntent(result.getData())
                    .getResult(ApiException.class);
            firebaseAuthWithGoogle(acct);

        } catch (ApiException e) {
            Log.w(TAG, "Autenticación con google cancelada", e);
        }
    }

    /**
     * Autentica al usuario en Firebase usando las credenciales de Google.
     *
     * @param acct Cuenta de Google obtenida del sign-in
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                        if (isNew) {
                            navigateToUserSetUpActivity();
                        } else {
                            navigateToMainActivity();
                        }
                    } else {
                        Log.e(TAG, "Autenticación con google fallida");
                    }
                });
    }

    /**
     * Realiza el inicio de sesión con email y contraseña.
     * Valida los campos y autentica al usuario con Firebase.
     */
    private void signInWithEmail() {
        String email = binding.editTextEmail.getText().toString().trim();
        String pass  = binding.editTextPassword.getText().toString().trim();
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        navigateToMainActivity();
                    } else {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Navega a la actividad de registro.
     */
    private void navigateToSignUpActivity() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);
    }

    /**
     * Navega a la actividad de configuración de usuario.
     */
    private void navigateToUserSetUpActivity() {
        Intent intent = new Intent(SignInActivity.this, UserSetUpActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);
    }

    /**
     * Navega a la actividad principal.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);
    }

    /**
     * Valida si un email tiene un formato correcto.
     *
     * @param email Email a validar
     * @return true si el email es válido, false en caso contrario
     */
    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}