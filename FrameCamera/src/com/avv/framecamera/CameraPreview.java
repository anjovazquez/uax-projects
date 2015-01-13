package com.avv.framecamera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Vista que hemos creado en la que encapsulamos la superficie de
 * previsualización de la cámara
 * 
 * @author angelvazquez
 * 
 */
public class CameraPreview extends FrameLayout implements
		SurfaceHolder.Callback {

	private SurfaceView surfaceView;
	private Camera camera;

	private final int REAR_CAMERA = 0;
	private final int activeCameraId = this.REAR_CAMERA;

	/*
	 * Creamos la cámara y encapsulamos la superficie de previsualización y
	 * suscribimos nuestra vista a los cambios en la misma
	 */
	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.createCamera();
		if (this.camera == null) {
			return;
		}
		this.surfaceView = new SurfaceView(context);
		this.addView(this.surfaceView);

		SurfaceHolder holder = this.surfaceView.getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setKeepScreenOn(true);
	}

	/*
	 * Si no existe la cámara comprobamos que la cámara está presente en el
	 * dispositivo y si está disponible si ya existe paramos la previsualización
	 * y liberamos
	 */
	public void createCamera() {
		if (this.camera != null) {
			this.camera.stopPreview();
			this.camera.release();
		}

		if (this.getContext().getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			try {
				this.camera = Camera.open(this.activeCameraId);
			} catch (Exception e) {
				Toast.makeText(this.getContext(), "Error al abrir la cámara",
						Toast.LENGTH_LONG).show();
				return;
			}
		} else {
			Toast.makeText(this.getContext(), "No hay cámara",
					Toast.LENGTH_LONG).show();
			return;
		}
	}

	public Camera getCamera() {
		return this.camera;
	}

	/*
	 * Comprobamos si la cámara está disponible en cuanto se crea la superficie
	 * de previsualización y lo relacionamos con la cámara (non-Javadoc)
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder
	 * )
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (this.getCamera() != null) {
			try {
				this.camera.setPreviewDisplay(holder);
				this.camera.startPreview();
			} catch (Exception e) {
				Log.d("Error al poner la vista previa de la cámara: ",
						e.getMessage());
			}
		}
	}

	/*
	 * Si cambia la superficie para que mantenga la relación de aspecto
	 * 
	 * @see
	 * android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder
	 * , int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (holder.getSurface() == null) {
			return;
		}

		this.camera.stopPreview();

		Display display = ((WindowManager) this.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(0, info);
		int cameraOrientation = info.orientation;
		Size cameraSize = this.camera.getParameters().getPreviewSize();
		if (((display.getRotation() == Surface.ROTATION_0) || ((display
				.getRotation() == Surface.ROTATION_180)))
				&& ((cameraOrientation == 90) || (cameraOrientation == 270))) {
			cameraSize.width = this.camera.getParameters().getPreviewSize().height;
			cameraSize.height = this.camera.getParameters().getPreviewSize().width;

			this.camera.setDisplayOrientation(90);
		}

		float ratio = (float) cameraSize.width / (float) cameraSize.height;

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				(int) (height * ratio), height, Gravity.CENTER);
		this.surfaceView.setLayoutParams(params);

		try {
			this.camera.setPreviewDisplay(holder);
			this.camera.startPreview();
		} catch (Exception e) {
			Log.d("CameraPreview",
					"Error al mostrar vista previa: " + e.getMessage());
		}
	}

	/*
	 * Paramos la previsualización y liberamos la cámara si la superficie se
	 * destruye si se sale de la actividad por ejemplo
	 * 
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.
	 * SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		this.camera.stopPreview();
		this.camera.release();
		this.camera = null;

		holder.removeCallback(this);
	}

}
