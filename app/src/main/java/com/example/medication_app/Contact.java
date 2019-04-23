package com.example.medication_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Contact extends AppCompatActivity {
    public String fullname;
    public String phoneNumber;
    public String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }

    public Contact(String fullname, String phonenumber, String email)
    {
        this.fullname = fullname;
        this.phoneNumber = phonenumber;
        this.email = email;
    }
}
