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
import com.example.d308project.data.Excursion;
import com.example.d308project.data.Vehicle;

public class ExcursionDetailActivity extends AppCompatActivity {

    private EditText editTitle, editDate;
    private Switch switchAlert;
    private Button btnSave, btnBack;
    private AppDatabase db;
    private int vacationId;
    private int excursionId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_detail);

        editTitle = findViewById(R.id.editExcursionTitle);
        editDate = findViewById(R.id.editExcursionDate);
        switchAlert = findViewById(R.id.switchAlert);
        btnSave = findViewById(R.id.btnSaveExcursion);
        btnBack = findViewById(R.id.backButton);

        db = AppDatabase.getInstance(getApplicationContext());

        if (getIntent().hasExtra("vacationId")) {
            vacationId = getIntent().getIntExtra("vacationId", -1);
        }

        if (getIntent().hasExtra("excursionId")) {
            excursionId = getIntent().getIntExtra("excursionId", -1);
            loadExcursion();
        }

        btnSave.setOnClickListener(v -> {
            saveExcursion();
        });

        btnBack.setOnClickListener(v -> finish());

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String formattedDate = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editDate.setText(formattedDate);
        };

        editDate.setOnClickListener(v -> {
            new DatePickerDialog(
                    ExcursionDetailActivity.this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void loadExcursion() {
        Excursion excursion = db.excursionDao().getExcursionById(excursionId);
        if (excursion != null) {
            editTitle.setText(excursion.title);
            editDate.setText(excursion.date);
            switchAlert.setChecked(excursion.alertsEnabled);
        }
    }

    private void saveExcursion() {
        String title = editTitle.getText().toString().trim();
        String date = editDate.getText().toString().trim();
        boolean alertsEnabled = switchAlert.isChecked();

        if (title.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Title and date are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(date)) {
            Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            Date excursionDate = sdf.parse(date);
            if (excursionDate == null) throw new ParseException("Null parsed date", 0);

            // Fetch the parent vacation and validate the date range
            Vehicle vehicle = db.vacationDao().getVacationById(vacationId);
            if (vehicle == null) {
                Toast.makeText(this, "Parent vacation not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            Date startDate = sdf.parse(vehicle.startDate);
            Date endDate = sdf.parse(vehicle.endDate);

            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Invalid vacation date range.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (excursionDate.before(startDate) || excursionDate.after(endDate)) {
                Toast.makeText(this, "Excursion must be within the vacation dates: "
                        + vehicle.startDate + " to " + vehicle.endDate, Toast.LENGTH_LONG).show();
                return;
            }

        } catch (ParseException e) {
            Toast.makeText(this, "Date parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        if (excursionId == -1) {
            // New excursion
            Excursion newExcursion = new Excursion();
            newExcursion.vacationOwnerId = vacationId;
            newExcursion.title = title;
            newExcursion.date = date;
            db.excursionDao().insertExcursion(newExcursion);
        } else {
            // Update existing
            Excursion existing = db.excursionDao().getExcursionById(excursionId);
            if (existing != null) {
                existing.title = title;
                existing.date = date;
                db.excursionDao().updateExcursion(existing);
            }
        }

        scheduleExcursionAlert(title, date);
        Toast.makeText(this, "Excursion saved", Toast.LENGTH_SHORT).show();
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

    private void scheduleExcursionAlert(String title, String dateStr) {
        long triggerTime = parseDateToMillis(dateStr);

        Calendar nowCal = Calendar.getInstance();
        nowCal.set(Calendar.HOUR_OF_DAY, 0);
        nowCal.set(Calendar.MINUTE, 0);
        nowCal.set(Calendar.SECOND, 0);
        nowCal.set(Calendar.MILLISECOND, 0);
        long todayMidnight = nowCal.getTimeInMillis();

        if (triggerTime < todayMidnight) {
            Toast.makeText(this, "Alert date must not be in the past", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ExcursionAlertReceiver.class);
        intent.putExtra("excursionTitle", title);

        int requestCode = title.hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

        Toast.makeText(this, "Alert set for excursion", Toast.LENGTH_SHORT).show();
    }

    private long parseDateToMillis(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            sdf.setLenient(false);
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : -1;
        } catch (Exception e) {
            return -1;
        }
    }
}
