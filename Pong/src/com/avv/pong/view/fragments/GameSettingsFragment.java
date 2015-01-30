package com.avv.pong.view.fragments;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.avv.pong.R;
import com.avv.pong.preferences.AppPreferences;
import com.avv.pong.view.activities.OnButtonPressedListener;

/**
 * Controla el menú de ajustes Se cargan las preferencias si existen y se
 * modifican en función del pulsado
 * 
 * @author angelvazquez
 * 
 */
public class GameSettingsFragment extends Fragment {

	public static final int LOAD_IMAGE_RESULTS = 1;
	private Button bCustomize;
	private final OnButtonPressedListener listener;

	public GameSettingsFragment(OnButtonPressedListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_settings_layout, container,
				false);
		this.bCustomize = (Button) view.findViewById(R.id.bCustomize);
		this.bCustomize.setOnClickListener(new OnButtonClickListener());

		RadioGroup difficulty = ((RadioGroup) view
				.findViewById(R.id.rDifficulty));
		this.loadSavedPreferences(difficulty, "Difficulty");
		difficulty.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.r1:
					AppPreferences.getInstance(
							GameSettingsFragment.this.getActivity())
							.savePreference("Difficulty", "4");
					break;
				case R.id.r2:
					AppPreferences.getInstance(
							GameSettingsFragment.this.getActivity())
							.savePreference("Difficulty", "8");
					break;
				case R.id.r3:
					AppPreferences.getInstance(
							GameSettingsFragment.this.getActivity())
							.savePreference("Difficulty", "12");
					break;
				default:
					break;
				}

			}
		});

		RadioGroup lifes = ((RadioGroup) view.findViewById(R.id.rLifes));
		this.loadSavedPreferences(lifes, "Lifes");
		lifes.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rLifes3:
					AppPreferences.getInstance(
							GameSettingsFragment.this.getActivity())
							.savePreference("Lifes", "3");
					break;
				case R.id.rLifes5:
					AppPreferences.getInstance(
							GameSettingsFragment.this.getActivity())
							.savePreference("Lifes", "5");
					break;

				case R.id.rLifes7:
					AppPreferences.getInstance(
							GameSettingsFragment.this.getActivity())
							.savePreference("Lifes", "7");
					break;
				default:
					break;
				}

			}
		});

		return view;
	}

	private class OnButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			GameSettingsFragment.this.startActivityForResult(intent,
					GameSettingsFragment.LOAD_IMAGE_RESULTS);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case LOAD_IMAGE_RESULTS:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImageUri = data.getData();

				Bitmap image;
				try {
					image = MediaStore.Images.Media.getBitmap(this
							.getActivity().getContentResolver(),
							selectedImageUri);

					this.listener.onBackgroundLoaded(image);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			break;

		default:
			break;
		}
	}

	public void loadSavedPreferences(RadioGroup options, String pref) {
		String preference = AppPreferences.getInstance(this.getActivity())
				.getPreference(pref);
		if (preference != null) {
			if (pref.equals("Lifes")) {
				if (preference.equals("3")) {
					options.check(R.id.rLifes3);
				} else {
					if (preference.equals("5")) {
						options.check(R.id.rLifes5);

					} else {
						if (preference.equals("7")) {
							options.check(R.id.rLifes7);

						}
					}
				}
			}

			if (pref.equals("Difficulty")) {
				if (preference.equals("4")) {
					options.check(R.id.r1);

				} else {
					if (preference.equals("8")) {
						options.check(R.id.r2);

					} else {
						if (preference.equals("12")) {
							options.check(R.id.r3);

						}
					}
				}
			}
		}

	}

}
