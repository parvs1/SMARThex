package com.example.medication_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BluetoothConnectActivity extends AppCompatActivity
{
	public final String TAG = "MEDICATION_ADHERENCE";

	private UARTConnection uartConnection;
	BluetoothAdapter bluetoothAdapter;
	ListView devicesList;
	ArrayList<BluetoothDevice> devices;
	ArrayAdapter<BluetoothDevice> deviceAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	BluetoothDevice bluetoothDevice;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.bluetooth_connect);
		devicesList = (ListView) findViewById(R.id.deviceList);
		devices = new ArrayList<>();

		deviceAdapter = new DeviceListAdapter(this, R.layout.device, devices);
		devicesList.setAdapter(deviceAdapter);

		final Button nextButton = (Button)findViewById(R.id.nextButton);
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.putExtra("bluetoothDevice", bluetoothDevice);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				bluetoothDevice = devices.get(position);

				ImageView check = findViewById(R.id.check);
				check.setVisibility(View.VISIBLE);
				Toast.makeText(BluetoothConnectActivity.this, "Connected to " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
				nextButton.setVisibility(View.VISIBLE);
			}
		});

		// Initializes Bluetooth adapter.
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		bluetoothAdapter = bluetoothManager.getAdapter();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("This app needs location access");
				builder.setMessage("Please grant location access so this app can detect beacons.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					public void onDismiss(DialogInterface dialog) {
						requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
					}
				});
				builder.show();
			}
		}
		onClickScan();
	}
	private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

	public void onRequestPermissionsResult(int requestCode,
										   String permissions[],
										   int[] grantResults)
	{
		switch (requestCode)
		{
			case PERMISSION_REQUEST_COARSE_LOCATION:
			{
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					Log.d(TAG, "coarse location permission granted");
				}
				else
				{
					final AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Functionality limited");
					builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
					builder.setPositiveButton(android.R.string.ok, null);
					builder.setOnDismissListener(new DialogInterface.OnDismissListener()
					{
						@Override
						public void onDismiss(DialogInterface dialog)
						{
						}
					});
					builder.show();
				}
				return;
			}
		}
	}

	@TargetApi(21)
	private void onClickScan() {
		// Ensures Bluetooth is available on the device and it is enabled. If not,
		// displays a dialog requesting user permission to enable Bluetooth.
		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		} else {
			Log.d(TAG,"SCAN");
			// NOTE: construct instance of callback or else stopScan() does nothing
			final ScanCallback scanCallback = new ScanCallback() {
				@Override
				public void onScanResult(int callbackType, ScanResult result) {
					BluetoothDevice device = result.getDevice();

					if (!devices.contains(device) && device.getName() != null && device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
						devices.add(device);
						deviceAdapter.notifyDataSetChanged();
					}

					super.onScanResult(callbackType, result);
				}
			};
			// TODO ScanFilter https://developer.android.com/reference/android/bluetooth/le/ScanFilter
			bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
					Log.e(TAG,"Stopped LeScan");
				}
			}, 30000);
		}
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

}