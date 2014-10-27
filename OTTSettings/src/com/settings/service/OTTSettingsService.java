package com.settings.service;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.settings.bean.WifiHotspot;
import com.settings.bean.WifiHotspotConfig;
import com.settings.ethernet.EthernetEnabler;
import com.settings.utils.DataUtils;
import com.settings.utils.DisplaySettings;
import com.settings.utils.LogUtil;
import com.settings.utils.SettingsCommon;
import com.settings.wifihotspot.WifiAdmin;
import com.settings.wifihotspot.WifiApAdmin;

public class OTTSettingsService extends Service{

	private static final String TAG = "OTTSettingsService";	
	private WifiHotspot wifiHotspot = new WifiHotspot();
	private static final String Data_Key_SSID = "com.settings.ssid";
	private static final String Data_Key_SECURITY = "com.settings.security";
	private static final String Data_Key_PWD = "com.settings.password";
	
	private static final String Data_Wireless_Switch = "com.wifi.network.isOpen";
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// 如果上次退出程序之前设置过wifi热点，并且没有关闭，则启动wifi热点，并将上次设置的信息显示在上面
//		LogUtil.d(TAG, "OTTSettingsService-----------onCreate()");
		
		boolean wirelessIsOpen = DataUtils.getPreference(this, Data_Wireless_Switch, true);
		
		if (wirelessIsOpen) {
			if (WifiHotspotConfig.getInstance(this).shouldRestoreWifiHotspot()) {
				LogUtil.d(TAG, "in OTTSettingsService, wifi hotspot is opened");
				String ssid = DataUtils.getPreference(this, Data_Key_SSID, "DbstarAP");
				String password = DataUtils.getPreference(this, Data_Key_PWD, "12345678");
				String security = DataUtils.getPreference(this, Data_Key_SECURITY, "WPA2 PSK");
				wifiHotspot.setSsid(ssid);
				wifiHotspot.setPassword(password);
				wifiHotspot.setSecurity(security);
				
//				LogUtil.d(TAG, "OTTSettingsService-----------ssid=" + ssid);
//				LogUtil.d(TAG, "OTTSettingsService-----------password=" + password);
//				LogUtil.d(TAG, "OTTSettingsService-----------security=" + security);
				
				wifiHotspotConnect(wifiHotspot);
//				LogUtil.d(TAG, "OTTSettingsService-----------open wifi hotspot");			
			} else {
				LogUtil.d(TAG, "OTTSettingsService-----------wifi is opened!");
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(true);
				// TODO:找到上次关机之前连接的ssid，在这看是否有此ssid，如果有就连接
				String connectSsid = wifiManager.getConnectionInfo().getSSID().toString();
				LogUtil.d(TAG, "OTTSettingsService-----------connectSsid = " + connectSsid);
//				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//				NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				
				if (connectSsid != null) {
					wifiManager.reassociate();
				}
				
//				List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
//				
//				LogUtil.d(TAG, "OTTSettingsService-----------configuredNetworks = " + configuredNetworks);						
//				if (configuredNetworks != null) {
//					for (WifiConfiguration config : configuredNetworks) {
//						LogUtil.d(TAG, "OTTSettingsService-----------config.SSID = " + config.SSID.toString());						
//						wifiManager.reassociate();
//					}
//				}
				
			}
		} else {
			LogUtil.d(TAG, "OTTSettingsService-----------wireless is closed! and eth0 should stract!");	
			EthernetManager ethernetManager = (EthernetManager) getSystemService(Context.ETH_SERVICE);
			ethernetManager.setEthEnabled(true);
		}
		
	}
	
	private void wifiHotspotConnect(WifiHotspot wifiHotspot) {
		WifiApAdmin wifiAp = new WifiApAdmin(this);
//				wifiAp.startWifiAp("\"HotSpot\"", "hhhhhh123");
		wifiAp.startWifiAp(wifiHotspot.getSsid(), wifiHotspot.getPassword());
		
		WifiAdmin wifiAdmin = new WifiAdmin(this) {
			
			@Override
			public void onNotifyWifiConnected() {
				LogUtil.d("OTTSettingsService", "have connected success!");
				LogUtil.d("OTTSettingsService", "###############################");
				
			}
			
			@Override
			public void onNotifyWifiConnectFailed() {
				LogUtil.d("OTTSettingsService", "have connected failed!");
				LogUtil.d("OTTSettingsService", "###############################");
				
			}
			
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
				registerReceiver(receiver, filter);
				return null;
			}
			
			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				unregisterReceiver(receiver);
			}					
		};
		
		wifiAdmin.openWifi();
		wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(), getTypeOfSecurity(wifiHotspot));
//				wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(),  WifiAdmin.TYPE_WPA);
	}
	
	private int getTypeOfSecurity(WifiHotspot wifiHotspot) {
		int type = WifiAdmin.TYPE_WPA;
		if (wifiHotspot != null) {
			if (wifiHotspot.getSecurity().equals("Open")) {
				type = WifiAdmin.TYPE_NO_PASSWD;
			} else if (wifiHotspot.getSecurity().equals("WPA PSK")) {
				type = WifiAdmin.TYPE_WEP;								
			} else {				
				type = WifiAdmin.TYPE_WPA;				
			}
		}
		return type;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
