package com.example.medication_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

public class Alarm3Receiver extends BroadcastReceiver {

    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage
    AlarmManager alarmManager;
    PendingIntent Level4Intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        //get name of medicine
        String nameToAlert = intent.getStringExtra("nameToAlert");
        Log.i(TAG, "Starting alarm level 3 activity for " + nameToAlert);

        SmsManager smsManager = SmsManager.getDefault();

        // TO DO: Make setting to set this phone number
        String phoneNo = "4125763105";

        String smsMessage = "SmartHex is notifying you that " + nameToAlert + " wasn't taken.";
        //replace first parameter when setting implemented
        smsManager.sendTextMessage(phoneNo, null, smsMessage, null, null);

        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent Level4Receiver = new Intent(context, Alarm4Receiver.class);
        Level4Receiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Level4Receiver.putExtra("nameToAlert", nameToAlert);

        Level4Intent = PendingIntent.getBroadcast(context, 994, Level4Receiver, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set Level 4 to start in 5 minutes
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (5*60*1000), Level4Intent);
    }
}
