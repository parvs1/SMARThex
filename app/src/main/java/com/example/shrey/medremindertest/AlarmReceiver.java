package com.example.shrey.medremindertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage

    @Override
    public void onReceive(Context context, Intent intent) {

        Medicine temp = (Medicine)intent.getSerializableExtra("medicineToAlert");
        Log.i(TAG, "Starting alarm activity for " + temp.medicineName);

        Intent i = new Intent(context, FullscreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("medicineToAlert", temp);
        context.startActivity(i);
    }
}
