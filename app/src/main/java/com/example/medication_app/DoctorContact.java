package com.example.medication_app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class DoctorContact extends AppCompatActivity {

    Button choose;
    Button doc;
    EditText message;
    String univ;
    public String ballas;
    public final int PICK_CONTACT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_contact);

        choose = findViewById(R.id.choosedoc);
        doc = findViewById(R.id.doctor);
        message = findViewById(R.id.messagetodoc);

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        ballas = message.getText().toString();

        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smsMessage = message.getText().toString();

                SmsManager.getDefault().sendTextMessage(univ, null, smsMessage, null, null);

                Toast.makeText(getApplicationContext(),"Message successfully sent to doctor.", Toast.LENGTH_LONG).show();
                doc.setVisibility(View.INVISIBLE);
                finish();
            }
        });




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_CONTACT)
        {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String buttonText = "Doctor Number" + cursor.getString(column);
            choose.setText(buttonText);
            univ = cursor.getString(column);
            doc.setVisibility(View.VISIBLE);
        }
    }
}
