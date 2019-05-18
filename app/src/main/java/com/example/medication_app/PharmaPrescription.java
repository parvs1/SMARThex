package com.example.medication_app;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PharmaPrescription extends AppCompatActivity {
    Button prescription;
    EditText receivedText;
    ArrayList<Medicine> medicines;
    String aa;
    public final String CHANNEL_ID = "0";
    public final int notificationLevel1Id = 0;
    public String medname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();

        setContentView(R.layout.activity_pharma_prescription);
        medicines = new ArrayList<Medicine>();
        getMedicinesfromFile();

        prescription = findViewById(R.id.button);
         receivedText = findViewById(R.id.editMedName);

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

        final Spinner medicineSelector = (Spinner) findViewById(R.id.medicineSelector);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(PharmaPrescription.this, android.R.layout.simple_spinner_item, medNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medicineSelector.setAdapter(spinnerAdapter);

         medicineSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 medname = spinnerAdapter.getItem(position);
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
         });





        prescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentText = "Your request for " + medname + " has been sent to your doctor!";

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                        .setSmallIcon(R.drawable.baseline_notification_important_24)
                        .setContentText(contentText)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(notificationLevel1Id, builder.build());

            }
        });
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = getString(R.string.channel_name);
        String description = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
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

        if(medicinesFileTextArray.length < 5) {
            setResult(RESULT_CANCELED);
            Toast.makeText(this, "Create a medicine first!", Toast.LENGTH_LONG).show();
            finish();
        }
        else {
            for (int i = 0; i < medicinesFileTextArray.length; i += 5) {
                String tempName = medicinesFileTextArray[i];
                String tempHour = medicinesFileTextArray[i + 1];
                String tempMin = medicinesFileTextArray[i + 2];
                int tempAlarmRequestCode = Integer.parseInt(medicinesFileTextArray[i + 3]);
                String daysString = medicinesFileTextArray[i + 4];

                String[] daysArray = daysString.split(",");

                boolean[] days = new boolean[7];
                for (int day = 0; day < days.length; day++)
                    days[day] = Boolean.parseBoolean(daysArray[day]);

                medicines.add(new Medicine(tempName, tempHour, tempMin, tempAlarmRequestCode, days));
            }
        }
    }


}
