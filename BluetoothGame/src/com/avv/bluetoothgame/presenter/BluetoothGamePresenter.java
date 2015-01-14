package com.avv.bluetoothgame.presenter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.View;

import com.avv.bluetoothgame.receiver.BluetoothDeviceSelectedReceiver;
import com.avv.bluetoothgame.receiver.ObservableDevice;
import com.avv.bluetoothgame.threads.ClientConnectionThread;
import com.avv.bluetoothgame.threads.ConnectedThread;
import com.avv.bluetoothgame.threads.ServerConnectionThread;
import com.avv.bluetoothgame.view.BluetoothGameView;
import com.avv.bluetoothgame.view.adapter.PaintColor;

public class BluetoothGamePresenter implements Presenter, ConnectionListener,
		Observer {

	private static final int SERVER = 0;
	private static final int CLIENT = 1;
	private int rol = -1;

	public static final String NAME = "BluetoothGame";
	public static final UUID[] THE_UUIDS = {
			UUID.fromString("42ad6984-fd66-4b11-900f-a52b454e34ae"),
			UUID.fromString("42ad6985-fd66-4b11-900f-a52b454e34ae") };

	final int REQUEST_DISCOVERABLE = 1;
	final int REQUEST_ENABLE_BLUETOOTH = 2;

	private final BluetoothGameView bgView;

	private ConnectedThread connectedThread;
	private ClientConnectionThread clientConnectionThread;
	private ServerConnectionThread serverConnectionThread;

	private BluetoothDeviceSelectedReceiver bluetoothDeviceSelectedReceiver;
	private final Hashtable<String, ConnectedThread> connectedDevices;

	public BluetoothGamePresenter(BluetoothGameView view) {
		this.bgView = view;
		this.connectedDevices = new Hashtable<String, ConnectedThread>();
	}

	private void openSelectDevice() {
		this.bgView
				.getContext()
				.startActivity(
						new Intent(
								"android.bluetooth.devicepicker.action.LAUNCH")
								.putExtra(
										"android.bluetooth.devicepicker.extra.NEED_AUTH",
										false)
								.putExtra(
										"android.bluetooth.devicepicker.extra.FILTER_TYPE",
										0)
								.setFlags(
										Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
	}

	public void onServerClickListener(View view) {
		Intent discoverIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				120);
		((Activity) this.bgView.getContext()).startActivityForResult(
				discoverIntent, this.REQUEST_DISCOVERABLE);
	}

	public void onClientClickListener(View view) {
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableBluetoothIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) this.bgView.getContext()).startActivityForResult(
					enableBluetoothIntent, this.REQUEST_ENABLE_BLUETOOTH);
			return;
		}

		this.openSelectDevice();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_DISCOVERABLE:
			if (resultCode != Activity.RESULT_CANCELED) {
				this.serverConnectionThread = new ServerConnectionThread(this);
				this.serverConnectionThread.start();

				this.bgView.renderServerRolUI();
				this.rol = SERVER;
			}
			break;
		case REQUEST_ENABLE_BLUETOOTH:
			if (resultCode == Activity.RESULT_OK) {
				this.openSelectDevice();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void resume() {
		ObservableDevice device = ObservableDevice.getInstance();
		device.addObserver(this);

		this.bluetoothDeviceSelectedReceiver = new BluetoothDeviceSelectedReceiver();
		this.bgView
				.getContext()
				.registerReceiver(
						this.bluetoothDeviceSelectedReceiver,
						new IntentFilter(
								"android.bluetooth.devicepicker.action.DEVICE_SELECTED"));
	}

	@Override
	public void pause() {
		this.bgView.getContext().unregisterReceiver(
				this.bluetoothDeviceSelectedReceiver);
		this.closeAllThreads();
	}

	private void closeAllThreads() {
		if (this.serverConnectionThread != null) {
			this.serverConnectionThread.cancel();
			this.serverConnectionThread = null;
		}
		if (this.clientConnectionThread != null) {
			this.clientConnectionThread.cancel();
			this.clientConnectionThread = null;
		}
		if (this.connectedThread != null) {
			this.connectedThread.cancel();
			this.connectedThread = null;
		}
	}

	@Override
	public void onConnected(BluetoothSocket socket) {
		if (this.rol == CLIENT) {
			((Activity) this.bgView).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					BluetoothGamePresenter.this.bgView.renderClientRolUI();

				}
			});
		}

		this.connectedThread = new ConnectedThread(socket,
				this.bgView.getContext(), this);
		this.connectedThread.start();
		if (this.rol == SERVER) {
			this.connectedDevices.put(socket.getRemoteDevice().getAddress(),
					this.connectedThread);
		}
	}

	@Override
	public void onConnectionFailed(String message) {

	}

	@Override
	public void onDisconnected(String message) {

	}

	@Override
	public void update(Observable observable, Object paramObject) {
		ObservableDevice observableObject = ObservableDevice.getInstance();

		if (this.clientConnectionThread != null) {
			this.clientConnectionThread.cancel();
		}

		this.clientConnectionThread = new ClientConnectionThread(
				observableObject.getDevice(), this);
		this.clientConnectionThread.start();

		this.rol = CLIENT;
	}

	public void changeColor(PaintColor paintColor) {
		if ((this.rol == CLIENT) && (this.connectedThread != null)) {
			String hexColor = String.format("#%06X",
					(0xFFFFFF & paintColor.getColor()));
			this.connectedThread.send(hexColor.getBytes());
		}

		if ((this.rol == SERVER) && !this.connectedDevices.isEmpty()) {
			for (Enumeration<String> en = this.connectedDevices.keys(); en
					.hasMoreElements();) {
				String key = en.nextElement();
				ConnectedThread th = this.connectedDevices.get(key);
				String hexColor = String.format("#%06X",
						(0xFFFFFF & paintColor.getColor()));
				// this.connectedThread.send(hexColor.getBytes());
				th.send(hexColor.getBytes());
			}
		}

	}

	@Override
	public void onMessageReceived(final String message) {

		if (this.rol == CLIENT) {
			((Activity) this.bgView).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					BluetoothGamePresenter.this.bgView
							.renderColorBackground(Color.parseColor(message));

				}
			});

		}
	}

}
