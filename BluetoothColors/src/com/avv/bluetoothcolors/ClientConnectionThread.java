package com.avv.bluetoothcolors;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class ClientConnectionThread extends Thread {

	private final ConnectionListener connectionListener;
	private final BluetoothSocket socket;

	public ClientConnectionThread(BluetoothDevice device,
			ConnectionListener connectionListener) {
		this.connectionListener = connectionListener;
		BluetoothSocket socketTemp = null;

		try {
			socketTemp = device
					.createInsecureRfcommSocketToServiceRecord(MainActivity.THE_UUID);
		} catch (Exception e) {
			connectionListener
					.onConnectionFailed("No se pudo crear el socket: "
							+ e.getMessage());
		}
		socket = socketTemp;
	}
	
	@Override
	public void run() {
		try{
			socket.connect();
			connectionListener.onConnected(socket);
		}
		catch(Exception e){
			connectionListener.onConnectionFailed("Error al conectar: "+e.getStackTrace());
			try{
				socket.close();
			}
			catch(Exception e1){
				
			}
		}
	}
	
	public void cancel() {
		// TODO Auto-generated method stub
		try {
			socket.close();
		} catch (Exception e) {
			connectionListener.onConnectionFailed("Se canceló el hilo");
		}
	}

}
