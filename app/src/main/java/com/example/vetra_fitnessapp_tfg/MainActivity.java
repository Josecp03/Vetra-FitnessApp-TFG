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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Objects;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextView titleText;
    private ImageView toolbarIcon;
    private static final int RC_NOTIFICATIONS = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        requestNotificationPermissionIfNeeded();

        createNotificationChannel();

        // Configurar barras de navegación
        setupNavigationAndToolbar();

    }

    @SuppressLint("SetTextI18n")
    private void setupNavigationAndToolbar() {

        // Configurar el toolbar personalizado
        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);

        // Ocultar el título por defecto
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Referencias al texto y al icono del toolbar
        titleText = toolbar.findViewById(R.id.toolbar_title);
        toolbarIcon = toolbar.findViewById(R.id.toolbar_image);

        // Configura la barra de navegación inferior
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Definir las secciones principales de la app para el comportamiento del toolbar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_workouts,
                R.id.navigation_chatgpt,
                R.id.navigation_profile
        ).build();

        // Obtener el controlador de navegación asociado al nav_host_fragment
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        // Vincular la BottomNavigationView con el NavController para gestionar la navegación
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Cambiar dinámicamente el título del toolbar según el destino de navegación
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "mi_canal_por_defecto",
                    "Notificaciones de entrenamiento",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Canal para recordatorios diarios");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

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
