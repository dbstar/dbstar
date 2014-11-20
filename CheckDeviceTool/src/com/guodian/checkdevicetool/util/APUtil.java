package com.guodian.checkdevicetool.util;

import java.lang.reflect.Method;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class APUtil {
	public static boolean isWifiAPEnable(WifiManager wifiManager) {
		try {
			Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
			method.setAccessible(true);
			boolean isEnable = (Boolean) method.invoke(wifiManager);
			
			return isEnable;
		} catch (Exception e) {
			Log.d("APTest", "in isWifiApEnabled() found Exception =" + e);
			e.printStackTrace();
		}
		return false;
	}
	
	// 创建热点之前，要先关闭热点服务
	public static void closeWifiAp(WifiManager wifiManager) {
		if (isWifiAPEnable(wifiManager)) {
			try {
				Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
				method.setAccessible(true);

				WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);

				Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
				method2.invoke(wifiManager, config, false);
			} catch (Exception e) {
				Log.d("APTest", "in closeWifiAp fount Exception = " + e);
				e.printStackTrace();
			}
		}
	}
}
