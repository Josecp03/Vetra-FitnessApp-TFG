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


public class SignUpActivity extends AppCompatActivity {

    // Atributos
    private ActivitySignUpBinding binding;
    private static final String TAG = SignUpActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleGoogleSignInResult
            );
    private boolean hasNavigated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Obtiene el ID del cliente de Google
                .requestEmail() // Solicitar el correo electrónico
                .build(); // Construir el objeto GoogleSignInOptions
        googleClient = GoogleSignIn.getClient(this, googleOptions);

        // Listener para el botón de volver
        binding.buttonBack.setOnClickListener(v -> {

            // Crear el intent para navegar a SignInActivity
            Intent i = new Intent(SignUpActivity.this, SignInActivity.class);

            // Lanzar el intent
            startActivity(i);

            // Aplicar animación de transición
            overridePendingTransition(R.anim.slide_in_left_fade, R.anim.slide_out_right_fade);

        });

        // Listener para el botón de SignUp
        binding.buttonSignUp.setOnClickListener(v -> signUpWithEmail());

        // Listener para el botón de SignUp con Google
        binding.buttonGoogle.setOnClickListener(v-> signInWithGoogle());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasNavigated) return;

        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null) {
            current.reload().addOnCompleteListener(t -> {
                if (current.isEmailVerified()) {
                    hasNavigated = true;
                    navigateToUserSetUpActivity();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasNavigated) return;
        FirebaseUser current = mAuth.getCurrentUser();
        if (current != null) {
            current.reload().addOnCompleteListener(t -> {
                if (current.isEmailVerified()) {
                    hasNavigated = true;
                    navigateToUserSetUpActivity();
                }
            });
        }
    }

    private void signUpWithEmail() {

        // Obtener los valores de los campos de texto
        String email = binding.editTextEmail.getText().toString().trim();
        String pass  = binding.editTextPassword.getText().toString().trim();
        String username = binding.editTextUserName.getText().toString().trim();

        // Comprobar que los campos no estén vacíos
        if (email.isEmpty() || pass.isEmpty() || username.isEmpty()) {

            // Mostrar un mensaje de error
            Toast.makeText(this, "Email, username and password are required", Toast.LENGTH_SHORT).show();
            return;

        }

        // Comprobar que el email sea válido
        if (!isEmailValid(email)) {

            // Mostrar un mensaje de error
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;

        }

        // Registrar con Firebase
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Nueva cuenta
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();
                        user.updateProfile(req).addOnCompleteListener(u -> {
                            user.sendEmailVerification().addOnCompleteListener(v -> {
                                if (v.isSuccessful()) {
                                    Toast.makeText(
                                            this,
                                            "Verification email sent",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                } else {
                                    Toast.makeText(
                                            this,
                                            "Failed to send verification email",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            });
                        });
                    } else {
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            // Ya existía: intentar sign-in para refrescar estado
                            mAuth.signInWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener(this, signInTask -> {
                                        if (signInTask.isSuccessful()) {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            user.reload().addOnCompleteListener(r -> {
                                                if (user.isEmailVerified()) {
                                                    hasNavigated = true;
                                                    navigateToUserSetUpActivity();
                                                } else {
                                                    Toast.makeText(
                                                            this,
                                                            "Account exists. Please verify your email.",
                                                            Toast.LENGTH_SHORT
                                                    ).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(
                                                    this,
                                                    "Account exists. Please sign in.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(
                                    this,
                                    "Error registering: " + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        // Crear un objeto de autenticación con el token de Google
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        // Iniciar la autenticación con Firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    // Comporbar si la autenticación fue exitosa
                    if (task.isSuccessful()) {

                        // Guardar en variable booleana si es nuevo usuario
                        boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();

                        // Obtener el usuario autenticado por Google
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Comprobar el estado de la variable booleana
                        if (isNew) {

                            // Actualizar el perfil del usuario
                            String googleName = acct.getDisplayName();

                            // Crear un UserProfileChangeRequest para actualizar el nombre de usuario
                            UserProfileChangeRequest updateRequest =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(googleName)
                                            .build();

                            // Navegar a la actividad específica
                            user.updateProfile(updateRequest).addOnCompleteListener(updateTask -> navigateToUserSetUpActivity());

                        } else {

                            // Mostrar mensaje de error
                            Toast.makeText(this, "Account already exists. Please sign in", Toast.LENGTH_SHORT).show();

                            // Cerrar sesión y volver a SignIn
                            mAuth.signOut();
                            googleClient.signOut();
                            finish();

                        }

                    } else {

                        // Manejar errores de autenticación
                        Log.e(TAG, "Autenticación con google fallida");

                    }

                });

    }

    private void handleGoogleSignInResult(androidx.activity.result.ActivityResult result) {

        // Comprobar si el usuario canceló la operación
        if (result.getData() == null) {
            return;
        }

        try {

            // Obtener la cuenta de Google del resultado
            GoogleSignInAccount acct = GoogleSignIn
                    .getSignedInAccountFromIntent(result.getData())
                    .getResult(ApiException.class);

            // Autenticar con la cuenta de Google
            firebaseAuthWithGoogle(acct);

        } catch (ApiException e) {

            // Manejar errores de autenticación con Google
            Log.w(TAG, "Autenticación con google cancelada", e);

        }
    }

    private void signInWithGoogle() {

        // Crear un intent para iniciar sesión con Google
        Intent intent = googleClient.getSignInIntent();

        // Iniciar la actividad de Google Sign-In
        googleSignInLauncher.launch(intent);

    }

    private void navigateToUserSetUpActivity() {

        // Crear el intent para navegar a UserSetUpActivity
        Intent intent = new Intent(SignUpActivity.this, UserSetUpActivity.class);

        // Limpiar el back-stack para que no pueda volver a SignIn
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Lanzar el intent
        startActivity(intent);

        // Aplicar animación de transición
        overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);

        finish();

    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}