package com.dbstar.model;

import android.os.SystemClock;
import android.util.Log;

public class TDTTimeController {
	private static final String TAG = "TDTTimeController";

	public static void handleTDTTime(long millis) {
		Log.d(TAG, " handleTDTTime : set time: " + millis);
		
		SystemClock.setCurrentTimeMillis(millis);
		
		Log.d(TAG, " handleTDTTime : current time: " + SystemClock.currentThreadTimeMillis());
	}
}
