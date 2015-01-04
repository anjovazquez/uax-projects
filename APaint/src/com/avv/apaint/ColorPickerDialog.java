/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.avv.apaint;

import android.os.Bundle;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.*;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class ColorPickerDialog extends DialogFragment {

	public interface OnColorChangedListener {
		void colorChanged(int color);
	}

	private OnColorChangedListener mListener;
	private int mInitialColor;
	private ColorPickerView colorPicker;

	public ColorPickerDialog(OnColorChangedListener listener, int initialColor) {

		mListener = listener;
		mInitialColor = initialColor;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getDialog().setTitle("Pick a Color");
		View view = inflater.inflate(R.layout.color_chooser_dialog, container,
				false);
		
		colorPicker = (ColorPickerView)view.findViewById(R.id.colorPicker);
		colorPicker.setInitialColor(mInitialColor);
		colorPicker.setOnColorChangedListener(new OnColorChangedListener() {
			public void colorChanged(int color) {
				mListener.colorChanged(color);
				dismiss();
			}
		});
		
		return view;
	}
}
