package com.avv.bluetoothgame.presenter;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

import com.avv.bluetoothgame.receiver.BluetoothDeviceSelectedReceiver;
import com.avv.bluetoothgame.receiver.ObservableDevice;
import com.avv.bluetoothgame.threads.ClientConnectionThread;
import com.avv.bluetoothgame.threads.ConnectedThread;
import com.avv.bluetoothgame.threads.ServerConnectionThread;
import com.avv.bluetoothgame.view.BluetoothGameView;

public class BluetoothGamePresenter implements Presenter, ConnectionListener,
		Observer {

	public static final String NAME = "BluetoothColors";
	public static final UUID THE_UUID = UUID
			.fromString("42ad6984-fd66-4b11-900f-a52b454e34ae");

	final int REQUEST_DISCOVERABLE = 1;
	final int REQUEST_ENABLE_BLUETOOTH = 2;

	private BluetoothGameView bgView;

	private ConnectedThread connectedThread;
	private ClientConnectionThread clientConnectionThread;
	private ServerConnectionThread serverConnectionThread;

	private BluetoothDeviceSelectedReceiver bluetoothDeviceSelectedReceiver;

	public BluetoothGamePresenter(BluetoothGameView view) {
		this.bgView = view;
	}

	private void openSelectDevice() {
		bgView.getContext()
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
		((Activity) bgView.getContext()).startActivityForResult(discoverIntent,
				REQUEST_DISCOVERABLE);
	}

	public void onClientClickListener(View view) {
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableBluetoothIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) bgView.getContext()).startActivityForResult(
					enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
			return;
		}

		openSelectDevice();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_DISCOVERABLE:
			if (resultCode != Activity.RESULT_CANCELED) {
				serverConnectionThread = new ServerConnectionThread(this);
				serverConnectionThread.start();
			}
			break;
		case REQUEST_ENABLE_BLUETOOTH:
			if (resultCode == Activity.RESULT_OK) {
				openSelectDevice();
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
		bgView.getContext()
				.registerReceiver(
						bluetoothDeviceSelectedReceiver,
						new IntentFilter(
								"android.bluetooth.devicepicker.action.DEVICE_SELECTED"));
	}

	@Override
	public void pause() {
		bgView.getContext().unregisterReceiver(bluetoothDeviceSelectedReceiver);
		closeAllThreads();
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
		connectedThread = new ConnectedThread(socket,
				(Activity) bgView.getContext(), this);
		connectedThread.start();
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

		if (clientConnectionThread != null) {
			clientConnectionThread.cancel();
		}

		clientConnectionThread = new ClientConnectionThread(
				observableObject.getDevice(), this);
		clientConnectionThread.start();
	}

}
