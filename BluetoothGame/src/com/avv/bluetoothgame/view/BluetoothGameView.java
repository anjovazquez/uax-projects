package com.avv.bluetoothgame.view;

import android.content.Context;

/**
 * Estas son las operaciones que se pueden realizar en la UI y que deberán ser
 * implementadas por Activities o Fragments considerando estos puramente vista y
 * que no deben llevar código de modelo
 * 
 * @author angelvazquez
 * 
 */
public interface BluetoothGameView {

	Context getContext();

	void renderColorBackground(int color);

	void renderClientRolUI();

	void renderServerRolUI();

	void renderErrorUI();

}
