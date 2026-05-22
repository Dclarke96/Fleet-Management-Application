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

/**
 * Centralized Retrofit API client configuration.
 * Handles:
 * - Retrofit initialization
 * - JWT authorization headers
 * - HTTP logging
 * - OkHttp client configuration
 */
public class ApiClient {

    private static final String BASE_URL =
            "http://10.0.2.2:8080/";

    private static final String PREF_AUTH = "auth";

    private static final String KEY_JWT = "jwt";

    private static final int NETWORK_TIMEOUT_SECONDS = 30;

    private static Retrofit retrofit;

    /**
     * Returns the shared Retrofit client instance.
     */
    public static Retrofit getClient(
            Context context
    ) {

        if (retrofit == null) {

            // -------------------------------------------------
            // HTTP LOGGING
            // -------------------------------------------------

            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();

            loggingInterceptor.setLevel(
                    HttpLoggingInterceptor.Level.BODY
            );

            // -------------------------------------------------
            // JWT AUTHORIZATION INTERCEPTOR
            // -------------------------------------------------

            Interceptor authInterceptor = chain -> {

                SharedPreferences preferences =
                        context.getSharedPreferences(
                                PREF_AUTH,
                                Context.MODE_PRIVATE
                        );

                String token =
                        preferences.getString(KEY_JWT, null);

                Request request = chain.request();

                if (token != null) {

                    request = request.newBuilder()
                            .addHeader(
                                    "Authorization",
                                    "Bearer " + token
                            )
                            .build();
                }

                return chain.proceed(request);
            };

            // -------------------------------------------------
            // OKHTTP CLIENT
            // -------------------------------------------------

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(
                            NETWORK_TIMEOUT_SECONDS,
                            TimeUnit.SECONDS
                    )
                    .readTimeout(
                            NETWORK_TIMEOUT_SECONDS,
                            TimeUnit.SECONDS
                    )
                    .build();

            // -------------------------------------------------
            // RETROFIT CLIENT
            // -------------------------------------------------

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}