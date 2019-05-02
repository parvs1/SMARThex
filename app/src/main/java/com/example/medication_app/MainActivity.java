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
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public final String CHANNEL_ID = "0";
    public final int PERMISSION_ALL = 3;
    String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS
    };

    public String nfcID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        createNotificationChannel();

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

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

        String nfcIDFileText = "";

        try {
            fileInputStream = openFileInput("nfcID.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] buffer = new char[1024];
            int charRead;

            //creates string that contains text from 'nfcID.txt'
            while ((charRead = inputStreamReader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charRead);
                nfcIDFileText += readString;
            }

            nfcID = nfcIDFileText;

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
                String daysArray = medicinesFileTextArray[i + 3];

                String[] daysString = daysArray.split(",");

                boolean[] days = new boolean[7];
                for(int day = 0; day < days.length; day++)
                    days[day] = Boolean.parseBoolean(daysString[day]);

                medicines.add(new Medicine(tempName, tempHour, tempMin, days));
            }
        }
        else {
            boolean[] days = new boolean[7];
            medicines.add(new Medicine("Tap me to edit!", "00", "30", days)); //initial placeholder text to guide user through editing a medicine for first time
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
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent alarmReceiver = new Intent(MainActivity.this, Alarm1Receiver.class);
                alarmIntent = PendingIntent.getBroadcast(MainActivity.this, medicines.size() - 1, alarmReceiver, PendingIntent.FLAG_ONE_SHOT); //delete last alarm on the list (preceding ones will be replaced)

                alarmManager.cancel(alarmIntent);

                medicines.remove(position);

                //update list, file, and alarms
                adapter.notifyDataSetChanged();
                updateFile();
                setAlarms();

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

        handleIntent(getIntent());
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
                Log.i(TAG, "Created alarm for " + temp.medicineName + " at " + temp.hour + ":" + temp.minute + " on Sunday.");
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Monday
            if(days[1]) {
                Log.i(TAG, "Created alarm for " + temp.medicineName + " at " + temp.hour + ":" + temp.minute + " on Monday.");
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Tuesday
            if(days[2]) {
                Log.i(TAG, "Created alarm for " + temp.medicineName + " at " + temp.hour + ":" + temp.minute + " on Tuesday.");
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Wednesday
            if(days[3]) {
                Log.i(TAG, "Created alarm for " + temp.medicineName + " at " + temp.hour + ":" + temp.minute + " on Wednesday.");
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Thursday
            if(days[4]) {
                Log.i(TAG, "Created alarm for " + temp.medicineName + " at " + temp.hour + ":" + temp.minute + " on Thursday.");
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Friday
            if(days[5]) {
                Log.i(TAG, "Created alarm for " + temp.medicineName + " at " + temp.hour + ":" + temp.minute + " on Friday.");
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Saturday
            if(days[6]) {
                Log.i(TAG, "Created alarm for " + temp.medicineName + " at " + temp.hour + ":" + temp.minute + " on Saturday.");
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public static void setupForegroundDispatch(Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (getTextFromTag(tag).equals(nfcID)) { //Checking if the NFC Tag is what we set out for it to be. If so, then that means the tag is activated.

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.baseline_alarm_off_24)
                            .setContentTitle("Thanks for taking your medicine!")
                            .setContentText("Alarms for latest medicine have been cancelled.")
                            .setPriority(NotificationCompat.PRIORITY_MIN);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(111, builder.build());

                    //Cancel Level 2
                    Intent Level2Receiver = new Intent(getApplicationContext(), Alarm2Receiver.class);
                    PendingIntent Level2Intent = PendingIntent.getBroadcast(
                            getApplicationContext(), 992, Level2Receiver,
                            PendingIntent.FLAG_ONE_SHOT);

                    alarmManager.cancel(Level2Intent);

                    //Cancel Level 3
                    Intent Level3Receiver = new Intent(getApplicationContext(), Alarm3Receiver.class);
                    PendingIntent Level3Intent = PendingIntent.getBroadcast(
                            getApplicationContext(), 993, Level3Receiver,
                            PendingIntent.FLAG_ONE_SHOT);

                    alarmManager.cancel(Level3Intent);

                    //Cancel Level 4
                    Intent Level4Receiver = new Intent(getApplicationContext(), Alarm4Receiver.class);
                    PendingIntent Level4Intent = PendingIntent.getBroadcast(
                            getApplicationContext(), 994, Level4Receiver,
                            PendingIntent.FLAG_ONE_SHOT);

                    alarmManager.cancel(Level4Intent);
                }
            } else {
                //keep on going
            }
        }
    }


    protected String getTextFromTag(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                }
            }
        }

        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}