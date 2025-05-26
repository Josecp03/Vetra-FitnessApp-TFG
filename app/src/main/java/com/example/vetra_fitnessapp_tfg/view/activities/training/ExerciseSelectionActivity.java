package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.content.Intent;
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

public class ExerciseSelectionActivity extends AppCompatActivity {
    private ActivityExerciseSelectionBinding binding;
    private ExerciseSelectionController controller;
    private ExerciseAdapter adapter;

    private String selectedMuscle = "All the muscles";
    private String selectedEquipment = "All the equipment";

    private TextView tvOptionMuscles, tvOptionEquipment, tvHeader;
    private LinearLayout optionMuscles, optionEquipment, optionSearch;
    private LinearLayout placeholderContainer;
    private TextView tvNoExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

    private void doSearch() {
        updateHeaderText();
        showLoadingPlaceholder(true);

        boolean allMuscles = selectedMuscle.equals("All the muscles");
        boolean allEquip   = selectedEquipment.equals("All the equipment");

        if (allMuscles && allEquip) {
            controller.loadPopularExercises(
                    25, 0,
                    wrapCallback(updateAdapterCallback())
            );
        }
        else if (!allMuscles && allEquip) {
            controller.loadExercisesByTarget(
                    selectedMuscle.toLowerCase(),
                    Integer.MAX_VALUE, 0,
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
                    selectedMuscle.toLowerCase(),
                    Integer.MAX_VALUE, 0,
                    wrapCallback(new Callback<List<Exercise>>() {
                        @Override
                        public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> res) {
                            if (res.isSuccessful() && res.body() != null) {
                                List<Exercise> filtered = new ArrayList<>();
                                for (Exercise ex : res.body()) {
                                    if (ex.getEquipment().equalsIgnoreCase(selectedEquipment)) {
                                        filtered.add(ex);
                                    }
                                }
                                adapter.getItems().clear();
                                adapter.getItems().addAll(filtered);
                                adapter.notifyDataSetChanged();

                                if (filtered.isEmpty()) {
                                    tvNoExercises.setVisibility(View.VISIBLE);
                                    binding.rvPopularExercises.setVisibility(View.GONE);
                                } else {
                                    tvNoExercises.setVisibility(View.GONE);
                                    binding.rvPopularExercises.setVisibility(View.VISIBLE);
                                }
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

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

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
}
