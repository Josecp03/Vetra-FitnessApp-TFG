package com.example.vetra_fitnessapp_tfg;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vetra_fitnessapp_tfg.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextView titleText;
    private ImageView toolbarIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
                R.id.navigation_nutrition,
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
            } else if (id == R.id.navigation_nutrition) {
                titleText.setText("Nutrition");
            } else if (id == R.id.navigation_profile) {
                titleText.setText("Profile");
            }
        });
    }
}
