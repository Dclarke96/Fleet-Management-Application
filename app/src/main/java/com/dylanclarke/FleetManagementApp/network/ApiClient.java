package com.dylanclarke.FleetManagementApp.network;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            // -----------------------------
            // 1. LOGGING INTERCEPTOR (IMPORTANT)
            // -----------------------------
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // -----------------------------
            // 2. AUTH INTERCEPTOR (YOUR JWT)
            // -----------------------------
            Interceptor authInterceptor = chain -> {

                SharedPreferences prefs =
                        context.getSharedPreferences("auth", Context.MODE_PRIVATE);

                String token = prefs.getString("jwt", null);

                Request request = chain.request();

                if (token != null) {
                    request = request.newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                }

                return chain.proceed(request);
            };

            // -----------------------------
            // 3. OKHTTP CLIENT
            // -----------------------------
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(logging) // 👈 MUST BE INCLUDED
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            // -----------------------------
            // 4. RETROFIT
            // -----------------------------
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}