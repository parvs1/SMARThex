package com.example.medication_app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    Button contactsButton;
    Button nfcButton;
    Button resetButton;
    NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public final int PICK_CONTACT = 4;
    public final String TAG = "MEDICATION_ADHERENCE";//TAG for log usage
    ArrayList<Medicine> medicines;
    private PendingIntent alarmIntent;
    private AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        contactsButton = (Button) findViewById(R.id.contactButton);
        nfcButton = (Button) findViewById(R.id.nfcButton);
        resetButton = (Button) findViewById(R.id.resetAlarmsButton);

        FileInputStream fileInputStream = null;
        String emergencyContactFileText = "";

        try {
            fileInputStream = openFileInput("emergencyContact.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] buffer = new char[1024];
            int charRead;

            //creates string that contains text from 'emergencyContact.txt'
            while ((charRead = inputStreamReader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charRead);
                emergencyContactFileText += readString;
            }

            String buttonText = "         Emergency Contact Set to: " + emergencyContactFileText;
            contactsButton.setText(buttonText);

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

            String buttonText = "         NFC ID: " + nfcIDFileText;
            nfcButton.setText(buttonText);

        } catch (Exception e) {
            e.printStackTrace();
        }


        contactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle("Reset All Alarms?");
                alertDialog.setMessage("Are you sure you want to reset all your alarms? Alarms that already occured may be triggered again.");

                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Reset Alarms",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "Resetting Alarms.");
                                setAlarms();
                            }
                        });
                alertDialog.show();
            }
        });

        handleIntent(getIntent());
    }

    public void setAlarms(){
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
            for (int i = 0; i < medicinesFileTextArray.length; i+=5) {
                String tempName = medicinesFileTextArray[i];
                String tempHour = medicinesFileTextArray[i + 1];
                String tempMin = medicinesFileTextArray[i + 2];
                int tempAlarmRequestCode = Integer.parseInt(medicinesFileTextArray[i+3]);
                String daysString = medicinesFileTextArray[i + 4];

                String[] daysArray = daysString.split(",");

                boolean[] days = new boolean[7];
                for(int day = 0; day < days.length; day++)
                    days[day] = Boolean.parseBoolean(daysArray[day]);

                medicines.add(new Medicine(tempName, tempHour, tempMin, tempAlarmRequestCode, days));
            }
        }
        
        for (int i = 0; i < medicines.size(); i++) {
            Medicine temp = medicines.get(i);

            Intent alarmReceiver = new Intent(this, Alarm1Receiver.class);
            alarmReceiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            alarmReceiver.putExtra("nameToAlert", temp.medicineName);
            alarmReceiver.putExtra("requestCode", temp.alarmRequestCode);

            //Lets the other application continue the process as if we are owning it
            alarmIntent = PendingIntent.getBroadcast(this, temp.alarmRequestCode, alarmReceiver, PendingIntent.FLAG_CANCEL_CURRENT);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_CONTACT)
        {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String buttonText = "Emergency Contact Set to: " + cursor.getString(column);
            contactsButton.setText(buttonText);
            updateContact(cursor.getString(column));
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

                updateNFCID(getTextFromTag(tag));

                String buttonText = "NFC ID: " + getTextFromTag(tag);
                nfcButton.setText(buttonText);
            }
        }

        else {
                //keep on going
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

    public void updateContact(String phoneNo) {
        //create or update file 'emergencyContact.txt'
        String filename = "emergencyContact.txt";

        String fileContents = phoneNo;

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

    public void updateNFCID(String nfcID)
    {
        //create or update file 'emergencyContact.txt'
        String filename = "nfcID.txt";

        String fileContents = nfcID;

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

    @Override // Essential to have onPause and onResume methods for the activity.
    protected void onPause() {
        super.onPause();

        stopForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setupForegroundDispatch(this, mNfcAdapter);
    }

}
