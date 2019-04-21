package com.example.medication_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    public final int REQUEST_CODE = 99; //code for starting editMedicine Activity and obtaining its result
    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage
    public final String CHANNEL_ID = "0";
    public final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        requestPermissions();


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

            medSchedule.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                   //To do: cancel the alarm the user just deleted

                    medicines.remove(position);

                    //update list, file, and alarms
                    adapter.notifyDataSetChanged();
                    updateFile();
                    setAlarms();

                    return true;
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
            updateFile();
        }
    }

    public void updateFile(){
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

    public void setAlarms() {
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        for (int i = 0; i < medicines.size(); i++){
            Medicine temp = medicines.get(i);

            Intent alarmReceiver = new Intent(MainActivity.this, Alarm1Receiver.class);
            alarmReceiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            alarmReceiver.putExtra("nameToAlert", temp.medicineName);
            alarmReceiver.putExtra("requestCode", i);

            //Lets the other application continue the process as if we are owning it
            alarmIntent = PendingIntent.getBroadcast(MainActivity.this, i, alarmReceiver, PendingIntent.FLAG_UPDATE_CURRENT);


            // Set the alarm to start at the Medicine time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(temp.minute));

            //Repeat the alarm for the specified frequency of days
            alarmMgr.setInexactRepeating(alarmMgr.RTC_WAKEUP, calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY * temp.frequency, alarmIntent);

            Log.i(TAG,"Set up " + temp.medicineName + "'s alarm.");
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestPermissions(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                Toast.makeText(this, "This app need access to the SMS Permission in order to text your selected emergency contact! Please grant access!", Toast.LENGTH_LONG);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                Toast.makeText(this, "This app need access to the Call Phone Permission in order to call your selected emergency contact! Please grant access!", Toast.LENGTH_LONG);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    requestPermissions();
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    requestPermissions();
                }
                return;
            }
        }
    }

}