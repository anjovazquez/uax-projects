package com.avv.bluetoothgame.threads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.avv.bluetoothgame.presenter.BluetoothGamePresenter;
import com.avv.bluetoothgame.presenter.ConnectionListener;

public class ClientConnectionThread extends Thread {

	private final ConnectionListener connectionListener;
	private BluetoothSocket socket;

	private final BluetoothDevice device;

	public ClientConnectionThread(BluetoothDevice device,
			ConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
		this.device = device;
	}

	@Override
	public void run() {

		for (int i = 0; i < 2; i++) {
			BluetoothSocket socketTemp = null;

			try {
				socketTemp = this.device
						.createInsecureRfcommSocketToServiceRecord(BluetoothGamePresenter.THE_UUIDS[i]);
			} catch (Exception e) {
				this.connectionListener
						.onConnectionFailed("No se pudo crear el socket: "
								+ e.getMessage());
				continue;
			}
			this.socket = socketTemp;
			try {
				this.socket.connect();
				this.connectionListener.onConnected(this.socket);
				break;
			} catch (Exception e) {
				e.printStackTrace();
				this.connectionListener
						.onConnectionFailed("Error al conectar: "
								+ e.getStackTrace());
				try {
					this.socket.close();
				} catch (Exception e1) {

				}
			}
		}
	}

	public void cancel() {
		// TODO Auto-generated method stub
		try {
			this.socket.close();
		} catch (Exception e) {
			this.connectionListener.onConnectionFailed("Se canceló el hilo");
		}
	}

}
