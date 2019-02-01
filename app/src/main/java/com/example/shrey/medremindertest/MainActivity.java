package com.example.shrey.medremindertest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    ListView medSchedule; //list view of medicines
    ArrayList<Medicine> medicines; //array list that holds medicine objects created by user
    ArrayAdapter<Medicine> adapter; //adapter for medicines array list and medSchedule listview
    FloatingActionButton addMedicine; //floating action button on MainActivity
    public final int REQUEST_CODE = 4; //code for starting editMedicine Activity and obtaining its result
    public final String TAG = "com.med-adherence"; //TAG for log usage
    private BluetoothAdapter bleadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String medicinesFileText = "";
        FileInputStream fileInputStream = null;

        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();

            // initialize the bluetooth adapter
            final BluetoothManager bleManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
            bleadapter = bleManager.getAdapter();

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

            medicines = new ArrayList<Medicine>();

            //if medicinesFileTextArray contains a medicine (min length for a medicine is 4); this only triggers if user has not created a medicine yet
            if (medicinesFileTextArray.length > 2) {

                //iterate line by line to create the arraylist of medicines
                for (int i = 0; i < medicinesFileTextArray.length; i += 4) {
                    String tempName = medicinesFileTextArray[i];
                    String tempHour = medicinesFileTextArray[i + 1];
                    String tempMin = medicinesFileTextArray[i + 2];
                    int tempFreq = Integer.parseInt(medicinesFileTextArray[i + 3]);

                    medicines.add(new Medicine(tempName, tempHour, tempMin, tempFreq));
                }
            } else
                medicines.add(new Medicine("Tap me to edit!", "00", "30", 1)); //initial placeholder text to guide user through editing a medicine for first time

            //create and set array adapter for medicines and medSchedule listview
            adapter = new ArrayAdapter<Medicine>(this, android.R.layout.simple_list_item_1, medicines);
            medSchedule = (ListView) findViewById(R.id.medSchedule);
            medSchedule.setAdapter(adapter);

            medSchedule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)/*on item click indicates an edit*/ {

                    Intent editMedicineActivity = new Intent(MainActivity.this, EditMedicineActivity.class);
                    editMedicineActivity.putExtra("medicineToEdit", medicines.get(position)); //send original medicine values as placeholders for edit activity

                    medicines.remove(position); //remove old unedited medicine

                    startActivityForResult(editMedicineActivity, REQUEST_CODE);
                }
            });

            //create floating action button that adds a new medicine to the medicines list when clicked on
            FloatingActionButton addMedicine = (FloatingActionButton) findViewById(R.id.addMedicine);
            addMedicine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editMedicineActivity = new Intent(MainActivity.this, EditMedicineActivity.class);
                    editMedicineActivity.putExtra("medicineToEdit", new Medicine("temp", "-1", "-1")); //provide placeholders so we can use EditMedicineActivity instead of creating a redundant new one

                    startActivityForResult(editMedicineActivity, REQUEST_CODE);
                }
            });
        }
    }

        //Runs when returning from EditMedicineActivity (after creating or editing a medicine)
        @Override
        protected void onActivityResult ( int requestCode, int resultCode, Intent data){
            // REQUEST_CODE is defined above
            if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

                //add medicine received from activity (if an edit, we already removed the original one)
                Medicine newMedicine = (Medicine) data.getSerializableExtra("editedMedicine");
                medicines.add(newMedicine);
                adapter.notifyDataSetChanged(); //update listview on change

                setAlarms();

                //create or update file 'medicinesFile.txt'
                String filename = "medicinesFile.txt";

                String fileContents = "";

                //add each medicine from medicines list to fileContents string, separating each attribute with a new line for later iteration
                for (int i = 0; i < medicines.size(); i++) {
                    Medicine temp = medicines.get(i);
                    fileContents += temp.medicineName + "\n";
                    fileContents += temp.hour + "\n";
                    fileContents += temp.minute + "\n";
                    fileContents += temp.frequency + "\n";
                }

                FileOutputStream outputStream;

                //try creating a file 'medicinesFile.txt' with content 'fileContents'
                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
                    outputWriter.write(fileContents);
                    outputWriter.close();

                    //Log.e(TAG, "Saved as..." + fileContents); //uncomment to read file if need be

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }



    public void setAlarms() {
        Log.e(TAG, "Started creating the alarms.");

        for (int i = 0; i < medicines.size(); i++){
            Medicine temp = medicines.get(i);

            alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
            intent.putExtra("medicineToAlert",temp);

            //Lets the other application continue the process as if we are owning it
            alarmIntent = PendingIntent.getBroadcast(MainActivity.this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Set the alarm to start at the Medicine time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(temp.minute));

            //Repeat the alarm for the specified frequency of days
            alarmMgr.setInexactRepeating(alarmMgr.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY * temp.frequency, alarmIntent);

            Log.e(TAG,"Set up " + temp.medicineName + "'s alarm.");
        }
    }

}
