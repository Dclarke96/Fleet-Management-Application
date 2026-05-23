package com.dylanclarke.FleetManagementApp.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Handles creation and editing of maintenance records.
 */
public class MaintenanceDetailActivity extends AppCompatActivity {

    private static final String EXTRA_VEHICLE_ID =
            "vehicleId";

    private static final String EXTRA_MAINTENANCE_ID =
            "maintenanceId";

    private static final String ALERT_ENTITY_TYPE =
            "Maintenance";

    private static final String ALERT_EVENT_TYPE =
            "service";

    private EditText editDescription;

    private EditText editDate;

    private EditText editCost;

    private Switch switchAlert;

    private Button btnSave;

    private Button btnBack;

    private MaintenanceRepository maintenanceRepository;

    private long vehicleId = -1L;

    private long maintenanceId = -1L;

    private final SimpleDateFormat sdf =
            new SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
            );

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maintenance_detail);

        initializeViews();

        initializeRepository();

        loadIntentData();

        setupListeners();

        if (maintenanceId != -1L) {
            loadMaintenance();
        }
    }

    /**
     * Initializes UI view references.
     */
    private void initializeViews() {

        editDescription =
                findViewById(R.id.editMaintenanceTitle);

        editDate =
                findViewById(R.id.editMaintenanceDate);

        editCost =
                findViewById(R.id.editMaintenanceCost);

        switchAlert =
                findViewById(R.id.switchAlert);

        btnSave =
                findViewById(R.id.btnSaveMaintenance);

        btnBack =
                findViewById(R.id.backButton);
    }

    /**
     * Initializes repository dependencies.
     */
    private void initializeRepository() {

        maintenanceRepository =
                new MaintenanceRepository(
                        getApplicationContext()
                );
    }

    /**
     * Loads activity intent extras.
     */
    private void loadIntentData() {

        Intent intent = getIntent();

        vehicleId =
                intent.getLongExtra(
                        EXTRA_VEHICLE_ID,
                        -1L
                );

        maintenanceId =
                intent.getLongExtra(
                        EXTRA_MAINTENANCE_ID,
                        -1L
                );
    }

    /**
     * Configures UI interaction listeners.
     */
    private void setupListeners() {

        btnSave.setOnClickListener(
                v -> saveMaintenance()
        );

        btnBack.setOnClickListener(
                v -> finish()
        );

        Calendar calendar =
                Calendar.getInstance();

        editDate.setOnClickListener(
                v -> showDatePicker(calendar)
        );
    }

    /**
     * Displays the date picker dialog.
     */
    private void showDatePicker(
            Calendar calendar
    ) {

        new DatePickerDialog(
                this,
                (view, year, month, day) ->
                        editDate.setText(
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

    /**
     * Loads an existing maintenance record.
     */
    private void loadMaintenance() {

        maintenanceRepository.getMaintenanceById(
                maintenanceId,
                new MaintenanceRepository.SingleMaintenanceCallback() {

                    @Override
                    public void onSuccess(
                            MaintenanceRecord record
                    ) {

                        runOnUiThread(() -> {

                            editDescription.setText(
                                    record.getDescription()
                            );

                            editDate.setText(
                                    record.getServiceDate()
                            );

                            if (record.getCost() != null) {

                                editCost.setText(
                                        String.valueOf(
                                                record.getCost()
                                        )
                                );
                            }

                            switchAlert.setChecked(
                                    record.isAlertsEnabled()
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
     * Saves a maintenance record through the repository.
     */
    private void saveMaintenance() {

        Double cost = parseCost();

        if (cost == null
                && !editCost.getText()
                .toString()
                .trim()
                .isEmpty()) {

            showToast("Cost must be a valid number");

            return;
        }

        MaintenanceRecord record =
                buildMaintenanceRecord(cost);

        boolean isNew =
                maintenanceId == -1L;

        if (isNew) {

            addMaintenance(record);

        } else {

            updateMaintenance(record);
        }
    }

    /**
     * Creates a MaintenanceRecord from form input.
     */
    private MaintenanceRecord buildMaintenanceRecord(
            Double cost
    ) {

        MaintenanceRecord record =
                new MaintenanceRecord();

        if (maintenanceId != -1L) {
            record.setId(maintenanceId);
        }

        record.setVehicleId((int) vehicleId);

        record.setDescription(
                editDescription.getText()
                        .toString()
                        .trim()
        );

        record.setServiceDate(
                editDate.getText()
                        .toString()
                        .trim()
        );

        record.setCost(cost);

        record.setAlertsEnabled(
                switchAlert.isChecked()
        );

        return record;
    }

    /**
     * Attempts to parse maintenance cost input.
     */
    private Double parseCost() {

        String costText =
                editCost.getText()
                        .toString()
                        .trim();

        if (costText.isEmpty()) {
            return null;
        }

        try {

            return Double.parseDouble(costText);

        } catch (NumberFormatException e) {

            return null;
        }
    }

    /**
     * Creates a new maintenance record.
     */
    private void addMaintenance(
            MaintenanceRecord record
    ) {

        maintenanceRepository.addMaintenance(
                record,
                new MaintenanceRepository.AddMaintenanceCallback() {

                    @Override
                    public void onSuccess(
                            MaintenanceRecord created
                    ) {

                        runOnUiThread(() ->
                                handleSaveSuccess(
                                        record,
                                        "Maintenance created"
                                )
                        );
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
     * Updates an existing maintenance record.
     */
    private void updateMaintenance(
            MaintenanceRecord record
    ) {

        maintenanceRepository.updateMaintenance(
                record,
                new MaintenanceRepository.UpdateMaintenanceCallback() {

                    @Override
                    public void onSuccess(
                            MaintenanceRecord updated
                    ) {

                        runOnUiThread(() ->
                                handleSaveSuccess(
                                        record,
                                        "Maintenance updated"
                                )
                        );
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
     * Handles successful maintenance save operations.
     */
    private void handleSaveSuccess(
            MaintenanceRecord record,
            String successMessage
    ) {

        if (record.isAlertsEnabled()) {

            scheduleMaintenanceAlert(
                    record.getDescription(),
                    record.getServiceDate()
            );
        }

        showToast(successMessage);

        finish();
    }

    /**
     * Schedules a future maintenance alert notification.
     */
    private void scheduleMaintenanceAlert(
            String description,
            String serviceDate
    ) {

        long triggerTime =
                parseDateToMillis(serviceDate);

        if (triggerTime < System.currentTimeMillis()) {

            showToast(
                    "Alert date must not be in the past"
            );

            return;
        }

        AlarmManager alarmManager =
                (AlarmManager) getSystemService(
                        Context.ALARM_SERVICE
                );

        Intent intent =
                new Intent(
                        this,
                        MaintenanceAlertReceiver.class
                );

        intent.putExtra(
                "title",
                description
        );

        intent.putExtra(
                "entityType",
                ALERT_ENTITY_TYPE
        );

        intent.putExtra(
                "eventType",
                ALERT_EVENT_TYPE
        );

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(
                        this,
                        description.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        if (alarmManager != null) {

            alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
            );
        }
    }

    /**
     * Converts a yyyy-MM-dd date string into milliseconds.
     */
    private long parseDateToMillis(
            String dateString
    ) {

        try {

            Date date =
                    sdf.parse(dateString);

            return date != null
                    ? date.getTime()
                    : -1L;

        } catch (ParseException e) {

            return -1L;
        }
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