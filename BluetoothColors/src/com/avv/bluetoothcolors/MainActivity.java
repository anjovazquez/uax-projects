package com.avv.bluetoothcolors;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements ConnectionListener {

	public static final String NAME = "BluetoothColors";
	public static final UUID THE_UUID = UUID
			.fromString("42ad6984-fd66-4b11-900f-a52b454e34ae");

	final int REQUEST_DISCOVERABLE = 1;
	final int REQUEST_ENABLE_BLUETOOTH = 2;
	private ServerConnectionThread serverConnectionThread;
	private ClientConnectionThread clientConnectionThread;
	private ConnectedThread connectedThread;

	public BroadcastReceiver bluetoothDeviceSelectedReceiver = new BroadcastReceiver() {
		public void onReceive(android.content.Context context, Intent intent) {
			BluetoothDevice device = (BluetoothDevice) intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (clientConnectionThread != null) {
				clientConnectionThread.cancel();
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// ((TextView)findViewById(id))
				}
			});

			clientConnectionThread = new ClientConnectionThread(device,
					MainActivity.this);
			clientConnectionThread.start();
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		registerReceiver(bluetoothDeviceSelectedReceiver, new IntentFilter(
				"android.bluetooth.devicepicker.action.DEVICE_SELECTED"));
	}

	public void onSendClickListener(View view) {
		if (connectedThread != null) {
			connectedThread.send(new String("Hola Bluetooth!!").getBytes());
		}
	}

	public void onClientClickListener(View view) {
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableBluetoothIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetoothIntent,
					REQUEST_ENABLE_BLUETOOTH);
			return;
		}

		openSelectDevice();
	}

	public void onServerClickListener(View view) {
		Intent discoverIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				120);
		startActivityForResult(discoverIntent, REQUEST_DISCOVERABLE);
	}

	public void onCloseClickListener(View view) {
		closeAllThreads();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_DISCOVERABLE:
			if (resultCode != RESULT_CANCELED) {
				serverConnectionThread = new ServerConnectionThread(this);
				serverConnectionThread.start();
			}
			break;
		case REQUEST_ENABLE_BLUETOOTH:
			if (resultCode == RESULT_OK) {
				openSelectDevice();
			}
			break;
		default:
			break;
		}
	}

	private void openSelectDevice() {
		startActivity(new Intent("android.bluetooth.devicepicker.action.LAUNCH")
				.putExtra("android.bluetooth.devicepicker.extra.NEED_AUTH",
						false)
				.putExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 0)
				.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(bluetoothDeviceSelectedReceiver);
		closeAllThreads();
		super.onDestroy();
	}

	private void closeAllThreads() {
		if (serverConnectionThread != null) {
			serverConnectionThread.cancel();
			serverConnectionThread = null;
		}
		if (clientConnectionThread != null) {
			clientConnectionThread.cancel();
			clientConnectionThread = null;
		}
		if (connectedThread != null) {
			connectedThread.cancel();
			connectedThread = null;
		}
	}

	@Override
	public void onConnected(BluetoothSocket socket) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateConnectUI();
			}
		});

		connectedThread = new ConnectedThread(socket, this, this);
		connectedThread.start();
	}

	@Override
	public void onConnectionFailed(final String message) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				updateDisconnectUI(message);
			}
		});

		serverConnectionThread = null;
		clientConnectionThread = null;
	}

	@Override
	public void onDisconnected(final String message) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateDisconnectUI(message);
			}
		});
		connectedThread = null;
	}

	public void updateConnectUI() {
		((Button) findViewById(R.id.bClient)).setEnabled(false);
		((Button) findViewById(R.id.bServer)).setEnabled(false);

		((Button) findViewById(R.id.bSend)).setEnabled(true);
		((Button) findViewById(R.id.bClose)).setEnabled(true);
	}

	public void updateDisconnectUI(final String message) {
		((Button) findViewById(R.id.bClient)).setEnabled(true);
		((Button) findViewById(R.id.bServer)).setEnabled(true);

		((Button) findViewById(R.id.bSend)).setEnabled(false);
		((Button) findViewById(R.id.bClose)).setEnabled(true);
	}

}
