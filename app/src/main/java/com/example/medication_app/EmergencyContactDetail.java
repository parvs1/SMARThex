package com.example.medication_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class EmergencyContactDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_detail);
        TextView v = (TextView) findViewById(R.id.phoneNumber);
        Contact temp = (Contact) getIntent().getSerializableExtra("contactobject");
    }

}
