package com.avv.bluetoothgame.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import com.avv.bluetoothgame.presenter.BluetoothGamePresenter;
import com.avv.bluetoothgame.presenter.ConnectionListener;

public class ServerConnectionThread extends Thread {

	private final ConnectionListener connectionListener;
	private final BluetoothServerSocket acceptSocket;

	ServerConnectionThread serverConnectionThread;

	public ServerConnectionThread(ConnectionListener connectionListener) {
		// TODO Auto-generated constructor stub
		this.connectionListener = connectionListener;

		BluetoothServerSocket acceptSocketTemp = null;
		try {
			acceptSocketTemp = BluetoothAdapter.getDefaultAdapter()
					.listenUsingInsecureRfcommWithServiceRecord(
							BluetoothGamePresenter.NAME, BluetoothGamePresenter.THE_UUID);
		} catch (Exception e) {
			connectionListener.onConnectionFailed("No se pudo crear el socket");
		}

		acceptSocket = acceptSocketTemp;
	}

	@Override
	public void run() {
		BluetoothSocket socket = null;

		while (true) {
			try {
				socket = acceptSocket.accept();
			} catch (Exception e) {
				connectionListener
						.onConnectionFailed("Error al aceptar el socket "
								+ e.getMessage());
				break;
			}

			if (socket != null) {
				connectionListener.onConnected(socket);
				try {
					acceptSocket.close();
				} catch (Exception e) {

				}
				break;
			}
		}
	}

	public void cancel() {
		try {
			acceptSocket.close();
		} catch (Exception e) {
			connectionListener.onConnectionFailed("Se canceló el hilo");
		}
	}
}
