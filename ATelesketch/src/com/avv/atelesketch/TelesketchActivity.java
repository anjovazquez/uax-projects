package com.avv.atelesketch;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.avv.atelesketch.ShakeDetector.Listener;

public class TelesketchActivity extends Activity implements
		SensorEventListener, Listener {

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private TelesketchView view;
	private ShakeDetector shakeDetector;

	/*
	 * Comprobamos que disponemos del sensor a utilizar, si no podemos utilizar
	 * el de gravedad utilizaremos el acelerómetro y si no está presente ninguno
	 * lo notificaremos al usuario
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_telesketch);

		this.view = (TelesketchView) this.findViewById(R.id.telesketchView);

		Resources resources = this.getResources();

		this.mSensorManager = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		if (this.mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
			this.mSensor = this.mSensorManager
					.getDefaultSensor(Sensor.TYPE_GRAVITY);
			Toast.makeText(this,
					resources.getString(R.string.gravity_sensor_present),
					Toast.LENGTH_SHORT).show();
		} else {
			if (this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
				this.mSensor = this.mSensorManager
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				Toast.makeText(
						this,
						resources
								.getString(R.string.accelerometer_sensor_present),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this,
						resources.getString(R.string.no_sensors_present),
						Toast.LENGTH_SHORT).show();
			}
		}

		this.shakeDetector = new ShakeDetector(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.telesketch, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/*
	 * Aquí recibimos las actualizaciones del sensor y actualizamos nuestra
	 * vista TelesketchView contenida en el layout de la actividad
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// float readx = event.values[0] / 2;
		// float ready = event.values[1] * 1.3f;

		float readx = event.values[0];
		float ready = event.values[1];
		this.view.setData(readx, ready);
	}

	/*
	 * Registramos nuestra actividad como listener del sensor para volver a
	 * recibir actualizaciones del sensor
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.mSensorManager.registerListener(this, this.mSensor,
				SensorManager.SENSOR_DELAY_GAME);
		this.shakeDetector.start(this.mSensorManager);
	}

	/*
	 * Desregistramos nuestra actividad como listener del sensor para dejar de
	 * recibir actualizaciones que nos reducirían la vida de la batería
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.mSensorManager.unregisterListener(this);
		this.shakeDetector.stop();
	}

	/*
	 * En cuanto nuestro detector nos comunique que ha ocurrido el evento
	 * borramos la pizarra del teleskech
	 * 
	 * @see com.avv.atelesketch.ShakeDetector.Listener#shakeDetected()
	 */
	@Override
	public void shakeDetected() {
		this.view.clear();
		Toast.makeText(this, "Borrando pizarra...", Toast.LENGTH_SHORT).show();
	}

}
