package com.example.d308project.ui;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.MaintenanceRecord;
import com.example.d308project.data.Vehicle;

import java.util.*;

public class VehicleDetailActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final int REQUEST_MANAGE_MAINTENANCE = 1001;

    private EditText editName, editMake, editModel, editYear;
    private Switch switchAlert;
    private Button btnSave, btnDelete, btnShare, btnBack, btnManageMaintenance;
    private ListView maintenanceListView;

    private AppDatabase db;
    private int vehicleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        requestNotificationPermission();

        setContentView(R.layout.activity_vehicle_detail);

        editName = findViewById(R.id.editVehicleName);
        editMake = findViewById(R.id.editMake);
        editModel = findViewById(R.id.editModel);
        editYear = findViewById(R.id.editYear);
        switchAlert = findViewById(R.id.switchAlert);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);
        btnManageMaintenance = findViewById(R.id.btnManageMaintenance);

        maintenanceListView = findViewById(R.id.maintenanceList);

        db = AppDatabase.getInstance(getApplicationContext());

        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
            loadVehicle();
            btnDelete.setVisibility(View.VISIBLE);
            btnManageMaintenance.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
            btnManageMaintenance.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveVehicle());
        btnBack.setOnClickListener(v -> finish());
        btnShare.setOnClickListener(v -> shareVehicleDetails());

        btnDelete.setOnClickListener(v -> attemptDeleteVehicle());

        btnManageMaintenance.setOnClickListener(v -> {
            Intent intent = new Intent(this, MaintenanceListActivity.class);
            intent.putExtra("vehicleId", vehicleId);
            startActivityForResult(intent, REQUEST_MANAGE_MAINTENANCE);
        });
    }

    private void loadVehicle() {
        Vehicle vehicle = db.vehicleDao().getVehicleById(vehicleId);
        if (vehicle == null) return;

        editName.setText(vehicle.name);
        editMake.setText(vehicle.make);
        editModel.setText(vehicle.model);
        editYear.setText(String.valueOf(vehicle.year));
        switchAlert.setChecked(vehicle.alertsEnabled);

        List<String> maintenanceTitles = new ArrayList<>();
        List<MaintenanceRecord> records =
                db.maintenanceDao().getMaintenanceForVehicle(vehicleId);

        for (MaintenanceRecord r : records) {
            maintenanceTitles.add(r.title + " (" + r.date + ")");
        }

        maintenanceListView.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        maintenanceTitles)
        );
    }

    private void saveVehicle() {
        String name = editName.getText().toString().trim();
        String make = editMake.getText().toString().trim();
        String model = editModel.getText().toString().trim();
        String yearText = editYear.getText().toString().trim();
        boolean alertsEnabled = switchAlert.isChecked();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(make)
                || TextUtils.isEmpty(model) || TextUtils.isEmpty(yearText)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int year = Integer.parseInt(yearText);
        Vehicle vehicle = new Vehicle(name, make, model, year, alertsEnabled);

        if (vehicleId == -1) {
            db.vehicleDao().insertVehicle(vehicle);
            vehicleId = db.vehicleDao().getLastInsertedId();
        } else {
            vehicle.id = vehicleId;
            db.vehicleDao().updateVehicle(vehicle);
        }

        Toast.makeText(this, "Vehicle saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void attemptDeleteVehicle() {
        int maintenanceCount =
                db.maintenanceDao().countMaintenanceForVehicle(vehicleId);

        if (maintenanceCount > 0) {
            Toast.makeText(this,
                    "Delete maintenance records first",
                    Toast.LENGTH_LONG).show();
            return;
        }

        db.vehicleDao().deleteVehicleById(vehicleId);
        Toast.makeText(this, "Vehicle deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void shareVehicleDetails() {
        String message =
                "Vehicle Details\n\n" +
                        "Name: " + editName.getText() + "\n" +
                        "Make: " + editMake.getText() + "\n" +
                        "Model: " + editModel.getText() + "\n" +
                        "Year: " + editYear.getText();

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share vehicle using"));
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "vehicle_alerts",
                    "Vehicle Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            getSystemService(NotificationManager.class)
                    .createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_MAINTENANCE) {
            loadVehicle();
        }
    }
}
