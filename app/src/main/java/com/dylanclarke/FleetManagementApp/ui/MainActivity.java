package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;

/**
 * Main application landing screen.
 *
 * Provides navigation entry points into
 * core Fleet Management features.
 */
public class MainActivity extends AppCompatActivity {

    private Button btnViewVehicles;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeViews();

        btnViewVehicles.setOnClickListener(
                v -> openVehicleList()
        );
    }

    /**
     * Initializes UI view references.
     */
    private void initializeViews() {

        btnViewVehicles =
                findViewById(R.id.btnGoToVehicles);
    }

    /**
     * Navigates to the vehicle management screen.
     */
    private void openVehicleList() {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        VehicleListActivity.class
                );

        startActivity(intent);
    }
}