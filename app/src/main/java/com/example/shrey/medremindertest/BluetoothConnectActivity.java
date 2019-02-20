package com.example.shrey.medremindertest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class BluetoothConnectActivity extends AppCompatActivity {
	public final String TAG = "MEDICATION_ADHERENCE";

	BluetoothManager BluetoothManager;
	BluetoothAdapter bluetoothAdapter;
	ListView devicesList;
	ArrayList<BluetoothDevice> devices;
	ArrayAdapter<BluetoothDevice> deviceAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private MintuReceiver mintuReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_connect);
		devicesList = (ListView)findViewById(R.id.devicesList);
		devices = new ArrayList<BluetoothDevice>();

		deviceAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1,devices);
		devicesList.setAdapter(deviceAdapter);

		mintuReceiver = new MintuReceiver();

		devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BluetoothDevice temp = devices.get(position);


			}
		});

		// Register for broadcasts when a device is discovered.
		//BluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();

		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		if (!bluetoothAdapter.isDiscovering())
			bluetoothAdapter.startDiscovery();

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		BluetoothConnectActivity.this.registerReceiver(mintuReceiver, filter);
	}

	protected void onDestroy() {
		super.onDestroy();

		// Don't forget to unregister the ACTION_FOUND receiver.
		unregisterReceiver(receiver);
	}

}