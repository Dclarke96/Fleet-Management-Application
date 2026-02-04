package com.example.d308project.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.Vehicle;

import java.util.Calendar;
import java.util.Locale;

public class VehicleDetailActivity extends AppCompatActivity {

    private EditText editTitle, editLocation, editStartDate, editEndDate;
    private Switch switchAlert;
    private Button btnSave, btnDelete, btnShare, btnBack;
    private Button btnAddMaintenance, btnManageMaintenance;

    private AppDatabase db;
    private int vehicleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);

        // Bind views
        editTitle = findViewById(R.id.editTitle);
        editLocation = findViewById(R.id.editLocation);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        switchAlert = findViewById(R.id.switchAlert);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);
        btnAddMaintenance = findViewById(R.id.btnAddMaintenance);
        btnManageMaintenance = findViewById(R.id.btnManageMaintenance);

        db = AppDatabase.getInstance(getApplicationContext());

        // Check if editing existing vehicle
        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            if (vehicleId != -1) {
                loadVehicle();
            }
        }

        // Date picker setup
        Calendar calendar = Calendar.getInstance();

        editStartDate.setOnClickListener(v -> new DatePickerDialog(
                this,
                (view, year, month, day) ->
                        editStartDate.setText(String.format(
                                Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        editEndDate.setOnClickListener(v -> new DatePickerDialog(
                this,
                (view, year, month, day) ->
                        editEndDate.setText(String.format(
                                Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        // Button listeners
        btnSave.setOnClickListener(v -> saveVehicle());
        btnBack.setOnClickListener(v -> finish());

        btnAddMaintenance.setOnClickListener(v -> {
            if (vehicleId == -1) {
                Toast.makeText(this, "Save vehicle first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, MaintenanceDetailActivity.class);
            intent.putExtra("vehicleId", vehicleId);
            startActivity(intent);
        });

        btnManageMaintenance.setOnClickListener(v -> {
            if (vehicleId == -1) {
                Toast.makeText(this, "Save vehicle first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, MaintenanceListActivity.class);
            intent.putExtra("vehicleId", vehicleId);
            startActivity(intent);
        });
    }

    private void loadVehicle() {
        Vehicle vehicle = db.vehicleDao().getVehicleById(vehicleId);
        if (vehicle == null) return;

        editTitle.setText(vehicle.make + " " + vehicle.model);
        editLocation.setText(vehicle.licensePlate);
        editStartDate.setText(vehicle.startDate);
        editEndDate.setText(vehicle.endDate);
        switchAlert.setChecked(vehicle.maintenanceAlertsEnabled);
    }

    private void saveVehicle() {
        String title = editTitle.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String startDate = editStartDate.getText().toString().trim();
        String endDate = editEndDate.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vehicle name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Vehicle vehicle;
        if (vehicleId == -1) {
            vehicle = new Vehicle();
        } else {
            vehicle = db.vehicleDao().getVehicleById(vehicleId);
            if (vehicle == null) vehicle = new Vehicle();
        }

        String[] parts = title.split(" ", 2);
        vehicle.make = parts.length > 0 ? parts[0] : "";
        vehicle.model = parts.length > 1 ? parts[1] : "";
        vehicle.licensePlate = location;
        vehicle.startDate = startDate;
        vehicle.endDate = endDate;
        vehicle.maintenanceAlertsEnabled = switchAlert.isChecked();

        if (vehicleId == -1) {
            vehicleId = (int) db.vehicleDao().insertVehicle(vehicle);
        } else {
            db.vehicleDao().updateVehicle(vehicle);
        }

        Toast.makeText(this, "Vehicle saved", Toast.LENGTH_SHORT).show();
    }
}
