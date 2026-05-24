package com.dylanclarke.FleetManagementApp.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.data.VehicleRepository;
import com.dylanclarke.FleetManagementApp.util.VehicleValidator;
import com.dylanclarke.FleetManagementApp.ui.state.LoadingController;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Calendar;
import java.util.Locale;

/**
 * Activity responsible for creating, editing,
 * deleting, and sharing vehicle records.
 */
public class VehicleDetailActivity extends AppCompatActivity {

    // ---------------------------------------------------------
    // UI COMPONENTS
    // ---------------------------------------------------------

    private EditText editTitle;
    private EditText editMake;
    private EditText editModel;
    private EditText editYear;
    private EditText editLocation;
    private EditText editStartDate;
    private EditText editEndDate;

    private Switch switchAlert;

    private Button btnSave;
    private Button btnBack;
    private Button btnAddMaintenance;
    private Button btnManageMaintenance;
    private Button btnDelete;
    private Button btnShare;

    // ---------------------------------------------------------
    // DATA
    // ---------------------------------------------------------

    private VehicleRepository vehicleRepository;

    /**
     * Active vehicle ID.
     * -1 indicates a new unsaved vehicle.
     */
    private long vehicleId = -1L;

    private LoadingController loadingController;

    // ---------------------------------------------------------
    // LIFECYCLE
    // ---------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_detail);

        initializeViews();
        initializeRepository();
        initializeListeners();

        loadingController = new LoadingController(
                btnSave,
                null,
                btnDelete,
                btnShare,
                btnAddMaintenance,
                btnManageMaintenance
        );

