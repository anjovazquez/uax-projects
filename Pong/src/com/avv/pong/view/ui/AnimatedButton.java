package com.avv.pong.view.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Boton personalizado que admite un listener al que comunicara que una
 * animacion ha terminado
 * 
 * @author angelvazquez
 * 
 */
public class AnimatedButton extends Button {

	private OnAnimationEndListener listener;

	public AnimatedButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AnimatedButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AnimatedButton(Context context) {
		super(context);
	}

	public void setOnAnimationEndListener(OnAnimationEndListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onAnimationEnd() {
		super.onAnimationEnd();
		this.listener.onAnimationEnd(this.getId());
	}

}
