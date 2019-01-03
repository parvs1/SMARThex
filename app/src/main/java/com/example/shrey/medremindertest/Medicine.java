package com.example.shrey.medremindertest;

public class Medicine {

    public String medicineName;
    public int hour;
    public int minute;
    public int frequency;//days in between doses

    public Medicine()
    {
        medicineName = "";
        hour = 0;
        minute = 0;
        frequency = 1;
    }

    public Medicine(String mName, int h, int m)
    {
        medicineName = mName;
        hour = h;
        minute = m;
        frequency = 1;
    }


    public Medicine (String mName, int h, int m, int fr)
    {
        medicineName = mName;
        hour = h;
        minute = m;
        frequency = fr;
    }

    public String toString()
    {
        return "" + medicineName + " - " + hour + ":" + minute;
    }
}
