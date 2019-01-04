package com.example.shrey.medremindertest;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    ListView medSchedule;
    ArrayList<Medicine> medicines;
    ArrayAdapter<Medicine> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        medicines = new ArrayList<Medicine>();

        medicines.add(new Medicine("Tums", 6, 00));
        medicines.add(new Medicine("Lonzo", 7, 30));

        adapter = new ArrayAdapter<Medicine>(this, android.R.layout.simple_list_item_1, medicines);


        medSchedule = (ListView)findViewById(R.id.medSchedule);
        medSchedule.setAdapter(adapter);
    }

    @OnClick(R.id.buttonAddMedicine)
    public void onClickAddMedicine()
    {
        //do whatever on click.
    }
}
