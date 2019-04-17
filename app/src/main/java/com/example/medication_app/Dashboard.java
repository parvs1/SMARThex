package com.example.medication_app;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
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
    Button connectBtn;
    int REQUEST_CODE_BLECONNECT = 98;
    int REQUEST_CODE_MODEDIT = 99;
    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        modules = new ArrayList<Module>();

        ArrayList<String> testTimesMod1 = new ArrayList<String>();
        testTimesMod1.add("1:40");
        modules.add(new Module(1, "", testTimesMod1));

        ArrayList<String> testTimesMod2 = new ArrayList<String>();
        testTimesMod2.add("1:40");
        modules.add(new Module(2, "", testTimesMod2));

        ArrayList<String> testTimesMod3 = new ArrayList<String>();
        testTimesMod3.add("1:40");
        modules.add(new Module(3, "", testTimesMod3));

        ArrayList<String> testTimesMod4 = new ArrayList<String>();
        testTimesMod4.add("1:40");
        modules.add(new Module(4, "", testTimesMod4));

        ArrayList<String> testTimesMod5 = new ArrayList<String>();
        testTimesMod5.add("1:40");
        modules.add(new Module(5, "", testTimesMod5));

        ArrayList<String> testTimesMod6 = new ArrayList<String>();
        testTimesMod6.add("1:40");
        modules.add(new Module(6, "", testTimesMod6));

        moduleBtn1 = (Button)findViewById(R.id.moduleBtn1);
        moduleBtn1.setText(modules.get(0).modBtnText());
        moduleBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(0)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
            }
        });

        moduleBtn2 = (Button)findViewById(R.id.moduleBtn2);
        moduleBtn2.setText(modules.get(1).modBtnText());
        moduleBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(1)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
            }
        });


        moduleBtn3 = (Button)findViewById(R.id.moduleBtn3);
        moduleBtn3.setText(modules.get(2).modBtnText());
        moduleBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(2)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
            }
        });


        moduleBtn4 = (Button)findViewById(R.id.moduleBtn4);
        moduleBtn4.setText(modules.get(3).modBtnText());
        moduleBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(3)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
            }
        });


        moduleBtn5 = (Button)findViewById(R.id.moduleBtn5);
        moduleBtn5.setText(modules.get(4).modBtnText());
        moduleBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(4)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
            }
        });


        moduleBtn6 = (Button)findViewById(R.id.moduleBtn6);
        moduleBtn6.setText(modules.get(5).modBtnText());
        moduleBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
                editModuleActivity.putExtra("moduleToEdit", modules.get(5)); //send original medicine values as placeholders for edit activity

                startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
            }
        });

        connectBtn = (Button)findViewById(R.id.sendDataButton);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent connectActivity = new Intent(Dashboard.this, BluetoothConnectActivity.class);

                startActivityForResult(connectActivity, REQUEST_CODE_BLECONNECT);
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
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_BLECONNECT) {

            BluetoothDevice bluetoothDevice = (BluetoothDevice)data.getParcelableExtra("bluetoothDevice");
            startConnection(bluetoothDevice);

            if(uartConnection.isConnected()) {
                connectBtn.setText("Send Data");
                connectBtn.setBackgroundColor(Color.GREEN);

                connectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        byte[] result = modules.toString().getBytes();
                        Log.e(TAG, result.toString());
                        sendMessage(result);
                    }
                });
            }
        }


        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MODEDIT) {

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
