package com.avv.bluetoothgame.view.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avv.bluetoothgame.R;

public class ColorsAdapter extends ArrayAdapter<PaintColor> {

	private final LayoutInflater inflater;

	public ColorsAdapter(Context context, List<PaintColor> colors) {
		super(context, R.layout.color_item_dropdown_list, colors);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = this.inflater.inflate(R.layout.color_item_list, null);
		ImageView ivColor = (ImageView) convertView.findViewById(R.id.color);
		TextView tvColor = (TextView) convertView.findViewById(R.id.color_name);
		final PaintColor color = this.getItem(position);
		if ((ivColor != null) && (color != null) && (tvColor != null)) {
			ivColor.setBackgroundColor(color.getColor());
			tvColor.setText(color.getColorName());
		}
		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		convertView = this.inflater.inflate(R.layout.color_item_dropdown_list,
				null);
		ImageView ivColor = (ImageView) convertView.findViewById(R.id.color);
		TextView tvColor = (TextView) convertView.findViewById(R.id.color_name);
		final PaintColor color = this.getItem(position);
		if ((ivColor != null) && (color != null) && (tvColor != null)) {
			ivColor.setBackgroundColor(color.getColor());
			tvColor.setText(color.getColorName());
		}
		return convertView;
	}
}
