package com.avv.atelesketch;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class TelesketchActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private TelesketchView view;
	private TextView info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telesketch);

		view = (TelesketchView) findViewById(R.id.telesketchView);
		info = (TextView) findViewById(R.id.info);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			Toast.makeText(this, "Gravity sensor", Toast.LENGTH_SHORT).show();
		} else {
			if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
				mSensor = mSensorManager
						.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				Toast.makeText(this, "Accelometer sensor", Toast.LENGTH_SHORT)
						.show();
			} else {

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.telesketch, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub

		// Toast.makeText(this,
		// "x: "+event.values[0]+" y: "+event.values[1]+" z: "+event.values[2],
		// Toast.LENGTH_SHORT).show();
		info.setText("x: " + event.values[0] + " y: " + event.values[1]);
		view.setData(event.values[0], event.values[1]);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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
