package com.example.medication_app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    ArrayList<Medicine> medicines;
    Context context;
    private PendingIntent alarmIntent;
    private AlarmManager alarmManager;
    public final String TAG = "MEDICATION_ADHERENCE";//TAG for log usage

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            String medicinesFileText = "";
            FileInputStream fileInputStream = null;

            try {
                fileInputStream = context.openFileInput("medicinesFile.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

                char[] buffer = new char[1024];
                int charRead;

                //creates string that contains text from 'medicinesFile.txt'
                while ((charRead = inputStreamReader.read(buffer)) > 0) {
                    String readString = String.copyValueOf(buffer, 0, charRead);
                    medicinesFileText += readString;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            String[] medicinesFileTextArray = medicinesFileText.split("\n"); //split text from 'medicinesFile.txt' by line

            medicines = new ArrayList<Medicine>();

            //if medicinesFileTextArray contains a medicine (min length for a medicine is 4); this only triggers if user has not created a medicine yet
            if (medicinesFileTextArray.length > 2) {

                //iterate line by line to create the arraylist of medicines
                for (int i = 0; i < medicinesFileTextArray.length; i+=4) {
                    String tempName = medicinesFileTextArray[i];
                    String tempHour = medicinesFileTextArray[i + 1];
                    String tempMin = medicinesFileTextArray[i + 2];
                    String daysString = medicinesFileTextArray[i + 3];

                    String[] daysArray = daysString.split(",");

                    boolean[] days = new boolean[7];
                    for(int day = 0; day < days.length; day++)
                        days[day] = Boolean.parseBoolean(daysArray[day]);

                    medicines.add(new Medicine(tempName, tempHour, tempMin, days));
                }
            }
        }

        if(!medicines.isEmpty())
            setAlarms();
    }

    public void setAlarms() {

        for (int i = 0; i < medicines.size(); i++) {
            Medicine temp = medicines.get(i);

            Intent alarmReceiver = new Intent(context, Alarm1Receiver.class);
            alarmReceiver.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            alarmReceiver.putExtra("nameToAlert", temp.medicineName);
            alarmReceiver.putExtra("requestCode", i);

            //Lets the other application continue the process as if we are owning it
            alarmIntent = PendingIntent.getBroadcast(context, i, alarmReceiver, PendingIntent.FLAG_CANCEL_CURRENT);

            // Set the alarm to start at the Medicine time.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.hour));
            calendar.set(Calendar.MINUTE, Integer.parseInt(temp.minute));

            boolean[] days = temp.days;

            //Sunday
            if(days[0]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Monday
            if(days[1]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Tuesday
            if(days[2]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Wednesday
            if(days[3]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Thursday
            if(days[4]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Friday
            if(days[5]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

            //Saturday
            if(days[6]) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                Log.i(TAG, "Created alarm for " + temp.medicineName + " with calendar as " + calendar.getTime().toString());
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, alarmIntent);
            }

        }
    }
}
