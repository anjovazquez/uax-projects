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
 * Controla el menú de nueva partida
 * 
 * @author angelvazquez
 * 
 */
public class NewGameFragment extends Fragment implements OnAnimationEndListener {
	private final OnButtonPressedListener listener;
	private AnimatedButton bOnePlayer;
	private AnimatedButton bTwoPlayers;

	public NewGameFragment(OnButtonPressedListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_newgame_layout, container,
				false);
		this.bOnePlayer = (AnimatedButton) view.findViewById(R.id.bOnePlayer);
		this.bTwoPlayers = (AnimatedButton) view.findViewById(R.id.bTwoPlayers);
		this.bOnePlayer.setOnAnimationEndListener(this);
		this.bTwoPlayers.setOnAnimationEndListener(this);

		this.bOnePlayer.setOnClickListener(new OnButtonClickListener());
		this.bTwoPlayers.setOnClickListener(new OnButtonClickListener());
		return view;
	}

	/**
	 * Inicia animación a la vista seleccionada
	 * 
	 * @author angelvazquez
	 * 
	 */
	private class OnButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Animation animScale = AnimationUtils.loadAnimation(
					NewGameFragment.this.getActivity(), R.anim.rotate);

			NewGameFragment.this.getView().findViewById(v.getId())
					.setAnimation(animScale);
			NewGameFragment.this.getView().findViewById(v.getId())
					.startAnimation(animScale);
		}
	}

	/*
	 * Cuando termina la animación se sigue con la acción
	 * 
	 * @see com.avv.pong.view.ui.OnAnimationEndListener#onAnimationEnd(int)
	 */
	@Override
	public void onAnimationEnd(int id) {
		NewGameFragment.this.listener.onButtonPressed(id);
	}

}
