package com.dylanclarke.FleetManagementApp.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.dylanclarke.FleetManagementApp.R;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRecord;
import com.dylanclarke.FleetManagementApp.data.MaintenanceRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// DESIGN FOR SCALABILITY:
// Feature-specific activity keeps UI modular,
// allowing independent expansion of application features.

public class MaintenanceDetailActivity extends AppCompatActivity {

    private EditText editDescription, editDate;
    private Switch switchAlert;
    private Button btnSave, btnBack;

    private MaintenanceRepository maintenanceRepo;
    private int vehicleId;
    private int maintenanceId = -1;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_detail);

        editDescription = findViewById(R.id.editMaintenanceTitle);
        editDate = findViewById(R.id.editMaintenanceDate);
        switchAlert = findViewById(R.id.switchAlert);
        btnSave = findViewById(R.id.btnSaveMaintenance);
        btnBack = findViewById(R.id.backButton);

        maintenanceRepo = new MaintenanceRepository(getApplicationContext());

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
        editDate.setOnClickListener(v -> showDatePicker(calendar));
    }

    private void showDatePicker(Calendar calendar) {
        new DatePickerDialog(
                this,
                (view, year, month, day) -> editDate.setText(
                        String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
                ),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void loadMaintenance() {
        MaintenanceRecord record = maintenanceRepo.getMaintenanceById(maintenanceId);
        if (record != null) {
            editDescription.setText(record.getDescription());
            editDate.setText(record.getServiceDate());
            switchAlert.setChecked(record.isAlertsEnabled());
        }
    }

    private void saveMaintenance() {
        MaintenanceRecord record;
        boolean isNew = maintenanceId == -1;

        if (isNew) {
            record = new MaintenanceRecord();
            record.setVehicleId(vehicleId);
        } else {
            record = maintenanceRepo.getMaintenanceById(maintenanceId);
            if (record == null) {
                record = new MaintenanceRecord();
                record.setVehicleId(vehicleId);
                isNew = true;
            }
        }

        record.setDescription(editDescription.getText().toString().trim());
        record.setServiceDate(editDate.getText().toString().trim());
        record.setAlertsEnabled(switchAlert.isChecked());

        // ✅ Use repository to handle validation and insertion/update
        boolean success = isNew
                ? maintenanceRepo.addMaintenance(record, this)
                : maintenanceRepo.updateMaintenance(record, this);

        if (!success) return; // validation failed, repository already shows Toast

        if (record.isAlertsEnabled()) {
            scheduleMaintenanceAlert(record.getDescription(), record.getServiceDate());
        }

        Toast.makeText(this, "Maintenance saved", Toast.LENGTH_SHORT).show();
        finish();
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

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                description.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }
    }

    private long parseDateToMillis(String dateStr) {
        try {
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : -1;
        } catch (ParseException e) {
            return -1;
        }
    }
}
