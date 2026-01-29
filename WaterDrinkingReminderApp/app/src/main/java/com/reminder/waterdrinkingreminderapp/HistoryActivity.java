package com.reminder.waterdrinkingreminderapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    private ListView lvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lvHistory = findViewById(R.id.lv_history);

        loadHistory();
    }

    private void loadHistory() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> historySet = sharedPreferences.getStringSet("water_history", new HashSet<>());

        // Add a toast to confirm loading
        Toast.makeText(this, "History loaded. Total entries: " + historySet.size(), Toast.LENGTH_SHORT).show();

        List<String> historyData = new ArrayList<>(historySet);
        Collections.sort(historyData, Collections.reverseOrder()); // Sort to show latest first

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyData);
        lvHistory.setAdapter(adapter);
    }
}
