package com.dylanclarke.FleetManagementApp.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dylanclarke.FleetManagementApp.R;

/**
 * Receives scheduled maintenance alert broadcasts
 * and displays user notifications.
 */
public class MaintenanceAlertReceiver extends BroadcastReceiver {

    private static final String TAG =
            "MaintenanceAlertReceiver";

    private static final String CHANNEL_ID =
            "vehicle_alerts";

    private static final String EXTRA_TITLE =
            "title";

    private static final String EXTRA_ENTITY_TYPE =
            "entityType";

    private static final String EXTRA_EVENT_TYPE =
            "eventType";

    @Override
    public void onReceive(
            Context context,
            Intent intent
    ) {

        String title =
                intent.getStringExtra(EXTRA_TITLE);

        String entityType =
                intent.getStringExtra(EXTRA_ENTITY_TYPE);

        String eventType =
                intent.getStringExtra(EXTRA_EVENT_TYPE);

        if (title == null
                || entityType == null
                || eventType == null) {

            Log.e(
                    TAG,
                    "Missing required alert intent extras"
            );

            return;
        }

        String message =
                buildMessage(
                        entityType,
                        title,
                        eventType
                );

        showNotification(
                context,
                entityType,
                message
        );
    }

    /**
     * Displays a maintenance-related notification.
     */
    private void showNotification(
            Context context,
            String entityType,
            String message
    ) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                R.drawable.ic_launcher_foreground
                        )
                        .setContentTitle(
                                entityType + " Alert"
                        )
                        .setContentText(message)
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify(
                        (int) System.currentTimeMillis(),
                        builder.build()
                );
    }

    /**
     * Builds a user-friendly notification message.
     */
    private String buildMessage(
            String entityType,
            String title,
            String eventType
    ) {

        switch (entityType) {

            case "Vehicle":
                return "Vehicle \""
                        + title
                        + "\" has an upcoming reminder.";

            case "Maintenance":
                return "Maintenance \""
                        + title
                        + "\" is "
                        + eventType
                        + " today.";

            default:
                return title + " has an alert today.";
        }
    }
}