package com.avv.bluetoothgame.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avv.bluetoothgame.R;
import com.avv.bluetoothgame.presenter.BluetoothGamePresenter;
import com.avv.bluetoothgame.view.adapter.ColorsAdapter;
import com.avv.bluetoothgame.view.adapter.PaintColor;

/**
 * Nuestra actividad que será nuestra Vista en el patrón MVP implementa la
 * interfaz de operaciones de vista
 * 
 * @author angel.vazquez
 * 
 */
public class BluetoothGame extends Activity implements BluetoothGameView {

	private BluetoothGamePresenter presenter;

	private Spinner spinColors;

	private TextView tInfo;

	private Button bRolClient;

	private Button bRolServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_bluetooth_game);

		this.tInfo = (TextView) this.findViewById(R.id.welcome_to_bg);
		this.bRolClient = (Button) this.findViewById(R.id.client);
		this.bRolServer = (Button) this.findViewById(R.id.server);

		List<PaintColor> colors = this.initColors();
		this.spinColors = (Spinner) this.findViewById(R.id.colors);
		this.spinColors.setAdapter(new ColorsAdapter(this, colors));
		this.spinColors.setOnItemSelectedListener(new OnItemSelectedListener() {

			/*
			 * Una vez se selecciona un elemento se comunica al Presenter para
			 * que haga lo que necesite con el
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemSelectedListener#onItemSelected
			 * (android.widget.AdapterView, android.view.View, int, long)
			 */
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				PaintColor pColor = (PaintColor) BluetoothGame.this.spinColors
						.getSelectedItem();
				Toast.makeText(
						BluetoothGame.this,
						BluetoothGame.this.spinColors.getSelectedItem()
								.toString(), Toast.LENGTH_LONG).show();
				BluetoothGame.this.presenter.changeColor(pColor);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}

		});

		/**
		 * Inicializamos el Presenter al que le pasamos el conocimiento sobre la
		 * vista
		 */
		this.presenter = new BluetoothGamePresenter(this);

		/**
		 * Delegamos en el Presenter las operaciones tipicas que antes haciamos
		 * en la vista y que se considera que no deberian estar aqui con el fin
		 * de desacoplar
		 */
		this.presenter.resume();
	}

	/*
	 * Una vez se destruye la vista delegamos las operaciones necesarias en el
	 * Presenter
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		this.presenter.pause();
		super.onDestroy();
	}

	/**
	 * Delegamos el listener del boton de actuar como cliente
	 * 
	 * @param view
	 */
	public void onClientRolClick(View view) {
		this.presenter.onClientClickListener(view);
	}

	/**
	 * Delegamos el listener del boton de actuar como servidor
	 * 
	 * @param view
	 */
	public void onServerRolClick(View view) {
		this.presenter.onServerClickListener(view);
	}

	/*
	 * Las respuestas las delegamos en el presenter
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.presenter.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Inicializamos los colores posibles
	 * 
	 * @return
	 */
	private List<PaintColor> initColors() {
		ArrayList<PaintColor> colors = new ArrayList<PaintColor>();
		Resources res = this.getResources();
		colors.add(new PaintColor(Color.BLACK, res.getString(R.string.black)));
		colors.add(new PaintColor(Color.RED, res.getString(R.string.red)));
		colors.add(new PaintColor(Color.YELLOW, res.getString(R.string.yellow)));
		colors.add(new PaintColor(Color.MAGENTA, res
				.getString(R.string.magenta)));
		colors.add(new PaintColor(Color.GREEN, res.getString(R.string.green)));
		colors.add(new PaintColor(Color.BLUE, res.getString(R.string.blue)));
		colors.add(new PaintColor(Color.WHITE, res.getString(R.string.white)));
		return colors;
	}

	@Override
	public Context getContext() {
		return this;
	}

	/*
	 * Cambia el color del fondo
	 * 
	 * @see
	 * com.avv.bluetoothgame.view.BluetoothGameView#renderColorBackground(int)
	 */
	@Override
	public void renderColorBackground(int color) {
		this.spinColors.getRootView().setBackgroundColor(color);
	}

	/*
	 * Modifica la UI cuando se actua como cliente
	 * 
	 * @see com.avv.bluetoothgame.view.BluetoothGameView#renderClientRolUI()
	 */
	@Override
	public void renderClientRolUI() {
		// TODO Auto-generated method stub
		this.spinColors.setVisibility(View.GONE);

		this.tInfo.setVisibility(View.GONE);
		this.bRolClient.setVisibility(View.GONE);
		this.bRolServer.setVisibility(View.GONE);
	}

	/*
	 * Modifica la UI cuando se actua como servidor
	 * 
	 * @see com.avv.bluetoothgame.view.BluetoothGameView#renderServerRolUI()
	 */
	@Override
	public void renderServerRolUI() {
		this.spinColors.setVisibility(View.VISIBLE);

		this.tInfo.setVisibility(View.GONE);
		this.bRolClient.setVisibility(View.GONE);
		this.bRolServer.setVisibility(View.GONE);
	}

	/*
	 * Modifica la UI cuando ocurre un error
	 * 
	 * @see com.avv.bluetoothgame.view.BluetoothGameView#renderErrorUI()
	 */
	@Override
	public void renderErrorUI() {

	}
}
