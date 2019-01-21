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

    String[] time = new String[2];
    int doseFrequency = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_medicine);

        Medicine medicineToEdit = (Medicine)getIntent().getSerializableExtra("medicineToEdit");

        //if adding new medicine, not editing, change medicineToEdit
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

        String timePickerButtonHourText = ""+Integer.parseInt(medicineToEdit.hour)%12;
        if (timePickerButtonHourText.length() == 1)
            timePickerButtonHourText = "0" + timePickerButtonHourText;

        String timePickerButtonMinText = ""+Integer.parseInt(medicineToEdit.minute);
        if (timePickerButtonMinText.length() == 1)
            timePickerButtonMinText = "0" + timePickerButtonMinText;


        final Button timePickerButton = (Button)findViewById(R.id.timePickerButton);
        String timeText = "" + timePickerButtonHourText + ":" + timePickerButtonMinText;
        timePickerButton.setText(timeText);
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

                        String selectedHourText = ""+selectedHour;
                        if (selectedHourText.length() == 1)
                            selectedHourText = "0" + selectedHourText;

                        String selectedMinText = ""+selectedMinute;
                        if (selectedMinText.length() == 1)
                            selectedMinText = "0" + selectedMinText;

                        time[0] = selectedHourText;
                        time[1] = selectedMinText;

                        String timeText = "" + selectedHourText + ":" + selectedMinText;
                        timePickerButton.setText(timeText);
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

                Intent editedMedicine = new Intent();
                editedMedicine.putExtra("editedMedicine", tempMedicine);

                setResult(RESULT_OK,editedMedicine);
                finish();
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
