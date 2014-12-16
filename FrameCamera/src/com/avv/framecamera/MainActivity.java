package com.avv.framecamera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
				.getAbsolutePath() + File.separator+ photoFile;

		try {
			FileOutputStream fos = new FileOutputStream(photoPath);
			fos.write(data);
			fos.close();
		} catch (IOException e) {
			Toast.makeText(this, "No se pudo guardar el archivo",
					Toast.LENGTH_LONG).show();
		}
		
		Toast.makeText(this, "La foto se guardó en: "+photoPath, Toast.LENGTH_LONG).show();
		
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);
		
		camera.startPreview();
	}

}
