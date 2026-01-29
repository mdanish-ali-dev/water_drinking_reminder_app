package com.reminder.waterdrinkingreminderapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome, tvProgress;
    private ProgressBar progressBar;
    private Button btnAdd100, btnAdd250, btnAdd500, btnViewHistory, btnSettings;

    private SharedPreferences sharedPreferences;

    private int dailyGoal;
    private int totalWaterConsumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        tvWelcome = findViewById(R.id.tv_welcome);
        tvProgress = findViewById(R.id.tv_progress);
        progressBar = findViewById(R.id.progress_bar);
        btnAdd100 = findViewById(R.id.btn_add_100);
        btnAdd250 = findViewById(R.id.btn_add_250);
        btnAdd500 = findViewById(R.id.btn_add_500);
        btnViewHistory = findViewById(R.id.btn_view_history);
        btnSettings = findViewById(R.id.btn_settings);

        loadUserData();
        updateUI();

        btnAdd100.setOnClickListener(v -> addWater(100));
        btnAdd250.setOnClickListener(v -> addWater(250));
        btnAdd500.setOnClickListener(v -> addWater(500));

        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserData() {
        String userName = sharedPreferences.getString("user_name", "User");
        dailyGoal = sharedPreferences.getInt("daily_goal", 2000);
        totalWaterConsumed = sharedPreferences.getInt("total_water_consumed", 0);

        tvWelcome.setText("Welcome, " + userName + "!");
    }

    private void updateUI() {
        tvProgress.setText("Today's Progress: " + totalWaterConsumed + " / " + dailyGoal + " ml");

        int progress = (int) (((double) totalWaterConsumed / dailyGoal) * 100);
        progressBar.setProgress(progress);

        if (totalWaterConsumed >= dailyGoal) {
            Toast.makeText(this, "Goal Completed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addWater(int amount) {
        totalWaterConsumed += amount;

        // Get a mutable copy of the history set
        Set<String> history = new HashSet<>(sharedPreferences.getStringSet("water_history", new HashSet<>()));
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        history.add(currentDate + ": " + amount + " ml");

        // Use a single editor to commit both changes
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("total_water_consumed", totalWaterConsumed);
        editor.putStringSet("water_history", history);
        editor.commit(); // Use commit() for synchronous saving

        // Add a toast to confirm saving
        Toast.makeText(this, "History saved. Total entries: " + history.size(), Toast.LENGTH_SHORT).show();

        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data and update UI in case settings were changed
        loadUserData();
        updateUI();
    }

    // Force rebuild
}
