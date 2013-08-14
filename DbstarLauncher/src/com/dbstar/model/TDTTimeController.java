package com.dbstar.model;

import com.dbstar.util.LogUtil;

import android.os.SystemClock;

public class TDTTimeController {
	private static final String TAG = "TDTTimeController";

	public static void handleTDTTime(long millis) {
	    LogUtil.d(TAG, " handleTDTTime : set time: " + millis);
		
		SystemClock.setCurrentTimeMillis(millis);
		
		LogUtil.d(TAG, " handleTDTTime : current time: " + SystemClock.currentThreadTimeMillis());
	}
}
