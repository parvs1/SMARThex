package com.example.medication_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    ListView medProfile;
    ArrayList<String> profilers;
    ArrayAdapter<String> tings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        profilers.add(0,"Check your Alarms");
        profilers.add(1,"View Your Medicines");
        profilers.add(2,"Contact your Doctor");
        profilers.add(3,"Renew your Prescription");

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

        medProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent i = new Intent(Profile.this, MainActivity.class);
                    startActivity(i);
                }
                if(position == 1)
                {
                    Intent j = new Intent(Profile.this, Dashboard.class);
                    startActivity(j);
                }
                if(position == 2)
                {
                    Intent k = new Intent(Profile.this, MainActivity.class);
                    startActivity(k);
                }
                if(position == 3)
                {
                    Intent l = new Intent(Profile.this, MainActivity.class);
                    startActivity(l);
                }
            }
        });


    }

}
