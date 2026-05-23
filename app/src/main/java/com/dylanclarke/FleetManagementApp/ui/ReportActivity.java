package com.dylanclarke.FleetManagementApp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRepository;
import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.data.VehicleRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Displays a combined maintenance and vehicle report.
 */
public class ReportActivity extends AppCompatActivity {

    private static final String TIMESTAMP_FORMAT =
            "yyyy-MM-dd HH:mm:ss";

    private RecyclerView rvReport;

    private TextView tvTimestamp;

    private Button btnBack;

    private MaintenanceRepository maintenanceRepository;

    private VehicleRepository vehicleRepository;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

        initializeViews();

        initializeRepositories();

        setupRecyclerView();

        displayTimestamp();

        setupListeners();

        loadReportData();
    }

    /**
     * Initializes UI view references.
     */
    private void initializeViews() {

        rvReport =
                findViewById(R.id.rvReport);

        tvTimestamp =
                findViewById(R.id.tvReportTimestamp);

        btnBack =
                findViewById(R.id.btnBackToVehicles);
    }

    /**
     * Initializes repository dependencies.
     */
    private void initializeRepositories() {

        maintenanceRepository =
                new MaintenanceRepository(this);

        vehicleRepository =
                new VehicleRepository(this);
    }

    /**
     * Configures RecyclerView behavior and styling.
     */
    private void setupRecyclerView() {

        rvReport.setLayoutManager(
                new LinearLayoutManager(this)
        );

        DividerItemDecoration divider =
                new DividerItemDecoration(
                        rvReport.getContext(),
                        DividerItemDecoration.VERTICAL
                );

        rvReport.addItemDecoration(divider);
    }

    /**
     * Displays the report generation timestamp.
     */
    private void displayTimestamp() {

        String timestamp =
                new SimpleDateFormat(
                        TIMESTAMP_FORMAT,
                        Locale.US
                ).format(new Date());

        tvTimestamp.setText(
                "Report generated at: "
                        + timestamp
        );
    }

    /**
     * Configures UI interaction listeners.
     */
    private void setupListeners() {

        btnBack.setOnClickListener(
                v -> finish()
        );
    }

    /**
     * Loads vehicle and maintenance data
     * required for the report.
     */
    private void loadReportData() {

        vehicleRepository.getAllVehicles(
                new VehicleRepository.VehicleCallback() {

                    @Override
                    public void onSuccess(
                            List<Vehicle> vehicles
                    ) {

                        loadMaintenanceData(vehicles);
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
     * Loads maintenance records after vehicles load successfully.
     */
    private void loadMaintenanceData(
            List<Vehicle> vehicles
    ) {

        maintenanceRepository.getAllMaintenance(
                new MaintenanceRepository.MaintenanceCallback() {

                    @Override
                    public void onSuccess(
                            List<MaintenanceRecord> maintenanceRecords
                    ) {

                        runOnUiThread(() -> {

                            rvReport.setAdapter(
                                    new ReportAdapter(
                                            maintenanceRecords,
                                            vehicles
                                    )
                            );
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