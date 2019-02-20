package com.example.medication_app;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity
{
    private UARTConnection uartConnection;

    ArrayList<Module> modules;
    Button moduleBtn1;
    Button moduleBtn2;
    Button moduleBtn3;
    Button moduleBtn4;
    Button moduleBtn5;
    Button moduleBtn6;
    int REQUEST_CODE = 99;
    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        modules = new ArrayList<Module>();

        ArrayList<String> testTimesMod1 = new ArrayList<String>();
        testTimesMod1.add("1:40");
        modules.add(new Module(1, "Lonzo", testTimesMod1));

        ArrayList<String> testTimesMod2 = new ArrayList<String>();
        testTimesMod2.add("13:40");
        testTimesMod2.add("14:02");
        modules.add(new Module(2, "Russ", testTimesMod2));

        ArrayList<String> testTimesMod3 = new ArrayList<String>();
        testTimesMod3.add("10:00");
        modules.add(new Module(3, "PG", testTimesMod3));

        ArrayList<String> testTimesMod4 = new ArrayList<String>();
        testTimesMod4.add("15:50");
        modules.add(new Module(4, "Steven Adams", testTimesMod4));

        ArrayList<String> testTimesMod5 = new ArrayList<String>();
        testTimesMod5.add("1:40");
        modules.add(new Module(5, "Test", testTimesMod5));

        ArrayList<String> testTimesMod6 = new ArrayList<String>();
        testTimesMod6.add("1:40");
        testTimesMod6.add("5:50");
        modules.add(new Module(6, "PlaceHolder", testTimesMod6));

        moduleBtn1 = (Button)findViewById(R.id.moduleBtn1);
        moduleBtn1.setText(modules.get(0).modBtnText());
        moduleBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(0)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE);
            }
        });

        moduleBtn2 = (Button)findViewById(R.id.moduleBtn2);
        moduleBtn2.setText(modules.get(1).modBtnText());
        moduleBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(1)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE);
            }
        });


        moduleBtn3 = (Button)findViewById(R.id.moduleBtn3);
        moduleBtn3.setText(modules.get(2).modBtnText());
        moduleBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(2)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE);
            }
        });


        moduleBtn4 = (Button)findViewById(R.id.moduleBtn4);
        moduleBtn4.setText(modules.get(3).modBtnText());
        moduleBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(3)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE);
            }
        });


        moduleBtn5 = (Button)findViewById(R.id.moduleBtn5);
        moduleBtn5.setText(modules.get(4).modBtnText());
        moduleBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(4)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE);
            }
        });


        moduleBtn6 = (Button)findViewById(R.id.moduleBtn6);
        moduleBtn6.setText(modules.get(5).modBtnText());
        moduleBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(5)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE);
            }
        });

        Button sendDataButton = (Button)findViewById(R.id.sendDataButton);
        sendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendData();
            }
        });

    }

    public void startConnection(BluetoothDevice bluetoothDevice) {
        // TODO make sure any current flutter/connection is properly closed?
        this.uartConnection = new UARTConnection(getApplicationContext(), bluetoothDevice, Constants.FLUTTER_UART_SETTINGS);
        this.uartConnection.addRxDataListener(new UARTConnection.RXDataListener() {
            @Override
            public void onRXData(byte[] newData) {

            }
        });
    }

    /**
     * Send a BLE message to the currently-connected Flutter
     * @param bytes the message to be sent
     * @return true if bytes are successfully written to the UART connection, false otherwise
     */
    public synchronized boolean sendMessage(byte[] bytes) {
        if (this.uartConnection == null) {
            Log.e(Constants.LOG_TAG, "requested sendMessage with null uartConnection");
            return false;
        }
        return this.uartConnection.writeBytes(bytes);
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            //add medicine received from activity (if an edit, we already removed the original one)
            Module newModule = (Module) data.getSerializableExtra("editedModule");
            modules.set(newModule.module - 1, newModule);


            if (newModule.module == 1)
                moduleBtn1.setText(modules.get(0).modBtnText());
            if (newModule.module == 2)
                moduleBtn2.setText(modules.get(1).modBtnText());
            if (newModule.module == 3)
                moduleBtn3.setText(modules.get(2).modBtnText());
            if (newModule.module == 4)
                moduleBtn4.setText(modules.get(3).modBtnText());
            if (newModule.module == 5)
                moduleBtn5.setText(modules.get(4).modBtnText());
            if (newModule.module == 6)
                moduleBtn6.setText(modules.get(5).modBtnText());
        }
    }
}
