package com.avv.bluetoothgame.receiver;

import java.util.Observable;

import android.bluetooth.BluetoothDevice;

public class ObservableDevice extends Observable {

	private BluetoothDevice device;
	private static ObservableDevice instance;

	public ObservableDevice() {
	}

	public static ObservableDevice getInstance() {
		if (instance == null) {
			instance = new ObservableDevice();
		}
		return instance;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
		setChanged();
		notifyObservers();
	}

	public BluetoothDevice getDevice() {
		return device;
	}

}
