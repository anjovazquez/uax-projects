package com.avv.localization;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class MapInfoFragment extends DialogFragment {

	private String origin;
	private String destination;
	private String distance;

	public MapInfoFragment(String origin, String destination, String distance) {
		this.origin = origin;
		this.destination = destination;
		this.distance = distance;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(
				getResources().getString(R.string.title_map_info_dialog));
		View view = inflater
				.inflate(R.layout.map_info_layout, container, false);
		((TextView)view.findViewById(R.id.distance_title)).setText(getResources().getString(R.string.distance_title,
				origin, destination));
		((TextView)view.findViewById(R.id.distance)).setText(distance);
		return view;
	}
}
