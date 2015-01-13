package com.avv.atelesketch;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

	private final int SHAKE_THRESHOLD = 1600;
	private final int SENSOR_X = 0;
	private final int SENSOR_Y = 1;
	private final int SENSOR_Z = 2;
	private long lastUpdate;
	private float x;
	private float y;
	private float z;
	private float last_x;
	private float last_y;
	private float last_z;

	private SensorManager sensorManager;
	private Sensor accelerometer;

	/*
	 * Registramos un listener a nuestro detector para que en cuanto se produzca
	 * la detección lo notifique mediante una llamada al interesado
	 */
	public interface Listener {
		/** Called on the main thread when the device is shaken. */
		void shakeDetected();
	}

	private final Listener listener;

	public ShakeDetector(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * Cuando cambian los valores del sensor se nos notificará en este método
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		this.detectShake(event);
	}

	/*
	 * El algoritmo utilizado para detectar el agitado del dispositivo tal y
	 * como lo hemos encapsulado podríamos sustituir este método en esta clase
	 * para cambiar la forma de detectarlo como guardando una lista de muestras
	 * de aceleración y detectando un cambio brusco o cualquier otra
	 * implementación
	 */
	private void detectShake(SensorEvent event) {
		long curTime = System.currentTimeMillis();
		if ((curTime - this.lastUpdate) > 100) {
			long diffTime = (curTime - this.lastUpdate);
			this.lastUpdate = curTime;

			this.x = event.values[this.SENSOR_X];
			this.y = event.values[this.SENSOR_Y];
			this.z = event.values[this.SENSOR_Z];

			float speed = (Math
					.abs(((((this.x - this.last_x) + this.y) - this.last_y) + this.z)
							- this.last_z) / diffTime) * 10000;

			if (speed > this.SHAKE_THRESHOLD) {
				this.listener.shakeDetected();
			}
			this.last_x = this.x;
			this.last_y = this.y;
			this.last_z = this.z;
		}
	}

	/**
	 * Se registra como escuchador de los cambios del acelerómetro
	 */
	public boolean start(SensorManager sensorManager) {
		// Already started?
		if (this.accelerometer != null) {
			return true;
		}
		this.accelerometer = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// If this phone has an accelerometer, listen to it.
		if (this.accelerometer != null) {
			this.sensorManager = sensorManager;
			sensorManager.registerListener(this, this.accelerometer,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		return this.accelerometer != null;
	}

	/**
	 * Se desregistra como escuchador de los cambios
	 */
	public void stop() {
		if (this.accelerometer != null) {
			this.sensorManager.unregisterListener(this, this.accelerometer);
			this.sensorManager = null;
			this.accelerometer = null;
		}
	}

}
