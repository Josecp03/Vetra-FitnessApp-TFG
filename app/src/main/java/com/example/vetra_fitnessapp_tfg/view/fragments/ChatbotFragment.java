package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentChatbotBinding;
import com.example.vetra_fitnessapp_tfg.utils.KeyStoreManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import android.text.Layout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

public class ChatbotFragment extends Fragment {

    private FragmentChatbotBinding binding;
    private KeyStoreManager keyStore;
    private FirebaseFirestore db;
    private String uid;

    // Datos del usuario
    private int age, height, calorieGoal;
    private double weight;
    private String gender;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Inicialización
        keyStore = new KeyStoreManager();
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Estado inicial: ocultamos mensajes
        binding.scrollMessages.setVisibility(View.GONE);
        binding.messagesContainer.setVisibility(View.GONE);

        // Carga datos y habilita sugerencias
        loadUserData();

        // Enviar texto manual
        binding.btnSend.setOnClickListener(v -> {
            String msg = binding.etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) return;
            showMessage(msg);
        });

        // Abrir diálogo de clear chat
        binding.btnClear.setOnClickListener(v -> showClearChatDialog());

        // Abrir diálogo de usage limit
        binding.btnUsageLimit.setOnClickListener(v -> showUsageLimitDialog());

        return root;
    }

    private void loadUserData() {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    age = doc.getLong("age").intValue();
                    height = doc.getLong("height").intValue();
                    weight = doc.getDouble("weight");
                    calorieGoal = doc.getLong("user_calories").intValue();
                    String encGender = doc.getString("gender");
                    gender = keyStore.decrypt(encGender);
                    setupSuggestionButtons();
                });
    }

    private void setupSuggestionButtons() {
        binding.btnSugSnack.setOnClickListener(v -> showMessage(buildSnackPrompt()));
        binding.btnSugWeight.setOnClickListener(v -> showMessage(buildIdealWeightPrompt()));
        binding.btnSugRoutine.setOnClickListener(v -> showMessage(buildRoutinePrompt()));
        binding.btnSugMeal.setOnClickListener(v -> showMessage(buildMealPrompt()));
    }

    private void showUsageLimitDialog() {
        View dlg = getLayoutInflater().inflate(R.layout.dialog_usage_limit, null);
        BottomSheetDialog d = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        d.setContentView(dlg);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
    }

    private void showClearChatDialog() {
        // Inflate del layout de tu diálogo
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_clear_chat, null);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        MaterialButton btnConfirm = dialogView.findViewById(R.id.buttonClearChat);
        btnConfirm.setOnClickListener(v -> {
            clearMessages();
            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private String buildSnackPrompt() {
        return String.format(
                "VetraGPT, I’m targeting %d kcal/day. Suggest a quick snack under 150 kcal that’s high in protein and easy to prepare.",
                calorieGoal
        );
    }

    @SuppressLint("DefaultLocale")
    private String buildIdealWeightPrompt() {
        return String.format(
                "Hello VetraGPT, as a %d-year-old %s, %d cm tall, what’s my ideal weight range according to standard health guidelines?",
                age, gender, height
        );
    }

    @SuppressLint("DefaultLocale")
    private String buildRoutinePrompt() {
        return String.format(
                "Hey VetraGPT, I’m a %d-year-old %s, %.1f kg, and new to workouts. I can train 3×30 min/week. Suggest a simple full-body beginner routine.",
                age, gender, weight
        );
    }

    @SuppressLint("DefaultLocale")
    private String buildMealPrompt() {
        return String.format(
                "VetraGPT, I have a daily goal of %d kcal. Recommend a balanced dinner under 600 kcal with ≥ 25 g protein and plenty of veggies.",
                calorieGoal
        );
    }

    private void showMessage(String msg) {
        binding.suggestionsContainer.setVisibility(View.GONE);
        binding.scrollMessages.setVisibility(View.VISIBLE);
        binding.messagesContainer.setVisibility(View.VISIBLE);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int maxBubbleWidth = (int)(screenWidth * 0.66f);

        TextView bubble = new TextView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dpToPx(8), 0, dpToPx(8));
        lp.gravity = Gravity.END;  // si quieres seguir manteniendo la burbuja a la derecha
        bubble.setLayoutParams(lp);

        bubble.setMaxWidth(maxBubbleWidth);
        bubble.setBackgroundResource(R.drawable.user_bubble_background);
        bubble.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        bubble.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.goldman));
        bubble.setIncludeFontPadding(false);
        bubble.setTextColor(Color.WHITE);
        bubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        // Justificación de texto (API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            bubble.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        bubble.setText(msg);

        binding.messagesContainer.addView(bubble);
        binding.etMessage.setText("");
        binding.scrollMessages.post(() ->
                binding.scrollMessages.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    private void showBotMessage(String msg) {
        binding.suggestionsContainer.setVisibility(View.GONE);
        binding.scrollMessages.setVisibility(View.VISIBLE);
        binding.messagesContainer.setVisibility(View.VISIBLE);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int maxBubbleWidth = (int)(screenWidth * 0.66f);

        TextView bubble = new TextView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dpToPx(8), 0, dpToPx(8));
        // Empujamos la burbuja a la izquierda
        lp.gravity = Gravity.START;
        bubble.setLayoutParams(lp);

        bubble.setMaxWidth(maxBubbleWidth);
        bubble.setBackgroundResource(R.drawable.bot_bubble_background); // fondo negro
        bubble.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        bubble.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.goldman));
        bubble.setIncludeFontPadding(false);
        bubble.setText(msg);
        bubble.setTextColor(Color.WHITE);
        bubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        // Texto alineado a la izquierda dentro de la burbuja
        bubble.setGravity(Gravity.START);

        binding.messagesContainer.addView(bubble);
        binding.scrollMessages.post(() ->
                binding.scrollMessages.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    private void clearMessages() {
        binding.messagesContainer.removeAllViews();
        binding.scrollMessages.setVisibility(View.GONE);
        binding.messagesContainer.setVisibility(View.GONE);
        binding.suggestionsContainer.setVisibility(View.VISIBLE);
    }

    private int dpToPx(int dp){
        float d = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * d);
    }
}
