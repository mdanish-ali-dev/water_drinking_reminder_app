package com.reminder.waterdrinkingreminderapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class SetupActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spGoal, spReminderInterval;
    private TextView tvWakeUpTime, tvSleepTime;
    private Button btnSave;
    private int wakeUpHour, wakeUpMinute, sleepHour, sleepMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        etName = findViewById(R.id.et_name);
        spGoal = findViewById(R.id.sp_goal);
        tvWakeUpTime = findViewById(R.id.tv_wake_up_time);
        tvSleepTime = findViewById(R.id.tv_sleep_time);
        spReminderInterval = findViewById(R.id.sp_reminder_interval);
        btnSave = findViewById(R.id.btn_save);

        // Populate reminder interval spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.reminder_intervals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spReminderInterval.setAdapter(adapter);

        // Populate goal spinner
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.water_goal_options, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGoal.setAdapter(goalAdapter);


        tvWakeUpTime.setOnClickListener(v -> showTimePickerDialog(true));
        tvSleepTime.setOnClickListener(v -> showTimePickerDialog(false));

        btnSave.setOnClickListener(v -> {
            saveSetupData();
        });
    }

    private void showTimePickerDialog(boolean isWakeUpTime) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            if (isWakeUpTime) {
                wakeUpHour = hourOfDay;
                wakeUpMinute = minute;
                tvWakeUpTime.setText(time);
            } else {
                sleepHour = hourOfDay;
                sleepMinute = minute;
                tvSleepTime.setText(time);
            }
        }, currentHour, currentMinute, DateFormat.is24HourFormat(this));

        timePickerDialog.show();
    }

    private void saveSetupData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // User Name
        editor.putString("user_name", etName.getText().toString());

        // Daily Goal
        String goalStr = spGoal.getSelectedItem().toString().replaceAll("[^\\d]", "");
        editor.putInt("daily_goal", Integer.parseInt(goalStr));

        // Wake-up and Sleep Times
        editor.putString("wake_up_time", String.format(Locale.getDefault(), "%02d:%02d", wakeUpHour, wakeUpMinute));
        editor.putString("sleep_time", String.format(Locale.getDefault(), "%02d:%02d", sleepHour, sleepMinute));

        // Reminder Interval
        String intervalStr = spReminderInterval.getSelectedItem().toString();
        int intervalMinutes = Integer.parseInt(intervalStr.split(" ")[0]);
        editor.putInt("reminder_interval", intervalMinutes);
        
        // Default reminder setting
        editor.putBoolean("reminders_enabled", true);

        // Mark setup as complete
        editor.putBoolean("is_setup_complete", true);

        editor.apply();

        // Schedule Alarms
        AlarmHelper alarmHelper = new AlarmHelper();
        alarmHelper.scheduleReminder(this);
        alarmHelper.scheduleMidnightReset(this);

        Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show();

        // Navigate to MainActivity
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
