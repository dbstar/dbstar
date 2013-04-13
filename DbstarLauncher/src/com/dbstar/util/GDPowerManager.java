package com.dbstar.util;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Log;

public class GDPowerManager {
	private static final String TAG = "GDPowerManager";
	private static final String SleepPrepertyName = "dbstar.deepsleep";
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
		if (SystemProperties.getInt(SleepPrepertyName, 0) == 1) {
			Log.d(TAG, "-----PartialWakeLock, DeepSleep -----");
			return;
		}

		Log.d(TAG, "----- PartialWakeLock -----");
		if (sWakeLock != null) {
			return;
		}

		sWakeLock = createPartialWakeLock(context);
		sWakeLock.acquire();
	}

	public void acquireFullWakeLock(Context context) {
		if (SystemProperties.getInt(SleepPrepertyName, 0) == 1) {
			Log.d(TAG, "----- FullWakeLock, DeepSleep -----");
			return;
		}

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
}
