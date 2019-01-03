package com.example.shrey.medremindertest;

import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class EditMedicine extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editMedName;
    Button timePickerButton;
    Button saveButton;
    Spinner frequencySpinner;

    int[] time = new int[1];
    int doseFrequency = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_medicine);

        editMedName = (EditText)findViewById(R.id.editMedName);

        final Button timePickerButton = (Button)findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditMedicine.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timePickerButton.setText(selectedHour + ":" + selectedMinute);
                        time[0] = selectedHour;
                        time[1] = selectedMinute;
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        frequencySpinner = (Spinner) findViewById(R.id.frequencySpinner);

        final ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.frequency_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        frequencySpinner.setAdapter(spinnerAdapter);

        frequencySpinner.setOnItemClickListener(this);

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Medicine tempMedicine = new Medicine(editMedName.getText().toString(), time[0], time[1], doseFrequency);
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        doseFrequency = pos + 1;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
