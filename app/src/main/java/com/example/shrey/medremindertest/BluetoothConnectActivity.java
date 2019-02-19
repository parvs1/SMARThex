package com.example.shrey.medremindertest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BluetoothConnectActivity extends AppCompatActivity {
    public final String TAG = "MEDICATION_ADHERENCE";

    BluetoothManager BluetoothManager;
    BluetoothAdapter bleAdapter;
    ListView devicesList;
    ArrayList<BluetoothDevice> devices;
    ArrayAdapter<BluetoothDevice> deviceAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connect);
        devicesList = (ListView)findViewById(R.id.devicesList);
        devices = new ArrayList<BluetoothDevice>();

        deviceAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1,devices);
        devicesList.setAdapter(deviceAdapter);

        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice temp = devices.get(position);


            }
        });

        // Register for broadcasts when a device is discovered.

        BluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bleAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bleAdapter.isDiscovering())
            bleAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Found a device");

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                    Log.e(TAG,"Found LE Device!");
                    devices.add(device);
                    deviceAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

}