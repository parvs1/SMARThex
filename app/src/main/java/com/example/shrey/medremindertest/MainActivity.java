package com.example.shrey.medremindertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView medSchedule;
    ArrayList<Medicine> medicines = new ArrayList<Medicine>();
    ArrayAdapter<Medicine> adapter = new ArrayAdapter<Medicine>(this, android.R.layout.simple_list_item_1, medicines);
    Button addMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        medicines.add(new Medicine("Tums", 6, 00));
        medicines.add(new Medicine("Lanzo", 7, 30));
        medicines.add(new Medicine("Tums", 7, 30, "PM"));


        medSchedule = (ListView)findViewById(R.id.medSchedule);
        medSchedule.setAdapter(adapter);

        Button addMedicine = (Button)findViewById(R.id.addMedicine);
        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
