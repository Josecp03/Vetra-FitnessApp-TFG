package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.controller.training.ExerciseSelectionController;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityExerciseSelectionBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.view.adapters.training.ExerciseAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import androidx.core.content.res.ResourcesCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ExerciseSelectionActivity extends AppCompatActivity {
    private ActivityExerciseSelectionBinding binding;
    private ExerciseSelectionController controller;
    private ExerciseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.optionMuscles.setOnClickListener(v -> showTargetSelectorDialog());
        binding.buttonBack.setOnClickListener(v -> finish());

        binding.rvPopularExercises.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseAdapter(
                new ArrayList<>(),
                exercise -> { /* añadir a rutina */ },
                exercise -> {
                    Intent i = new Intent(this, ExerciseDetailActivity.class);
                    i.putExtra("exercise", exercise);
                    startActivity(i);
                }
        );
        binding.rvPopularExercises.setAdapter(adapter);

        controller = new ExerciseSelectionController();
        // para no quedarnos en sólo 10 ejercicios
        controller.loadPopularExercises(
                25,    // límite
                0,     // offset
                new Callback<List<Exercise>>() {
                    @Override
                    public void onResponse(Call<List<Exercise>> call, Response<List<Exercise>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            adapter.getItems().clear();
                            adapter.getItems().addAll(res.body());
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override public void onFailure(Call<List<Exercise>> call, Throwable t) {
                        Log.e("ExerciseSel", "Error loading", t);
                    }
                }
        );
    }

    private void showTargetSelectorDialog() {
        View dlg = getLayoutInflater().inflate(R.layout.dialog_target_selector, null, false);
        BottomSheetDialog sheet = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        sheet.setContentView(dlg);
        Objects.requireNonNull(sheet.getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout ll = dlg.findViewById(R.id.llTargets);
        sheet.setCancelable(false);

        // Cuando se muestre, ajustamos el behavior
        sheet.setOnShowListener(dialog -> {
            FrameLayout bottomSheet = sheet.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

            // 1) Que NO sea arrastrable
            behavior.setDraggable(false);  // Material Components ≥1.3.0

            // 2) Fijar altura mínima (peek) o estado
            behavior.setHideable(false);
            behavior.setFitToContents(false);
            behavior.setHalfExpandedRatio(0.6f);    // 60% de la pantalla
            behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        });

        controller.loadTargetList(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> res) {
                sheet.setCancelable(true);
                if (!res.isSuccessful() || res.body() == null) {
                    Toast.makeText(ExerciseSelectionActivity.this,
                            "Error loading targets", Toast.LENGTH_SHORT).show();
                    sheet.dismiss();
                    return;
                }
                Typeface goldman = ResourcesCompat.getFont(
                        ExerciseSelectionActivity.this, R.font.goldman);
                for (String t : res.body()) {
                    MaterialButton btn = new MaterialButton(
                            ExerciseSelectionActivity.this, null,
                            com.google.android.material.R.attr.materialButtonOutlinedStyle
                    );
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    lp.setMargins(0, 0, 0, dpToPx(8));
                    btn.setLayoutParams(lp);

                    String label = t.substring(0,1).toUpperCase() + t.substring(1);
                    btn.setText(label);
                    btn.setTypeface(goldman);
                    btn.setAllCaps(false);
                    btn.setIncludeFontPadding(false);
                    btn.setTextSize(16);
                    btn.setTextColor(getColor(R.color.white));
                    btn.setBackgroundTintList(
                            android.content.res.ColorStateList.valueOf(getColor(R.color.dark_gray))
                    );
                    btn.setOnClickListener(v -> {
                        Toast.makeText(ExerciseSelectionActivity.this,
                                "Selected: " + label, Toast.LENGTH_SHORT).show();
                        sheet.dismiss();
                        // aquí puedes filtrar tu RecyclerView por target...
                    });
                    ll.addView(btn);
                }
            }
            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                sheet.setCancelable(true);
                Toast.makeText(ExerciseSelectionActivity.this,
                        "Network error", Toast.LENGTH_SHORT).show();
                sheet.dismiss();
            }
        });

        sheet.show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
