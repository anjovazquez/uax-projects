package com.avv.localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectMarkerFragment extends DialogFragment {
	
	public interface OnMarkerChangeListener {
		void markerChanged(int marker);
	}

	private SimpleAdapter simpleAdapter;
	private OnMarkerChangeListener listener;
	
	public SelectMarkerFragment(OnMarkerChangeListener listener) {
		this.listener = listener;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String[] from = { "marker", "marker_name" };
		int[] to = { R.id.marker, R.id.marker_name };

		Resources res = getActivity().getResources();
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> marker0 = new HashMap<String, String>();
		marker0.put("marker", Integer.toString(R.drawable.blue_darker_mark));
		marker0.put("marker_name", res.getString(R.string.blue_darker_mark));
		aList.add(marker0);
		HashMap<String, String> marker1 = new HashMap<String, String>();
		marker1.put("marker", Integer.toString(R.drawable.blue_mark));
		marker1.put("marker_name", res.getString(R.string.blue_mark));
		aList.add(marker1);
		HashMap<String, String> marker2 = new HashMap<String, String>();
		marker2.put("marker", Integer.toString(R.drawable.dark_marker));
		marker2.put("marker_name", res.getString(R.string.dark_marker));
		aList.add(marker2);
		HashMap<String, String> marker3 = new HashMap<String, String>();
		marker3.put("marker", Integer.toString(R.drawable.green_darker_mark));
		marker3.put("marker_name", res.getString(R.string.green_darker_mark));
		aList.add(marker3);
		HashMap<String, String> marker4 = new HashMap<String, String>();
		marker4.put("marker", Integer.toString(R.drawable.green_mark));
		marker4.put("marker_name", res.getString(R.string.green_mark));
		aList.add(marker4);
		HashMap<String, String> marker5 = new HashMap<String, String>();
		marker5.put("marker", Integer.toString(R.drawable.orange_mark));
		marker5.put("marker_name", res.getString(R.string.orange_mark));
		aList.add(marker5);
		HashMap<String, String> marker6 = new HashMap<String, String>();
		marker6.put("marker", Integer.toString(R.drawable.pink_mark));
		marker6.put("marker_name", res.getString(R.string.pink_mark));
		aList.add(marker6);
		HashMap<String, String> marker7 = new HashMap<String, String>();
		marker7.put("marker", Integer.toString(R.drawable.red_mark));
		marker7.put("marker_name", res.getString(R.string.red_mark));
		aList.add(marker7);
		simpleAdapter = new SimpleAdapter(getActivity(), aList,
				R.layout.map_mark_item, from, to);

		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.map_mark_list, container, false);
		ListView markerList = (ListView) view
				.findViewById(R.id.map_marker_list);
		markerList.setAdapter(simpleAdapter);

		markerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				HashMap<String, String> item = (HashMap<String, String>) adapter.getItemAtPosition(position);
				listener.markerChanged(Integer.parseInt(item.get("marker")));
				Log.d(getClass().getCanonicalName(), "debug");
				dismiss();
			}
		});
		return view;
	}

}
