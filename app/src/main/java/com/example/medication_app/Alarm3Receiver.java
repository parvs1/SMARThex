package com.example.medication_app;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Alarm3Receiver extends BroadcastReceiver {

    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage
    AlarmManager alarmManager;
    PendingIntent Level4Intent;


    @Override
    public void onReceive(Context context, Intent intent) {
        //get name of medicine
        String nameToAlert = intent.getStringExtra("nameToAlert");
        Log.i(TAG, "Starting alarm level 3 activity for " + nameToAlert);

        //get requestCode
        int requestCode = intent.getIntExtra("requestCode", 0);


        String phoneNo = "";
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = context.openFileInput("emergencyContact.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            char[] buffer = new char[1024];
            int charRead;

            //creates string that contains text from 'emergencyContact.txt'
            while ((charRead = inputStreamReader.read(buffer)) > 0) {
                String readString = String.copyValueOf(buffer, 0, charRead);
                phoneNo += readString;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String smsMessage = "SmartHex is notifying you that " + nameToAlert + " wasn't taken yet.";

        SmsManager.getDefault().sendTextMessage(phoneNo, null, smsMessage, null, null);

        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent Level4Receiver = new Intent(context, Alarm4Receiver.class);
        Level4Receiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Level4Receiver.putExtra("nameToAlert", nameToAlert);
        Level4Receiver.putExtra("requestCode", requestCode);

        Level4Intent = PendingIntent.getBroadcast(context, 994, Level4Receiver, PendingIntent.FLAG_ONE_SHOT);

        // Set Level 4 to start in 5 minutes
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + (5*60*1000), Level4Intent);
    }
}
