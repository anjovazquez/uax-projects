package com.avv.bluetoothcolors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;

public class ConnectedThread extends Thread {

	private final BluetoothSocket socket;

	private final InputStream inputStream;
	private final OutputStream outputStream;

	private Activity activity;

	private ConnectionListener connectionListener;

	Vibrator vibrator;

	public ConnectedThread(BluetoothSocket socket, Activity activity,
			ConnectionListener connectionListener) {

		this.socket = socket;
		this.activity = activity;
		this.connectionListener = connectionListener;
		vibrator = (Vibrator) activity
				.getSystemService(Context.VIBRATOR_SERVICE);

		InputStream inTemp = null;
		OutputStream outTemp = null;

		try {
			inTemp = socket.getInputStream();
			outTemp = socket.getOutputStream();
		} catch (IOException e) {
			connectionListener
					.onDisconnected("No se pueden abrir los canales de E/S");
		}

		inputStream = inTemp;
		outputStream = outTemp;
	}

	@Override
	public void run() {
		byte[] buffer = new byte[1024];
		int readed;

		while (true) {
			try {

				readed = inputStream.read(buffer);

				if (readed > 0) {
					final byte temp[] = new byte[readed];
					System.arraycopy(buffer, 0, temp, 0, readed);

					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(activity, new String(temp),
									Toast.LENGTH_SHORT).show();
						}
					});

					vibrator.vibrate(100);
				}

			} catch (IOException e) {
				connectionListener.onDisconnected("Error al leer "
						+ e.getMessage());
				break;
			}
		}
	}

	public void send(byte[] buffer) {
		try {
			outputStream.write(buffer);
		} catch (Exception e) {
			connectionListener.onDisconnected("Error al escribir "
					+ e.getMessage());
		}
	}

	public void cancel() {
		// TODO Auto-generated method stub
		try {
			socket.close();
		} catch (Exception e) {

		}
	}

}
