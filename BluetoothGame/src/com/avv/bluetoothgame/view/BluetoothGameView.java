package com.avv.bluetoothgame.view;

import android.content.Context;

public interface BluetoothGameView {

	Context getContext();

	void renderColorBackground(int color);

	void renderClientRolUI();

	void renderServerRolUI();

}
