package com.example.d308project.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.d308project.R;
import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.MaintenanceRecord;
import com.example.d308project.data.Vehicle;

public class MaintenanceDetailActivity extends AppCompatActivity {

    private EditText editDescription, editDate;
    private Switch switchAlert;
    private Button btnSave, btnBack;
    private AppDatabase db;
    private int vehicleId;
    private int maintenanceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_detail); // Updated layout name

        editDescription = findViewById(R.id.editMaintenanceTitle);
        editDate = findViewById(R.id.editMaintenanceDate);
        switchAlert = findViewById(R.id.switchAlert);
        btnSave = findViewById(R.id.btnSaveMaintenance);
        btnBack = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(getApplicationContext());

        if (getIntent().hasExtra("vehicleId")) {
            vehicleId = getIntent().getIntExtra("vehicleId", -1);
        }

        if (getIntent().hasExtra("maintenanceId")) {
            maintenanceId = getIntent().getIntExtra("maintenanceId", -1);
            loadMaintenance();
        }

        btnSave.setOnClickListener(v -> saveMaintenance());
        btnBack.setOnClickListener(v -> finish());

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            String formattedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editDate.setText(formattedDate);
        };

        editDate.setOnClickListener(v -> {
            new DatePickerDialog(
                    MaintenanceDetailActivity.this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void loadMaintenance() {
        MaintenanceRecord record = db.maintenanceDao().getMaintenanceById(maintenanceId);
        if (record != null) {
            editDescription.setText(record.description);
            editDate.setText(record.serviceDate);
            switchAlert.setChecked(record.alertsEnabled);
        }
    }

    private void saveMaintenance() {
        String description = editDescription.getText().toString().trim();
        String serviceDate = editDate.getText().toString().trim();
        boolean alertsEnabled = switchAlert.isChecked();

        if (description.isEmpty() || serviceDate.isEmpty()) {
            Toast.makeText(this, "Description and date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(serviceDate)) {
            Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date within vehicle range
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date maintenanceDate = sdf.parse(serviceDate);
            Vehicle vehicle = db.vehicleDao().getVehicleById(vehicleId);
            if (vehicle == null) {
                Toast.makeText(this, "Parent vehicle not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            Date startDate = sdf.parse(vehicle.startDate);
            Date endDate = sdf.parse(vehicle.endDate);

            if (maintenanceDate.before(startDate) || maintenanceDate.after(endDate)) {
                Toast.makeText(this, "Maintenance must be within vehicle dates: "
                        + vehicle.startDate + " to " + vehicle.endDate, Toast.LENGTH_LONG).show();
                return;
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Date parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (maintenanceId == -1) {
            // Insert new maintenance record
            MaintenanceRecord newRecord = new MaintenanceRecord();
            newRecord.vehicleId = vehicleId;
            newRecord.description = description;
            newRecord.serviceDate = serviceDate;
            newRecord.alertsEnabled = alertsEnabled;
            db.maintenanceDao().insertMaintenance(newRecord);
        } else {
            // Update existing record
            MaintenanceRecord existing = db.maintenanceDao().getMaintenanceById(maintenanceId);
            if (existing != null) {
                existing.description = description;
                existing.serviceDate = serviceDate;
                existing.alertsEnabled = alertsEnabled;
                db.maintenanceDao().updateMaintenance(existing);
            }
        }

        if (alertsEnabled) {
            scheduleMaintenanceAlert(description, serviceDate);
        }

        Toast.makeText(this, "Maintenance saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void scheduleMaintenanceAlert(String description, String serviceDate) {
        long triggerTime = parseDateToMillis(serviceDate);
        if (triggerTime < System.currentTimeMillis()) {
            Toast.makeText(this, "Alert date must not be in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MaintenanceAlertReceiver.class);
        intent.putExtra("maintenanceDescription", description);

        int requestCode = description.hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private long parseDateToMillis(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : -1;
        } catch (ParseException e) {
            return -1;
        }
    }
}
