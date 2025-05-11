package com.example.vetra_fitnessapp_tfg.view.activities.training;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.ActivityExerciseDetailBinding;
import com.example.vetra_fitnessapp_tfg.model.training.Exercise;
import com.example.vetra_fitnessapp_tfg.model.training.ExerciseHistoryEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ExerciseDetailActivity extends AppCompatActivity {
    private ActivityExerciseDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExerciseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1) Grab the exercise
        Exercise ex = (Exercise) getIntent().getSerializableExtra("exercise");
        String title = toCamelCase(ex.getName());

        // 2) Toolbar back button
        binding.buttonBack.setOnClickListener(v -> finish());

        // 3) Header title
        binding.tvDetailTitle.setText(title);
        binding.tvDetailTitle.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailTitle.setTextColor(ContextCompat.getColor(this, R.color.white));

        // 4) GIF
        Glide.with(this).load(ex.getGifUrl()).into(binding.ivDetail);

        // 5) Name / target / secondary
        binding.tvDetailName.setText(title);
        binding.tvDetailName.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailName.setTextColor(ContextCompat.getColor(this, R.color.black));

        binding.tvDetailTarget.setText("Primary: " + toCamelCase(ex.getTarget()));
        binding.tvDetailTarget.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailTarget.setTextColor(ContextCompat.getColor(this, R.color.black));

        binding.tvDetailSecondary.setText("Secondary: "
                + toCamelCase(String.join(", ", ex.getSecondaryMuscles())));
        binding.tvDetailSecondary.setTypeface(ResourcesCompat.getFont(this, R.font.goldman));
        binding.tvDetailSecondary.setTextColor(ContextCompat.getColor(this, R.color.black));

        // 6) Instructions list
        buildInstructions(ex.getInstructions());

        // 7) **Now** load the history under the “History” header
        loadHistory(
                ex.getId(),
                ex.getGifUrl(),
                title
        );
    }

    private void buildInstructions(List<String> instructions) {
        binding.llInstructions.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        int circleDp = (int) (48 * density + 0.5f);
        int marginDp = (int) (12 * density + 0.5f);
        int num = 1;

        for (String step : instructions) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, marginDp, 0, marginDp);

            // step number
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

            // instruction text
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

    private void loadHistory(String exerciseId, String photoUrl, String name) {
        binding.llHistory.removeAllViews();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("exerciseHistory")
                .whereEqualTo("user_id", uid)
                .whereEqualTo("exercise_id", exerciseId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        ExerciseHistoryEntry entry = doc.toObject(ExerciseHistoryEntry.class);

                        View card = LayoutInflater.from(this)
                                .inflate(R.layout.item_exercise_history, binding.llHistory, false);

                        ImageView iv   = card.findViewById(R.id.ivExerciseThumb);
                        TextView tvN   = card.findViewById(R.id.tvExerciseName);
                        TextView tvD   = card.findViewById(R.id.tvDate);
                        LinearLayout ll = card.findViewById(R.id.llSetsContainer);

                        Glide.with(this)
                                .load(photoUrl)
                                .circleCrop()
                                .into(iv);
                        tvN.setText(name);
                        tvD.setText(entry.getDate());

                        ll.removeAllViews();
                        List<ExerciseHistoryEntry.SetRecord> sets = entry.getSets();
                        for (int i = 0; i < sets.size(); i++) {
                            ExerciseHistoryEntry.SetRecord s = sets.get(i);

                            View row = LayoutInflater.from(this)
                                    .inflate(R.layout.item_routine_set, ll, false);

                            // alternamos el fondo: filas pares (i+1)%2==0 blancas
                            if ((i + 1) % 2 == 0) {
                                row.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
                            } else {
                                row.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
                            }

                            TextView tvNum    = row.findViewById(R.id.tvSetNumber);
                            TextView tvWeight = row.findViewById(R.id.tvWeight);
                            TextView tvReps   = row.findViewById(R.id.tvReps);

                            tvNum.setText(String.valueOf(s.getSetNumber()));
                            tvWeight.setText(String.valueOf(s.getWeight()));
                            tvReps.setText(String.valueOf(s.getReps()));

                            ll.addView(row);
                        }

                        binding.llHistory.addView(card);
                    }
                });
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
