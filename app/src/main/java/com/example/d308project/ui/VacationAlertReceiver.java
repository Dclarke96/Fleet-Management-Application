package com.example.d308project.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.d308project.R;

public class VacationAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String type = intent.getStringExtra("type");

        if (title == null || type == null) {
            android.util.Log.e("VacationAlertReceiver", "Missing title or type in intent extras");
            return;
        }

        String message = "Vacation \"" + title + "\" is " + ("start".equals(type) ? "starting" : "ending") + " today!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "vacation_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Vacation Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

}
