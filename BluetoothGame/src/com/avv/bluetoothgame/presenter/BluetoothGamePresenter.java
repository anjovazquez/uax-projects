package com.avv.bluetoothgame.presenter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.View;

import com.avv.bluetoothgame.receiver.BluetoothDeviceSelectedReceiver;
import com.avv.bluetoothgame.receiver.ObservableDevice;
import com.avv.bluetoothgame.threads.ClientConnectionThread;
import com.avv.bluetoothgame.threads.ConnectedThread;
import com.avv.bluetoothgame.threads.ServerConnectionThread;
import com.avv.bluetoothgame.view.BluetoothGameView;
import com.avv.bluetoothgame.view.adapter.PaintColor;

/**
 * La clase Presenter en este caso ejecuta la logica necesaria y renderiza en
 * base a los resultados la vista a la que esta ligada Si fuera un ejercicio mas
 * amplio podriamos tener un modelo en el que se definieran casos de uso el
 * Presenter se encargaria de mantener separada la logica de negocio de las
 * operaciones en la interfaz grafica
 * 
 * @author angelvazquez
 * 
 */
public class BluetoothGamePresenter implements Presenter, ConnectionListener,
		Observer {

	private static final int SERVER = 0;
	private static final int CLIENT = 1;
	private int rol = -1;

	public static final String NAME = "BluetoothGame";

	public static final UUID[] THE_UUIDS = {
			UUID.fromString("1749b3b2-c60c-49cc-b2f1-c02c3d44bae1"),
			UUID.fromString("284bea18-288b-4f22-b594-0a512caf310d"),
			UUID.fromString("83c69e00-2dfc-4d1d-a8ff-e61d0219a1cf"),
			UUID.fromString("75455f70-e866-42d8-8e2b-2ba2c19d0bfc") };

	final int REQUEST_DISCOVERABLE = 1;
	final int REQUEST_ENABLE_BLUETOOTH = 2;

	private final BluetoothGameView bgView;

	private ConnectedThread connectedThread;
	private ClientConnectionThread clientConnectionThread;
	private ServerConnectionThread serverConnectionThread;

	private BluetoothDeviceSelectedReceiver bluetoothDeviceSelectedReceiver;
	private final Hashtable<String, ConnectedThread> connectedDevices;

	/**
	 * Constructor del Presenter el cual conoce las operaciones de la vista
	 * 
	 * @param view
	 */
	public BluetoothGamePresenter(BluetoothGameView view) {
		this.bgView = view;
		this.connectedDevices = new Hashtable<String, ConnectedThread>();
	}

	private void openSelectDevice() {
		this.bgView
				.getContext()
				.startActivity(
						new Intent(
								"android.bluetooth.devicepicker.action.LAUNCH")
								.putExtra(
										"android.bluetooth.devicepicker.extra.NEED_AUTH",
										false)
								.putExtra(
										"android.bluetooth.devicepicker.extra.FILTER_TYPE",
										0)
								.setFlags(
										Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
	}

	/**
	 * Listener que se activa cuando se acciona el boton de actuar como servidor
	 * lanza un intent para activar el Bluetooth y permanecer visible un tiempo
	 * 
	 * @param view
	 */
	public void onServerClickListener(View view) {
		Intent discoverIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
				120);
		((Activity) this.bgView.getContext()).startActivityForResult(
				discoverIntent, this.REQUEST_DISCOVERABLE);
	}

	/**
	 * Listener que se activa cuando se acciona el boton de actuar como cliente
	 * Si el bluetooth no está activados se lanza un Intent para activarlo si ya
	 * está activo lanzamos la operacion de buscar el servidor con el cual
	 * emparejar
	 * 
	 * @param view
	 */
	public void onClientClickListener(View view) {
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			Intent enableBluetoothIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) this.bgView.getContext()).startActivityForResult(
					enableBluetoothIntent, this.REQUEST_ENABLE_BLUETOOTH);
			return;
		}

		this.openSelectDevice();
	}

	/**
	 * Las respuestas a las peticiones de los intents nos llegarían a la
	 * actividad pero lo que hacemos es que la actividad (que consideramos como
	 * vista) delega en su Presenter la logica
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_DISCOVERABLE:
			if (resultCode != Activity.RESULT_CANCELED) {
				this.serverConnectionThread = new ServerConnectionThread(this);
				this.serverConnectionThread.start();

				this.bgView.renderServerRolUI();
				this.rol = SERVER;
			}
			break;
		case REQUEST_ENABLE_BLUETOOTH:
			if (resultCode == Activity.RESULT_OK) {
				this.openSelectDevice();
			}
			break;
		default:
			break;
		}
	}

	/*
	 * Regisstramos un broadcast receiver delegado como si fuera en la propia
	 * actividad y para capturar el resultado que nos pueda llegar registramos
	 * un Observador en el Presenter de forma que cuando cambie su valor se nos
	 * notifique y podamos extraer el valor del device con el que se ha
	 * conectado
	 * 
	 * @see com.avv.bluetoothgame.presenter.Presenter#resume()
	 */
	@Override
	public void resume() {
		ObservableDevice device = ObservableDevice.getInstance();
		device.addObserver(this);

		this.bluetoothDeviceSelectedReceiver = new BluetoothDeviceSelectedReceiver();
		this.bgView
				.getContext()
				.registerReceiver(
						this.bluetoothDeviceSelectedReceiver,
						new IntentFilter(
								"android.bluetooth.devicepicker.action.DEVICE_SELECTED"));
	}

	/*
	 * Desregistramos el broadcastreceiver de una forma delegada cuando llame la
	 * actividad a este método y tambien cerramos los threads abiertos
	 * 
	 * @see com.avv.bluetoothgame.presenter.Presenter#pause()
	 */
	@Override
	public void pause() {
		this.bgView.getContext().unregisterReceiver(
				this.bluetoothDeviceSelectedReceiver);
		this.closeAllThreads();
	}

	/**
	 * Cerramos los threads y liberamos
	 * 
	 */
	private void closeAllThreads() {
		if (this.serverConnectionThread != null) {
			this.serverConnectionThread.cancel();
			this.serverConnectionThread = null;
		}
		if (this.clientConnectionThread != null) {
			this.clientConnectionThread.cancel();
			this.clientConnectionThread = null;
		}
		if (this.connectedThread != null) {
			this.connectedThread.cancel();
			this.connectedThread = null;
		}
	}

	/************************* Listeners de conexion Bluetooth ***********************/

	/*
	 * Una vez que la conexion se ha producido se llama a este metodo con el
	 * socket de conexion establecido se abre el thread de comunicacion por
	 * donde se enviaran los mensajes.
	 * 
	 * En el caso de que el dispositivo ejerza rol cliente actualizamos la vista
	 * llamando a la interfaz de nuestra vista asociada.
	 * 
	 * En el caso de que el dispositivo ejerza rol servidor guarda la referencia
	 * del dispositivo y el thread correspondiente para enviar los mensajes a
	 * todos sus suscritos
	 * 
	 * @see
	 * com.avv.bluetoothgame.presenter.ConnectionListener#onConnected(android
	 * .bluetooth.BluetoothSocket)
	 */
	@Override
	public void onConnected(BluetoothSocket socket) {
		if (this.rol == CLIENT) {
			((Activity) this.bgView).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					BluetoothGamePresenter.this.bgView.renderClientRolUI();

				}
			});
		}

		this.connectedThread = new ConnectedThread(socket,
				this.bgView.getContext(), this);
		this.connectedThread.start();
		if (this.rol == SERVER) {
			this.connectedDevices.put(socket.getRemoteDevice().getAddress(),
					this.connectedThread);
		}
	}

	/*
	 * Cuando ocurre un fallo deberemos notificarlo mediante un cambio en la
	 * interfaz grafica
	 * 
	 * @see
	 * com.avv.bluetoothgame.presenter.ConnectionListener#onConnectionFailed
	 * (java.lang.String)
	 */
	@Override
	public void onConnectionFailed(String message) {
		((Activity) this.bgView).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				BluetoothGamePresenter.this.bgView.renderErrorUI();

			}
		});

		this.serverConnectionThread = null;
		this.clientConnectionThread = null;
	}

	/*
	 * Cuando ocurre una desconexion deberemos notificarlo mediante un cambio en
	 * la interfaz grafica
	 * 
	 * @see
	 * com.avv.bluetoothgame.presenter.ConnectionListener#onDisconnected(java
	 * .lang.String)
	 */
	@Override
	public void onDisconnected(String message) {
		((Activity) this.bgView).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				BluetoothGamePresenter.this.bgView.renderErrorUI();

			}
		});
		this.connectedThread = null;
	}

	/*
	 * El cliente recibe el mensaje y nuestro Presenter opera con el sobre la
	 * vista comunicándole un cambio
	 * 
	 * @see
	 * com.avv.bluetoothgame.presenter.ConnectionListener#onMessageReceived(
	 * java.lang.String)
	 */
	@Override
	public void onMessageReceived(final String message) {

		if (this.rol == CLIENT) {
			((Activity) this.bgView).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					BluetoothGamePresenter.this.bgView
							.renderColorBackground(Color.parseColor(message));

				}
			});

		}
	}

	/*
	 * El método que nos avisa que el objeto observado ha cambiado deberemos
	 * tomar una decisión, al consultarlo deberemos obtener el dispositivo a
	 * conectar e iniciamos un thread de conexion cliente para intentar la
	 * conexion al servidor
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable observable, Object paramObject) {
		ObservableDevice observableObject = ObservableDevice.getInstance();

		if (this.clientConnectionThread != null) {
			this.clientConnectionThread.cancel();
		}

		this.clientConnectionThread = new ClientConnectionThread(
				observableObject.getDevice(), this);
		this.clientConnectionThread.start();

		this.rol = CLIENT;
	}

	/**
	 * Enviamos el color seleccionado a los clientes. Si el rol es servidor y
	 * hay dispositivos conectados se envia el mensaje, en este caso el color
	 * seleccionado
	 * 
	 * @param paintColor
	 */
	public void changeColor(PaintColor paintColor) {
		if ((this.rol == CLIENT) && (this.connectedThread != null)) {
			String hexColor = String.format("#%06X",
					(0xFFFFFF & paintColor.getColor()));
			this.connectedThread.send(hexColor.getBytes());
		}

		if ((this.rol == SERVER) && !this.connectedDevices.isEmpty()) {
			for (Enumeration<String> en = this.connectedDevices.keys(); en
					.hasMoreElements();) {
				String key = en.nextElement();
				ConnectedThread th = this.connectedDevices.get(key);
				String hexColor = String.format("#%06X",
						(0xFFFFFF & paintColor.getColor()));
				// this.connectedThread.send(hexColor.getBytes());
				th.send(hexColor.getBytes());
			}
		}

	}

}
