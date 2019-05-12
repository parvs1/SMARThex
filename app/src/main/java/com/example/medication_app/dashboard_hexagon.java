package com.example.medication_app;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class dashboard_hexagon extends AppCompatActivity {
    ImageButton setting;
    ImageButton alarm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_hex);
        setting = (ImageButton) findViewById(R.id.settings);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(dashboard_hexagon.this, SettingsActivity.class);
                startActivity(i);

            }
        });
        alarm = (ImageButton)findViewById(R.id.alarms);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(dashboard_hexagon.this, MainActivity.class);
                startActivity(j);

            }
        });
    }
}
