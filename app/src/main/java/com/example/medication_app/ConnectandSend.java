package com.example.medication_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;


public class ConnectandSend extends AppCompatActivity {

    public final String TAG = "MEDICATION_ADHERENCE";
    private UARTConnection uartConnection;
    BluetoothAdapter bluetoothAdapter;
    ListView devicesList;
    ArrayList<BluetoothDevice> devices;
    ArrayAdapter<BluetoothDevice> deviceAdapter;
    private BluetoothGatt bluetoothGatt;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MAX_RETRIES = 100;
    private static final int CONNECTION_TIMEOUT_IN_SECS = 15;

    /* Latches to handle serialization of async reads/writes */
    private CountDownLatch startLatch = new CountDownLatch(1);
    private CountDownLatch doneLatch = new CountDownLatch(1);
    private CountDownLatch resultLatch = new CountDownLatch(1);

    /* UUIDs for the communication lines */
    private UUID uartUUID, txUUID, rxUUID, rxConfigUUID;

    private List<UARTConnection.RXDataListener> rxListeners = new ArrayList<>();
    private int connectionState;
    private BluetoothGatt btGatt;
    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;
    // NOTE: issues with writing if descriptor is not written to first.
    private boolean onDescriptorWriteForNotify = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectand_send);
        devicesList = (ListView) findViewById(R.id.deviceList);
        devices = new ArrayList<>();

        deviceAdapter = new DeviceListAdapter(this, R.layout.device, devices);
        devicesList.setAdapter(deviceAdapter);

        final Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                setResult(RESULT_OK, intent);
                finish();
            }
        });
        devicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice bluetoothDevice = devices.get(position);

                startConnection(bluetoothDevice);

                if (uartConnection.isConnected()) {
                    ImageView check = findViewById(R.id.check);
                    check.setVisibility(View.VISIBLE);
                    Toast.makeText(ConnectandSend.this, "Connected to " + bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                    nextButton.setVisibility(View.VISIBLE);
                }
            }
        });

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        onClickScan();
    }

    @TargetApi(21)
    private void onClickScan() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d(TAG, "SCAN");
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
                    Log.e(TAG, "Stopped LeScan");
                }
            }, 30000);
        }
    }

    synchronized public byte[] writeBytesWithResponse(byte[] bytes) {
        if (!onDescriptorWriteForNotify) {
            Log.e(TAG, "Refusing to call writeBytes since onDescriptorWriteForNotify is false");
            return new byte[]{};
        }
        try {
            startLatch = new CountDownLatch(1);
            doneLatch = new CountDownLatch(1);
            resultLatch = new CountDownLatch(1);

            tx.setValue(bytes);
            boolean success;
            int retryCount = 0;
            while (!(success = btGatt.writeCharacteristic(tx))) {
                if (retryCount > MAX_RETRIES) {
                    break;
                }
                retryCount++;
            }
            if (success) {
                // Wait for a successful write and a response
                startLatch.countDown();
                try {
                    doneLatch.await(100, TimeUnit.MILLISECONDS);
                    resultLatch.await(100, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Error: " + e);
                    return new byte[]{};
                }

                // Retrieve and return response
                byte[] res = rx.getValue();
                return Arrays.copyOf(res, res.length);
            }
        } catch (Exception e) {

        }
        Log.e(TAG, "Unable to write bytes to tx");
        return new byte[]{};
    }
    private boolean establishUARTConnection(Context context, final BluetoothDevice device) {
        Log.d(TAG, "UARTConnection.establishUARTConnection");
        // TODO check device (multiple devices?)
        this.bluetoothGatt = device.connectGatt(this, true, this);
        //deviceGatt.put(device.getAddress(), this.btGatt);

        // Initialize serialization
        startLatch.countDown();
        try {
            if (!doneLatch.await(CONNECTION_TIMEOUT_IN_SECS, TimeUnit.SECONDS)) {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        // Enable RX notification
        if (!bluetoothGatt.setCharacteristicNotification(rx, true)) {
            Log.e(TAG, "Unable to set characteristic notification");
            return false;
        }
        BluetoothGattDescriptor descriptor = rx.getDescriptor(rxConfigUUID);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        if (!btGatt.writeDescriptor(descriptor)) {
            Log.e(TAG, "Unable to set descriptor");
            return false;
        }
        Log.d(TAG, "Successfully established connection to " + device);
        return true;

    }
    private void startConnection(BluetoothDevice bleDevice) {
        BluetoothGattCallback gattCallback = new BluetoothGattCallback(){};
        bluetoothGatt = bleDevice.connectGatt(this, true, gattCallback);
    }
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        Log.d(TAG, "UARTConnection.onConnectionStateChange");
        connectionState = newState;
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                gatt.discoverServices();
            }
        }
    }
}
