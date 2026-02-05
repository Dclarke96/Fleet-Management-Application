package com.example.d308project.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308project.R;
import com.example.d308project.data.MaintenanceRepository;
import com.example.d308project.data.VehicleRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        tvTimestamp = findViewById(R.id.tvReportTimestamp);
        btnBack = findViewById(R.id.btnBackToVehicles);

        // Initialize repositories
        maintenanceRepo = new MaintenanceRepository(this);
        vehicleRepo = new VehicleRepository(this);

        // Professional timestamp with date and time
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                .format(new Date());
        tvTimestamp.setText("Report generated at: " + timestamp);

        // Set up RecyclerView
        rvReport.setLayoutManager(new LinearLayoutManager(this));

        // Add dividers for a table-like professional appearance
        DividerItemDecoration divider = new DividerItemDecoration(
                rvReport.getContext(),
                DividerItemDecoration.VERTICAL
        );
        rvReport.addItemDecoration(divider);

        // Set adapter with data
        rvReport.setAdapter(new ReportAdapter(
                maintenanceRepo.getAllMaintenance(),
                vehicleRepo.getAllVehicles()
        ));

        // Back button returns to VehicleListActivity
        btnBack.setOnClickListener(v -> finish());
    }
}
