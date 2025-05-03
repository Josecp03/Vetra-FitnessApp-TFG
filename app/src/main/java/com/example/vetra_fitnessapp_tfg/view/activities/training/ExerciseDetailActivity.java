package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityExerciseDetailBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;

public class ExerciseDetailActivity extends AppCompatActivity {
    private ActivityExerciseDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Exercise ex = (Exercise) getIntent().getSerializableExtra("exercise");
        String title = toCamelCase(ex.getName());

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.tvDetailTitle.setText(title);
        binding.tvDetailTitle.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailTitle.setTextColor(ContextCompat.getColor(this, R.color.white));

        Glide.with(this).load(ex.getGifUrl()).into(binding.ivDetail);

        binding.tvDetailName.setText(title);
        binding.tvDetailName.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailName.setTextColor(ContextCompat.getColor(this, R.color.black));

        binding.tvDetailTarget.setText("Primary: " + toCamelCase(ex.getTarget()));
        binding.tvDetailTarget.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailTarget.setTextColor(ContextCompat.getColor(this, R.color.black));

        binding.tvDetailSecondary.setText("Secondary: " + toCamelCase(String.join(", ", ex.getSecondaryMuscles())));
        binding.tvDetailSecondary.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailSecondary.setTextColor(ContextCompat.getColor(this, R.color.black));

        binding.llInstructions.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int circleDp = (int)(48 * density + 0.5f);
        int marginDp = (int)(12 * density + 0.5f);
        int num = 1;

        for (String step : ex.getInstructions()) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, marginDp, 0, marginDp);

            TextView tvNum = new TextView(this);
            LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(circleDp, circleDp);
            tvNum.setLayoutParams(numLp);
            tvNum.setText(String.valueOf(num++));
            tvNum.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvNum.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tvNum.setIncludeFontPadding(false);
            tvNum.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
            tvNum.setGravity(Gravity.CENTER);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(ContextCompat.getColor(this, R.color.black));
            tvNum.setBackground(bg);

            TextView tvStep = new TextView(this);
            LinearLayout.LayoutParams stepLp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            stepLp.setMarginStart(marginDp);
            tvStep.setLayoutParams(stepLp);
            tvStep.setText(step);
            tvStep.setTextColor(ContextCompat.getColor(this, R.color.black));
            tvStep.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvStep.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
            tvStep.setIncludeFontPadding(false);
            tvStep.setLineSpacing(4 * density, 1f);
            tvStep.setMinHeight(circleDp);

            row.addView(tvNum);
            row.addView(tvStep);
            binding.llInstructions.addView(row);
        }
    }

    private String toCamelCase(String s) {
        StringBuilder out = new StringBuilder();
        for (String w : s.split("\\s+")) {
            if (w.isEmpty()) continue;
            out.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1))
                    .append(" ");
        }
        return out.toString().trim();
    }
}
