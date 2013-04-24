package com.dbstar.util;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class GDPowerManager {
	private static final String TAG = "GDPowerManager";

	private static final String ALARM_PATH = "/sys/class/aml_rtc/alarm";
	private PowerManager.WakeLock sWakeLock;

	public PowerManager.WakeLock createPartialWakeLock(Context context) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
	}

	public PowerManager.WakeLock createFullWakeLock(Context context) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
	}

	public void acquirePartialWakeLock(Context context) {
		Log.d(TAG, "----- PartialWakeLock -----");
		if (sWakeLock != null) {
			return;
		}

		sWakeLock = createPartialWakeLock(context);
		sWakeLock.acquire();
	}

	public void acquireFullWakeLock(Context context) {
		Log.d(TAG, "----- FullWakeLock -----");
		if (sWakeLock != null) {
			return;
		}

		sWakeLock = createFullWakeLock(context);
		sWakeLock.acquire();
	}

	public void releaseWakeLock() {
		if (sWakeLock != null) {
			sWakeLock.release();
			sWakeLock = null;
		}
	}

	public int getAlarm() {
		String str = FileOperation.read(ALARM_PATH);
		return Integer.parseInt(str);
	}

	public void setAlarm(int seconds) {
		FileOperation.write(ALARM_PATH, Integer.toString(seconds));
	}

	public void clearAlarm() {
		FileOperation.write(ALARM_PATH, "0");
	}
}
