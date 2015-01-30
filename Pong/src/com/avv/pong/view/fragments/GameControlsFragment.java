package com.avv.pong.view.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.avv.pong.R;
import com.avv.pong.preferences.AppPreferences;
import com.avv.pong.view.activities.OnButtonPressedListener;

/**
 * Fragment que controla el menú de selección de modo de control del juego
 * 
 * @author angelvazquez
 * 
 */
public class GameControlsFragment extends Fragment {

	public GameControlsFragment(OnButtonPressedListener listener) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_control_settings_layout,
				container, false);
		RadioGroup controls = ((RadioGroup) view.findViewById(R.id.rControls));
		this.loadSavedPreferences(controls, "Control");
		controls.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.cFinger:
					AppPreferences.getInstance(
							GameControlsFragment.this.getActivity())
							.savePreference("Control", "Finger");
					break;
				case R.id.cSensor:
					AppPreferences.getInstance(
							GameControlsFragment.this.getActivity())
							.savePreference("Control", "Sensor");
					break;
				default:
					break;
				}

			}
		});
		return view;
	}

	/**
	 * Carga las preferencias si existieran
	 * 
	 * @param options
	 * @param pref
	 */
	public void loadSavedPreferences(RadioGroup options, String pref) {
		String preference = AppPreferences.getInstance(this.getActivity())
				.getPreference(pref);
		if (preference != null) {
			if (pref.equals("Control")) {
				if (preference.equals("Finger")) {
					options.check(R.id.cFinger);
				} else {
					if (preference.equals("Sensor")) {
						options.check(R.id.cSensor);

					}
				}
			}
		}
	}
}
