package com.example.medication_app;

import android.annotation.TargetApi;
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

public class EditMedicineActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editMedName; //textbox for setting medicineName
    Button saveButton; //button for saving the new or edited medicine
    Spinner frequencySpinner; //spinner to set number of days between doses
    public final String TAG = "com.med-adherence"; //TAG for log usage

    String[] time = new String[2]; //time array that holds a string of hours and minutes in separate elements
    int doseFrequency = 1; //sets default frequency to 1 for spinner

    @Override
    @TargetApi(24)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_medicine);

        //get the medicine from the parent activity
        Medicine medicineToEdit = (Medicine)getIntent().getSerializableExtra("medicineToEdit");

        //if adding new medicine, not editing, change medicineToEdit to some default placeholders
        if(medicineToEdit.medicineName.equals("temp") && Integer.parseInt(medicineToEdit.hour) == -1 && Integer.parseInt(medicineToEdit.minute) == -1)
        {
            medicineToEdit.medicineName = "";
            medicineToEdit.hour = "13";
            medicineToEdit.minute = "35";
            time[0] = "13";
            time[1] = "35";
        }

        editMedName = (EditText)findViewById(R.id.editMedName);
        editMedName.setText(medicineToEdit.medicineName);

        //get hour from medicine for first part of timePickerButton text
        String timePickerButtonHourText = ""+Integer.parseInt(medicineToEdit.hour)%12;
        //if only 1 digit, add 0 before
        if (timePickerButtonHourText.length() == 1)
            timePickerButtonHourText = "0" + timePickerButtonHourText;

        //get minute from medicine for second part of timePickerButton text
        String timePickerButtonMinText = ""+Integer.parseInt(medicineToEdit.minute);
        //again, if only 1 digit, add a 0 before
        if (timePickerButtonMinText.length() == 1)
            timePickerButtonMinText = "0" + timePickerButtonMinText;

        final Button timePickerButton = (Button)findViewById(R.id.timePickerButton);

        //set text to HH:MM
        String timeText = "" + timePickerButtonHourText + ":" + timePickerButtonMinText;
        timePickerButton.setText(timeText);

        //when button is clicked on, create a timePicker that defaults to a time from the button's text
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

                        //if hour or minute is only 1 digit, add a 0 before

                        String selectedHourText = ""+selectedHour;
                        if (selectedHourText.length() == 1)
                            selectedHourText = "0" + selectedHourText;

                        String selectedMinText = ""+selectedMinute;
                        if (selectedMinText.length() == 1)
                            selectedMinText = "0" + selectedMinText;

                        time[0] = selectedHourText;
                        time[1] = selectedMinText;

                        //set the timePickerButton's text to the new time set by the user
                        String timeText = "" + selectedHourText + ":" + selectedMinText;
                        timePickerButton.setText(timeText);
                    }
                }, hour, minute, false);

                mTimePicker.setTitle("Select Time to Take this Dose of Medication");
                mTimePicker.show();
            }
        });


        frequencySpinner = (Spinner) findViewById(R.id.frequencySpinner);

        //set the items in the spinner to be choices of n days
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.frequency_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        frequencySpinner.setAdapter(spinnerAdapter);

        //since 0th index is 1 day, subtract 1 from frequency to set initial position
        frequencySpinner.setSelection(medicineToEdit.frequency-1);
        //create the onItemSelectedListener
        frequencySpinner.setOnItemSelectedListener(this);

        //when finished editing or creating, click this button to save changes
        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Medicine tempMedicine = new Medicine(editMedName.getText().toString(), time[0], time[1], doseFrequency);
                Log.d(TAG, ""+ tempMedicine);

                Intent editedMedicine = new Intent();
                editedMedicine.putExtra("editedMedicine", tempMedicine);

                setResult(RESULT_OK,editedMedicine);
                finish();
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        doseFrequency = pos + 1;    //since pos 0 is equivalent to 1 day, add 1
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
