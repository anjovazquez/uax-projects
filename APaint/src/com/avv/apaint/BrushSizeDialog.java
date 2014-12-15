package com.avv.apaint;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

public class BrushSizeDialog extends DialogFragment {

	public interface OnSizeChangedListener {
		void sizeChanged(int size);
	}

	private OnSizeChangedListener mListener;
	private int mInitialSize;

	private SeekBar brushSizeSeekBar;

	public BrushSizeDialog(OnSizeChangedListener listener, int initialSize) {
		mListener = listener;
		mInitialSize = initialSize;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle("Tamaño de pincel");
		View view = inflater.inflate(R.layout.brush_chooser_dialog, container,
				false);
		
		brushSizeSeekBar = (SeekBar)view.findViewById(R.id.brush_size);
		brushSizeSeekBar.setProgress(mInitialSize);

		((Button) view.findViewById(R.id.bOk))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mListener.sizeChanged(brushSizeSeekBar.getProgress());
						BrushSizeDialog.this.dismiss();
					}
				});

		((Button) view.findViewById(R.id.bCancel))
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						BrushSizeDialog.this.dismiss();
					}
				});

		return view;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(savedInstanceState);
	}

}
