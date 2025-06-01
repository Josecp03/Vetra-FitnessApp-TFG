package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivitySignUpBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

/**
 * Actividad para el registro de nuevos usuarios.
 * Permite crear cuentas mediante email/contraseña y Google Sign-In.
 * Incluye validación de contraseñas seguras y verificación de email.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class SignUpActivity extends AppCompatActivity {

    /** Binding para acceder a las vistas de la actividad */
    private ActivitySignUpBinding binding;

    /** Tag para logging */
    private static final String TAG = SignUpActivity.class.getSimpleName();

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
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Obtiene el ID del cliente de Google
                .requestEmail() // Solicitar el correo electrónico
                .build(); // Construir el objeto GoogleSignInOptions
        googleClient = GoogleSignIn.getClient(this, googleOptions);

        binding.buttonBack.setOnClickListener(v -> {
            Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);
        });
        binding.buttonSignUp.setOnClickListener(v -> signUpWithEmail());
        binding.buttonGoogle.setOnClickListener(v-> signInWithGoogle());
    }

    /**
     * Realiza el registro con email y contraseña.
     * Valida los campos, verifica la fortaleza de la contraseña y crea la cuenta en Firebase.
     */
    private void signUpWithEmail() {
        String email = binding.editTextEmail.getText().toString().trim();
        String pass  = binding.editTextPassword.getText().toString().trim();
        String username = binding.editTextUserName.getText().toString().trim();
        if (email.isEmpty() || pass.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Email, username and password are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isPasswordStrong(pass)) {
            Toast.makeText(
                    this,
                    "Please enter a stronger password (chars, lowercase, uppercase, digit, special)",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this,
                                    "Account already registered. Use another provider",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(this,
                                    "Error registering: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                        return;
                    }
                    FirebaseUser user = mAuth.getCurrentUser();
                    UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build();

                    user.updateProfile(req).addOnCompleteListener(uTask -> {
                        user.sendEmailVerification()
                                .addOnCompleteListener(vTask -> {
                                    if (vTask.isSuccessful()) {
                                        Toast.makeText(
                                                SignUpActivity.this,
                                                "Verification email sent. Please check your inbox (and spam).",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    } else {
                                        Toast.makeText(
                                                SignUpActivity.this,
                                                "Failed to send verification email.",
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                    Intent intent = new Intent(
                                            SignUpActivity.this,
                                            VerifyEmailActivity.class
                                    );
                                    intent.addFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );
                                    startActivity(intent);
                                    finish();
                                });
                    });
                });
    }

    /**
     * Verifica si una contraseña cumple con los criterios de seguridad.
     * Debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, dígitos y caracteres especiales.
     *
     * @param password Contraseña a validar
     * @return true si la contraseña es segura, false en caso contrario
     */
    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) return false;
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";
        return password.matches(pattern);
    }

    /**
     * Autentica al usuario en Firebase usando las credenciales de Google para registro.
     * Solo permite registro de nuevos usuarios, no inicio de sesión de usuarios existentes.
     *
     * @param acct Cuenta de Google obtenida del sign-in
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (isNew) {
                            String googleName = acct.getDisplayName();
                            UserProfileChangeRequest updateRequest =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(googleName)
                                            .build();
                            user.updateProfile(updateRequest).addOnCompleteListener(updateTask -> navigateToUserSetUpActivity());
                        } else {
                            Toast.makeText(this, "Account already exists. Please sign in", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            googleClient.signOut();
                            finish();
                        }
                    } else {
                        Log.e(TAG, "Autenticación con google fallida");
                    }
                });
    }

    /**
     * Maneja el resultado del proceso de Google Sign-In para registro.
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
     * Inicia el proceso de registro con Google.
     */
    private void signInWithGoogle() {
        Intent intent = googleClient.getSignInIntent();
        googleSignInLauncher.launch(intent);
    }

    /**
     * Navega a la actividad de configuración de usuario.
     */
    private void navigateToUserSetUpActivity() {
        Intent intent = new Intent(SignUpActivity.this, UserSetUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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