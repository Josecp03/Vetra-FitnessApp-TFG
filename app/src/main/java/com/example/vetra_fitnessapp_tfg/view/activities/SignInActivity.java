package com.example.vetra_fitnessapp_tfg.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vetra_fitnessapp_tfg.MainActivity;
import com.example.vetra_fitnessapp_tfg.R;
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

public class SignInActivity extends AppCompatActivity {

    // Atributos
    private ActivitySignInBinding binding;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleClient;
    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleGoogleSignInResult
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Obtiene el ID del cliente de Google
                .requestEmail() // Solicitar el correo electrónico
                .build(); // Construir el objeto GoogleSignInOptions
        googleClient = GoogleSignIn.getClient(this, googleOptions);

        // Listener para el botón de SignIn
        binding.buttonSignIn.setOnClickListener(v -> {
            Toast.makeText(SignInActivity.this, "Login pressed", Toast.LENGTH_SHORT).show();
        });

        // Listener para el botón de Google
        binding.buttonGoogle.setOnClickListener(v -> {
            Toast.makeText(SignInActivity.this, "Google pressed", Toast.LENGTH_SHORT).show();
        });

        // Listener para el texto "Forget your password?"
        binding.textViewForgetPassword.setOnClickListener(v -> showForgotPasswordDialog());

        // Listener para el texto "Sign up here"
        binding.textViewSignUp.setOnClickListener(v -> {

            // Crear intent para navegar a SignUpActivity
            Intent i = new Intent(SignInActivity.this, SignUpActivity.class);

            // Lanzar el intent
            startActivity(i);

            // Aplicar animación de transición
            overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);

        });

    }

    private void showForgotPasswordDialog() {

        // Inflar la vista personalizada del diálogo desde el layout XML
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater().inflate(R.layout.dialog_recover_password, null);

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        // Asignar la vista inflada al diálogo
        bottomSheetDialog.setContentView(dialogView);

        // Establecer fondo transparente para que los bordes sean redondeados o personalizados
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Mostrar el diálogo
        bottomSheetDialog.show();

        // Obtener la vista del bottom sheet
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        // Forzar que ocupe toda la altura de la pantalla
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    private void signInWithGoogle() {

        // Crear un intent para iniciar sesión con Google
        Intent intent = googleClient.getSignInIntent();

        // Iniciar la actividad de Google Sign-In
        googleSignInLauncher.launch(intent);

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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        // Crear un objeto de autenticación con el token de Google
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        // Iniciar la autenticación con Firebase
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    // Comporbar si la autenticación fue exitosa
                    if (task.isSuccessful()) {

                        // Navegar a la actividad principal
                        navigateToMainActivity();

                    } else {

                        // Manejar errores de autenticación
                        Log.e(TAG, "Autenticación con google fallida");

                    }

                });

    }

    private void signInWithEmail() {

        // Obtener los valores de los campos de texto
        String email = binding.editTextEmail.getText().toString().trim();
        String pass  = binding.editTextPassword.getText().toString().trim();

        // Comprobar que los campos no estén vacíos
        if (email.isEmpty() || pass.isEmpty()) {

            // Mostrar un mensaje de error
            Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
            return;

        }

        // Iniciar sesión con Firebase
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {

                    // Comporbar si la autenticación fue exitosa
                    if (task.isSuccessful()) {

                        // Navegar a la actividad principal
                        navigateToMainActivity();

                    } else {

                        // Manejar errores de autenticación
                        Log.e(TAG, "Inicio de sesión con correo fallido");

                    }

                });

    }

    private void resetPassword() {

        // Obtener el valor del campo de texto
        String email = binding.editTextEmail.getText().toString().trim();

        // Comprobar que el campo no esté vacío
        if (email.isEmpty()) {

            // Mostrar un mensaje de error
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return;

        }

        // Enviar enlace de recuperación de contraseña a Firebase
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {

                    // Comporbar si el enlace fue enviado
                    if (task.isSuccessful()) {

                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();

                    } else {

                        // Manejar errores de autenticación
                        Log.e(TAG, "Error al envira el enlace");

                    }

                });

    }

    private void navigateToMainActivity() {

        // Crear el intent para navegar a la actividad principal
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);

        // Lanzar el intent
        startActivity(intent);

        // Finalizar la actividad actual
        finish();

    }


}