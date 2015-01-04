package com.avv.atelesketch;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class TelesketchActivity extends Activity implements SensorEventListener {

	private static final float SHAKE_THRESHOLD = 500;
	private SensorManager mSensorManager;
	private Sensor mSensor;

	private TelesketchView view;
	private TextView info;
	private long lastUpdateTime;
	private float x;
	private float last_x;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telesketch);

		view = (TelesketchView) findViewById(R.id.telesketchView);
		info = (TextView) findViewById(R.id.info);

		Resources resources = getResources();

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			Toast.makeText(this,
					resources.getString(R.string.gravity_sensor_present),
					Toast.LENGTH_SHORT).show();
		} else {
			if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
				mSensor = mSensorManager
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.telesketch, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
			info.setText("x: " + event.values[0] + " y: " + event.values[1]);
			view.setData(event.values[0]/2, (event.values[1]*1.3f));

			long now = System.currentTimeMillis();
			long ellapse = now - lastUpdateTime;
			if (ellapse > 100) {
				lastUpdateTime = now;

				x = event.values[0];

				float speed = Math.abs(x - last_x )
						/ ellapse * 10000;

				if (speed > SHAKE_THRESHOLD) {
					view.clear();
				}
				last_x = x;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

}
