package com.dbstar.widget;

import android.content.Context;
import android.util.AttributeSet;

public class GDMenuGallery extends GDLoopGallery {

	private static final String TAG = "GDMenuGallery";

	public GDMenuGallery(Context context) {
		super(context);
		
		initialize();
	}
	
//	public GDMenuGallery(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		
//		initialize();
//	}

	public GDMenuGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initialize();
	}
	
	private void initialize() {
	}
	
	public boolean moveToNext() {
//		Log.d(TAG, "moveToNext");
		return super.moveToNext();
	}
	
	public boolean moveToPrev() {
//		Log.d(TAG, "moveToPrev");
		return super.moveToPrev();
	}
}
