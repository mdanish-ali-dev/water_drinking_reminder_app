package com.reminder.waterdrinkingreminderapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_REMIND = "com.reminder.waterdrinkingreminderapp.ACTION_REMIND";
    public static final String ACTION_MIDNIGHT_RESET = "com.reminder.waterdrinkingreminderapp.ACTION_MIDNIGHT_RESET";
    private static final String CHANNEL_ID = "water_reminder_channel";
    private static final int NOTIFICATION_ID = 101;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_REMIND:
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    boolean remindersEnabled = prefs.getBoolean("notifications_enabled", true);
                    if (remindersEnabled) {
                        showReminderNotification(context);
                    }
                    break;
                case ACTION_MIDNIGHT_RESET:
                    resetWaterIntake(context);
                    break;
            }
        }
    }

    private void showReminderNotification(Context context) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Placeholder icon
                .setContentTitle("Time to drink water 💧")
                .setContentText("Stay hydrated to stay healthy!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void resetWaterIntake(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt("total_water_consumed", 0).apply();
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Water Reminder";
            String description = "Channel for Water Drinking Reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
