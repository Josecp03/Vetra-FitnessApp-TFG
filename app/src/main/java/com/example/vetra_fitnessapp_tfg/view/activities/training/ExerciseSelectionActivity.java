package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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

public class ExerciseSelectionActivity extends AppCompatActivity {
    private ActivityExerciseSelectionBinding binding;
    private ExerciseSelectionController controller;
    private ExerciseAdapter adapter;

    // Para mantener la selección entre aperturas
    private String selectedMuscle   = "All the muscles";
    private String selectedEquipment= "All the equipment";

    // Referencias UI de los filtros
    private TextView   tvOptionMuscles, tvOptionEquipment;
    private LinearLayout optionMuscles, optionEquipment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new ExerciseSelectionController();

        // RecyclerView de populares
        binding.rvPopularExercises.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(
                new ArrayList<>(),
                ex -> { /* añadir a rutina */ },
                ex -> {
                    Intent i = new Intent(this, ExerciseDetailActivity.class);
                    i.putExtra("exercise", ex);
                    startActivity(i);
                }
        );
        binding.rvPopularExercises.setAdapter(adapter);

        // toolbar filters
        tvOptionMuscles    = binding.tvOptionMuscles;
        optionMuscles      = binding.optionMuscles;
        tvOptionEquipment  = binding.tvOptionEquipment;
        optionEquipment    = binding.optionEquipment;

        // quitamos font padding
        tvOptionMuscles.setIncludeFontPadding(false);
        tvOptionEquipment.setIncludeFontPadding(false);

        // estado inicial de ambos
        refreshAllFilters();

        // listeners para abrir diálogos
        optionMuscles.setOnClickListener(v -> showSelectorDialog(
                /* tipo */       "muscle",
                /* lista */      controller::loadTargetList,
                /* seleccionado */ selectedMuscle,
                /* al seleccionar */
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

        binding.buttonBack.setOnClickListener(v -> finish());

        // cargamos la lista completa (sin límite)
        controller.loadPopularExercises(Integer.MAX_VALUE, 0,
                new Callback<List<Exercise>>() {
                    @Override
                    public void onResponse(Call<List<Exercise>> call,
                                           Response<List<Exercise>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            adapter.getItems().clear();
                            adapter.getItems().addAll(res.body());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override public void onFailure(Call<List<Exercise>> c, Throwable t) {
                        Log.e("ExerciseSel", "Error loading", t);
                    }
                }
        );
    }

    /** Refresca ambos botones en toolbar (fondo y texto negro) */
    private void refreshAllFilters() {
        // músculo
        optionMuscles.setBackgroundResource(
                selectedMuscle.equals("All the muscles")
                        ? R.drawable.gender_selector_background
                        : R.drawable.filter_selected
        );
        tvOptionMuscles.setText(selectedMuscle);
        tvOptionMuscles.setTextColor(Color.BLACK);

        // equipamiento
        optionEquipment.setBackgroundResource(
                selectedEquipment.equals("All the equipment")
                        ? R.drawable.gender_selector_background
                        : R.drawable.filter_selected
        );
        tvOptionEquipment.setText(selectedEquipment);
        tvOptionEquipment.setTextColor(Color.BLACK);
    }

    /**
     * Muestra un BottomSheet para elegir target o equipment.
     *
     * @param type      "muscle" o "equipment" (no usado para UI, solo para logs)
     * @param loader    función controller.loadTargetList o loadEquipmentList
     * @param current   valor actualmente seleccionado
     * @param onSelect  callback con la cadena elegida
     */
    private void showSelectorDialog(
            String type,
            java.util.function.Consumer<Callback<List<String>>> loader,
            String current,
            java.util.function.Consumer<String> onSelect
    ) {
        View dlg = getLayoutInflater()
                .inflate(R.layout.dialog_exercise_selector, null, false);
        BottomSheetDialog sheet = new BottomSheetDialog(
                this, R.style.BottomSheetDialogTheme);
        sheet.setContentView(dlg);
        Objects.requireNonNull(sheet.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout ll = dlg.findViewById(R.id.llTargets);
        sheet.setCancelable(false);

        List<MaterialButton> btnList = new ArrayList<>();

        // estilos
        final int selBg     = getColor(R.color.white);
        final int selText   = getColor(R.color.dark_gray);
        final int unselBg   = getColor(R.color.dark_gray);
        final int unselText = getColor(R.color.white);

        Typeface goldman = ResourcesCompat.getFont(this, R.font.goldman);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, dpToPx(8));

        // botón “All the ...”
        String allLabel = type.equals("muscle")
                ? "All the muscles"
                : "All the equipment";
        MaterialButton allBtn = new MaterialButton(
                this, null,
                com.google.android.material.R.attr.materialButtonStyle
        );
        allBtn.setLayoutParams(lp);
        allBtn.setText(allLabel);
        allBtn.setTypeface(goldman);
        allBtn.setAllCaps(false);
        allBtn.setIncludeFontPadding(false);
        allBtn.setTextSize(16);
        if (allLabel.equals(current)) {
            allBtn.setBackgroundTintList(
                    ColorStateList.valueOf(selBg));
            allBtn.setTextColor(selText);
        } else {
            allBtn.setBackgroundTintList(
                    ColorStateList.valueOf(unselBg));
            allBtn.setTextColor(unselText);
        }
        ll.addView(allBtn);
        btnList.add(allBtn);

        // cargar lista real
        loader.accept(new Callback<List<String>>() {
            @Override
            public void onResponse(
                    Call<List<String>> call, Response<List<String>> res) {
                sheet.setCancelable(true);
                if (!res.isSuccessful() || res.body()==null) {
                    sheet.dismiss(); return;
                }
                for (String raw : res.body()) {
                    String label = raw.substring(0,1).toUpperCase()
                            + raw.substring(1);
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
                        btn.setBackgroundTintList(
                                ColorStateList.valueOf(selBg));
                        btn.setTextColor(selText);
                    } else {
                        btn.setBackgroundTintList(
                                ColorStateList.valueOf(unselBg));
                        btn.setTextColor(unselText);
                    }
                    ll.addView(btn);
                    btnList.add(btn);
                }
                // listener común
                for (MaterialButton b : btnList) {
                    b.setOnClickListener(v -> {
                        for (MaterialButton o : btnList) {
                            o.setBackgroundTintList(
                                    ColorStateList.valueOf(unselBg));
                            o.setTextColor(unselText);
                        }
                        b.setBackgroundTintList(
                                ColorStateList.valueOf(selBg));
                        b.setTextColor(selText);

                        onSelect.accept(b.getText().toString());
                        sheet.dismiss();
                    });
                }
            }
            @Override public void onFailure(Call<List<String>> c, Throwable t) {
                sheet.setCancelable(true);
                sheet.dismiss();
            }
        });

        sheet.setOnShowListener(d -> {
            FrameLayout bs = sheet.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bs)
                    .setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            BottomSheetBehavior.from(bs)
                    .setHalfExpandedRatio(0.6f);
            BottomSheetBehavior.from(bs)
                    .setDraggable(false);
            BottomSheetBehavior.from(bs)
                    .setHideable(false);
            BottomSheetBehavior.from(bs)
                    .setFitToContents(false);
        });

        sheet.show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources()
                .getDisplayMetrics().density);
    }
}
