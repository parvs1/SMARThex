package com.example.medication_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Alarm4Receiver extends BroadcastReceiver {

    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage

    @Override
    public void onReceive(Context context, Intent intent) {
        //get name of medicine
        String nameToAlert = intent.getStringExtra("nameToAlert");
        Log.i(TAG, "Starting alarm level 4 activity for " + nameToAlert);

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

        //Contact emergency contact
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.setData(Uri.parse("tel:" + phoneNo));
    }
}
