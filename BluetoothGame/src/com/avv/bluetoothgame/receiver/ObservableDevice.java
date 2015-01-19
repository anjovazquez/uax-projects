package com.avv.bluetoothgame.receiver;

import java.util.Observable;

import android.bluetooth.BluetoothDevice;

/**
 * Este objeto nos valdra para encapsular el resultado que nos devolvera el
 * BroadcastReceiver declarado. Registraremo el Presenter como Observador de
 * este objeto y cuando cambie se nos notificara
 * 
 * @author angelvazquez
 * 
 */
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
		this.setChanged();
		this.notifyObservers();
	}

	public BluetoothDevice getDevice() {
		return this.device;
	}

}
