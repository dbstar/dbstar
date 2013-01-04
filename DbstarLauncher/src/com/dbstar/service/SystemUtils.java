package com.dbstar.service;

import android.os.SystemProperties;

public class SystemUtils {
	
	private static final String SmartHomePrepertyName = "service.smarthome.started";
	
	public static void startSmartHomeServer() {
		SystemProperties.set(SmartHomePrepertyName, "1");
	}
	
	public static void stopSmartHomeServer() {
		SystemProperties.set(SmartHomePrepertyName, "0");
	}
}
