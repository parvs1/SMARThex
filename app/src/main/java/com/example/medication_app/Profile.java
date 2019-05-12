package com.example.medication_app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    ListView medProfile;
    ArrayList<String> profilers;
    ArrayAdapter<String> tings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        profilers.add("Manage your Appointments");
        profilers.add("Check your Alarms");
        profilers.add("View Your Medicines");
        profilers.add("Contact your Doctor");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        medProfile = (ListView) findViewById(R.id.medProfile);
        tings = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,profilers);
        medProfile.setAdapter(tings);


    }

}
