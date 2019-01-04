package com.example.shrey.medremindertest;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import butterknife.OnItemSelected;

public class EditMedicineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editMedName;
    Button timePickerButton;
    Button saveButton;
    Spinner frequencySpinner;
    public final String TAG = "F";

    int[] time = new int[2];
    int doseFrequency = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_medicine);

        Medicine medicineToEdit = (Medicine)getIntent().getSerializableExtra("medicineToEdit");

        editMedName = (EditText)findViewById(R.id.editMedName);
        editMedName.setText(medicineToEdit.medicineName);

        final Button timePickerButton = (Button)findViewById(R.id.timePickerButton);
        final String tempTime = "" + medicineToEdit.hour%12 + ":" + medicineToEdit.minute;
        timePickerButton.setText(tempTime);
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditMedicineActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        tempTime = "" + selectedHour%12 + ":" + selectedMinute;
                        timePickerButton.setText(tempTime);
                        time[0] = selectedHour;
                        time[1] = selectedMinute;
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        frequencySpinner = (Spinner) findViewById(R.id.frequencySpinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.frequency_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        frequencySpinner.setAdapter(spinnerAdapter);

        frequencySpinner.setSelection(medicineToEdit.frequency-1);

        frequencySpinner.setOnItemSelectedListener(this);

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Medicine tempMedicine = new Medicine(editMedName.getText().toString(), time[0], time[1], doseFrequency);
                Log.d(TAG, ""+ tempMedicine);

                Intent editedMedicine = new Intent(EditMedicineActivity.this, MainActivity.class);
                editedMedicine.putExtra("editedMedicine", tempMedicine);


            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        doseFrequency = pos + 1;
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
