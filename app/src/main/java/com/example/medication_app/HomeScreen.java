package com.example.medication_app;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {
    Button editAlarms;
    Button editMedSchedule;
    Button settings;
    Button dashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        editAlarms = (Button) findViewById(R.id.edalarms);
        editAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(i);
            }
        });

    settings = (Button) findViewById(R.id.settings);
    settings.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Intent i = new Intent(HomeScreen.this, SettingsActivity.class);
        startActivity(i);

    }
    });

    dashboard = (Button) findViewById(R.id.dashboard);

    dashboard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                Intent i = new Intent(HomeScreen.this, Dashboard.class);
                startActivity(i);

            }
        });
        editMedSchedule = (Button) findViewById(R.id.edSchedule);
        editMedSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this, MedicineSchedule.class);
                startActivity(i);
            }
        });

    }

}
