package com.avv.bluetoothgame.presenter;

import android.bluetooth.BluetoothSocket;

public interface ConnectionListener {

	public void onConnected(BluetoothSocket socket);

	public void onConnectionFailed(final String message);

	public void onDisconnected(final String message);
}
