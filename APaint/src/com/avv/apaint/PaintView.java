package com.avv.apaint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PaintView extends View {

	private Paint circlePaint;
	private Path circlePath;
	
	

	private boolean clear = false;

	private List<TouchLine> mTouchLines;
	private List<TouchLine> mTouchLinesRedo;
	private TouchLine currentLine;
	
	private Bitmap bitmap;

	private void init() {

		
		mTouchLines = new ArrayList<TouchLine>();
		mTouchLinesRedo = new ArrayList<TouchLine>();
		Path mPath = new Path();
		Paint mPaint = new Paint();
		// mCanvas = new Canvas();

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.GREEN);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		mTouchLines.add(currentLine = new TouchLine(mPath, mPaint));

		circlePaint = new Paint();
		circlePath = new Path();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.BLUE);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);
	}

	public PaintView(Context context) {
		super(context);
		init();
	}

	public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public PaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (clear) {
			canvas.drawColor(this.getContext().getResources()
					.getColor(R.color.red_light));
			mTouchLines.clear();
			mTouchLinesRedo.clear();
			mTouchLines.add(new TouchLine(preparePath(), preparePaint()));
			clear = false;
		}
		if (!mTouchLines.isEmpty()) {
			for (TouchLine t : mTouchLines) {
				canvas.drawPath(t.getPath(), t.getPaint());
			}
		}
		canvas.drawPath(circlePath, circlePaint);
		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		Log.d(getClass().getCanonicalName(), "X: " + x + " Y:" + y);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			currentLine = new TouchLine(preparePath(), preparePaint());
			mTouchLines.add(currentLine);
			// mPath.moveTo(x, y);
			currentLine.getPath().moveTo(x, y);
			mX = x;
			mY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			currentLine.getPath().quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
			circlePath.reset();
			circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
			break;
		case MotionEvent.ACTION_UP:
			currentLine.getPath().lineTo(mX, mY);
			// mPath.lineTo(mX, mY);
			circlePath.reset();
			saveBitmapView();
			break;
		default:
			break;
		}		
		invalidate();
		return true;
	}

	private Paint preparePaint() {
		Paint mPaint = new Paint();
		mPaint.setColor(currentLine.getPaint().getColor());
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(currentLine.getPaint().getStrokeWidth());
		return mPaint;
	}

	private Path preparePath() {
		Path mPath = new Path();
		return mPath;
	}

	private float mX, mY;

	public void setBrushColor(int color) {
		currentLine = new TouchLine(preparePath(), preparePaint());
		currentLine.getPaint().setColor(color);
	}

	public int getBrushColor() {
		return currentLine.getPaint().getColor();
	}

	public void setBrushSize(int size) {
		currentLine = new TouchLine(preparePath(), preparePaint());
		currentLine.getPaint().setStrokeWidth(size);
	}

	public int getBrushSize() {
		return new Float(currentLine.getPaint().getStrokeWidth()).intValue();
	}

	public void clear() {
		clear = true;
		invalidate();
	}

	public void undo() {
		if (!mTouchLines.isEmpty()) {
			currentLine = mTouchLines.remove(mTouchLines.size() - 1);
			mTouchLinesRedo.add(currentLine);
			invalidate();
		}
	}

	public void redo() {
		if (!mTouchLinesRedo.isEmpty()) {
			currentLine = mTouchLinesRedo.remove(mTouchLinesRedo.size() - 1);
			mTouchLines.add(currentLine);
			invalidate();
		}
	}
	
	public Uri saveBitmapView(){
		File file = new File(this.getContext().getCacheDir(), "temp.jpeg");
		try {
			bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			this.draw(canvas);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return Uri.parse(file.getAbsolutePath());
	}

}
