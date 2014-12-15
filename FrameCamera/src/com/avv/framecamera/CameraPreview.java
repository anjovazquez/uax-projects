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

public class CameraPreview extends FrameLayout implements
		SurfaceHolder.Callback {

	private SurfaceView surfaceView;

	private Camera camera;

	private final int REAR_CAMERA = 0;
	private final int FRONT_CAMERA = 1;
	private int activeCameraId = REAR_CAMERA;

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		createCamera();
		if (camera == null) {
			return;
		}

		surfaceView = new SurfaceView(context);
		addView(surfaceView);

		SurfaceHolder holder = surfaceView.getHolder();
		holder.addCallback(this);

		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.setKeepScreenOn(true);
	}

	public void createCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
		}

		if (getContext().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			try {
				this.camera = Camera.open(activeCameraId);
			} catch (Exception e) {
				Toast.makeText(getContext(), "Error al abrir la cámara",
						Toast.LENGTH_LONG).show();
				return;
			}
		} else {
			Toast.makeText(getContext(), "No hay cámara", Toast.LENGTH_LONG)
					.show();
			return;
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
		} catch (Exception e) {
			Log.d("Error al poner la vista previa de la cámara: ",
					e.getMessage());
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if(holder.getSurface() == null){
			return;
		}
		
		camera.stopPreview();
		
		Display display = ((WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(0, info);
		int cameraOrientation = info.orientation;
		
		Size cameraSize = camera.getParameters().getPreviewSize();
		
		if(display.getRotation() == Surface.ROTATION_0 || display.getRotation() == Surface.ROTATION_180 && (cameraOrientation == 90 || cameraOrientation == 270)){
			cameraSize.width = camera.getParameters().getPreviewSize().height;
			cameraSize.height = camera.getParameters().getPreviewSize().width;
			
			camera.setDisplayOrientation(90);
			
			float ratio = (float) cameraSize.width / (float) cameraSize.height;
			
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (height*ratio), height, Gravity.CENTER);
			surfaceView.setLayoutParams(params);
			
			try{
				camera.setPreviewDisplay(holder);
				camera.startPreview();
			} catch (Exception e){
				Log.d("CameraPreview", "Error al mostrar vista previa: "+e.getMessage());
			}
		
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
		camera = null;

		holder.removeCallback(this);
	}

}
