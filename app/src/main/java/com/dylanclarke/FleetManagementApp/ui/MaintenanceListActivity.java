package com.dylanclarke.FleetManagementApp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRepository;

import java.util.List;

/**
 * Displays all maintenance records associated with a vehicle.
 */
public class MaintenanceListActivity extends AppCompatActivity {

    private static final String EXTRA_VEHICLE_ID =
            "vehicleId";

    private static final String EXTRA_MAINTENANCE_ID =
            "maintenanceId";

    private LinearLayout maintenanceContainer;

    private Button btnAddMaintenance;

    private Button btnBack;

    private MaintenanceRepository maintenanceRepository;

    private long vehicleId = -1L;

    private boolean isLoading = false;

    @Override
    protected void onCreate(
            @Nullable Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maintenance_list);

        initializeViews();

        initializeRepository();

        loadIntentData();

        setupListeners();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (vehicleId != -1L) {
            loadMaintenanceRecords();
        }
    }

    /**
     * Initializes UI view references.
     */
    private void initializeViews() {

        maintenanceContainer =
                findViewById(R.id.maintenanceContainer);

        btnAddMaintenance =
                findViewById(R.id.btnAddMaintenance);

        btnBack =
                findViewById(R.id.backButton);
    }

    /**
     * Initializes repository dependencies.
     */
    private void initializeRepository() {

        maintenanceRepository =
                new MaintenanceRepository(
                        getApplicationContext()
                );
    }

    /**
     * Loads activity intent extras.
     */
    private void loadIntentData() {

        Intent intent = getIntent();

        vehicleId =
                intent.getLongExtra(
                        EXTRA_VEHICLE_ID,
                        -1L
                );
    }

    /**
     * Configures UI interaction listeners.
     */
    private void setupListeners() {

        btnAddMaintenance.setOnClickListener(
                v -> openCreateMaintenanceScreen()
        );

        btnBack.setOnClickListener(
                v -> finish()
        );
    }

    /**
     * Opens the maintenance creation screen.
     */
    private void openCreateMaintenanceScreen() {

        Intent intent =
                new Intent(
                        this,
                        MaintenanceDetailActivity.class
                );

        intent.putExtra(
                EXTRA_VEHICLE_ID,
                vehicleId
        );

        startActivity(intent);
    }

    /**
     * Loads maintenance records for the selected vehicle.
     */
    private void loadMaintenanceRecords() {

        if (isLoading) {
            return;
        }

        isLoading = true;

        maintenanceContainer.removeAllViews();

        maintenanceRepository.getMaintenanceForVehicle(
                vehicleId,
                new MaintenanceRepository.MaintenanceCallback() {

                    @Override
                    public void onSuccess(
                            List<MaintenanceRecord> records
                    ) {

                        runOnUiThread(() -> {

                            maintenanceContainer.removeAllViews();

                            for (MaintenanceRecord record : records) {

                                addMaintenanceView(record);
                            }

                            isLoading = false;
                        });
                    }

                    @Override
                    public void onError(
                            String error
                    ) {

                        runOnUiThread(() -> {

                            isLoading = false;

                            showToast(error);
                        });
                    }
                }
        );
    }

    /**
     * Adds a maintenance item view to the container.
     */
    private void addMaintenanceView(
            MaintenanceRecord record
    ) {

        View itemView =
                getLayoutInflater().inflate(
                        R.layout.item_maintenance_record,
                        maintenanceContainer,
                        false
                );

        TextView txtDescription =
                itemView.findViewById(
                        R.id.txtMaintenanceDescription
                );

        TextView txtDate =
                itemView.findViewById(
                        R.id.txtMaintenanceDate
                );

        Button btnEdit =
                itemView.findViewById(
                        R.id.btnEditMaintenance
                );

        Button btnDelete =
                itemView.findViewById(
                        R.id.btnDeleteMaintenance
                );

        txtDescription.setText(
                record.getDescription()
        );

        txtDate.setText(
                record.getServiceDate()
        );

        btnEdit.setOnClickListener(
                v -> openEditMaintenanceScreen(record)
        );

        btnDelete.setOnClickListener(
                v -> deleteMaintenance(record)
        );

        maintenanceContainer.addView(itemView);
    }

    /**
     * Opens the maintenance edit screen.
     */
    private void openEditMaintenanceScreen(
            MaintenanceRecord record
    ) {

        Intent intent =
                new Intent(
                        this,
                        MaintenanceDetailActivity.class
                );

        intent.putExtra(
                EXTRA_MAINTENANCE_ID,
                record.getId()
        );

        intent.putExtra(
                EXTRA_VEHICLE_ID,
                vehicleId
        );

        startActivity(intent);
    }

    /**
     * Deletes a maintenance record.
     */
    private void deleteMaintenance(
            MaintenanceRecord record
    ) {

        maintenanceRepository.deleteMaintenance(
                record.getId(),
                new MaintenanceRepository.DeleteMaintenanceCallback() {

                    @Override
                    public void onSuccess() {

                        runOnUiThread(() -> {

                            showToast("Maintenance deleted");

                            loadMaintenanceRecords();
                        });
                    }

                    @Override
                    public void onError(
                            String error
                    ) {

                        runOnUiThread(() ->
                                showToast(error)
                        );
                    }
                }
        );
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