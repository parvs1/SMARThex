package com.example.medication_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Calendar;

public class Alarm1Receiver extends BroadcastReceiver
{
	public final String CHANNEL_ID = "0";
	public final int notificationLevel1Id = 0;
	public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage
    AlarmManager alarmManager;
    PendingIntent Level2Intent;

	@Override
	public void onReceive(Context context, Intent intent)
	{
	    //get name of medicine
		String nameToAlert = intent.getStringExtra("nameToAlert");
		Log.i(TAG, "Starting alarm level 1 activity for " + nameToAlert);

		//create and execute notification
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.baseline_notification_important_24)
				.setContentTitle("Take " + nameToAlert)
				.setContentText("It is time for you to take " + nameToAlert)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
				.setPriority(NotificationCompat.PRIORITY_HIGH);

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		// notificationId is a unique int for each notification that you must define
		notificationManager.notify(notificationLevel1Id, builder.build());

		alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent Level2Receiver = new Intent(context, Alarm2Receiver.class);
        Level2Receiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Level2Receiver.putExtra("nameToAlert", nameToAlert);

        Level2Intent = PendingIntent.getBroadcast(context, 992, Level2Receiver, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set Level 2 to start in 5 minutes
        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (5*60*1000), Level2Intent);
	}

	//activate when in contact with NFC TAG
	public void cancelAlarm(){
	    alarmManager.cancel(Level2Intent);
    }
}
