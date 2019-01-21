package com.example.shrey.medremindertest;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView medSchedule;
    ArrayList<Medicine> medicines;
    ArrayAdapter<Medicine> adapter;
    FloatingActionButton addMedicine;
    public final int REQUEST_CODE = 4;
    public final String TAG = "medadhererance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String medicinesFileText = "";
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = openFileInput("medicinesFile.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] buffer = new char[1024];
            int charRead;

            //add way to convert the 'medicinesText' file into the medicines ArrayList
            while ((charRead=inputStreamReader.read(buffer))>0)
            {
                String readString = String.copyValueOf(buffer,0,charRead);
                medicinesFileText+=readString;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!medicinesFileText.equals(""))
            Log.e(TAG, medicinesFileText);

        medicines = new ArrayList<Medicine>();

        medicines.add(new Medicine("Tums", "06", "00"));
        medicines.add(new Medicine("Lanzo", "07", "30"));

        adapter = new ArrayAdapter<Medicine>(this, android.R.layout.simple_list_item_1, medicines);

        medSchedule = (ListView)findViewById(R.id.medSchedule);
        medSchedule.setAdapter(adapter);

        medSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent editMedicineActivity = new Intent(MainActivity.this, EditMedicineActivity.class);
                editMedicineActivity.putExtra("medicineToEdit", medicines.get(position));

                medicines.remove(position);

                startActivityForResult(editMedicineActivity, REQUEST_CODE);
            }
        });

        FloatingActionButton addMedicine = (FloatingActionButton) findViewById(R.id.addMedicine);
        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editMedicineActivity = new Intent(MainActivity.this, EditMedicineActivity.class);
                editMedicineActivity.putExtra("medicineToEdit", new Medicine("temp", "-1", "-1"));

                startActivityForResult(editMedicineActivity, REQUEST_CODE);
            }
        });
    }

    // ActivityOne.java, time to handle the result of the sub-activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            Medicine newMedicine = (Medicine)data.getSerializableExtra("editedMedicine");
            medicines.add(newMedicine);
            adapter.notifyDataSetChanged();

            String filename = "medicinesFile.txt";

            String fileContents = "";

            for(int i = 0; i<medicines.size(); i++)
            {
                Medicine temp = medicines.get(i);
                fileContents += temp.medicineName + "\n";
                fileContents += temp.hour + "\n";
                fileContents += temp.minute + "\n";
                fileContents += temp.frequency + "\n";
            }

            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                OutputStreamWriter outputWriter=new OutputStreamWriter(outputStream);
                outputWriter.write(fileContents);
                outputWriter.close();

                //Log.e(TAG, "Saved as..." + fileContents); //Uncomment this to read the text file in the log if need be

            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    }
}
