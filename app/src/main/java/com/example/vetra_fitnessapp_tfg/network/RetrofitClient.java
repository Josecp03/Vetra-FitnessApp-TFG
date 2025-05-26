package com.example.vetra_fitnessapp_tfg.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class RetrofitClient {
    private static final String BASE_URL = "https://exercisedb.p.rapidapi.com/";
    private static Retrofit retrofit;

    public static ExerciseApiService getService() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override public Response intercept(Chain chain) throws IOException {
                            Request req = chain.request().newBuilder()
                                    .addHeader("x-rapidapi-key", "440d48ca01mshb9178145c398148p1c905ajsn498799d4ab35")
                                    .addHeader("x-rapidapi-host", "exercisedb.p.rapidapi.com")
                                    .build();
                            return chain.proceed(req);
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ExerciseApiService.class);
    }
}
