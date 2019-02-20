package com.example.medication_app;

import java.io.Serializable;

public class Medicine implements Serializable {

    public String medicineName; //name of medicine
    public String hour; //hour dose is taken
    public String minute;   //minute does is taken
    public int frequency; //days in between doses

    public Medicine()
    {
        medicineName = "";
        hour = "0";
        minute = "0";
        frequency = 1;
    }

    public Medicine(String mName, String h, String m)
    {
        medicineName = mName;
        hour = h;
        minute = m;
        frequency = 1;
    }


    public Medicine (String mName, String h, String m, int fr)
    {
        medicineName = mName;
        hour = h;
        minute = m;
        frequency = fr;
    }

    public String toString()
    {
        //unless it is original place holder, make each item for listview read 'medicineName; taken every n days

        if (medicineName.equals("Tap me to edit!"))
            return medicineName;
        else {
            String description = "" + medicineName + " at " + hour + ":" + minute;
            if (frequency == 1)
                description = description + "; taken every day";
            else
                description = description + "; taken every " + frequency + " days";

            return description;
        }
    }

}
