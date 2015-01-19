package com.avv.bluetoothgame.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Es un receiver que declaramos para obtener la seleccion del dispositivo a
 * emparejar una vez que hemos mostrado la lista de dipositivos dentro del
 * alcance
 * 
 * @author angelvazquez
 * 
 */
public class BluetoothDeviceSelectedReceiver extends BroadcastReceiver {

	public BluetoothDeviceSelectedReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		BluetoothDevice device = (BluetoothDevice) intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

		ObservableDevice observableDevice = ObservableDevice.getInstance();
		observableDevice.setDevice(device);
	};

}
