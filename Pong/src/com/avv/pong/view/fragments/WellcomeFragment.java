package com.avv.pong.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.avv.pong.R;
import com.avv.pong.view.activities.OnButtonPressedListener;
import com.avv.pong.view.ui.AnimatedButton;
import com.avv.pong.view.ui.OnAnimationEndListener;

/**
 * Fragment que maneja el menu principal
 * 
 * @author angelvazquez
 * 
 */
public class WellcomeFragment extends Fragment implements
		OnAnimationEndListener {

	private AnimatedButton bNewGame;
	private AnimatedButton bSettings;
	private AnimatedButton bControls;

	private Animation animScale;
	private final OnButtonPressedListener listener;

	public WellcomeFragment(OnButtonPressedListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		this.animScale = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.scale);

		View view = inflater.inflate(R.layout.menu_layout, container, false);
		this.bNewGame = (AnimatedButton) view.findViewById(R.id.bNewGame);
		this.bSettings = (AnimatedButton) view.findViewById(R.id.bSettings);
		this.bControls = (AnimatedButton) view.findViewById(R.id.bControls);

		this.bNewGame.setOnAnimationEndListener(this);
		this.bSettings.setOnAnimationEndListener(this);
		this.bControls.setOnAnimationEndListener(this);

		this.bNewGame.setOnClickListener(new OnButtonClickListener());
		this.bSettings.setOnClickListener(new OnButtonClickListener());
		this.bControls.setOnClickListener(new OnButtonClickListener());
		return view;
	}

	/**
	 * Listener de pulsado de boton, cuando se pulsa se lanza la animacion de
	 * vista del botón
	 * 
	 * 
	 */
	private class OnButtonClickListener implements OnClickListener {

		@Override
		public void onClick(final View v) {

			Animation animScale = AnimationUtils.loadAnimation(
					WellcomeFragment.this.getActivity(), R.anim.scale);

			v.setAnimation(animScale);
			v.startAnimation(WellcomeFragment.this.animScale);
		}
	}

	/*
	 * Cuando la vista (botón) detecta que ha terminado la animación podemos
	 * proseguir con la acción asociada
	 * 
	 * @see com.avv.pong.view.ui.OnAnimationEndListener#onAnimationEnd(int)
	 */
	@Override
	public void onAnimationEnd(int id) {
		WellcomeFragment.this.listener.onButtonPressed(id);
	}

}
