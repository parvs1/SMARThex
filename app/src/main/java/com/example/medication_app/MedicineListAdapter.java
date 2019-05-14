package com.example.medication_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicineListAdapter extends ArrayAdapter<Medicine> {

    private LayoutInflater mLayoutInflater;
    private ArrayList<Medicine> medicines;
    private int mViewResourceId;

    public MedicineListAdapter(Context context, int resId, ArrayList<Medicine> meds){
        super(context, resId, meds);
        medicines = meds;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = resId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Medicine temp = medicines.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm, parent, false);
        }


        if (temp != null) {
            TextView alarmMedName = (TextView) convertView.findViewById(R.id.alarmMedName);

            if (alarmMedName != null) {
                alarmMedName.setText(temp.medicineName);
            }

            TextView alarmTime = (TextView) convertView.findViewById(R.id.alarmTime);

            if(alarmTime != null) {
                String time = "" + Integer.parseInt(temp.hour)%12 + ":" + temp.minute;

                if(Integer.parseInt(temp.hour) < 12 && Integer.parseInt(temp.hour) > 0)
                    time += "AM";
                else if(Integer.parseInt(temp.hour) > 12)
                    time += "PM";
                else if (Integer.parseInt(temp.hour) == 0)
                    time = "12:" + temp.minute + "AM";
                else //hour = 12 noon
                    time = "12:" + temp.minute + "PM";

                alarmTime.setText(time);
            }

            TextView alarmDays = (TextView) convertView.findViewById(R.id.alarmDays);

            if(alarmDays != null) {
                String days = "";
                if(temp.days[0])
                    days+= "S ";
                if(temp.days[1])
                    days+= "M ";
                if(temp.days[2])
                    days+= "T ";
                if(temp.days[3])
                    days+= "W ";
                if(temp.days[4])
                    days+= "Th ";
                if(temp.days[5])
                    days+= "F ";
                if(temp.days[6])
                    days+= "Sa ";

                alarmDays.setText(days);
            }
        }

        return convertView;
    }

}
