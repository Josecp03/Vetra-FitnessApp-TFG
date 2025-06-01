package com.example.vetra_fitnessapp_tfg;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityMainBinding;
import com.example.vetra_fitnessapp_tfg.utils.Prefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Objects;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Actividad principal de la aplicación Vetra Fitness.
 * Maneja la navegación entre fragmentos principales y configura el sistema de notificaciones.
 * Contiene la barra de navegación inferior y el toolbar personalizado.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    /** Binding para acceder a las vistas de la actividad */
    private ActivityMainBinding binding;

    /** TextView del título en el toolbar personalizado */
    private TextView titleText;

    /** ImageView del icono en el toolbar personalizado */
    private ImageView toolbarIcon;

    /** Código de solicitud para permisos de notificaciones */
    private static final int RC_NOTIFICATIONS = 2001;

    /**
     * Método llamado al crear la actividad.
     * Configura la orientación, solicita permisos de notificaciones,
     * crea el canal de notificaciones y configura la navegación.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestNotificationPermissionIfNeeded();
        createNotificationChannel();
        setupNavigationAndToolbar();
    }

    /**
     * Configura la navegación inferior y el toolbar personalizado.
     * Establece los títulos dinámicos según el fragmento actual.
     */
    @SuppressLint("SetTextI18n")
    private void setupNavigationAndToolbar() {
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        titleText = toolbar.findViewById(R.id.toolbar_title);
        toolbarIcon = toolbar.findViewById(R.id.toolbar_image);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_workouts,
                R.id.navigation_chatgpt,
                R.id.navigation_profile
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();
            if (id == R.id.navigation_home) {
                titleText.setText("Home");
            } else if (id == R.id.navigation_workouts) {
                titleText.setText("Workouts");
            } else if (id == R.id.navigation_chatgpt) {
                titleText.setText("Chatbot");
            } else if (id == R.id.navigation_profile) {
                titleText.setText("Profile");
            }
        });
    }

    /**
     * Crea el canal de notificaciones para Android O y versiones superiores.
     * La importancia del canal depende de la configuración del usuario guardada en Prefs.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean enabled = Prefs.isNotificationsEnabled(this);
            int importance = enabled
                    ? NotificationManager.IMPORTANCE_DEFAULT
                    : NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(
                    "mi_canal_por_defecto",
                    "Notificaciones de entrenamiento",
                    importance
            );
            channel.setDescription("Canal para recordatorios diarios");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Solicita permisos de notificaciones si es necesario (Android 13+).
     */
    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        RC_NOTIFICATIONS
                );
            }
        }
    }

    /**
     * Maneja el resultado de las solicitudes de permisos.
     *
     * @param requestCode Código de la solicitud de permiso
     * @param permissions Array de permisos solicitados
     * @param grantResults Array de resultados de los permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_NOTIFICATIONS) {
            boolean granted = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                Toast.makeText(this, "Please enable notification permissions in the app settings", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
