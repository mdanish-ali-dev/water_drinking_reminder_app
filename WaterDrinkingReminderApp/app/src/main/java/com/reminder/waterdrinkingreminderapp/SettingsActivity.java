package com.reminder.waterdrinkingreminderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private EditText etDailyGoal;
    private Spinner spinnerReminderInterval;
    private SwitchMaterial switchNotifications;
    private Button btnSaveSettings, btnResetData;

    private SharedPreferences sharedPreferences;
    private AlarmHelper alarmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        alarmHelper = new AlarmHelper();

        etDailyGoal = findViewById(R.id.et_daily_goal);
        spinnerReminderInterval = findViewById(R.id.spinner_reminder_interval);
        switchNotifications = findViewById(R.id.switch_notifications);
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnResetData = findViewById(R.id.btn_reset_data);

        loadSettings();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReminderInterval.setAdapter(adapter);

        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnResetData.setOnClickListener(v -> showResetDataConfirmationDialog());
    }

    private void loadSettings() {
        int dailyGoal = sharedPreferences.getInt("daily_goal", 2000);
        int reminderInterval = sharedPreferences.getInt("reminder_interval", 60);
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);

        etDailyGoal.setText(String.valueOf(dailyGoal));
        switchNotifications.setChecked(notificationsEnabled);

        // Set spinner selection based on saved interval
        String[] intervalArray = getResources().getStringArray(R.array.reminder_intervals);
        for (int i = 0; i < intervalArray.length; i++) {
            if (intervalArray[i].startsWith(String.valueOf(reminderInterval))) {
                spinnerReminderInterval.setSelection(i);
                break;
            }
        }
    }

    private void saveSettings() {
        int dailyGoal = Integer.parseInt(etDailyGoal.getText().toString());
        String selectedInterval = spinnerReminderInterval.getSelectedItem().toString();
        int reminderInterval = Integer.parseInt(selectedInterval.split(" ")[0]);
        boolean notificationsEnabled = switchNotifications.isChecked();

        sharedPreferences.edit()
                .putInt("daily_goal", dailyGoal)
                .putInt("reminder_interval", reminderInterval)
                .putBoolean("notifications_enabled", notificationsEnabled)
                .apply();

        if (notificationsEnabled) {
            alarmHelper.scheduleReminder(this);
        } else {
            alarmHelper.cancelReminder(this);
        }

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showResetDataConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Data")
                .setMessage("Are you sure you want to reset all your data? This action cannot be undone.")
                .setPositiveButton("Yes, Reset", (dialog, which) -> resetAllData())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetAllData() {
        sharedPreferences.edit().clear().apply();
        alarmHelper.cancelReminder(this);
        // Optionally, restart the app or navigate to the setup screen
        Toast.makeText(this, "All data has been reset.", Toast.LENGTH_SHORT).show();
        finishAffinity();
        startActivity(new Intent(this, SplashActivity.class));
    }
}
