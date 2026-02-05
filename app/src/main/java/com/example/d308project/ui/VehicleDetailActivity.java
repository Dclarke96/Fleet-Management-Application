package com.example.d308project.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.d308project.R;
import com.example.d308project.data.Vehicle;
import com.example.d308project.data.VehicleRepository;

import java.util.Calendar;
import java.util.Locale;

public class VehicleDetailActivity extends AppCompatActivity {

    private EditText editTitle, editLocation, editStartDate, editEndDate;
    private Switch switchAlert;
    private Button btnSave, btnBack, btnAddMaintenance, btnManageMaintenance;

    private VehicleRepository vehicleRepo;
    private int vehicleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);

        // Bind views
        editTitle = findViewById(R.id.editTitle);
        editLocation = findViewById(R.id.editLocation); // location field
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        switchAlert = findViewById(R.id.switchAlert);

        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnAddMaintenance = findViewById(R.id.btnAddMaintenance);
        btnManageMaintenance = findViewById(R.id.btnManageMaintenance);

        // Initialize repository
        vehicleRepo = new VehicleRepository(getApplicationContext());

        // Check if editing existing vehicle
        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            if (vehicleId != -1) {
                loadVehicle();
            }
        }

        // Date picker setup
        Calendar calendar = Calendar.getInstance();
        editStartDate.setOnClickListener(v -> showDatePicker(editStartDate, calendar));
        editEndDate.setOnClickListener(v -> showDatePicker(editEndDate, calendar));

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

    private void showDatePicker(EditText editText, Calendar calendar) {
        new DatePickerDialog(
                this,
                (view, year, month, day) -> editText.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
                ),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void loadVehicle() {
        Vehicle vehicle = vehicleRepo.getVehicleById(vehicleId);
        if (vehicle == null) return;

        editTitle.setText(vehicle.getMake() + " " + vehicle.getModel());
        editLocation.setText(vehicle.getLocation()); // ✅ updated
        editStartDate.setText(vehicle.getStartDate());
        editEndDate.setText(vehicle.getEndDate());
        switchAlert.setChecked(vehicle.isMaintenanceAlertsEnabled());
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
            vehicle = vehicleRepo.getVehicleById(vehicleId);
            if (vehicle == null) vehicle = new Vehicle();
        }

        // Split title into make and model
        String[] parts = title.split(" ", 2);
        vehicle.setMake(parts.length > 0 ? parts[0] : "");
        vehicle.setModel(parts.length > 1 ? parts[1] : "");
        vehicle.setLocation(location); // ✅ updated
        vehicle.setStartDate(startDate);
        vehicle.setEndDate(endDate);
        vehicle.setMaintenanceAlertsEnabled(switchAlert.isChecked());

        // Save via repository
        if (vehicleId == -1) {
            int newId = vehicleRepo.addVehicle(vehicle, this); // ✅ validation with Context
            if (newId == -1) return; // validation failed
            vehicleId = newId;
        } else {
            vehicleRepo.updateVehicle(vehicle, this); // ✅ validation with Context
        }

        Toast.makeText(this, "Vehicle saved", Toast.LENGTH_SHORT).show();
    }
}
