package com.example.medication_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Contact {
    public String fullname;
    public String phoneNumber;
    public String email;

    public Contact()
    {
        fullname = "";
        phoneNumber = "";
        email = "";
    }


    public Contact(String fullname, String phonenumber, String email)
    {
        this.fullname = fullname;
        this.phoneNumber = phonenumber;
        this.email = email;
    }
}
