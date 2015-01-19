package com.avv.bluetoothgame.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.avv.bluetoothgame.presenter.BluetoothGamePresenter;
import com.avv.bluetoothgame.presenter.ConnectionListener;

/**
 * El thread de conexion del servidor
 * 
 * Poseemos una serie de UUIDs declarados en el Presenter para que el servidor
 * pueda admitir varias conexiones bluetooth, cada emparejamiento debe ser con
 * un UUID diferente, cuando se conecta un cliente el servidor acepta la
 * conexion guarda el canal de comunicacion y se pone a esperar por otro socket
 * con el siguiente UUID
 * 
 * @author angelvazquez
 * 
 */
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

		for (int i = 0; i < BluetoothGamePresenter.THE_UUIDS.length; i++) {
			BluetoothServerSocket acceptSocketTemp = null;
			try {
				acceptSocketTemp = BluetoothAdapter.getDefaultAdapter()
						.listenUsingInsecureRfcommWithServiceRecord(
								BluetoothGamePresenter.NAME,
								BluetoothGamePresenter.THE_UUIDS[i]);

				this.acceptSocket = acceptSocketTemp;

				BluetoothSocket socket = null;

				socket = this.acceptSocket.accept();

				if (socket != null) {
					this.connectionListener.onConnected(socket);
				}

			} catch (Exception e) {
				this.connectionListener
						.onConnectionFailed("Error al aceptar el socket "
								+ e.getMessage());
			} finally {
				if (this.acceptSocket != null) {
					try {
						this.acceptSocket.close();
					} catch (Exception e) {
						Log.d(this.getClass().getName(),
								"Error al cerrar el socket");
					}
				}
			}
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
