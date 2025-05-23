package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentHomeBinding;
import com.example.vetra_fitnessapp_tfg.utils.KeyStoreManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private KeyStoreManager keyStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        keyStore = new KeyStoreManager();

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonMyWorkouts.setOnClickListener(v -> {

            // Reemplazar el fragmento actual con el fragmento de Workouts
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new WorkoutFragment())
                    .addToBackStack(null)
                    .commit();

            // Actualizar la vista de la barra de navegación
            BottomNavigationView nav = requireActivity().findViewById(R.id.nav_view);
            nav.setSelectedItemId(R.id.navigation_workouts);

        });

        binding.buttonMyChatbot.setOnClickListener(v -> {

            // Reemplazar el fragmento actual con el fragmento de Workouts
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new ChatbotFragment())
                    .addToBackStack(null)
                    .commit();

            // Actualizar la vista de la barra de navegación
            BottomNavigationView nav = requireActivity().findViewById(R.id.nav_view);
            nav.setSelectedItemId(R.id.navigation_chatgpt);

        });


        // Leer perfil de Firestore
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener((DocumentSnapshot doc) -> {
                    // Foto sin descifrar
                    Glide.with(this)
                            .load(doc.getString("profile_photo_url"))
                            .placeholder(R.drawable.ic_profile_picture)
                            .into(binding.profileImage);

                    // Desencriptar y mostrar
                    String encryptedUser = doc.getString("username");
                    String decryptedUser = keyStore.decrypt(encryptedUser);
                    binding.usernameText.setText("Hello, " + (decryptedUser != null ? decryptedUser : "there"));
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment", "Error leyendo usuario", e);
                    binding.usernameText.setText("Hello!");
                });

    }

}