        initializeVehicleState();
    }

    // ---------------------------------------------------------
    // INITIALIZATION
    // ---------------------------------------------------------

    /**
     * Binds all layout views.
     */
    private void initializeViews() {

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
    }

    /**
     * Initializes repository dependencies.
     */
    private void initializeRepository() {
        vehicleRepository =
                new VehicleRepository(getApplicationContext());
    }

    /**
     * Loads vehicle state if editing an existing record.
     */
    private void initializeVehicleState() {

        if (getIntent().hasExtra("vehicleId")) {

            vehicleId =
                    getIntent().getLongExtra("vehicleId", -1L);

            if (vehicleId != -1L) {

                loadVehicle();

                btnDelete.setVisibility(vehicleId == -1L ? View.GONE : View.VISIBLE);
            }
        }
    }

    /**
     * Registers click listeners and date pickers.
     */
    private void initializeListeners() {

        Calendar calendar = Calendar.getInstance();

        editStartDate.setOnClickListener(v ->
                showDatePicker(editStartDate, calendar)
        );

        editEndDate.setOnClickListener(v ->
                showDatePicker(editEndDate, calendar)
        );

        btnSave.setOnClickListener(v -> saveVehicle());

        btnBack.setOnClickListener(v -> finish());

        btnAddMaintenance.setOnClickListener(v ->
                openMaintenanceDetail()
        );

        btnManageMaintenance.setOnClickListener(v ->
                openMaintenanceList()
        );

        btnDelete.setOnClickListener(v ->
                confirmDeleteVehicle()
        );

        btnShare.setOnClickListener(v ->
                shareVehicle()
        );
    }

    // ---------------------------------------------------------
    // VEHICLE LOADING
    // ---------------------------------------------------------

    /**
     * Loads an existing vehicle from the API.
     */
    private void loadVehicle() {

        loadingController.show();

        vehicleRepository.getVehicleById(
                vehicleId,
                new VehicleRepository.SingleVehicleCallback() {

                    @Override
                    public void onSuccess(Vehicle vehicle) {

                        runOnUiThread(() -> {
                            loadingController.hide();
                            populateVehicleFields(vehicle);
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() -> {
                            loadingController.hide();
                            showToast(error, Toast.LENGTH_LONG);
                        });
                    }
                }
        );
    }

    /**
     * Populates form fields from a vehicle record.
     */
    private void populateVehicleFields(Vehicle vehicle) {

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

        switchAlert.setChecked(
                vehicle.isMaintenanceAlertsEnabled()
        );
    }

    // ---------------------------------------------------------
    // SAVE
    // ---------------------------------------------------------

    /**
     * Validates and saves the vehicle.
     */
    private void saveVehicle() {

        Vehicle vehicle = buildVehicleFromForm();

        if (vehicle == null) return;

        VehicleValidator.ValidationResult result =
                VehicleValidator.validate(vehicle);

        if (!result.isValid) {
            showToast(result.message, Toast.LENGTH_SHORT);
            return;
        }

        if (vehicleId == -1L) {
            createVehicle(vehicle);
        } else {
            updateVehicle(vehicle);
        }
    }

    /**
     * Builds a vehicle object from form input.
     */
    private Vehicle buildVehicleFromForm() {

        String title = editTitle.getText().toString().trim();
        String make = editMake.getText().toString().trim();
        String model = editModel.getText().toString().trim();
        String yearText = editYear.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String startDate = editStartDate.getText().toString().trim();
        String endDate = editEndDate.getText().toString().trim();

        int year = 0;

        if (!yearText.isEmpty()) {
            try {
                year = Integer.parseInt(yearText);
            } catch (NumberFormatException e) {
                showToast("Year must be a number", Toast.LENGTH_SHORT);
                return null;
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

        return vehicle;
    }

    /**
     * Creates a new vehicle through the API.
     */
    private void createVehicle(Vehicle vehicle) {

        loadingController.show();

        btnSave.postDelayed(() -> {

            vehicleRepository.addVehicle(
                    vehicle,
                    new VehicleRepository.AddVehicleCallback() {

                        @Override
                        public void onSuccess(Vehicle createdVehicle) {

                            runOnUiThread(() -> {

                                loadingController.hide();

                                vehicleId = createdVehicle.getId();
                                btnDelete.setVisibility(View.VISIBLE);

                                showToast("Vehicle created", Toast.LENGTH_SHORT);
                            });
                        }

                        @Override
                        public void onError(String error) {

                            runOnUiThread(() -> {
                                loadingController.hide();
                                showToast(error, Toast.LENGTH_LONG);
                            });
                        }
                    }
            );

        }, 2000); // 👈 2 second artificial delay
    }

    /**
     * Updates an existing vehicle.
     */
    private void updateVehicle(Vehicle vehicle) {

        loadingController.show();

        btnSave.postDelayed(() -> {

            vehicleRepository.updateVehicle(
                    vehicle,
                    new VehicleRepository.UpdateVehicleCallback() {

                        @Override
                        public void onSuccess(Vehicle updatedVehicle) {

                            runOnUiThread(() -> {

                                loadingController.hide();

                                showToast("Vehicle updated", Toast.LENGTH_SHORT);
                            });
                        }

                        @Override
                        public void onError(String error) {

                            runOnUiThread(() -> {
                                loadingController.hide();
                                showToast(error, Toast.LENGTH_LONG);
                            });
                        }
                    }
            );

        }, 2000); // 👈 2 second artificial delay
    }

    // ---------------------------------------------------------
    // MAINTENANCE NAVIGATION
    // ---------------------------------------------------------

    /**
     * Opens maintenance creation screen.
     */
    private void openMaintenanceDetail() {

        if (!hasSavedVehicle()) {
            return;
        }

        Intent intent =
                new Intent(
                        this,
                        MaintenanceDetailActivity.class
                );

        intent.putExtra("vehicleId", vehicleId);

        startActivity(intent);
    }

    /**
     * Opens maintenance management screen.
     */
    private void openMaintenanceList() {

        if (!hasSavedVehicle()) {
            return;
        }

        Intent intent =
                new Intent(
                        this,
                        MaintenanceListActivity.class
                );

        intent.putExtra("vehicleId", vehicleId);

        startActivity(intent);
    }

    /**
     * Ensures a vehicle exists before allowing maintenance actions.
     */
    private boolean hasSavedVehicle() {

        if (vehicleId == -1L) {

            showToast(
                    "Save vehicle first",
                    Toast.LENGTH_SHORT
            );

            return false;
        }

        return true;
    }

    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    /**
     * Displays delete confirmation dialog.
     */
    private void confirmDeleteVehicle() {

        if (vehicleId == -1L) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Vehicle")
                .setMessage(
                        "Are you sure you want to delete this vehicle? "
                                + "This action cannot be undone."
                )
                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> deleteVehicle()
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the current vehicle.
     */
    private void deleteVehicle() {

        loadingController.show();

        vehicleRepository.deleteVehicle(
                vehicleId,
                new VehicleRepository.DeleteVehicleCallback() {

                    @Override
                    public void onSuccess() {

                        runOnUiThread(() -> {

                            loadingController.hide();

                            showToast("Vehicle deleted", Toast.LENGTH_SHORT);
                            finish();
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() -> {
                            loadingController.hide();
                            showToast(error, Toast.LENGTH_LONG);
                        });
                    }
                }
        );
    }

    // ---------------------------------------------------------
    // SHARE
    // ---------------------------------------------------------

    /**
     * Shares vehicle information using Android share intent.
     */
    private void shareVehicle() {

        if (!hasSavedVehicle()) return;

        loadingController.show();

        vehicleRepository.getVehicleById(
                vehicleId,
                new VehicleRepository.SingleVehicleCallback() {

                    @Override
                    public void onSuccess(Vehicle vehicle) {

                        runOnUiThread(() -> {
                            loadingController.hide();
                            launchShareIntent(vehicle);
                        });
                    }

                    @Override
                    public void onError(String error) {

                        runOnUiThread(() -> {
                            loadingController.hide();
                            showToast(error, Toast.LENGTH_LONG);
                        });
                    }
                }
        );
    }

    /**
     * Launches Android share sheet.
     */
    private void launchShareIntent(Vehicle vehicle) {

        String vehicleInfo =
                "Vehicle Info:\n"
                        + "Name: " + vehicle.getTitle() + "\n"
                        + "Make: " + vehicle.getMake() + "\n"
                        + "Model: " + vehicle.getModel() + "\n"
                        + "Year: " + vehicle.getYear() + "\n"
                        + "Location: " + vehicle.getLocation() + "\n"
                        + "Start Date: " + vehicle.getStartDate() + "\n"
                        + "End Date: " + vehicle.getEndDate();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");

        shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Vehicle Information"
        );

        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                vehicleInfo
        );

        startActivity(
                Intent.createChooser(
                        shareIntent,
                        "Share vehicle via"
                )
        );
    }

    // ---------------------------------------------------------
    // DATE PICKER
    // ---------------------------------------------------------

    /**
     * Displays a date picker dialog for date fields.
     */
    private void showDatePicker(
            EditText targetField,
            Calendar calendar
    ) {

        new DatePickerDialog(
                this,
                (view, year, month, day) ->
                        targetField.setText(
                                String.format(
                                        Locale.US,
                                        "%04d-%02d-%02d",
                                        year,
                                        month + 1,
                                        day
                                )
                        ),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // ---------------------------------------------------------
    // UTILITIES
    // ---------------------------------------------------------

    /**
     * Displays a toast message.
     */
    private void showToast(
            String message,
            int duration
    ) {

        Toast.makeText(
                this,
                message,
                duration
        ).show();
    }
}