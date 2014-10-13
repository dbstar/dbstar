package com.settings.bean;

import android.content.Context;

import com.settings.utils.DataUtils;

public class WifiHotspotConfig {
	private static final String Data_Key_IsOpenWifiHotsput = "com.settings.restoreWifiHotspot";
	
	private static WifiHotspotConfig instance;
	
	// 启动应用是否恢复WiFi热点
	private boolean restoreWifiHotspot = true;
	
	private WifiHotspotConfig(Context context) {
		restoreWifiHotspot = DataUtils.getPreference(context, Data_Key_IsOpenWifiHotsput, true);
	}
	
	/**
	 * 单例，取得一个实例
	 */
	public synchronized static WifiHotspotConfig getInstance(Context context) {
		if (instance == null) {
			instance = new WifiHotspotConfig(context);
		}
		return instance;
	}
	
	public boolean shouldRestoreWifiHotspot() {
		return restoreWifiHotspot;
	}
	
	public void setRestoreWifiHotspot(Context context, boolean isRestore) {
		if (this.restoreWifiHotspot == isRestore) {
			return;
		}
		this.restoreWifiHotspot = isRestore;
		
		DataUtils.savePreference(context, Data_Key_IsOpenWifiHotsput, isRestore);
	}
}
