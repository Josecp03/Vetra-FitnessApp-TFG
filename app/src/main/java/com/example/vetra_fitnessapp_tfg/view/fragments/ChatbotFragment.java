package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.text.LineBreaker;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.vetra_fitnessapp_tfg.network.ChatApiClient;
import com.example.vetra_fitnessapp_tfg.utils.KeyStoreManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import okhttp3.Call;

public class ChatbotFragment extends Fragment {

    private FragmentChatbotBinding binding;
    private ChatApiClient apiClient;
    private KeyStoreManager keyStore;
    private FirebaseFirestore db;
    private String uid;
    private int age, height, calorieGoal;
    private double weight;
    private String gender;
    private Call currentCall;
    private TextView loadingBubble;
    private long chatCount = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        apiClient = new ChatApiClient();
        keyStore  = new KeyStoreManager();
        db        = FirebaseFirestore.getInstance();
        uid       = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.scrollMessages.setVisibility(View.GONE);
        binding.messagesContainer.setVisibility(View.GONE);

        loadUserData();
        setupButtons();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
    }

    private void loadUserData() {
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    age         = doc.getLong("age").intValue();
                    height      = doc.getLong("height").intValue();
                    weight      = doc.getDouble("weight");
                    calorieGoal = doc.getLong("user_calories").intValue();
                    gender      = keyStore.decrypt(doc.getString("gender"));

                    chatCount = doc.contains("limit_chat") ? doc.getLong("limit_chat") : 0L;
                    checkLimit(chatCount);
                })
                .addOnFailureListener(e -> {
                    Log.w("ChatbotFragment", "Error loading user data", e);
                });
    }

    private void setupButtons() {
        binding.btnSend.setOnClickListener(v ->
                sendMessage(binding.etMessage.getText().toString().trim())
        );
        binding.btnClear.setOnClickListener(v -> showClearChatDialog());
        binding.btnUsageLimit.setOnClickListener(v -> showUsageLimitDialog());

        binding.btnSugSnack.setOnClickListener(v ->
                sendMessage(String.format(
                        "VetraGPT, I’m targeting %d kcal/day. Suggest a quick snack under 150 kcal that’s high in protein and easy to prepare.",
                        calorieGoal))
        );
        binding.btnSugWeight.setOnClickListener(v ->
                sendMessage(String.format(
                        "Hello VetraGPT, as a %d-year-old %s, %d cm tall, what’s my ideal weight range?",
                        age, gender, height))
        );
        binding.btnSugRoutine.setOnClickListener(v ->
                sendMessage(String.format(
                        "Hey VetraGPT, I’m a %d-year-old %s, %.1f kg, new to workouts. I can train 3×30 min/week. Suggest a beginner routine.",
                        age, gender, weight))
        );
        binding.btnSugMeal.setOnClickListener(v ->
                sendMessage(String.format(
                        "VetraGPT, I have a daily goal of %d kcal. Recommend a dinner under 600 kcal with ≥25 g protein and veggies.",
                        calorieGoal))
        );
    }

    private void sendMessage(String msg) {
        if (TextUtils.isEmpty(msg) || chatCount >= 100) return;

        binding.suggestionsContainer.setVisibility(View.GONE);
        binding.scrollMessages.setVisibility(View.VISIBLE);
        binding.messagesContainer.setVisibility(View.VISIBLE);
        showMessageBubble(msg, Gravity.END, R.drawable.user_bubble_background, Color.WHITE);

        startLoading();

        currentCall = apiClient.sendMessage(msg, new ChatApiClient.Callback() {
            @Override public void onSuccess(String response) {
                if (!isAdded()) return;
                stopLoading();
                showBotBubble(response);
                incrementChatCounter();
            }
            @Override public void onError(Exception e) {
                if (!isAdded()) return;
                stopLoading();
                showBotBubble("Error: " + e.getMessage());
                incrementChatCounter();
            }
        });
    }

    private void incrementChatCounter() {
        DocumentReference ref = db.collection("users").document(uid);
        db.runTransaction(tx -> {
            DocumentSnapshot snap = tx.get(ref);
            long current = snap.contains("limit_chat") ? snap.getLong("limit_chat") : 0L;
            long updated = current + 1;
            tx.update(ref, "limit_chat", updated);
            return updated;
        }).addOnSuccessListener(newCount -> {
            chatCount = newCount;
            checkLimit(chatCount);
        }).addOnFailureListener(e -> {
            Log.w("ChatbotFragment", "Error incrementing chat limit", e);
        });
    }


    private void checkLimit(long count) {
        boolean blocked = count >= 100;
        binding.etMessage.setEnabled(!blocked);
        binding.etMessage.setHint(blocked
                ? "Request limit reached"
                : "Type a message to VetraGPT");
        binding.btnSend.setEnabled(!blocked);
        setSuggestionsEnabled(!blocked);
    }

    private void startLoading() {
        binding.btnSend.setEnabled(false);
        binding.btnClear.setEnabled(false);
        binding.btnUsageLimit.setEnabled(false);
        binding.etMessage.setEnabled(false);
        setSuggestionsEnabled(false);

        loadingBubble = new TextView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dpToPx(8), 0, dpToPx(8));
        lp.gravity = Gravity.START;
        loadingBubble.setLayoutParams(lp);

        int maxW = (int)(getResources().getDisplayMetrics().widthPixels * .66f);
        loadingBubble.setMaxWidth(maxW);
        loadingBubble.setBackground(null);
        loadingBubble.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        loadingBubble.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.goldman));
        loadingBubble.setIncludeFontPadding(false);
        loadingBubble.setTextColor(Color.BLACK);
        loadingBubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            loadingBubble.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        loadingBubble.setText("Loading…");
        loadingBubble.setGravity(Gravity.START);

        binding.messagesContainer.addView(loadingBubble);
        binding.scrollMessages.post(() ->
                binding.scrollMessages.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    private void stopLoading() {
        if (loadingBubble != null) {
            binding.messagesContainer.removeView(loadingBubble);
            loadingBubble = null;
        }
        binding.btnSend.setEnabled(true);
        binding.btnClear.setEnabled(true);
        binding.btnUsageLimit.setEnabled(true);
        binding.etMessage.setEnabled(true);
        setSuggestionsEnabled(true);
    }

    private void setSuggestionsEnabled(boolean enabled) {
        for (int id : new int[]{
                R.id.btnSugSnack, R.id.btnSugWeight,
                R.id.btnSugRoutine, R.id.btnSugMeal
        }) {
            binding.getRoot().findViewById(id).setEnabled(enabled);
        }
    }

    private void showBotBubble(String msg) {
        showMessageBubble(msg, Gravity.START, R.drawable.bot_bubble_background, Color.BLACK);
    }

    @SuppressLint("DefaultLocale")
    private void showMessageBubble(String msg, int gravity, int bgRes, int textColor) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int maxW = (int)(screenWidth * .66f);

        TextView bubble = new TextView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dpToPx(8), 0, dpToPx(8));
        lp.gravity = gravity;
        bubble.setLayoutParams(lp);

        bubble.setMaxWidth(maxW);
        bubble.setBackgroundResource(bgRes);
        bubble.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        bubble.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.goldman));
        bubble.setIncludeFontPadding(false);
        bubble.setTextColor(textColor);
        bubble.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            bubble.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        bubble.setText(msg);
        bubble.setGravity(gravity);

        binding.messagesContainer.addView(bubble);
        binding.etMessage.setText("");
        binding.scrollMessages.post(() ->
                binding.scrollMessages.fullScroll(ScrollView.FOCUS_DOWN)
        );
    }

    private void showUsageLimitDialog() {
        View dlg = getLayoutInflater().inflate(R.layout.dialog_usage_limit, null);
        BottomSheetDialog d = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        d.setContentView(dlg);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        d.show();
    }

    private void showClearChatDialog() {
        View v = getLayoutInflater().inflate(R.layout.dialog_clear_chat, null);
        BottomSheetDialog dlg = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        dlg.setContentView(v);
        dlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        MaterialButton btn = v.findViewById(R.id.buttonClearChat);
        btn.setOnClickListener(x -> {
            binding.messagesContainer.removeAllViews();
            binding.scrollMessages.setVisibility(View.GONE);
            binding.messagesContainer.setVisibility(View.GONE);
            binding.suggestionsContainer.setVisibility(View.VISIBLE);
            checkLimit(chatCount);
            dlg.dismiss();
        });
        dlg.show();
    }

    private int dpToPx(int dp) {
        float d = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * d);
    }
}