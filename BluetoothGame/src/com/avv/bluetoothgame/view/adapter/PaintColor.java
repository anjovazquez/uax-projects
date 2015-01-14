package com.avv.bluetoothgame.view.adapter;

import java.io.Serializable;

public class PaintColor implements Serializable {

	private int color;
	private String colorName;

	public PaintColor(int color, String colorName) {
		this.color = color;
		this.colorName = colorName;
	}

	public int getColor() {
		return color;
	}

	public String getColorName() {
		return colorName;
	}

}
