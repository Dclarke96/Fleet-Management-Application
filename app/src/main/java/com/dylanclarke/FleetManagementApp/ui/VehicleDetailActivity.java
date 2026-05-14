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

    // IMPORTANT: must be long to match backend IDs (fixes crash)
    private long vehicleId = -1;

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

        vehicleRepo = new VehicleRepository(getApplicationContext());

        // FIX: use getLongExtra (prevents ClassCastException crash)
        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getLongExtra("vehicleId", -1L);

            if (vehicleId != -1L) {
                loadVehicle();
                btnDelete.setVisibility(View.VISIBLE);
            }
        }

        Calendar calendar = Calendar.getInstance();

        editStartDate.setOnClickListener(v -> showDatePicker(editStartDate, calendar));
        editEndDate.setOnClickListener(v -> showDatePicker(editEndDate, calendar));

        btnSave.setOnClickListener(v -> saveVehicle());
        btnBack.setOnClickListener(v -> finish());

        btnAddMaintenance.setOnClickListener(v -> {
            if (vehicleId == -1L) {
                Toast.makeText(this, "Save vehicle first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, MaintenanceDetailActivity.class);
            intent.putExtra("vehicleId", vehicleId);
            startActivity(intent);
        });

        btnManageMaintenance.setOnClickListener(v -> {
            if (vehicleId == -1L) {
                Toast.makeText(this, "Save vehicle first", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, MaintenanceListActivity.class);
            intent.putExtra("vehicleId", vehicleId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {

            if (vehicleId == -1L) return;

            new AlertDialog.Builder(this)
                    .setTitle("Delete Vehicle")
                    .setMessage("Are you sure you want to delete this vehicle? This cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        vehicleRepo.deleteVehicle(
                                vehicleId,
                                new VehicleRepository.DeleteVehicleCallback() {

                                    @Override
                                    public void onSuccess() {

                                        runOnUiThread(() -> {

                                            Toast.makeText(
                                                    VehicleDetailActivity.this,
                                                    "Vehicle deleted",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            finish();
                                        });
                                    }

                                    @Override
                                    public void onError(String error) {

                                        runOnUiThread(() -> {

                                            Toast.makeText(
                                                    VehicleDetailActivity.this,
                                                    error,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        });
                                    }
                                }
                        );
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnShare.setOnClickListener(v -> {
            if (vehicleId == -1L) {
                Toast.makeText(this, "Save vehicle first", Toast.LENGTH_SHORT).show();
                return;
            }

            Vehicle vehicle = vehicleRepo.getVehicleById((int) vehicleId);
            if (vehicle == null) return;

            String vehicleInfo =
                    "Vehicle Info:\n" +
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

    // -----------------------------
    // FIXED LOAD (API CONSISTENT)
    // -----------------------------
    private void loadVehicle() {

        vehicleRepo.getVehicleById(vehicleId, new VehicleRepository.SingleVehicleCallback() {
                    @Override
                    public void onSuccess(Vehicle vehicle) {

                        editTitle.setText(vehicle.getTitle());
                        editMake.setText(vehicle.getMake());
                        editModel.setText(vehicle.getModel());

                        editYear.setText(
                                vehicle.getYear() > 0
                                        ? String.valueOf(vehicle.getYear())
                                        : ""
                        );

                        editLocation.setText(vehicle.getLocation());
                        editStartDate.setText(vehicle.getStartDate());
                        editEndDate.setText(vehicle.getEndDate());
                        switchAlert.setChecked(vehicle.isMaintenanceAlertsEnabled());
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(
                                VehicleDetailActivity.this,
                                error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
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

        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);

        vehicle.setTitle(title);
        vehicle.setMake(make);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setLocation(location);
        vehicle.setStartDate(startDate);
        vehicle.setEndDate(endDate);
        vehicle.setMaintenanceAlertsEnabled(switchAlert.isChecked());

        if (vehicleId == -1L) {

            vehicleRepo.addVehicle(vehicle, new VehicleRepository.AddVehicleCallback() {

                @Override
                public void onSuccess(Vehicle createdVehicle) {

                    vehicleId = createdVehicle.getId();

                    Toast.makeText(
                            VehicleDetailActivity.this,
                            "Vehicle created",
                            Toast.LENGTH_SHORT
                    ).show();

                    btnDelete.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String error) {

                    Toast.makeText(
                            VehicleDetailActivity.this,
                            error,
                            Toast.LENGTH_LONG
                    ).show();
                }
            });

        } else {

            vehicleRepo.updateVehicle(
                    vehicle,
                    new VehicleRepository.UpdateVehicleCallback() {

                        @Override
                        public void onSuccess(Vehicle updatedVehicle) {

                            runOnUiThread(() -> {

                                Toast.makeText(
                                        VehicleDetailActivity.this,
                                        "Vehicle updated",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                        }

                        @Override
                        public void onError(String error) {

                            runOnUiThread(() -> {

                                Toast.makeText(
                                        VehicleDetailActivity.this,
                                        error,
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                        }
                    }
            );
        }
    }
}