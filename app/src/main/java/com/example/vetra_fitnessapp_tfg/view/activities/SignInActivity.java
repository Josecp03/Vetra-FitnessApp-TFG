package com.example.vetra_fitnessapp_tfg.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Objects;
import android.util.Patterns;

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
        binding.buttonSignIn.setOnClickListener(v -> signInWithEmail());

        // Listener para el botón de Google
        binding.buttonGoogle.setOnClickListener(v -> signInWithGoogle());

        // Listener para el texto "Forget your password?"
        binding.textViewForgetPassword.setOnClickListener(v -> showForgotPasswordDialog());

        // Listener para el texto "Sign up here"
        binding.textViewSignUp.setOnClickListener(v -> navigateToSignUpActivity());

    }

    private void showForgotPasswordDialog() {

        // Inflar el binding del diálogo
        DialogRecoverPasswordBinding dialogBinding = DialogRecoverPasswordBinding.inflate(getLayoutInflater());

        // Crear un BottomSheetDialog con un tema personalizado
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);

        // Asignar la vista al diálogo
        bottomSheetDialog.setContentView(dialogBinding.getRoot());

        // Establecer fondo transparente para que los bordes sean redondeados o personalizados
        Objects.requireNonNull(bottomSheetDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        // Listener para el botón de enviar email con enlace de recuperación
        dialogBinding.buttonSendMail.setOnClickListener(v -> {

            // Obtener el email ingresado
            String email = dialogBinding.editTextEmail.getText().toString().trim();

            // Comprobar que el email no esté vacío
            if (email.isEmpty()) {

                // Mostrar un mensaje de error
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
                return;

            }

            // Recuperar la contraseña envianod un correo al usuario
            resetPassword(email, bottomSheetDialog);

        });

        // Mostrar el diálogo
        bottomSheetDialog.show();

        // Obtener la vista del bottom sheet
        View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        // Forzar que ocupe toda la altura de la pantalla
        bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

    }

    private void resetPassword(String email, BottomSheetDialog dialog) {

        // Enviar enlace de recuperación de contraseña a Firebase
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, task -> {

                    // Comporbar si el enlace fue enviado
                    if (task.isSuccessful()) {

                        // Mostrar mensaje de éxito
                        Toast.makeText(this, "Password reset link sent to your email", Toast.LENGTH_SHORT).show();

                        // Cerrar el diálogo
                        dialog.dismiss();

                    } else {

                        // Manejar errores de autenticación
                        Log.e(TAG, "Error al envira el enlace");

                    }

                });

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

                        // Guardar en variable booleana si es nuevo usuario
                        boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();

                        // Comprobar el estado de la variable booleana
                        if (isNew) {

                            // Mostrar mensaje de error
                            Toast.makeText(this, "No account found. Please sign up first", Toast.LENGTH_SHORT).show();

                            // Obtener el usuario autenticado por Google (aún existe en Firebase)
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Eliminar el usuario
                            user.delete();

                        } else {

                            // Navegar a la actividad principal
                            navigatePostLogin();

                        }

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

        // Comprobar que el email sea válido
        if (!isEmailValid(email)) {

            // Mostrar un mensaje de error
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;

        }

        // Iniciar sesión con Firebase
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {

                    // Comporbar si la autenticación fue exitosa
                    if (task.isSuccessful()) {

                        // Navegar a la actividad principal
                        navigatePostLogin();

                    } else {

                        // Mostrar un mensaje de error
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();

                    }

                });

    }

    private void navigatePostLogin() {

        // Guardar el estado del perfil en SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isProfileComplete = prefs.getBoolean("isProfileComplete", false);

        // Iniializar el intent
        Intent intent;

        // Comprobar el estado del perfil
        if (isProfileComplete) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, UserSetUpActivity.class);
        }

        // Limpiar el back-stack para que no pueda volver a SignIn
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Lanzar el intent
        startActivity(intent);

        // Finalizar la actividad
        finish();

    }


    private void navigateToSignUpActivity() {

        // Crear intent para navegar a SignUpActivity
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);

        // Lanzar el intent
        startActivity(intent);

        // Aplicar animación de transición
        overridePendingTransition(R.anim.slide_in_right_fade, R.anim.slide_out_left_fade);

    }

    public static boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Comprobar si el usuario ya está autenticado
        if (mAuth.getCurrentUser() != null) {

            // Navegar a la actividad correspondiente
            navigatePostLogin();

        }
    }


}