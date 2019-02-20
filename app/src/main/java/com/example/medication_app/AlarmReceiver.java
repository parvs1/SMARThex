package com.example.medication_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver
{
	public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage

	@Override
	public void onReceive(Context context, Intent intent)
	{

		String nameToAlert = intent.getStringExtra("nameToAlert");
		Log.i(TAG, "Starting alarm activity for " + nameToAlert);

		Intent i = new Intent(context, FullscreenActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("nameToAlert", nameToAlert);
		context.startActivity(i);
	}
}
