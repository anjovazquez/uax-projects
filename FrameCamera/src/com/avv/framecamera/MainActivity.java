package com.avv.framecamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements PictureCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void takePhoto(View button) {
		((CameraPreview) findViewById(R.id.camera_preview)).getCamera()
				.takePicture(null, null, this);
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "picture_" + date + ".jpg";
		String photoPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + photoFile;

		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		
		Config config = bitmap.getConfig();
		Bitmap bitmapDecorate = Bitmap.createBitmap(
				new Double(bitmap.getWidth() * 1.2).intValue(), new Double(
						bitmap.getHeight() * 1.2).intValue(), config);
		bitmap = overlay(bitmapDecorate, bitmap);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		
		try {
			FileOutputStream fos = new FileOutputStream(photoPath);
			fos.write(byteArray);
			fos.close();
		} catch (IOException e) {
			Toast.makeText(this, "No se pudo guardar el archivo",
					Toast.LENGTH_LONG).show();
		}

		Toast.makeText(this, "La foto se guardó en: " + photoPath,
				Toast.LENGTH_LONG).show();

//		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);

		

		camera.startPreview();
	}

	private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(),
				bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawColor(getResources().getColor(android.R.color.holo_blue_bright));		
		
		int cx = (bmp1.getWidth() - bmp2.getWidth()) >> 1; 
	    int cy = (bmp1.getHeight() - bmp2.getHeight()) >> 1;
	    
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, cx, cy, null);
		return bmOverlay;
	}

}
