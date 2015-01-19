package com.avv.bluetoothgame.presenter;

public interface Presenter {

	/**
	 * Declaramos estos dos métodos que serán visibles para la vista con el fin
	 * de delegar y separar logica y en el caso de hacer operaciones especiales
	 * como registrar/desregistrar listeners de sensores,
	 * establecimiento/cerrado de sockets, etc..
	 */
	void resume();

	/**
	 * Declaramos estos dos métodos que serán visibles para la vista con el fin
	 * de delegar y separar logica y en el caso de hacer operaciones especiales
	 * como registrar/desregistrar listeners de sensores,
	 * establecimiento/cerrado de sockets, etc..
	 */
	void pause();
}
