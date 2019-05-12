package com.example.medication_app;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

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

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard_hex);

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
}
