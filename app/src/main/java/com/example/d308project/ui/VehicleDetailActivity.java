package com.example.d308project.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.Vehicle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class VehicleDetailActivity extends AppCompatActivity {

    private EditText editTitle, editLocation, editStartDate, editEndDate;
    private Switch switchAlert;
    private Button btnSave, btnDelete, btnShare, btnBack;
    private AppDatabase db;
    private int vehicleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail); // XML you provided

        // Bind views to XML IDs
        editTitle = findViewById(R.id.editTitle);
        editLocation = findViewById(R.id.editLocation);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        switchAlert = findViewById(R.id.switchAlert);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);

        db = AppDatabase.getInstance(getApplicationContext());

        // If editing an existing vehicle
        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            loadVehicle();
        }

        // Date picker dialogs for start/end dates
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener startDateListener = (view, year, month, day) -> {
            editStartDate.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day));
        };
        DatePickerDialog.OnDateSetListener endDateListener = (view, year, month, day) -> {
            editEndDate.setText(String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day));
        };

        editStartDate.setOnClickListener(v -> new DatePickerDialog(
                this, startDateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        editEndDate.setOnClickListener(v -> new DatePickerDialog(
                this, endDateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show());

        btnSave.setOnClickListener(v -> saveVehicle());
        btnBack.setOnClickListener(v -> finish());
        // You can implement btnDelete and btnShare later
    }

    private void loadVehicle() {
        Vehicle vehicle = db.vehicleDao().getVehicleById(vehicleId);
        if (vehicle != null) {
            editTitle.setText(vehicle.make + " " + vehicle.model); // or a custom name field
            editLocation.setText(vehicle.licensePlate);
            switchAlert.setChecked(vehicle.maintenanceAlertsEnabled);
            // If you later add startDate/endDate to Vehicle, set them here
        }
    }

    private void saveVehicle() {
        String title = editTitle.getText().toString().trim();
        String location = editLocation.getText().toString().trim();

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

        // For simplicity, store the title as make+model split by space
        String[] parts = title.split(" ", 2);
        vehicle.make = parts.length > 0 ? parts[0] : "";
        vehicle.model = parts.length > 1 ? parts[1] : "";
        vehicle.licensePlate = location;
        vehicle.maintenanceAlertsEnabled = switchAlert.isChecked();

        if (vehicleId == -1) {
            db.vehicleDao().insertVehicle(vehicle);
        } else {
            db.vehicleDao().updateVehicle(vehicle);
        }

        Toast.makeText(this, "Vehicle saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}
