package com.dbstar.DbstarDVB;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.util.Log;

class DbstarPM {

    private static final String TAG = "DbstarPM";
    private static final String SleepPrepertyName = "dbstar.deepsleep";
    private static PowerManager.WakeLock sWakeLock;

    static PowerManager.WakeLock createPartialWakeLock(Context context) {
        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    static PowerManager.WakeLock createFullWakeLock(Context context) {
        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
    }

    static void acquirePartialWakeLock(Context context) {
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

    static void acquireFullWakeLock(Context context) {
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

    static void releaseWakeLock() {
        if (sWakeLock != null) {
            sWakeLock.release();
            sWakeLock = null;
        }
    }
}
