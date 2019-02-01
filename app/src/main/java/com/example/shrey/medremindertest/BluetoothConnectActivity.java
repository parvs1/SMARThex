package com.example.shrey.medremindertest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BluetoothConnectActivity extends AppCompatActivity {
    private BluetoothAdapter bleadapter;
    public final String TAG = "MEDICATION_ADHERENCE";
    ListView devicesList;
    ArrayList<BluetoothDevice> devices;
    ArrayAdapter<BluetoothDevice> deviceAdapter;
    private final ScanCallback scanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            devices.add(result.getDevice());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);
        devicesList = (ListView)findViewById(R.id.deviceList);
        devices = new ArrayList<BluetoothDevice>();
        devicesList.setAdapter(deviceAdapter);
        deviceAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1,devices);



        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
        }

        final BluetoothManager bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleadapter = bleManager.getAdapter();
    }

    private void runLeScan() {
        // TODO ScanFilter https://developer.android.com/reference/android/bluetooth/le/ScanFilter
        bleadapter.getBluetoothLeScanner().startScan(scanCallback);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bleadapter.getBluetoothLeScanner().stopScan(scanCallback);
                Log.d(TAG, "Stopped Scanner");

            }
        }, 10000);
    }


}