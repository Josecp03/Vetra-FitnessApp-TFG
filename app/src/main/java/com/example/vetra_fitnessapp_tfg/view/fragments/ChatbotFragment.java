package com.example.vetra_fitnessapp_tfg.view.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.vetra_fitnessapp_tfg.R;
import com.example.vetra_fitnessapp_tfg.databinding.FragmentChatbotBinding;

public class ChatbotFragment extends Fragment {

    private FragmentChatbotBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Estado inicial: sin mensajes
        binding.scrollMessages.setVisibility(View.GONE);
        binding.messagesContainer.setVisibility(View.GONE);

        // Enviar mensaje
        binding.btnSend.setOnClickListener(v -> {
            String msg = binding.etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) return;
            showMessage(msg);
        });

        // Borrar conversación
        binding.btnClear.setOnClickListener(v -> clearMessages());

        return root;
    }

    private void showMessage(String msg) {
        // Ocultar sugerencias, mostrar scroll + container
        binding.suggestionsContainer.setVisibility(View.GONE);
        binding.scrollMessages.setVisibility(View.VISIBLE);
        binding.messagesContainer.setVisibility(View.VISIBLE);

        // Crear y añadir burbuja
        TextView bubble = new TextView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, dpToPx(8), 0, dpToPx(8));
        bubble.setLayoutParams(lp);
        bubble.setBackgroundResource(R.drawable.user_bubble_background);
        bubble.setPadding(
                dpToPx(16),
                dpToPx(12),
                dpToPx(16),
                dpToPx(12)
        );
        bubble.setText(msg);
        bubble.setTextColor(getResources().getColor(android.R.color.black));
        binding.messagesContainer.addView(bubble);

        // Limpiar input y desplazar
        binding.etMessage.setText("");
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

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
