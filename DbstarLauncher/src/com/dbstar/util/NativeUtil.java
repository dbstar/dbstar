package com.dbstar.util;

import android.os.SystemProperties;
import android.util.Log;

public class NativeUtil {
	private static final String TAG = "NativeUtil";

	private static final String DiskManageCmdPrepertyName = "service.disk_manage.cmd";
	private static final String DiskManageStatePrepertyName = "service.disk_manage.state";
	
	static {
		System.loadLibrary("nativeutils");
	}

	public static native int writeFile(String fileName, String str);
	public static native int runSystem(String command);


	public static int write(String filename, String value) {
		int count = 0;
        if (filename == null) {
            Log.e(TAG, "filename null!");
            return 0;
        }
		count = writeFile(filename, value);

        return count;
    }

	private static void deleteDefaultRouteEth() {
        SystemProperties.set(DiskManageCmdPrepertyName, "route");
        SystemProperties.set(DiskManageStatePrepertyName, "running");
    }

    public static int shell(String command) {
		int ret = 0;
        if (command == null) {
            Log.e(TAG, "filename null!");
			return -1;
        }

		Log.d(TAG, "shell(" + command + ")");
		if (command.equals("ip route del dev eth0"))
			deleteDefaultRouteEth();
		else
			runSystem(command);

		return ret;
    }
}
