package com.example.medication_app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class EditModule extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{

    ArrayList<Medicine> medicines;
    ArrayList<String> times;
    TextView timesList;
    public String medName;
    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage
    Spinner colorPicker;
    int color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_module);

        medicines = new ArrayList<Medicine>();
        getMedicinesfromFile();

        final Module moduleToEdit = (Module)getIntent().getSerializableExtra("moduleToEdit");
        medName = moduleToEdit.medicineName;

        times = moduleToEdit.times;
        timesList = findViewById(R.id.timesList);
        createTimesList();


        ArrayList<String> medNamesArrayList = new ArrayList<String>();
        for (int i = 0; i < medicines.size(); i++) {
            Boolean check = false;
            String tempName = medicines.get(i).medicineName;

            for(int index = 0; index < medNamesArrayList.size(); index++) {
                if (tempName.equals(medNamesArrayList.get(index)))
                    check = true;
            }

            if(!check)
                medNamesArrayList.add(tempName);
        }

        String[] medNames = new String[medNamesArrayList.size()];
        for (int i = 0; i < medNamesArrayList.size(); i++)
            medNames[i] = medNamesArrayList.get(i);

        final Spinner medicineSelector = (Spinner)findViewById(R.id.medicineSelector);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(EditModule.this, android.R.layout.simple_spinner_item, medNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicineSelector.setAdapter(spinnerAdapter);

        medicineSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String medicineName = spinnerAdapter.getItem(position);
                times.clear();
                for (int i = 0; i < medicines.size(); i++) {
                    if (medicines.get(i).medicineName.equals(medicineName))
                        times.add(medicines.get(i).hour + ":" + medicines.get(i).minute + ", " + daysArrayShortener((medicines.get(i).days)));
                }

                medName = medicineName;
                createTimesList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button finishButton = (Button)findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Module tempModule = new Module(moduleToEdit.module, medName, times);
                Log.e(TAG, tempModule.modBtnText());
                Log.e(TAG, tempModule.times.toString());

                Intent editedModule = new Intent();
                editedModule.putExtra("editedModule", tempModule);
                editedModule.putExtra("color", color);

                setResult(RESULT_OK, editedModule);
                finish();

            }
        });

        colorPicker = (Spinner) findViewById(R.id.colorPicker);

        //set the items in the spinner to be choices of n days
        ArrayAdapter<CharSequence> colorAdapter = ArrayAdapter.createFromResource(this, R.array.color_picker, android.R.layout.simple_spinner_item);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        colorPicker.setAdapter(colorAdapter);

        colorPicker.setSelection(0);
        //create the onItemSelectedListener
        colorPicker.setOnItemSelectedListener(this);
    }

    public void createTimesList() {
        String timeListString = "";

        for (int i = 0; i < times.size(); i++) {
            timeListString+= "◦ " + times.get(i);
            timeListString+= "\n";
        }

        timesList.setText(timeListString);
    }

    public void getMedicinesfromFile() {
        String medicinesFileText = "";
        FileInputStream fileInputStream = null;

        //try finding file 'medicinesFile.txt' if it exists
        try {
            fileInputStream = openFileInput("medicinesFile.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] buffer = new char[1024];
            int charRead;

            //creates string that contains text from 'medicinesFile.txt'
            while ((charRead = inputStreamReader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charRead);
                medicinesFileText += readString;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] medicinesFileTextArray = medicinesFileText.split("\n"); //split text from 'medicinesFile.txt' by line

        for (int i = 0; i < medicinesFileTextArray.length; i+=5) {
            String tempName = medicinesFileTextArray[i];
            String tempHour = medicinesFileTextArray[i + 1];
            String tempMin = medicinesFileTextArray[i + 2];
            int tempAlarmRequestCode = Integer.parseInt(medicinesFileTextArray[i+3]);
            String daysString = medicinesFileTextArray[i + 4];

            String[] daysArray = daysString.split(",");

            boolean[] days = new boolean[7];
            for(int day = 0; day < days.length; day++)
                days[day] = Boolean.parseBoolean(daysArray[day]);

            medicines.add(new Medicine(tempName, tempHour, tempMin, tempAlarmRequestCode, days));
        }
    }

    public String daysArrayShortener (boolean[] daysArray)
    {
        String weekSchedule = "";
        for(int i = 0; i < daysArray.length; i++){
            if(daysArray[i])
                weekSchedule += "t";
            else
                weekSchedule += "f";
        }

        return weekSchedule;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        color = pos;    //since pos 0 is equivalent to 1 day, add 1
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
