package com.avv.bluetoothgame.view.adapter;

import java.io.Serializable;

/**
 * Objeto serializable que representa un color y su nombre
 * 
 * @author angelvazquez
 * 
 */
public class PaintColor implements Serializable {

	private final int color;
	private final String colorName;

	public PaintColor(int color, String colorName) {
		this.color = color;
		this.colorName = colorName;
	}

	public int getColor() {
		return this.color;
	}

	public String getColorName() {
		return this.colorName;
	}

}
