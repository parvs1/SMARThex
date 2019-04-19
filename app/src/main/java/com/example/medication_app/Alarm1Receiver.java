package com.example.medication_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class Alarm1Receiver extends BroadcastReceiver
{
	public final String CHANNEL_ID = "0";
	public final int notificationId = 0;
	public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage

	@Override
	public void onReceive(Context context, Intent intent)
	{
		String nameToAlert = intent.getStringExtra("nameToAlert");
		Log.i(TAG, "Starting alarm level 1 activity for " + nameToAlert);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setSmallIcon(R.drawable.baseline_notification_important_24) //replace notification icon
				.setContentTitle("Take " + nameToAlert)
				.setContentText("It is time for you to take " + nameToAlert)
				.setPriority(NotificationCompat.PRIORITY_HIGH);

		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		// notificationId is a unique int for each notification that you must define
		notificationManager.notify(notificationId, builder.build());

		/*
		Intent i = new Intent(context, FullscreenActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("nameToAlert", nameToAlert);
		context.startActivity(i);*/
	}
}
