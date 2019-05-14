package com.example.medication_app;

import java.io.Serializable;

public class Medicine implements Serializable {

    public String medicineName; //name of medicine
    public String hour; //hour dose is taken
    public String minute;   //minute dose is taken
    public int alarmRequestCode;
    public boolean[] days; //days in between doses

    public Medicine()
    {
        medicineName = "";
        hour = "00";
        minute = "00";
        alarmRequestCode = 0;
        days = new boolean[7];
    }


    public Medicine (String mName, String h, String m, int arc, boolean[] d)
    {
        medicineName = mName;
        hour = h;
        minute = m;
        alarmRequestCode = arc;
        days = d;
    }

    public String toString()
    {
        //unless it is original place holder, make each item for listview read 'medicineName; taken every n days

        if (medicineName.equals("Add an alarm!"))
            return medicineName;
        else {
            String description = "" + medicineName + "     " + hour + ":" + minute + "     ";
            if(days[0])
                description += "Sun. ";
            if(days[1])
                description += "Mon. ";
            if(days[2])
                description += "Tues. ";
            if(days[3])
                description += "Wed. ";
            if(days[4])
                description += "Thurs. ";
            if(days[5])
                description += "Fri. ";
            if(days[6])
                description += "Sat. ";

            return description;
        }
    }

}