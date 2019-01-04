package com.example.shrey.medremindertest;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView medSchedule;
    ArrayList<Medicine> medicines;
    ArrayAdapter<Medicine> adapter;
    FloatingActionButton addMedicine;
    public final int requestCode = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        medicines = new ArrayList<Medicine>();

        medicines.add(new Medicine("Tums", 6, 00));
        medicines.add(new Medicine("Lanzo", 7, 30));

        adapter = new ArrayAdapter<Medicine>(this, android.R.layout.simple_list_item_1, medicines);

        medSchedule = (ListView)findViewById(R.id.medSchedule);
        medSchedule.setAdapter(adapter);

        medSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editMedicineActivity = new Intent(MainActivity.this, EditMedicineActivity.class);
                editMedicineActivity.putExtra("medicineToEdit", medicines.get(position));

                startActivityForResult(editMedicineActivity, requestCode);
            }
        });

        Button addMedicine = (Button)findViewById(R.id.addMedicine);
        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
