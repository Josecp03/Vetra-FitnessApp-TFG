package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentHomeBinding;
import com.example.vetra_fitnessapp_tfg.utils.KeyStoreManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Fragmento principal de la aplicación que muestra el dashboard del usuario.
 * Incluye navegación rápida, información del perfil y gráficos de actividad
 * semanal. Presenta estadísticas de ejercicios realizados en los últimos días.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class HomeFragment extends Fragment {

    /**
     * Binding para acceder a las vistas del fragmento.
     */
    private FragmentHomeBinding binding;

    /**
     * Instancia de Firestore para operaciones de base de datos.
     */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Usuario actual autenticado en Firebase.
     */
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    /**
     * Gestor de cifrado para datos sensibles.
     */
    private KeyStoreManager keyStore;

    /**
     * Crea y configura la vista del fragmento.
     *
     * @param inflater Inflater para crear la vista
     * @param container Contenedor padre
     * @param savedInstanceState Estado guardado
     * @return Vista configurada del fragmento
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        keyStore = new KeyStoreManager();
        return binding.getRoot();
    }

    /**
     * Configura la navegación, perfil de usuario y gráfico de actividad.
     *
     * @param view Vista del fragmento
     * @param savedInstanceState Estado guardado
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonMyWorkouts.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new WorkoutFragment())
                    .addToBackStack(null)
                    .commit();
            BottomNavigationView nav = requireActivity().findViewById(R.id.nav_view);
            nav.setSelectedItemId(R.id.navigation_workouts);
        });
        binding.buttonMyChatbot.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new ChatbotFragment())
                    .addToBackStack(null)
                    .commit();
            BottomNavigationView nav = requireActivity().findViewById(R.id.nav_view);
            nav.setSelectedItemId(R.id.navigation_chatgpt);
        });
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    Glide.with(this)
                            .load(doc.getString("profile_photo_url"))
                            .placeholder(R.drawable.ic_profile_picture)
                            .into(binding.profileImage);
                    String dec = keyStore.decrypt(doc.getString("username"));
                    binding.usernameText.setText("Hello, " + (dec!=null?dec:"there"));
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment","Error leyendo usuario",e);
                    binding.usernameText.setText("Hello!");
                });

        BarChart chart = binding.barChartWeekly;
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(true);
        chart.setGridBackgroundColor(Color.WHITE);
        chart.setExtraTopOffset(12f);
        chart.setExtraBottomOffset(24f);
        chart.setExtraLeftOffset(16f);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getAxisRight().setEnabled(false);
        int grey = ContextCompat.getColor(requireContext(), R.color.dark_gray);
        Typeface goldman = ResourcesCompat.getFont(requireContext(), R.font.goldman);
        XAxis x = chart.getXAxis();
        x.setTextColor(grey);
        x.setTypeface(goldman);
        x.setDrawGridLines(true);
        x.setGridColor(grey);
        x.setYOffset(4f);
        YAxis left = chart.getAxisLeft();
        left.setTextColor(grey);
        left.setTypeface(goldman);
        left.setDrawGridLines(true);
        left.setGridColor(grey);
        left.setSpaceTop(35f);
        chart.getAxisRight().setDrawGridLines(false);
        Legend legend = chart.getLegend();
        legend.setTypeface(goldman);
        legend.setTextColor(grey);
        legend.setYOffset(8f);
        loadWeeklyData(chart, grey, goldman);
    }

    /**
     * Carga y configura los datos del gráfico de actividad semanal.
     *
     * @param chart Gráfico de barras a configurar
     * @param grey Color para elementos del gráfico
     * @param goldman Fuente tipográfica para el gráfico
     */
    private void loadWeeklyData(BarChart chart, int grey, Typeface goldman) {
        String fromDate = getDateStringDaysAgo(6);

        db.collection("users")
                .document(user.getUid())
                .collection("exerciseHistory")
                .whereGreaterThanOrEqualTo("date", fromDate)
                .orderBy("date")
                .get()
                .addOnSuccessListener(query -> {
                    Map<String,Integer> counts = new TreeMap<>();
                    for (DocumentSnapshot d: query.getDocuments()) {
                        String date = d.getString("date");
                        counts.put(date, counts.getOrDefault(date,0)+1);
                    }

                    List<BarEntry> entries = new ArrayList<>();
                    List<String> labels = new ArrayList<>();
                    int i = 0;
                    for (String day : counts.keySet()) {
                        entries.add(new BarEntry(i, counts.get(day)));
                        labels.add(day.substring(5));
                        i++;
                    }

                    BarDataSet set = new BarDataSet(entries, "Exercises per day");
                    set.setColor(grey);
                    set.setValueTextColor(grey);
                    set.setValueTypeface(goldman);
                    set.setValueTextSize(12f);

                    BarData data = new BarData(set);
                    data.setBarWidth(0.6f);
                    chart.setData(data);

                    chart.getXAxis().setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            int idx = Math.round(value);
                            return (idx>=0 && idx<labels.size())?labels.get(idx):"";
                        }
                    });

                    chart.setFitBars(true);
                    chart.animateY(800);
                    chart.invalidate();
                })
                .addOnFailureListener(e -> Log.e("HomeFragment","Error cargando historial",e));
    }

    /**
     * Obtiene una fecha en formato string con los días especificados hacia atrás.
     *
     * @param daysAgo Número de días hacia atrás desde hoy
     * @return Fecha en formato YYYY-MM-DD
     */
    private String getDateStringDaysAgo(int daysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(cal.getTime());
    }
}
