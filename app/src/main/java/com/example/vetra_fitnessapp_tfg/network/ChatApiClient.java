// ChatApiClient.java
package com.example.vetra_fitnessapp_tfg.network;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Cliente para comunicarse con la API de ChatGPT a través de RapidAPI.
 * Maneja las solicitudes HTTP y el procesamiento de respuestas del chatbot.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class ChatApiClient {

    /**
     * Interfaz de callback para manejar respuestas asíncronas del chatbot.
     */
    public interface Callback {
        /**
         * Llamado cuando la solicitud es exitosa.
         *
         * @param message Mensaje de respuesta del chatbot
         */
        void onSuccess(String message);

        /**
         * Llamado cuando ocurre un error en la solicitud.
         *
         * @param e Excepción que describe el error
         */
        void onError(Exception e);
    }

    /** Tipo de contenido JSON para las solicitudes HTTP */
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /** URL de la API de ChatGPT en RapidAPI */
    private static final String URL     = "https://chatgpt-42.p.rapidapi.com/chat";

    /** Clave de API para autenticación con RapidAPI */
    private static final String API_KEY = "440d48ca01mshb9178145c398148p1c905ajsn498799d4ab35";

    /** Cliente HTTP configurado con timeouts apropiados */
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .build();

    /** Handler para ejecutar callbacks en el hilo principal */
    private final Handler main = new Handler(Looper.getMainLooper());

    /**
     * Envía un mensaje al chatbot y maneja la respuesta de forma asíncrona.
     * Procesa la respuesta eliminando formato Markdown y caracteres especiales.
     *
     * @param prompt Mensaje del usuario para enviar al chatbot
     * @param cb Callback para manejar la respuesta o errores
     * @return Call object que permite cancelar la solicitud si es necesario
     */
    public Call sendMessage(String prompt, @NonNull Callback cb) {

        String escaped = prompt.replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ")
                .replaceAll(" {2,}", " ")
                .trim();

        String body = "{\"messages\":[{\"role\":\"user\",\"content\":\""
                + escaped + "\"}],\"model\":\"gpt-4o-mini\"}";

        Request req = new Request.Builder()
                .url(URL)
                .post(RequestBody.create(body, JSON))
                .addHeader("x-rapidapi-key", API_KEY)
                .addHeader("x-rapidapi-host", "chatgpt-42.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();

        Call call = client.newCall(req);
        call.enqueue(new okhttp3.Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                main.post(() -> cb.onError(e));
            }
            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    main.post(() -> cb.onError(new IOException("Código: " + response.code())));
                    return;
                }
                String json = response.body().string();
                try {
                    JSONObject root = new JSONObject(json);
                    String content = root
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    String clean = content
                            .replaceAll("(?m)^\\s*#{1,6}\\s*", "")
                            .replaceAll("(\\*\\*|__)(.*?)\\1", "$2")
                            .replaceAll("(\\*|_)(.*?)\\1", "$2")
                            .replaceAll("(?m)^\\s*[-*+]\\s+", "")
                            .replaceAll("\\[([^\\]]+)]\\([^)]*\\)", "$1")
                            .replaceAll("\\\\\\[|\\\\\\]|\\\\\\(|\\\\\\)", "")
                            .replaceAll("\\\\text\\{([^}]*)\\}", "$1")
                            .replaceAll("\\\\times", "×")
                            .replaceAll("\\\\approx", "≈")
                            .replaceAll("\\\\\\,", " ")
                            .replaceAll(" {2,}", " ")
                            .replaceAll("\\\\", "")
                            .trim();

                    main.post(() -> cb.onSuccess(clean));
                } catch (JSONException e) {
                    main.post(() -> cb.onError(e));
                }
            }
        });

        return call;
    }
}