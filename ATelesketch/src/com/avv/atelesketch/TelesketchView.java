package com.avv.atelesketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

public class TelesketchView extends View {

	final float METER_TO_PIXEL = 10.0f;

	private Path mPath;
	private Paint mPaint;

	float radius = 30;
	float posX = radius;
	float posY = radius;

	float speedX = 0;
	float speedY = 0;

	long lastUpdateTime = 0;

	private void init() {
		mPath = new Path();
		mPaint = new Paint();

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

	}

	public TelesketchView(Context context) {
		super(context);
		init();
	}

	public TelesketchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public TelesketchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawPath(mPath, mPaint);
	}

	public void setData(float x, float y) {

		if (lastUpdateTime == 0) {
			posX = getWidth() / 2;
			posY = getHeight() / 2;

			mPath.moveTo(posX, posY);

			lastUpdateTime = System.currentTimeMillis();
			return;
		}

		long now = System.currentTimeMillis();
		long ellapse = now - lastUpdateTime;
		// if (ellapse > 1000) {
		lastUpdateTime = now;

		speedX += ((y * ellapse) / 1000.0f) * METER_TO_PIXEL;
		speedY += ((x * ellapse) / 1000.0f) * METER_TO_PIXEL;

		posX += ((speedX * ellapse) / 1000.0f);
		posY += ((speedY * ellapse) / 1000.0f);

		if (posX < radius) {
			posX = radius;
			speedX = 0;
		} else if (posX > (getWidth() - radius)) {
			posX = getWidth() - radius;
			speedX = 0;
		}

		if (posY < radius) {
			posY = radius;
			speedY = 0;
		} else if (posY > (getHeight() - radius)) {
			posY = getHeight() - radius;
			speedY = 0;
		}

		mPath.lineTo(posX, posY);
		invalidate();
		// }
	}

}
