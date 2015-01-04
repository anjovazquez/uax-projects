package com.avv.apaint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.avv.apaint.BrushSizeDialog.OnSizeChangedListener;
import com.avv.apaint.ColorPickerDialog.OnColorChangedListener;

public class PaintActivity extends Activity implements OnColorChangedListener,
		OnSizeChangedListener {

	private PaintView paintView;
	private ShareActionProvider mShareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paint);
		paintView = (PaintView) findViewById(R.id.paintView);
	}

	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.paint, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_brush_color:
			ColorPickerDialog colorPicker = new ColorPickerDialog(this,
					paintView.getBrushColor());
			colorPicker.show(getFragmentManager(), "color_chooser");
			break;
		case R.id.action_brush_size:
			BrushSizeDialog dialog = new BrushSizeDialog(this,
					paintView.getBrushSize());
			dialog.show(getFragmentManager(), "size_chooser");
			break;
		case R.id.action_recycle:
			paintView.clear();
			break;
		case R.id.action_undo:
			paintView.undo();
			break;
		case R.id.action_redo:
			paintView.redo();
			break;

		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void colorChanged(int color) {
		paintView.setBrushColor(color);
	}

	@Override
	public void sizeChanged(int size) {
		paintView.setBrushSize(size);
	}
}
