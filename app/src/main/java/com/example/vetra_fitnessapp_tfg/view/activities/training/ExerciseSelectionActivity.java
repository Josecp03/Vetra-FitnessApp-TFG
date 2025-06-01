package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.controller.training.ExerciseSelectionController;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityExerciseSelectionBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.ExerciseAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Actividad para la selección de ejercicios con filtros avanzados.
 * Permite buscar ejercicios por nombre, filtrar por grupo muscular y equipamiento,
 * y navegar a los detalles de cada ejercicio. Incluye sistema de placeholders
 * durante la carga y manejo de estados vacíos.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class ExerciseSelectionActivity extends AppCompatActivity {

    /**
     * Binding para acceder a las vistas del layout de la actividad.
     */
    private ActivityExerciseSelectionBinding binding;

    /**
     * Controlador para manejar la lógica de selección de ejercicios.
     */
    private ExerciseSelectionController controller;

    /**
     * Adaptador para mostrar la lista de ejercicios en el RecyclerView.
     */
    private ExerciseAdapter adapter;

    /**
     * Músculo seleccionado actualmente en los filtros.
     */
    private String selectedMuscle = "All the muscles";

    /**
     * Equipamiento seleccionado actualmente en los filtros.
     */
    private String selectedEquipment = "All the equipment";

    /**
     * TextView para mostrar la opción de músculos seleccionada.
     */
    private TextView tvOptionMuscles, tvOptionEquipment, tvHeader;

    /**
     * Layouts para las opciones de filtros.
     */
    private LinearLayout optionMuscles, optionEquipment, optionSearch;

    /**
     * Contenedor para mostrar placeholders durante la carga.
     */
    private LinearLayout placeholderContainer;

    /**
     * TextView para mostrar mensaje cuando no hay ejercicios.
     */
    private TextView tvNoExercises;

    /**
     * Método llamado al crear la actividad.
     * Inicializa el controlador, configura el RecyclerView y establece los filtros.
     *
     * @param savedInstanceState Estado guardado de la instancia anterior
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        controller = new ExerciseSelectionController();

        // Vínculos a vistas
        tvHeader            = binding.tvHeader;
        placeholderContainer = binding.placeholderContainer; // FrameLayout en tu XML
        tvNoExercises       = binding.tvNoExercises;

        binding.rvPopularExercises.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(
                new ArrayList<>(),
                exercise -> {
                    Intent data = new Intent();
                    data.putExtra("selected_exercise", exercise);
                    setResult(RESULT_OK, data);
                    finish();
                },
                exercise -> {
                    Intent i = new Intent(this, ExerciseDetailActivity.class);
                    i.putExtra("exercise", exercise);
                    startActivity(i);
                }
        );
        binding.rvPopularExercises.setAdapter(adapter);

        // Mostrar placeholder inicialmente
        showLoadingPlaceholder(true);

        // Filtros
        tvOptionMuscles    = binding.tvOptionMuscles;
        optionMuscles      = binding.optionMuscles;
        tvOptionEquipment  = binding.tvOptionEquipment;
        optionEquipment    = binding.optionEquipment;
        optionSearch       = binding.optionSearch;

        tvOptionMuscles.setIncludeFontPadding(false);
        tvOptionEquipment.setIncludeFontPadding(false);

        refreshAllFilters();

        optionMuscles.setOnClickListener(v -> showSelectorDialog(
                "muscle",
                controller::loadTargetList,
                selectedMuscle,
                sel -> {
                    selectedMuscle = sel;
                    refreshAllFilters();
                }
        ));

        optionEquipment.setOnClickListener(v -> showSelectorDialog(
                "equipment",
                controller::loadEquipmentList,
                selectedEquipment,
                sel -> {
                    selectedEquipment = sel;
                    refreshAllFilters();
                }
        ));

        optionSearch.setOnClickListener(v -> {
            doSearch();
        });

        binding.buttonBack.setOnClickListener(v -> finish());

        controller.loadPopularExercises(
                25, 0,
                wrapCallback(updateAdapterCallback())
        );
    }

    /**
     * Actualiza la interfaz de todos los filtros según las selecciones actuales.
     * Cambia los colores de fondo y texto para indicar filtros activos.
     */
    private void refreshAllFilters() {
        optionMuscles.setBackgroundResource(
                selectedMuscle.equals("All the muscles")
                        ? R.drawable.gender_selector_background
                        : R.drawable.filter_selected
        );
        tvOptionMuscles.setText(selectedMuscle);
        tvOptionMuscles.setTextColor(Color.BLACK);

        optionEquipment.setBackgroundResource(
                selectedEquipment.equals("All the equipment")
                        ? R.drawable.gender_selector_background
                        : R.drawable.filter_selected
        );
        tvOptionEquipment.setText(selectedEquipment);
        tvOptionEquipment.setTextColor(Color.BLACK);
    }

    /**
     * Ejecuta la búsqueda de ejercicios según los filtros y texto ingresado.
     * Maneja búsqueda por nombre, filtros por músculo y equipamiento.
     */
    private void doSearch() {
        String query = binding.editTextSearchExercise.getText()
                .toString()
                .trim();

        boolean allMuscles = selectedMuscle.equals("All the muscles");
        boolean allEquip   = selectedEquipment.equals("All the equipment");
        if (!query.isEmpty()) {
            updateHeaderForNameSearch(query);
            showLoadingPlaceholder(true);

            controller.loadExercisesByName(
                    query.toLowerCase(),
                    Integer.MAX_VALUE,
                    0,
                    new Callback<List<Exercise>>() {
                        @Override
                        public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> res) {
                            binding.placeholderContainer.setVisibility(View.GONE);
                            if (res.isSuccessful() && res.body() != null) {
                                List<Exercise> result = res.body();
                                List<Exercise> filtered = new ArrayList<>();
                                for (Exercise ex : result) {
                                    boolean matchMuscle = allMuscles ||
                                            ex.getTarget().equalsIgnoreCase(selectedMuscle);
                                    boolean matchEquip = allEquip ||
                                            ex.getEquipment().equalsIgnoreCase(selectedEquipment);
                                    if (matchMuscle && matchEquip) {
                                        filtered.add(ex);
                                    }
                                }
                                adapter.getItems().clear();
                                adapter.getItems().addAll(filtered);
                                adapter.notifyDataSetChanged();
                                boolean empty = filtered.isEmpty();
                                tvNoExercises.setVisibility(empty ? View.VISIBLE : View.GONE);
                                binding.rvPopularExercises.setVisibility(empty ? View.GONE : View.VISIBLE);
                            } else {
                                tvNoExercises.setVisibility(View.VISIBLE);
                                binding.rvPopularExercises.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onFailure(Call<List<Exercise>> call, Throwable t) {
                            binding.placeholderContainer.setVisibility(View.GONE);
                            tvNoExercises.setVisibility(View.VISIBLE);
                            binding.rvPopularExercises.setVisibility(View.GONE);
                            Log.e("ExerciseSel", "Error loading by name", t);
                        }
                    }
            );
            return;
        }

        updateHeaderText();
        showLoadingPlaceholder(true);

        if (allMuscles && allEquip) {
            controller.loadPopularExercises(25, 0, wrapCallback(updateAdapterCallback()));
        }
        else if (!allMuscles && allEquip) {
            controller.loadExercisesByTarget(
                    selectedMuscle.toLowerCase(), Integer.MAX_VALUE, 0,
                    wrapCallback(updateAdapterCallback())
            );
        }
        else if (allMuscles && !allEquip) {
            String eq = selectedEquipment.toLowerCase().replace(" ", "%20");
            controller.loadExercisesByEquipment(
                    eq, Integer.MAX_VALUE, 0,
                    wrapCallback(updateAdapterCallback())
            );
        }
        else {
            controller.loadExercisesByTarget(
                    selectedMuscle.toLowerCase(), Integer.MAX_VALUE, 0,
                    wrapCallback(new Callback<List<Exercise>>() {
                        @Override public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> res) {
                            if (res.isSuccessful() && res.body() != null) {
                                List<Exercise> temp = new ArrayList<>();
                                for (Exercise ex : res.body()) {
                                    if (ex.getEquipment().equalsIgnoreCase(selectedEquipment)) {
                                        temp.add(ex);
                                    }
                                }
                                adapter.getItems().clear();
                                adapter.getItems().addAll(temp);
                                adapter.notifyDataSetChanged();
                                boolean empty = temp.isEmpty();
                                tvNoExercises.setVisibility(empty ? View.VISIBLE : View.GONE);
                                binding.rvPopularExercises.setVisibility(empty ? View.GONE : View.VISIBLE);
                            } else {
                                tvNoExercises.setVisibility(View.VISIBLE);
                                binding.rvPopularExercises.setVisibility(View.GONE);
                            }
                        }
                        @Override public void onFailure(Call<List<Exercise>> call, Throwable t) {
                            Log.e("ExerciseSel", "Error loading", t);
                            tvNoExercises.setVisibility(View.VISIBLE);
                            binding.rvPopularExercises.setVisibility(View.GONE);
                        }
                    })
            );
        }
    }

    /**
     * Crea un callback que actualiza el adaptador con la lista de ejercicios.
     *
     * @return Callback configurado para manejar respuestas de la API
     */
    private Callback<List<Exercise>> updateAdapterCallback() {
        return new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> res) {
                binding.placeholderContainer.setVisibility(View.GONE);
                if (res.isSuccessful() && res.body() != null) {
                    List<Exercise> list = res.body();
                    adapter.getItems().clear();
                    adapter.getItems().addAll(list);
                    adapter.notifyDataSetChanged();

                    if (list.isEmpty()) {
                        tvNoExercises.setVisibility(View.VISIBLE);
                        binding.rvPopularExercises.setVisibility(View.GONE);
                    } else {
                        tvNoExercises.setVisibility(View.GONE);
                        binding.rvPopularExercises.setVisibility(View.VISIBLE);
                    }
                } else {
                    // fallo o body null
                    tvNoExercises.setVisibility(View.VISIBLE);
                    binding.rvPopularExercises.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                binding.placeholderContainer.setVisibility(View.GONE);
                tvNoExercises.setVisibility(View.VISIBLE);
                binding.rvPopularExercises.setVisibility(View.GONE);
                Log.e("ExerciseSel", "Error loading", t);
            }
        };
    }

    /**
     * Envuelve un callback existente añadiendo funcionalidad de placeholders.
     *
     * @param inner Callback interno a envolver
     * @return Callback envuelto con manejo de placeholders
     */
    private Callback<List<Exercise>> wrapCallback(Callback<List<Exercise>> inner) {
        return new Callback<List<Exercise>>() {
            @Override
            public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> res) {
                showLoadingPlaceholder(false);
                inner.onResponse(call, res);
            }
            @Override
            public void onFailure(Call<List<Exercise>> call, Throwable t) {
                showLoadingPlaceholder(false);
                inner.onFailure(call, t);
            }
        };
    }

    /**
     * Muestra u oculta los placeholders de carga.
     * Crea elementos visuales temporales mientras se cargan los datos reales.
     *
     * @param show true para mostrar placeholders, false para ocultarlos
     */
    private void showLoadingPlaceholder(boolean show) {
        placeholderContainer.removeAllViews();
        if (show) {
            LayoutInflater inflater = LayoutInflater.from(this);
            int count = 6; // número de placeholders
            for (int i = 0; i < count; i++) {
                View ph = inflater.inflate(R.layout.placeholder_exercise_item, placeholderContainer, false);
                placeholderContainer.addView(ph);
            }
            binding.rvPopularExercises.setVisibility(View.GONE);
            placeholderContainer.setVisibility(View.VISIBLE);
        } else {
            binding.rvPopularExercises.setVisibility(View.VISIBLE);
            placeholderContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Muestra un diálogo de selección para filtros (músculos o equipamiento).
     * Carga dinámicamente las opciones disponibles desde la API.
     *
     * @param type Tipo de filtro ("muscle" o "equipment")
     * @param loader Función para cargar las opciones del filtro
     * @param current Opción actualmente seleccionada
     * @param onSelect Callback para manejar la selección
     */
    private void showSelectorDialog(
            String type,
            java.util.function.Consumer<Callback<List<String>>> loader,
            String current,
            java.util.function.Consumer<String> onSelect
    ) {
        View dlg = getLayoutInflater().inflate(R.layout.dialog_exercise_selector, null, false);
        TextView tvTitle = dlg.findViewById(R.id.textDialogMuscleGroupTitle);
        tvTitle.setIncludeFontPadding(false);
        tvTitle.setText(
                "muscle".equals(type)
                        ? R.string.muscle_group
                        : R.string.equipment
        );

        BottomSheetDialog sheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        sheet.setContentView(dlg);
        Objects.requireNonNull(sheet.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout ll = dlg.findViewById(R.id.llTargets);
        sheet.setCancelable(false);

        List<MaterialButton> btnList = new ArrayList<>();
        int selBg    = getColor(R.color.white);
        int selText  = getColor(R.color.dark_gray);
        int unselBg  = getColor(R.color.dark_gray);
        int unselText= getColor(R.color.white);

        Typeface goldman = ResourcesCompat.getFont(this, R.font.goldman);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, dpToPx(8));

        String allLabel = type.equals("muscle") ? "All the muscles" : "All the equipment";
        MaterialButton allBtn = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonStyle);
        allBtn.setLayoutParams(lp);
        allBtn.setText(allLabel);
        allBtn.setTypeface(goldman);
        allBtn.setAllCaps(false);
        allBtn.setIncludeFontPadding(false);
        allBtn.setTextSize(16);
        if (allLabel.equals(current)) {
            allBtn.setBackgroundTintList(ColorStateList.valueOf(selBg));
            allBtn.setTextColor(selText);
        } else {
            allBtn.setBackgroundTintList(ColorStateList.valueOf(unselBg));
            allBtn.setTextColor(unselText);
        }
        ll.addView(allBtn);
        btnList.add(allBtn);

        loader.accept(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> res) {
                sheet.setCancelable(true);
                if (!res.isSuccessful() || res.body() == null) { sheet.dismiss(); return; }
                for (String raw : res.body()) {
                    String label = raw.substring(0,1).toUpperCase() + raw.substring(1);
                    MaterialButton btn = new MaterialButton(
                            ExerciseSelectionActivity.this, null,
                            com.google.android.material.R.attr.materialButtonStyle
                    );
                    btn.setLayoutParams(lp);
                    btn.setText(label);
                    btn.setTypeface(goldman);
                    btn.setAllCaps(false);
                    btn.setIncludeFontPadding(false);
                    btn.setTextSize(16);
                    if (label.equals(current)) {
                        btn.setBackgroundTintList(ColorStateList.valueOf(selBg));
                        btn.setTextColor(selText);
                    } else {
                        btn.setBackgroundTintList(ColorStateList.valueOf(unselBg));
                        btn.setTextColor(unselText);
                    }
                    ll.addView(btn);
                    btnList.add(btn);
                }
                for (MaterialButton b : btnList) {
                    b.setOnClickListener(v -> {
                        for (MaterialButton o : btnList) {
                            o.setBackgroundTintList(ColorStateList.valueOf(unselBg));
                            o.setTextColor(unselText);
                        }
                        b.setBackgroundTintList(ColorStateList.valueOf(selBg));
                        b.setTextColor(selText);
                        onSelect.accept(b.getText().toString());
                        sheet.dismiss();
                    });
                }
            }
            @Override public void onFailure(Call<List<String>> call, Throwable t) {
                sheet.setCancelable(true);
                sheet.dismiss();
            }
        });

        sheet.setOnShowListener(d -> {
            FrameLayout bs = sheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bs).setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            BottomSheetBehavior.from(bs).setHalfExpandedRatio(0.6f);
            BottomSheetBehavior.from(bs).setDraggable(false);
            BottomSheetBehavior.from(bs).setHideable(false);
            BottomSheetBehavior.from(bs).setFitToContents(false);
        });

        sheet.show();
    }

    /**
     * Convierte píxeles independientes de densidad a píxeles.
     *
     * @param dp Valor en dp a convertir
     * @return Valor equivalente en píxeles
     */
    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    /**
     * Actualiza el texto del encabezado según los filtros aplicados.
     */
    private void updateHeaderText() {
        boolean allMuscles = selectedMuscle.equals("All the muscles");
        boolean allEquip   = selectedEquipment.equals("All the equipment");

        if (allMuscles && allEquip) {
            tvHeader.setText(getString(R.string.popular_exercises));
        } else if (!allMuscles && allEquip) {
            tvHeader.setText("Exercises for " + selectedMuscle);
        } else if (allMuscles && !allEquip) {
            tvHeader.setText("Exercises with " + selectedEquipment);
        } else {
            tvHeader.setText("Exercises for " + selectedMuscle + " with " + selectedEquipment);
        }
    }

    /**
     * Actualiza el texto del encabezado para búsquedas por nombre.
     *
     * @param query Término de búsqueda a mostrar
     */
    private void updateHeaderForNameSearch(String query) {
        tvHeader.setText("Search results for: \"" + query + "\"");
    }

}
