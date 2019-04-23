package com.example.medication_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsContacts extends AppCompatActivity {

    ListView listView ;
    ArrayList<String> StoreContacts ;
    final int REQUEST_CODE = 1113;
    ArrayAdapter<String> arrayAdapter ;
    Cursor cursor ;
    String name, number,email ;
    public  static final int RequestPermissionCode  = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_contacts);
        listView = (ListView)findViewById(R.id.contactslist);

        StoreContacts = new ArrayList<String>();
        EnableRuntimePermission();

        GetContactsIntoArrayList(StoreContacts);

        arrayAdapter = new ArrayAdapter<>(SettingsContacts.this,android.R.layout.simple_expandable_list_item_1, StoreContacts);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(SettingsContacts.this, EmergencyContactDetail.class);
                intent.putExtra("itemNme",((TextView)view).getText());
                intent.putExtra("contactobject", listView.getItemIdAtPosition(position));
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }
    public void GetContactsIntoArrayList(ArrayList<String> StoreContacts)
    {
        cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);

        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            Contact c = new Contact(name,number,email);
            StoreContacts.add(name);
        }

        cursor.close();

    }

    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                SettingsContacts.this,
                Manifest.permission.READ_CONTACTS))
        {

            Toast.makeText(SettingsContacts.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(SettingsContacts.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }
    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult)
    {

        switch (RC)
        {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(SettingsContacts.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(SettingsContacts.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }


}
