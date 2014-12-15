package com.avv.apaint;

import android.graphics.Paint;
import android.graphics.Path;

public class TouchLine {
	
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
