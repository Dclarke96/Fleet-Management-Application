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

// DESIGN FOR SCALABILITY:
// Feature-specific activity keeps UI modular,
// allowing independent expansion of application features.

public class ReportActivity extends AppCompatActivity {

    private RecyclerView rvReport;
    private TextView tvTimestamp;
    private Button btnBack;

    private MaintenanceRepository maintenanceRepo;
    private VehicleRepository vehicleRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

        // Bind views
        rvReport = findViewById(R.id.rvReport);

        tvTimestamp =
                findViewById(R.id.tvReportTimestamp);

        btnBack =
                findViewById(R.id.btnBackToVehicles);

        // Initialize repositories
        maintenanceRepo =
                new MaintenanceRepository(this);

        vehicleRepo =
                new VehicleRepository(this);

        // Professional timestamp with date and time
        String timestamp =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.US
                ).format(new Date());

        tvTimestamp.setText(
                "Report generated at: " + timestamp
        );

        // Set up RecyclerView
        rvReport.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Add dividers for a table-like appearance
        DividerItemDecoration divider =
                new DividerItemDecoration(
                        rvReport.getContext(),
                        DividerItemDecoration.VERTICAL
                );

        rvReport.addItemDecoration(divider);

        // -----------------------------------------------------
        // LOAD VEHICLES THROUGH REPOSITORY CALLBACK
        // -----------------------------------------------------
        vehicleRepo.getAllVehicles(
                new VehicleRepository.VehicleCallback() {

                    @Override
                    public void onSuccess(List<Vehicle> vehicles) {

                        // AFTER vehicles load, load maintenance
                        maintenanceRepo.getAllMaintenance(
                                new MaintenanceRepository.MaintenanceCallback() {

                                    @Override
                                    public void onSuccess(List<MaintenanceRecord> maintenanceRecords) {

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
                                    public void onError(String error) {

                                        runOnUiThread(() -> {

                                            Toast.makeText(
                                                    ReportActivity.this,
                                                    error,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        });
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(String error) {

                        Toast.makeText(
                                ReportActivity.this,
                                error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

        // Back button returns to VehicleListActivity
        btnBack.setOnClickListener(v -> finish());
    }
}