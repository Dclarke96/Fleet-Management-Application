package com.example.d308project.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.d308project.R;

public class MaintenanceAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String entityType = intent.getStringExtra("entityType"); // Vehicle / Maintenance
        String eventType = intent.getStringExtra("eventType");   // due / reminder / service

        if (title == null || entityType == null || eventType == null) {
            Log.e("AlertReceiver", "Missing intent extras");
            return;
        }

        String message = buildMessage(entityType, title, eventType);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "vehicle_alerts")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(entityType + " Alert")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify((int) System.currentTimeMillis(), builder.build());
    }

    private String buildMessage(String entityType, String title, String eventType) {
        switch (entityType) {
            case "Vehicle":
                return "Vehicle \"" + title + "\" has an upcoming reminder.";

            case "Maintenance":
                return "Maintenance \"" + title + "\" is " + eventType + " today.";

            default:
                return title + " has an alert today.";
        }
    }
}
