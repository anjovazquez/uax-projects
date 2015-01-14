package com.avv.bluetoothgame.view.adapter;

import java.util.List;

import com.avv.bluetoothgame.R;
import com.avv.bluetoothgame.R.id;
import com.avv.bluetoothgame.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ColorsAdapter extends ArrayAdapter<PaintColor> {

	Context context;
	LayoutInflater inflater;

	public ColorsAdapter(Context context, List<PaintColor> colors) {
		super(context, R.layout.color_item_dropdown_list, colors);
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.color_item_list, null);
		ImageView ivColor = (ImageView) convertView.findViewById(R.id.color);
		TextView tvColor = (TextView) convertView.findViewById(R.id.color_name);
		final PaintColor color = getItem(position);
		if (ivColor != null && color != null && tvColor != null) {
			ivColor.setBackgroundColor(color.getColor());
			tvColor.setText(color.getColorName());
		}
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		convertView = inflater.inflate(R.layout.color_item_dropdown_list, null);
		ImageView ivColor = (ImageView) convertView.findViewById(R.id.color);
		TextView tvColor = (TextView) convertView.findViewById(R.id.color_name);
		final PaintColor color = getItem(position);
		if (ivColor != null && color != null && tvColor != null) {
			ivColor.setBackgroundColor(color.getColor());
			tvColor.setText(color.getColorName());
		}
		return convertView;
	}
}
