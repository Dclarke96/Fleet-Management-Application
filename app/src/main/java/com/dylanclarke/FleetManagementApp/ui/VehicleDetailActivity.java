package com.dylanclarke.FleetManagementApp.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.data.VehicleRepository;

import java.util.Calendar;
import java.util.Locale;

public class VehicleDetailActivity extends AppCompatActivity {

    private EditText editTitle, editMake, editModel, editYear, editLocation, editStartDate, editEndDate;
    private Switch switchAlert;
    private Button btnSave, btnBack, btnAddMaintenance, btnManageMaintenance, btnDelete, btnShare;

    private VehicleRepository vehicleRepo;
    private int vehicleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);

        // Bind views
        editTitle = findViewById(R.id.editTitle);
        editMake = findViewById(R.id.editMake);
        editModel = findViewById(R.id.editModel);
        editYear = findViewById(R.id.editYear);
        editLocation = findViewById(R.id.editLocation);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        switchAlert = findViewById(R.id.switchAlert);

        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        btnAddMaintenance = findViewById(R.id.btnAddMaintenance);
        btnManageMaintenance = findViewById(R.id.btnManageMaintenance);
        btnDelete = findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);

        // Initialize repository
        vehicleRepo = new VehicleRepository(getApplicationContext());

        // Check if editing existing vehicle
        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            if (vehicleId != -1) {
                loadVehicle();
                btnDelete.setVisibility(View.VISIBLE);
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

        // Delete button listener
        btnDelete.setOnClickListener(v -> {
            if (vehicleId == -1) return;
            new AlertDialog.Builder(this)
                    .setTitle("Delete Vehicle")
                    .setMessage("Are you sure you want to delete this vehicle? This cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        Vehicle vehicle = vehicleRepo.getVehicleById(vehicleId);
                        if (vehicle != null && vehicleRepo.deleteVehicle(vehicle)) {
                            Toast.makeText(this, "Vehicle deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Share button listener
        btnShare.setOnClickListener(v -> {
            if (vehicleId == -1) {
                Toast.makeText(this, "Save vehicle first", Toast.LENGTH_SHORT).show();
                return;
            }

            Vehicle vehicle = vehicleRepo.getVehicleById(vehicleId);
            if (vehicle == null) return;

            String vehicleInfo = "Vehicle Info:\n" +
                    "Name: " + vehicle.getTitle() + "\n" +
                    "Make: " + vehicle.getMake() + "\n" +
                    "Model: " + vehicle.getModel() + "\n" +
                    "Year: " + vehicle.getYear() + "\n" +
                    "Location: " + vehicle.getLocation() + "\n" +
                    "Start Date: " + vehicle.getStartDate() + "\n" +
                    "End Date: " + vehicle.getEndDate();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vehicle Information");
            shareIntent.putExtra(Intent.EXTRA_TEXT, vehicleInfo);
            startActivity(Intent.createChooser(shareIntent, "Share vehicle via"));
        });
    }

    private void showDatePicker(EditText editText, Calendar calendar) {
        new DatePickerDialog(
                this,
                (view, year, month, day) ->
                        editText.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void loadVehicle() {
        Vehicle vehicle = vehicleRepo.getVehicleById(vehicleId);
        if (vehicle == null) return;

        editTitle.setText(vehicle.getTitle()); // properly load title
        editMake.setText(vehicle.getMake());
        editModel.setText(vehicle.getModel());
        editYear.setText(vehicle.getYear() > 0 ? String.valueOf(vehicle.getYear()) : "");
        editLocation.setText(vehicle.getLocation());
        editStartDate.setText(vehicle.getStartDate());
        editEndDate.setText(vehicle.getEndDate());
        switchAlert.setChecked(vehicle.isMaintenanceAlertsEnabled());
    }

    private void saveVehicle() {
        String title = editTitle.getText().toString().trim();
        String make = editMake.getText().toString().trim();
        String model = editModel.getText().toString().trim();
        String yearStr = editYear.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String startDate = editStartDate.getText().toString().trim();
        String endDate = editEndDate.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vehicle name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = 0;
        if (!yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Year must be a number", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Vehicle vehicle;
        if (vehicleId == -1) {
            vehicle = new Vehicle();
        } else {
            vehicle = vehicleRepo.getVehicleById(vehicleId);
            if (vehicle == null) vehicle = new Vehicle();
        }

        vehicle.setTitle(title); // properly save title
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setLocation(location);
        vehicle.setStartDate(startDate);
        vehicle.setEndDate(endDate);
        vehicle.setMaintenanceAlertsEnabled(switchAlert.isChecked());

        if (vehicleId == -1) {
            int newId = vehicleRepo.addVehicle(vehicle);
            if (newId == -1) return;
            vehicleId = newId;
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            vehicleRepo.updateVehicle(vehicle);
        }

        Toast.makeText(this, "Vehicle saved", Toast.LENGTH_SHORT).show();
    }
}
