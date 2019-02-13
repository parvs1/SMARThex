package com.example.shrey.medremindertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, FullscreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("medicineToAlert", intent.getSerializableExtra("medicineToAlert"));
        context.startActivity(i);
    }
}
