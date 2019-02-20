package com.example.shrey.medremindertest;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MintuReceiver extends BroadcastReceiver {
    public final String TAG = "MEDICATION_ADHERENCE"; //TAG for log usage

    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Found a device");

        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if(device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                Log.e(TAG,"Found LE Device!");

            }
        }
    }

}
