package com.example.medication_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class Dashboard extends AppCompatActivity implements Serializable
{
	private UARTConnection uartConnection;

	ArrayList<Module> modules;
	ImageButton moduleBtn1;
	ImageButton moduleBtn2;
	ImageButton moduleBtn3;
	ImageButton moduleBtn4;
	ImageButton moduleBtn5;
	ImageButton moduleBtn6;
	ImageButton profile;
	TextView textViewModule1;
	TextView textViewModule2;
	TextView textViewModule3;
	TextView textViewModule4;
	TextView textViewModule5;
	Button connectBtn;
	Button alarm;
	int REQUEST_CODE_BLECONNECT = 98;
	int REQUEST_CODE_MODEDIT = 99;
	int hour,min,second;
	String time;
	public final String TAG = "MEDICATION_ADHERENCE";//TAG for log usage
	private AlarmManager alarmManager;
	public final int PERMISSION_ALL = 3;
	String[] PERMISSIONS = {
			Manifest.permission.SEND_SMS,
			Manifest.permission.CALL_PHONE,
			Manifest.permission.READ_CONTACTS,
			Manifest.permission.ACCESS_COARSE_LOCATION,
			Manifest.permission.ACCESS_FINE_LOCATION
	};
	public final String CHANNEL_ID = "0";
	public String nfcID;
	public static final String MIME_TEXT_PLAIN = "text/plain";
	NfcAdapter mNfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard_hex);

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if(!hasPermissions(this, PERMISSIONS)){
			ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
		}

		createNotificationChannel();

		FileInputStream fileInputStream = null;
		String nfcIDFileText = "";

		try {
			fileInputStream = openFileInput("nfcID.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

			char[] buffer = new char[1024];
			int charRead;

			//creates string that contains text from 'nfcID.txt'
			while ((charRead = inputStreamReader.read(buffer)) > 0) {
				String readString = String.copyValueOf(buffer, 0, charRead);
				nfcIDFileText += readString;
			}

			nfcID = nfcIDFileText;

		} catch (Exception e) {
			e.printStackTrace();
		}

		ImageButton setting = (ImageButton) findViewById(R.id.settings);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, SettingsActivity.class);
                startActivity(i);

            }
        });

        profile = (ImageButton) findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view)
			{
				Intent profileActivity = new Intent(Dashboard.this,Profile.class);
				startActivity(profileActivity);
			}
		});

        alarm = (Button) findViewById(R.id.editAlarms);
        alarm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent j = new Intent(Dashboard.this, MainActivity.class);
				startActivity(j);
			}
		});

		textViewModule1 = findViewById(R.id.textViewModule1);
		textViewModule2 = findViewById(R.id.textViewModule2);
		textViewModule3 = findViewById(R.id.textViewModule3);
		textViewModule4 = findViewById(R.id.textViewModule4);
		textViewModule5 = findViewById(R.id.textViewModule5);

		modules = new ArrayList<>();

		for (int i = 0; i < 5; i++)
			modules.add(new Module(i + 1));

		moduleBtn1 = (ImageButton) findViewById(R.id.moduleBtn1);
		if (!modules.get(0).medicineName.equals("-1"))
		{
			moduleBtn1.setImageDrawable(getDrawable(R.drawable.hexagon));
			moduleBtn1.setColorFilter(Color.RED);
		}

		moduleBtn2 = (ImageButton) findViewById(R.id.moduleBtn2);
		if (!modules.get(1).medicineName.equals("-1"))
		{
			moduleBtn2.setImageDrawable(getDrawable(R.drawable.hexagon));
			moduleBtn2.setColorFilter(Color.CYAN);
		}

		moduleBtn3 = (ImageButton) findViewById(R.id.moduleBtn3);
		if (!modules.get(2).medicineName.equals("-1"))
		{
			moduleBtn3.setImageDrawable(getDrawable(R.drawable.hexagon));
			moduleBtn3.setColorFilter(Color.GREEN);
		}

		moduleBtn4 = (ImageButton) findViewById(R.id.moduleBtn4);
		if (!modules.get(3).medicineName.equals("-1"))
		{
			moduleBtn4.setImageDrawable(getDrawable(R.drawable.hexagon));
			moduleBtn4.setColorFilter(Color.YELLOW);
		}

		moduleBtn5 = (ImageButton) findViewById(R.id.moduleBtn5);
		if (!modules.get(4).medicineName.equals("-1"))
		{
			moduleBtn5.setImageDrawable(getDrawable(R.drawable.hexagon));
			moduleBtn5.setColorFilter(Color.BLUE);
		}

		moduleBtn1.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
				editModuleActivity.putExtra("moduleToEdit", modules.get(0)); //send original medicine values as placeholders for edit activity

				startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
			}
		});

		moduleBtn2.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
				editModuleActivity.putExtra("moduleToEdit", modules.get(1)); //send original medicine values as placeholders for edit activity

				startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
			}
		});

		moduleBtn3.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
				editModuleActivity.putExtra("moduleToEdit", modules.get(2)); //send original medicine values as placeholders for edit activity

				startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
			}
		});

		moduleBtn4.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
				editModuleActivity.putExtra("moduleToEdit", modules.get(3)); //send original medicine values as placeholders for edit activity

				startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
			}
		});

		moduleBtn5.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent editModuleActivity = new Intent(Dashboard.this, EditModule.class);
				editModuleActivity.putExtra("moduleToEdit", modules.get(4)); //send original medicine values as placeholders for edit activity

				startActivityForResult(editModuleActivity, REQUEST_CODE_MODEDIT);
			}
		});


		connectBtn = (Button) findViewById(R.id.viewmed);
		connectBtn.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent connectActivity = new Intent(Dashboard.this, BluetoothConnectActivity.class);

				startActivityForResult(connectActivity, REQUEST_CODE_BLECONNECT);
			}
		});

	}


	public void startConnection(BluetoothDevice bluetoothDevice)
	{
		// TODO make sure any current flutter/connection is properly closed?
		this.uartConnection = new UARTConnection(getApplicationContext(), bluetoothDevice, Constants.FLUTTER_UART_SETTINGS);
		this.uartConnection.addRxDataListener(new UARTConnection.RXDataListener()
		{
			@Override
			public void onRXData(byte[] newData)
			{

			}
		});
	}

	/**
	 * Send a BLE message to the currently-connected ble device
	 *
	 * @param bytes the message to be sent
	 * @return true if bytes are successfully written to the UART connection, false otherwise
	 */
	public synchronized boolean sendMessage(byte[] bytes)
	{
		if (this.uartConnection == null)
		{
			Log.e(Constants.LOG_TAG, "requested sendMessage with null uartConnection");
			return false;
		}
		return this.uartConnection.writeBytes(bytes);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_BLECONNECT)
		{

			BluetoothDevice bluetoothDevice = (BluetoothDevice) data.getParcelableExtra("bluetoothDevice");
			startConnection(bluetoothDevice);
			Snackbar.make(getWindow().getDecorView().getRootView(), "Connected!", Snackbar.LENGTH_LONG)
					.setAction("CLOSE", new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{

						}
					})
					.setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
					.show();
			Log.i(TAG, bluetoothDevice.getAddress());

			if (uartConnection.isConnected())
			{
				connectBtn.setText("Send Data");

				connectBtn.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						byte[] message;
						final byte[] result = new byte[6];
						ArrayList<byte[]> resultNames = new ArrayList<>();
						ArrayList<byte[]> resultLists = new ArrayList<>();

						time = "\"" + Math.round((System.currentTimeMillis() / 1000.0));

						Log.e("TIME", time);

						message = time.getBytes();
						sendMessage(message);

						for (int i = 0; i < modules.size(); i++)
						{

							String colon = "-";
							String semicolon = ";";

							if (!modules.get(i).medicineName.equals("-1"))
							{
								Module module = modules.get(i);

								//send medicine name
								message = module.medicineName.getBytes();
								sendMessage(message);
								//wait for BLE to receive


								sendMessage(colon.getBytes());

								//send module number integer as a string
								String modNum = Integer.toString(module.module);
								message = modNum.getBytes();
								sendMessage(message);
								//wait for BLE to receive

								sendMessage(colon.getBytes());

								//send number of times for BLE to store for this module as a String
								String numTimes = Integer.toString(module.times.size());
								message = numTimes.getBytes();
								sendMessage(message);
								//wait for BLE to receive

								sendMessage(colon.getBytes());

								for(int j = 0; j < module.times.size(); j++){
									message = module.times.get(j).getBytes();
									sendMessage(message);
								}

								sendMessage(semicolon.getBytes());
							}
						}
					}
				});
			}

			sendMessage("\"".getBytes());
            /*
            final ArrayList<byte[]> bbrr = new ArrayList<>();
            final byte[] result = new byte [6];
            final ArrayList<byte[]> resultnames = new ArrayList<>();
            final ArrayList<byte[]> resultlists = new ArrayList<>();
            connectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(int i = 0; i < modules.size(); i++) {
                        result[i] = (byte) modules.get(i).module;
                        resultnames.add(modules.get(i).times.toString().getBytes());
                        resultlists.add(modules.get(i).medicineName.getBytes());
                        Log.e(TAG, resultnames.toString());
                        sendMessage(resultnames);
                    }
                    bbrr.set(0,result);
                    bbrr.set(1,resultnames);
                    bbrr.set(2,resultlists);
                }
            });
            */
		}


		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MODEDIT)
		{
			//add medicine received from activity (if an edit, we already removed the original one)
			Module newModule = (Module) data.getSerializableExtra("editedModule");
			modules.set(newModule.module - 1, newModule);
			Log.e("module", Integer.toString(newModule.module));

			int color = data.getIntExtra("color", 0);
			int imageResource = R.drawable.hexagon_gray;

			switch(color){
				case 0:
					imageResource = R.drawable.hexagon_red;
					break;
				case 1:
					imageResource = R.drawable.hexagon_orange;
					break;
				case 2:
					imageResource = R.drawable.hexagon_green;
					break;
				case 3:
					imageResource = R.drawable.hexagon_blue;
					break;
				case 4:
					imageResource = R.drawable.hexagon_purple;
					break;
			}

			if (newModule.module == 1)
			{
				moduleBtn1.setImageResource(imageResource);
				textViewModule1.setText(modules.get(0).modBtnText());
				textViewModule1.setVisibility(View.VISIBLE);
			}
			if (newModule.module == 2)
			{
				moduleBtn2.setImageResource(imageResource);
				textViewModule2.setText(modules.get(1).modBtnText());
				textViewModule2.setVisibility(View.VISIBLE);
			}
			if (newModule.module == 3)
			{
				moduleBtn3.setImageResource(imageResource);
				textViewModule3.setText(modules.get(2).modBtnText());
				textViewModule3.setVisibility(View.VISIBLE);
			}
			if (newModule.module == 4)
			{
				moduleBtn4.setImageResource(imageResource);
				textViewModule4.setText(modules.get(3).modBtnText());
				textViewModule4.setVisibility(View.VISIBLE);
			}
			if (newModule.module == 5)
			{
				moduleBtn5.setImageResource(imageResource);
				textViewModule5.setText(modules.get(4).modBtnText());
				textViewModule5.setVisibility(View.VISIBLE);
			}
		}
	}

	public static boolean hasPermissions(Context context, String... permissions) {
		if (context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}

	@TargetApi(Build.VERSION_CODES.O)
	private void createNotificationChannel() {
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		CharSequence name = getString(R.string.channel_name);
		String description = getString(R.string.channel_description);
		int importance = NotificationManager.IMPORTANCE_HIGH;
		NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
		channel.setDescription(description);
		// Register the channel with the system; you can't change the importance
		// or other notification behaviors after this
		NotificationManager notificationManager = getSystemService(NotificationManager.class);
		notificationManager.createNotificationChannel(channel);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	public static void setupForegroundDispatch(Activity activity, NfcAdapter adapter) {
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][]{};

		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}

		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
	}

	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		adapter.disableForegroundDispatch(activity);
	}

	private void handleIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {

				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

				Intent Level2Receiver = new Intent(this, Alarm2Receiver.class);
				boolean isActive = (PendingIntent.getBroadcast(this, 992, Level2Receiver, PendingIntent.FLAG_NO_CREATE)) != null; //check if Level 2 alarm is active

				if (getTextFromTag(tag).equals(nfcID) && isActive) {

					Toast.makeText(this, "Confirmed! Thank you for taking your medication!", Toast.LENGTH_LONG).show();

					//Cancel Level 2
					PendingIntent Level2Intent = PendingIntent.getBroadcast(
							getApplicationContext(), 992, Level2Receiver,
							PendingIntent.FLAG_CANCEL_CURRENT);

					alarmManager.cancel(Level2Intent);

					//Cancel Level 3
					Intent Level3Receiver = new Intent(getApplicationContext(), Alarm3Receiver.class);
					PendingIntent Level3Intent = PendingIntent.getBroadcast(
							getApplicationContext(), 993, Level3Receiver,
							PendingIntent.FLAG_CANCEL_CURRENT);

					alarmManager.cancel(Level3Intent);

					//Cancel Level 4
					Intent Level4Receiver = new Intent(getApplicationContext(), Alarm4Receiver.class);
					PendingIntent Level4Intent = PendingIntent.getBroadcast(
							getApplicationContext(), 994, Level4Receiver,
							PendingIntent.FLAG_CANCEL_CURRENT);

					alarmManager.cancel(Level4Intent);
				}
				else if (getTextFromTag(tag).equals(nfcID) && !isActive)
					Toast.makeText(this, "No medicine alarms have been triggered yet. No need to take medicine right now.", Toast.LENGTH_LONG).show();
				else
					Toast.makeText(this, "Wrong NFC ID. If you think this is a mistake, then change the NFC id of your app using the SMARThex app settings.", Toast.LENGTH_LONG).show();
			} else {
				//keep on going
			}
		}
	}


	protected String getTextFromTag(Tag tag) {
		Ndef ndef = Ndef.get(tag);
		if (ndef == null) {
			// NDEF is not supported by this Tag.
			return null;
		}

		NdefMessage ndefMessage = ndef.getCachedNdefMessage();

		NdefRecord[] records = ndefMessage.getRecords();
		for (NdefRecord ndefRecord : records) {
			if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
				try {
					return readText(ndefRecord);
				} catch (UnsupportedEncodingException e) {
				}
			}
		}

		return null;
	}

	private String readText(NdefRecord record) throws UnsupportedEncodingException {
		/*
		 * See NFC forum specification for "Text Record Type Definition" at 3.2.1
		 *
		 * http://www.nfc-forum.org/specs/
		 *
		 * bit_7 defines encoding
		 * bit_6 reserved for future use, must be 0
		 * bit_5..0 length of IANA language code
		 */

		byte[] payload = record.getPayload();

		// Get the Text Encoding
		String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

		// Get the Language Code
		int languageCodeLength = payload[0] & 0063;

		// String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
		// e.g. "en"

		// Get the Text
		return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
	}

	@Override // Essential to have onPause and onResume methods for the activity.
	protected void onPause() {
		super.onPause();

		stopForegroundDispatch(this, mNfcAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		setupForegroundDispatch(this, mNfcAdapter);
	}
}
