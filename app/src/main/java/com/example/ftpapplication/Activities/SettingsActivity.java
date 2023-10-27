package com.example.ftpapplication.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ftpapplication.Fragments.Settings;
import com.example.ftpapplication.R;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_frag_container);
        getSupportActionBar().setTitle("Settings");
        if (findViewById(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.idFrameLayout, new Settings()).commit();
        }
    }
}

