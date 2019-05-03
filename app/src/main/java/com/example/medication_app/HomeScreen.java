package com.example.medication_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;

public class HomeScreen extends AppCompatActivity {
    Button editAlarms;
    Button editMedSchedule;
    Button settings;
    Button dashboard;
    private AlarmManager alarmManager;
    public final int PERMISSION_ALL = 3;
    String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public final String CHANNEL_ID = "0";
    public String nfcID;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    NfcAdapter mNfcAdapter;
    public final String TAG = "MEDICATION_ADHERENCE";//TAG for log usage


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        createNotificationChannel();

        editAlarms = (Button) findViewById(R.id.edalarms);
        editAlarms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(i);
            }
        });

        FileInputStream fileInputStream = null;
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

    settings = (Button) findViewById(R.id.settings);
    settings.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){
        Intent i = new Intent(HomeScreen.this, SettingsActivity.class);
        startActivity(i);

    }
    });

    dashboard = (Button) findViewById(R.id.dashboard);

    dashboard.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                Intent i = new Intent(HomeScreen.this, Dashboard.class);
                startActivity(i);

            }
        });
        editMedSchedule = (Button) findViewById(R.id.edSchedule);
        editMedSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeScreen.this, MedicineSchedule.class);
                startActivity(i);
            }
        });

        handleIntent(getIntent());

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

                Intent Level2Receiver = new Intent(getApplicationContext(), Alarm2Receiver.class);
                boolean isWorking = (PendingIntent.getBroadcast(getApplicationContext(), 992, Level2Receiver, PendingIntent.FLAG_ONE_SHOT)) != null; //check if Level 2 alarm is active

                if (getTextFromTag(tag).equals(nfcID) && isWorking) {

                    Toast.makeText(this, "Confirmed! Thank you for taking your medication!", Toast.LENGTH_LONG).show();

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.baseline_alarm_off_24)
                            .setContentTitle("Thanks for taking your medicine!")
                            .setContentText("Alarms for latest medicine have been cancelled.")
                            .setPriority(NotificationCompat.PRIORITY_MIN);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                    // notificationId is a unique int for each notification that you must define
                    notificationManager.notify(111, builder.build());

                    //Cancel Level 2
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
                else if (getTextFromTag(tag).equals(nfcID) && !isWorking)
                    Toast.makeText(this, "No alarms have triggered yet. No need to take any medicine right now.", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, "Wrong NFC Tag ID. If you think this is a mistake, then you can change the NFC ID in the SmartHex app settings.", Toast.LENGTH_LONG).show();

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
