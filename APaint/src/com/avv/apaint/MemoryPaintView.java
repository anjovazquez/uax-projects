package com.avv.apaint;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Path;

public class MemoryPaintView {

	private List<TouchLine> touchLines;
	private List<TouchLine> touchLinesRedo;
	private TouchLine currentLine;
	private Path circlePath;
	
	public void saveState(List<TouchLine> touchLines, List<TouchLine> touchLinesRedo, TouchLine currentLine, Path circlePath){
		this.touchLines = touchLines;
		this.touchLinesRedo = touchLinesRedo;
		this.currentLine = currentLine;
		this.circlePath = circlePath;
	}	
	
	public void clear(){
		touchLines = null;
		touchLinesRedo = null;
	}
	
	public Path getCirclePath() {
		return circlePath;
	}

	public List<TouchLine> getTouchLines() {
		return touchLines;
	}

	public List<TouchLine> getTouchLinesRedo() {
		return touchLinesRedo;
	}

	public TouchLine getCurrentLine() {
		return currentLine;
	}

	public void setCurrentLine(TouchLine currentLine) {
		this.currentLine = currentLine;
	}

	private MemoryPaintView () {
		touchLines = new ArrayList<TouchLine>();
		touchLinesRedo = new ArrayList<TouchLine>();
	}
	
	private static MemoryPaintView instance;
	
	public static MemoryPaintView getInstance(){
		if(instance==null){
			instance = new MemoryPaintView();
		}
		return instance;
	}
}
