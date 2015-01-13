package com.avv.framecamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * La actividad principal que controla la cámara
 * 
 * @author angelvazquez
 * 
 */
public class MainActivity extends Activity implements PictureCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
	}

	/*
	 * Método que se lanza cuando se presiona el botón de sacar foto declarado
	 * en el XML en el evento onClick
	 */
	public void takePhoto(View button) {
		((CameraPreview) this.findViewById(R.id.camera_preview)).getCamera()
				.takePicture(null, null, this);
	}

	/*
	 * Método callback que implementamos con la interfaz PictureCallback en el
	 * cual escribimos que queremos hacer con la foto tomada
	 */

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {

		String photoPath = this.generateName();
		Bitmap bitmapDecorate = this.composeBitmap(data);
		this.saveImage(photoPath, bitmapDecorate);
		this.addToGallery(photoPath);

		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
		((ImageView) this.findViewById(R.id.imageView)).setImageBitmap(bitmap);

		camera.startPreview();
	}

	/*
	 * Decodificamos los datos que se nos devuelven tras la foto tomada por la
	 * cámara
	 */
	private Bitmap composeBitmap(byte[] data) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		bitmap = this.overlay(bitmap);
		return bitmap;
	}

	/*
	 * Generamos un nombre a partir de la fecha actual con precisión hasta los
	 * segundos
	 */
	private String generateName() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "picture_" + date + ".jpg";
		String photoPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + photoFile;
		return photoPath;
	}

	/*
	 * Guarda la imagen en el dispositivo
	 */
	private void saveImage(String photoPath, Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
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
	}

	/*
	 * Es necesario notificarlo para que se pueda ver desde la galería
	 */
	private void addToGallery(String photoPath) {
		ContentValues values = new ContentValues();

		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.MediaColumns.DATA, photoPath);

		MainActivity.this.getContentResolver().insert(
				Images.Media.EXTERNAL_CONTENT_URI, values);
	}

	/*
	 * Creamos un bitmap un poco mayor que el decodificado a partir de los datos
	 * que nos envia la cámara y lo pintamos en un canvas, pintamos el fondo del
	 * color del marco y pintamos nuestro bitmap dentro a partir de las
	 * coordenadas (cx,cy) con lo cual nos quedará nuestro bitmap con un marco
	 * que lo rodea del color pintado
	 */
	private Bitmap overlay(Bitmap bitmap) {

		Bitmap bmOverlay = Bitmap.createBitmap(new Double(
				bitmap.getWidth() * 1.2).intValue(),
				new Double(bitmap.getHeight() * 1.2).intValue(), bitmap
						.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawColor(this.getResources().getColor(
				android.R.color.holo_blue_bright));

		int cx = (bmOverlay.getWidth() - bitmap.getWidth()) >> 1;
		int cy = (bmOverlay.getHeight() - bitmap.getHeight()) >> 1;

		canvas.drawBitmap(bmOverlay, new Matrix(), null);
		canvas.drawBitmap(bitmap, cx, cy, null);
		return bmOverlay;
	}

}
