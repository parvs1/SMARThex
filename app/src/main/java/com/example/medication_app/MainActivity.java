package com.example.medication_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    ListView medSchedule; //list view of medicines
    ArrayList<Medicine> medicines; //array list that holds medicine objects created by user
    ArrayAdapter<Medicine> adapter; //adapter for medicines array list and medSchedule listview
    FloatingActionButton addMedicine; //floating action button on MainActivity
    public final int REQUEST_CODE = 99; //code for starting editMedicine Activity and obtaining its result
    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage
    public final String CHANNEL_ID = "0";
    int positionToDelete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        String medicinesFileText = "";
        FileInputStream fileInputStream = null;

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
            for (int i = 0; i < medicinesFileTextArray.length; i+=4) {
                String tempName = medicinesFileTextArray[i];
                String tempHour = medicinesFileTextArray[i + 1];
                String tempMin = medicinesFileTextArray[i + 2];
                String daysString = medicinesFileTextArray[i + 3];

                String[] daysArray = daysString.split(",");

                boolean[] days = new boolean[7];
                for(int day = 0; day < days.length; day++)
                    days[day] = Boolean.parseBoolean(daysArray[day]);

                medicines.add(new Medicine(tempName, tempHour, tempMin, days));
            }
        }
        else {
            boolean[] days = new boolean[7];
            medicines.add(new Medicine("Add new alarms using the FAB below or tap existing ones to edit!", "00", "30", days)); //initial placeholder text to guide user through editing a medicine for first time
        }


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

        medSchedule.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Delete this Alarm?");
                alertDialog.setMessage("Are you sure you want to delete this alarm for " + medicines.get(position).medicineName + "?");
                positionToDelete = position;
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Intent alarmReceiver = new Intent(MainActivity.this, Alarm1Receiver.class);
                                alarmIntent = PendingIntent.getBroadcast(MainActivity.this, positionToDelete, alarmReceiver, PendingIntent.FLAG_CANCEL_CURRENT);
                                alarmManager.cancel(alarmIntent);


                                medicines.remove(positionToDelete);

                                //update list, file, and reset the alarms with the updated list
                                adapter.notifyDataSetChanged();
                                updateFile();
                            }
                        });
                alertDialog.show();

                return true;
            }
        });

        //create floating action button that adds a new medicine to the medicines list when clicked on
        addMedicine = (FloatingActionButton) findViewById(R.id.addMedicine);
        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editMedicineActivity = new Intent(MainActivity.this, EditMedicineActivity.class);
                boolean days[] = new boolean[7];
                editMedicineActivity.putExtra("medicineToEdit", new Medicine("te425252621afawetmp", "00", "00", days)); //provide placeholders so we can use EditMedicineActivity instead of creating a redundant new one

                startActivityForResult(editMedicineActivity, REQUEST_CODE);
            }
        });
    }


    //Runs when returning from EditMedicineActivity (after creating or editing a medicine)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            //add medicine received from activity (if an edit, we already removed the original one)
            Medicine newMedicine = (Medicine) data.getSerializableExtra("editedMedicine");
            medicines.add(newMedicine);

            adapter.notifyDataSetChanged(); //update listview on change

            setAlarms();
            updateFile();
        }
    }

    public void updateFile() {
        //create or update file 'medicinesFile.txt'
        String filename = "medicinesFile.txt";

        String fileContents = "";

        //add each medicine from medicines list to fileContents string, separating each attribute with a new line for later iteration
        for (int i = 0; i < medicines.size(); i++) {
            Medicine temp = medicines.get(i);
            fileContents += temp.medicineName + "\n";
            fileContents += temp.hour + "\n";
            fileContents += temp.minute + "\n";


            for(int day = 0; day < temp.days.length; day++)
                fileContents += temp.days[day] + ",";

            fileContents+="\n";
        }

        FileOutputStream outputStream;

        //try creating a file 'medicinesFile.txt' with content 'fileContents'
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(outputStream);
            outputWriter.write(fileContents);
            outputWriter.close();

            //Log.e(TAG, "Saved as:" + "\n" + fileContents); //uncomment to read file if need be

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAlarms() {

        for (int i = 0; i < medicines.size(); i++) {
            Medicine temp = medicines.get(i);

            Intent alarmReceiver = new Intent(MainActivity.this, Alarm1Receiver.class);
            alarmReceiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            alarmReceiver.putExtra("nameToAlert", temp.medicineName);
            alarmReceiver.putExtra("requestCode", i);

            //Lets the other application continue the process as if we are owning it
            alarmIntent = PendingIntent.getBroadcast(MainActivity.this, i, alarmReceiver, PendingIntent.FLAG_CANCEL_CURRENT);

            // Set the alarm to start at the Medicine time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(temp.minute));

            boolean[] days = temp.days;

            //Sunday
            if(days[0]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Monday
            if(days[1]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Tuesday
            if(days[2]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Wednesday
            if(days[3]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Thursday
            if(days[4]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Friday
            if(days[5]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Saturday
            if(days[6]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

        }
    }


}