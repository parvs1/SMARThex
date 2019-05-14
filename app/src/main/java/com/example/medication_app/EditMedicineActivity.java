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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Random;

public class EditMedicineActivity extends AppCompatActivity {

    EditText editMedName; //textbox for setting medicineName
    Button saveButton; //button for saving the new or edited medicine
    CheckBox sunCheck;
    CheckBox monCheck;
    CheckBox tuesCheck;
    CheckBox wedCheck;
    CheckBox thursCheck;
    CheckBox friCheck;
    CheckBox satCheck;
    boolean[] editedDays;
    int alarmRequestCode;
    public final String TAG = "com.med-adherence"; //TAG for log usage
    String[] time = new String[2]; //time array that holds a string of hours and minutes in separate elements

    @Override
    @TargetApi(24)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_medicine);

        //get the medicine from the parent activity
        Medicine medicineToEdit = (Medicine)getIntent().getSerializableExtra("medicineToEdit");
        alarmRequestCode = medicineToEdit.alarmRequestCode;

        editedDays = new boolean[7];

        //if adding new medicine, not editing, change medicineToEdit to some default placeholders and give it a unique, random request code
        if(medicineToEdit.medicineName.equals("te425252621afawetmp"))
        {
            medicineToEdit.medicineName = "";
            medicineToEdit.hour = "12";
            medicineToEdit.minute = "00";

            Random random = new Random();
            alarmRequestCode = random.nextInt(798) + 101;
        }

        time[0] = medicineToEdit.hour;
        time[1] = medicineToEdit.minute;

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


        boolean[] days = medicineToEdit.days;

        sunCheck = (CheckBox) findViewById(R.id.sunCheck);
        sunCheck.setChecked(days[0]);

        monCheck = (CheckBox) findViewById(R.id.monCheck);
        monCheck.setChecked(days[1]);

        tuesCheck = (CheckBox) findViewById(R.id.tuesCheck);
        tuesCheck.setChecked(days[2]);

        wedCheck = (CheckBox) findViewById(R.id.wedCheck);
        wedCheck.setChecked(days[3]);

        thursCheck = (CheckBox) findViewById(R.id.thursCheck);
        thursCheck.setChecked(days[4]);

        friCheck = (CheckBox) findViewById(R.id.friCheck);
        friCheck.setChecked(days[5]);

        satCheck = (CheckBox) findViewById(R.id.satCheck);
        satCheck.setChecked(days[0]);

        //when finished editing or creating, click this button to save changes
        saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editedDays[0] = sunCheck.isChecked();
                editedDays[1] = monCheck.isChecked();
                editedDays[2] = tuesCheck.isChecked();
                editedDays[3] = wedCheck.isChecked();
                editedDays[4] = thursCheck.isChecked();
                editedDays[5] = friCheck.isChecked();
                editedDays[6] = satCheck.isChecked();

                Medicine tempMedicine = new Medicine(editMedName.getText().toString(), time[0], time[1], alarmRequestCode, editedDays);
                Log.d(TAG, ""+ tempMedicine);

                Intent editedMedicine = new Intent();
                editedMedicine.putExtra("editedMedicine", tempMedicine);

                setResult(RESULT_OK,editedMedicine);
                finish();
            }
        });
    }

}
