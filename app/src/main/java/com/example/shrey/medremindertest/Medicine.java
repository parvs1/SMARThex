package com.example.shrey.medremindertest;

import java.io.Serializable;

public class Medicine implements Serializable {

    public String medicineName;
    public String hour;
    public String minute;
    public int frequency;//days in between doses

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
