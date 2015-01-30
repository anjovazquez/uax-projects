package com.avv.pong.view.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;

import com.avv.pong.R;
import com.avv.pong.preferences.AppPreferences;
import com.avv.pong.view.fragments.GameControlsFragment;
import com.avv.pong.view.fragments.GameSettingsFragment;
import com.avv.pong.view.fragments.NewGameFragment;
import com.avv.pong.view.fragments.WellcomeFragment;
import com.avv.pong.view.game.GameView;

/**
 * @author angelvazquez
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnButtonPressedListener, SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private GameView mGameView;
	private View fragmentContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.mGameView = (GameView) this.findViewById(R.id.gameView);

		this.fragmentContainer = this.findViewById(R.id.fragment_container);

		WellcomeFragment wellcomeFragment = new WellcomeFragment(this);
		this.getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, wellcomeFragment).commit();

		this.mSensorManager = (SensorManager) this
				.getSystemService(Context.SENSOR_SERVICE);
		if (this.mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			this.mSensor = this.mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.mSensorManager.registerListener(this, this.mSensor,
				SensorManager.SENSOR_DELAY_GAME);
		this.mGameView.onResume();
	}

	@Override
	protected void onPause() {
		this.mSensorManager.unregisterListener(this);
		this.mGameView.onPause();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * Se implementa la interfaz aquí para llevar un control centralizado de la
	 * navegación La actividad principal es listener de los eventos de click de
	 * los botones de los menus que se han creado mediante las transiciones de
	 * fragments, intercambiar los fragments hace que la aplicación sea más
	 * fluída
	 * 
	 * @see com.avv.pong.OnButtonPressedListener#onButtonPressed(int)
	 */
	@Override
	public void onButtonPressed(int buttonPressed) {
		String diff = AppPreferences.getInstance(this).getPreference(
				"Difficulty");
		String control = AppPreferences.getInstance(this).getPreference(
				"Control");
		String lifes = AppPreferences.getInstance(this).getPreference("Lifes");

		FragmentTransaction transaction = this.getSupportFragmentManager()
				.beginTransaction();
		switch (buttonPressed) {
		case R.id.bNewGame:
			NewGameFragment newGameFragment = new NewGameFragment(this);
			transaction.replace(R.id.fragment_container, newGameFragment);
			transaction.addToBackStack(null);
			transaction.setCustomAnimations(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);

			break;
		case R.id.bSettings:
			GameSettingsFragment settingsFragment = new GameSettingsFragment(
					this);
			transaction.replace(R.id.fragment_container, settingsFragment);
			transaction.addToBackStack(null);
			break;
		case R.id.bOnePlayer:
			this.fragmentContainer.setVisibility(View.GONE);

			int d = diff != null ? Integer.valueOf(diff) : 6;
			int l = lifes != null ? Integer.valueOf(lifes) : 3;
			int c = 0;
			if (control.equals("Sensor")) {
				c = 1;
			}

			this.mGameView.startNewGame(1, l, d, c);
			break;
		case R.id.bTwoPlayers:
			d = diff != null ? Integer.valueOf(diff) : 6;
			l = lifes != null ? Integer.valueOf(lifes) : 3;
			c = 0;
			if (control.equals("Sensor")) {
				c = 1;
			}

			this.fragmentContainer.setVisibility(View.GONE);
			this.mGameView.startNewGame(2, l, d, c);
			break;
		case R.id.bControls:
			GameControlsFragment controlsFragment = new GameControlsFragment(
					this);
			transaction.replace(R.id.fragment_container, controlsFragment);
			transaction.addToBackStack(null);
			break;

		default:
			break;
		}

		transaction.commit();
	}

	/*
	 * La operación de callback que nos dice que se ha cargado una nueva imagen
	 * para poner de fondo
	 * 
	 * @see
	 * com.avv.pong.OnButtonPressedListener#onBackgroundLoaded(android.graphics
	 * .Bitmap)
	 */
	@Override
	public void onBackgroundLoaded(Bitmap bitmap) {
		bitmap = Bitmap.createScaledBitmap(bitmap,
				new Double(this.mGameView.getWidth() * 0.8).intValue(),
				new Double(this.mGameView.getHeight() * 0.8).intValue(), true);
		Drawable d = new BitmapDrawable(bitmap);
		this.findViewById(R.id.main_layout).setBackgroundDrawable(d);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		this.mGameView.sensorMovement(event.values[0]);
	}

	/*
	 * Cuando se destruye la actividad liberamos recursos
	 * 
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.mGameView.releaseResources();
	}

}
