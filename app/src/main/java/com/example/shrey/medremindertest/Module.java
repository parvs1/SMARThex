package com.example.shrey.medremindertest;

import java.io.Serializable;
import java.util.ArrayList;

public class Module implements Serializable {
    public int module;
    public String medicineName;
    public ArrayList<String> times;

    public Module() {
        module = 1;
        medicineName = "-1";
        times = new ArrayList<String>();
    }

    public Module(int m, String mName, ArrayList<String> ts) {
        module = m;
        medicineName = mName;
        times = ts;
    }

    public String modBtnText() {
        return " Module " + module + " - " + medicineName + " ";
    }

    public String toString(){
        return "" + medicineName + " " + module + " || " + times.toString();
    }
}
