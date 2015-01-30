package com.avv.pong.view.activities;

import android.graphics.Bitmap;

public interface OnButtonPressedListener {

	void onButtonPressed(int buttonPressed);

	void onBackgroundLoaded(Bitmap bitmap);
}
