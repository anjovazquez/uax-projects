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

		this.presenter = new BluetoothGamePresenter(this);

		this.presenter.resume();
	}

	@Override
	protected void onDestroy() {
		this.presenter.pause();
		super.onDestroy();
	}

	public void onClientRolClick(View view) {
		this.presenter.onClientClickListener(view);
	}

	public void onServerRolClick(View view) {
		this.presenter.onServerClickListener(view);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		this.presenter.onActivityResult(requestCode, resultCode, data);
	}

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

	@Override
	public void renderColorBackground(int color) {
		this.spinColors.getRootView().setBackgroundColor(color);
	}

	@Override
	public void renderClientRolUI() {
		// TODO Auto-generated method stub
		this.spinColors.setVisibility(View.GONE);

		this.tInfo.setVisibility(View.GONE);
		this.bRolClient.setVisibility(View.GONE);
		this.bRolServer.setVisibility(View.GONE);
	}

	@Override
	public void renderServerRolUI() {
		this.spinColors.setVisibility(View.VISIBLE);

		this.tInfo.setVisibility(View.GONE);
		this.bRolClient.setVisibility(View.GONE);
		this.bRolServer.setVisibility(View.GONE);
	}
}
