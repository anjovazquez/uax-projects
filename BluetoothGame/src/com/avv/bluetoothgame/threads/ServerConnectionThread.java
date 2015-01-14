package com.avv.bluetoothgame.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.avv.bluetoothgame.presenter.BluetoothGamePresenter;
import com.avv.bluetoothgame.presenter.ConnectionListener;

public class ServerConnectionThread extends Thread {

	private final ConnectionListener connectionListener;
	private BluetoothServerSocket acceptSocket;
	ServerConnectionThread serverConnectionThread;

	public ServerConnectionThread(ConnectionListener connectionListener) {
		// TODO Auto-generated constructor stub
		this.connectionListener = connectionListener;
	}

	@Override
	public void run() {

		for (int i = 0; i < 2; i++) {
			BluetoothServerSocket acceptSocketTemp = null;
			try {
				acceptSocketTemp = BluetoothAdapter.getDefaultAdapter()
						.listenUsingInsecureRfcommWithServiceRecord(
								BluetoothGamePresenter.NAME,
								BluetoothGamePresenter.THE_UUIDS[i]);
			} catch (Exception e) {
				this.connectionListener
						.onConnectionFailed("No se pudo crear el socket");
			}

			this.acceptSocket = acceptSocketTemp;

			BluetoothSocket socket = null;

			// while (true) {
			try {
				socket = this.acceptSocket.accept();
			} catch (Exception e) {
				this.connectionListener
						.onConnectionFailed("Error al aceptar el socket "
								+ e.getMessage());
				// break;
			}

			if (socket != null) {
				this.connectionListener.onConnected(socket);
				try {
					this.acceptSocket.close();
				} catch (Exception e) {

				}
				// break;
			}
			// }
		}

	}

	public void cancel() {
		try {
			this.acceptSocket.close();
		} catch (Exception e) {
			this.connectionListener.onConnectionFailed("Se canceló el hilo");
		}
	}
}
