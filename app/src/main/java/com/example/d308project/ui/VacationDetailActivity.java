package com.example.d308project.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.d308project.data.AppDatabase;
import com.example.d308project.data.Excursion;
import com.example.d308project.data.Vacation;
import com.example.d308project.R;

import java.util.Calendar;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;


public class VacationDetailActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    private EditText editTitle, editHotel, editStartDate, editEndDate;
    private Switch switchAlert;
    private Button btnSave, btnDelete, btnShare, btnBack, btnAddExcursion, btnManageExcursions;
    private AppDatabase db;
    private int vacationId = -1;
    private ListView excursionListView;
    private static final int REQUEST_MANAGE_EXCURSIONS = 1001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        setContentView(R.layout.activity_vacation_detail);

        editTitle = findViewById(R.id.editTitle);
        editHotel = findViewById(R.id.editHotel);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        switchAlert = findViewById(R.id.switchAlert);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);
        btnAddExcursion = findViewById(R.id.btnAddExcursion);
        btnManageExcursions = findViewById(R.id.btnManageExcursions);
        excursionListView = findViewById(R.id.excursionList);

        db = AppDatabase.getInstance(getApplicationContext());

        if (getIntent().hasExtra("vacationId")) {
            vacationId = getIntent().getIntExtra("vacationId", -1);
            loadVacation();

            if (vacationId != -1) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> {
                    List<Excursion> excursions = db.excursionDao().getExcursionsForVacation(vacationId);
                    if (excursions != null && !excursions.isEmpty()) {
                        Toast.makeText(this, "Cannot delete vacation with existing excursions. Please delete them first.", Toast.LENGTH_LONG).show();
                    } else {
                        cancelExistingAlarms(vacationId);
                        db.vacationDao().deleteVacationById(vacationId);
                        Toast.makeText(this, "Vacation deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                btnManageExcursions.setVisibility(View.VISIBLE);
            }
        } else {
            btnManageExcursions.setVisibility(View.GONE);
        }

        setupDatePicker(editStartDate);
        setupDatePicker(editEndDate);

        btnSave.setOnClickListener(view -> saveVacation());
        btnShare.setOnClickListener(v -> shareVacationDetails());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous screen
            }
        });

        btnAddExcursion.setOnClickListener(v -> {
            if (vacationId != -1) {
                Intent intent = new Intent(VacationDetailActivity.this, ExcursionListActivity.class);
                intent.putExtra("vacationId", vacationId);
                startActivityForResult(intent, REQUEST_MANAGE_EXCURSIONS);
            } else {
                Toast.makeText(this, "Please save the vacation first.", Toast.LENGTH_SHORT).show();
            }
        });

        btnManageExcursions.setOnClickListener(v -> {
            if (vacationId != -1) {
                Intent intent = new Intent(VacationDetailActivity.this, ExcursionListActivity.class);
                intent.putExtra("vacationId", vacationId);
                startActivityForResult(intent, REQUEST_MANAGE_EXCURSIONS);
            }
        });
    }

    private void setupDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
                        editText.setText(date);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void loadVacation() {
        Vacation vacation = db.vacationDao().getVacationById(vacationId);
        if (vacation != null) {
            editTitle.setText(vacation.title);
            editHotel.setText(vacation.hotel);
            editStartDate.setText(vacation.startDate);
            editEndDate.setText(vacation.endDate);
            switchAlert.setChecked(vacation.alertsEnabled);
        }

        // Load and display excursions
        List<String> excursionTitles = new ArrayList<>();
        List<Excursion> excursions = db.excursionDao().getExcursionsForVacation(vacationId);
        for (Excursion e : excursions) {
            excursionTitles.add(e.title); // Customize as needed
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                excursionTitles
        );
        excursionListView.setAdapter(adapter);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "VacationAlerts";
            String description = "Channel for vacation start/end alerts";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("vacation_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void saveVacation() {
        String title = editTitle.getText().toString().trim();
        String hotel = editHotel.getText().toString().trim();
        String start = editStartDate.getText().toString().trim();
        String end = editEndDate.getText().toString().trim();
        boolean alertsEnabled = switchAlert.isChecked();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(hotel)) {
            Toast.makeText(this, "Hotel is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(start)) {
            Toast.makeText(this, "Start date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(end)) {
            Toast.makeText(this, "End date is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (end.compareTo(start) < 0) {
            Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
            return;
        }

        Vacation vacation = new Vacation(title, hotel, start, end, alertsEnabled);

        if (vacationId == -1) {
            db.vacationDao().insertVacation(vacation);
            vacationId = db.vacationDao().getLastInsertedId(); // Your DAO should implement this
        } else {
            vacation.id = vacationId;
            db.vacationDao().updateVacation(vacation);
        }

        if (alertsEnabled) {
            vacation.id = vacationId;
            scheduleVacationAlerts(vacation);
        } else {
            cancelExistingAlarms(vacation.id);
        }

        btnManageExcursions.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Vacation saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void scheduleVacationAlerts(Vacation vacation) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        cancelExistingAlarms(vacation.id);

        long startTime = parseDateToMillis(vacation.startDate);
        if (startTime > 0) {
            Intent startIntent = new Intent(this, VacationAlertReceiver.class);
            startIntent.putExtra("title", vacation.title);
            startIntent.putExtra("type", "start");

            PendingIntent startPendingIntent = PendingIntent.getBroadcast(
                    this,
                    vacation.id * 2,
                    startIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | getPendingIntentMutableFlag()
            );
            alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, startPendingIntent);
        }

        long endTime = parseDateToMillis(vacation.endDate);
        if (endTime > 0) {
            Intent endIntent = new Intent(this, VacationAlertReceiver.class);
            endIntent.putExtra("title", vacation.title);
            endIntent.putExtra("type", "end");

            PendingIntent endPendingIntent = PendingIntent.getBroadcast(
                    this,
                    vacation.id * 2 + 1,
                    endIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | getPendingIntentMutableFlag()
            );
            alarmManager.set(AlarmManager.RTC_WAKEUP, endTime, endPendingIntent);
        }
    }

    private void cancelExistingAlarms(int vacationId) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent startIntent = new Intent(this, VacationAlertReceiver.class);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(
                this,
                vacationId * 2,
                startIntent,
                PendingIntent.FLAG_NO_CREATE | getPendingIntentMutableFlag()
        );
        if (startPendingIntent != null) {
            alarmManager.cancel(startPendingIntent);
        }

        Intent endIntent = new Intent(this, VacationAlertReceiver.class);
        PendingIntent endPendingIntent = PendingIntent.getBroadcast(
                this,
                vacationId * 2 + 1,
                endIntent,
                PendingIntent.FLAG_NO_CREATE | getPendingIntentMutableFlag()
        );
        if (endPendingIntent != null) {
            alarmManager.cancel(endPendingIntent);
        }
    }

    private long parseDateToMillis(String dateString) {
        try {
            String[] parts = dateString.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, 9, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            return calendar.getTimeInMillis();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private int getPendingIntentMutableFlag() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return PendingIntent.FLAG_MUTABLE;
        } else {
            return 0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission is required to show alerts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareVacationDetails() {
        String title = editTitle.getText().toString().trim();
        String hotel = editHotel.getText().toString().trim();
        String start = editStartDate.getText().toString().trim();
        String end = editEndDate.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(hotel) || TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            Toast.makeText(this, "Please fill out all vacation fields before sharing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "Vacation Details:\n\n" +
                "Title: " + title + "\n" +
                "Hotel: " + hotel + "\n" +
                "Start Date: " + start + "\n" +
                "End Date: " + end + "\n";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share vacation using:");
        startActivity(shareIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_MANAGE_EXCURSIONS) {
            loadVacation();
        }
    }
}
