package com.avv.bluetoothgame.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothDeviceSelectedReceiver extends BroadcastReceiver {

	public BluetoothDeviceSelectedReceiver() {
		// TODO Auto-generated constructor stub
	}

	public void onReceive(Context context, Intent intent) {

		BluetoothDevice device = (BluetoothDevice) intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		ObservableDevice observableDevice = ObservableDevice.getInstance();
		observableDevice.setDevice(device);
	};

}
