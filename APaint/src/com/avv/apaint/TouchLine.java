package com.avv.apaint;

import java.io.Serializable;

import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;

public class TouchLine implements Serializable {
	
	private Path path;
	private Paint paint;
	
	public TouchLine(){
		
	}	

	public TouchLine(Path path, Paint paint) {
		this.path = path;
		this.paint = paint;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

}
