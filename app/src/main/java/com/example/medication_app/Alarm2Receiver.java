package com.example.medication_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Alarm2Receiver extends BroadcastReceiver {

    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage
    AlarmManager alarmManager;
    PendingIntent Level3Intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        //get name of medicine
        String nameToAlert = intent.getStringExtra("nameToAlert");
        Log.i(TAG, "Starting alarm level 2 activity for " + nameToAlert);

		Intent overlay = new Intent(context, FullscreenActivity.class);
		overlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		overlay.putExtra("nameToAlert", nameToAlert);
		context.startActivity(overlay);

        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent Level3Receiver = new Intent(context, Alarm3Receiver.class);
        Level3Receiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Level3Receiver.putExtra("nameToAlert", nameToAlert);

        Level3Intent = PendingIntent.getBroadcast(context, 993, Level3Receiver, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set Level 3 to start in 5 minutes
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (5*60*1000), Level3Intent);
    }
}
