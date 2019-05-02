package com.example.medication_app;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {
    Button editSchedule;
    Button settings;
    Button dashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        editSchedule = (Button) findViewById(R.id.editMedSchedule);
        editSchedule.setOnClickListener(new View.OnClickListener() {
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
        //Intent i = new Intent(HomeScreen.this, SettingsActivity.class);
        //startActivity(i);

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
    }

}
