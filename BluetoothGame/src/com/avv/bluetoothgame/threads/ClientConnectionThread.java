package com.avv.bluetoothgame.threads;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.avv.bluetoothgame.presenter.BluetoothGamePresenter;
import com.avv.bluetoothgame.presenter.ConnectionListener;

/**
 * El thread de conexion del cliente Poseemos una serie de UUIDs declarados en
 * el Presenter para que el servidor pueda admitir varias conexiones bluetooth,
 * cada emparejamiento debe ser con un UUID diferente, cuando se conecta un
 * cliente el servidor acepta la conexion guarda el canal de comunicacion y se
 * pone a esperar por otro socket con el siguiente UUID, como el cliente no sabe
 * que UUID esta esperando el servidor tiene que ir probando cada uno de la
 * lista y se queda con el primero
 * 
 * @author angelvazquez
 * 
 */
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

		for (int i = 0; i < BluetoothGamePresenter.THE_UUIDS.length; i++) {
			BluetoothSocket socketTemp = null;

			try {
				socketTemp = this.device
						.createInsecureRfcommSocketToServiceRecord(BluetoothGamePresenter.THE_UUIDS[i]);
				this.socket = socketTemp;
				this.socket.connect();
				this.connectionListener.onConnected(this.socket);
				break;
			} catch (IOException e) {
				e.printStackTrace();
				this.connectionListener
						.onConnectionFailed("Error al conectar: "
								+ e.getStackTrace());
				try {
					this.socket.close();
				} catch (Exception e1) {
					this.connectionListener
							.onConnectionFailed("Error al cerrar el socket: "
									+ e.getStackTrace());
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
