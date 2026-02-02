package com.example.d308project.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.example.d308project.R;

public class MaintenanceAlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("excursionTitle");

        if (title == null) {
            Log.e("ExcursionAlertReceiver", "Missing excursionTitle in intent extras");
            return;
        }

        String message = "Excursion \"" + title + "\" is happening today!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "vacation_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Excursion Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
