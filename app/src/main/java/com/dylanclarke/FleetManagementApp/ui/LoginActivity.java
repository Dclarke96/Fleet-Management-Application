package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.network.ApiClient;
import com.dylanclarke.FleetManagementApp.network.ApiResponse;
import com.dylanclarke.FleetManagementApp.network.ApiService;
import com.dylanclarke.FleetManagementApp.network.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Handles user authentication and JWT session initialization.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String PREF_AUTH = "auth";

    private static final String KEY_JWT = "jwt";

    private EditText etUsername;

    private EditText etPassword;

    private Button btnLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initializeViews();

        apiService = ApiClient
                .getClient(getApplicationContext())
                .create(ApiService.class);

        btnLogin.setOnClickListener(v -> loginUser());
    }

    /**
     * Initializes UI view references.
     */
    private void initializeViews() {

        etUsername = findViewById(R.id.etUsername);

        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
    }

    /**
     * Attempts user authentication through the backend API.
     */
    private void loginUser() {

        String username =
                etUsername.getText().toString().trim();

        String password =
                etPassword.getText().toString().trim();

        LoginRequest request =
                new LoginRequest(username, password);

        apiService.login(request)
                .enqueue(new Callback<ApiResponse<String>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<String>> call,
                            Response<ApiResponse<String>> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getData() != null) {

                            handleSuccessfulLogin(
                                    response.body().getData()
                            );

                        } else {

                            showToast(
                                    "Login failed (HTTP "
                                            + response.code()
                                            + ")"
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<String>> call,
                            Throwable t
                    ) {

                        showToast(
                                "Connection failed: "
                                        + t.getMessage()
                        );

                        t.printStackTrace();
                    }
                });
    }

    /**
     * Saves JWT token and navigates to the main application screen.
     */
    private void handleSuccessfulLogin(
            String token
    ) {

        SharedPreferences preferences =
                getSharedPreferences(
                        PREF_AUTH,
                        MODE_PRIVATE
                );

        preferences.edit()
                .putString(KEY_JWT, token)
                .apply();

        showToast("Login successful");

        startActivity(
                new Intent(
                        LoginActivity.this,
                        MainActivity.class
                )
        );

        finish();
    }

    /**
     * Displays a short user message.
     */
    private void showToast(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}