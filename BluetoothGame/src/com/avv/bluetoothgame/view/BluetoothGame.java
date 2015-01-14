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
import android.widget.Spinner;

import com.avv.bluetoothgame.R;
import com.avv.bluetoothgame.presenter.BluetoothGamePresenter;
import com.avv.bluetoothgame.view.adapter.ColorsAdapter;
import com.avv.bluetoothgame.view.adapter.PaintColor;

/**
 * @author angel.vazquez
 * 
 */
public class BluetoothGame extends Activity implements BluetoothGameView{

	private BluetoothGamePresenter presenter;

	private Spinner spinColors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth_game);

		List<PaintColor> colors = initColors();
		spinColors = (Spinner) findViewById(R.id.colors);
		spinColors.setAdapter(new ColorsAdapter(this, colors));
		
		presenter = new BluetoothGamePresenter(this);

		presenter.resume();
	}

	@Override
	protected void onDestroy() {
		presenter.pause();
		super.onDestroy();
	}

	public void onClientClickListener(View view) {
		presenter.onClientClickListener(view);
	}

	public void onServerClickListener(View view) {
		presenter.onServerClickListener(view);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);		
		presenter.onActivityResult(requestCode, resultCode, data);		
	}

	private List<PaintColor> initColors() {
		ArrayList<PaintColor> colors = new ArrayList<PaintColor>();
		Resources res = getResources();
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
}
