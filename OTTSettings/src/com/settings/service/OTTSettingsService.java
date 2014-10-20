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

import com.settings.bean.WifiHotspot;
import com.settings.bean.WifiHotspotConfig;
import com.settings.ethernet.EthernetEnabler;
import com.settings.utils.DataUtils;
import com.settings.utils.LogUtil;
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
		
		// 检测/cache/command0、/cache/command1是否存在，如果存在，则进入非强制升级
		CheckUpgradeFileTask task = new CheckUpgradeFileTask();
		task.execute();
		
		// 如果上次退出程序之前设置过wifi热点，并且没有关闭，则启动wifi热点，并将上次设置的信息显示在上面
		LogUtil.d(TAG, "OTTSettingsService-----------onCreate()");
		
		boolean wirelessIsOpen = DataUtils.getPreference(this, Data_Wireless_Switch, true);
		
		if (wirelessIsOpen) {
			if (WifiHotspotConfig.getInstance(this).shouldRestoreWifiHotspot()) {
				LogUtil.d(TAG, "OTTSettingsService-----------wifi hotspot is opened");
				String ssid = DataUtils.getPreference(this, Data_Key_SSID, "DbstarAP");
				String password = DataUtils.getPreference(this, Data_Key_PWD, "12345678");
				String security = DataUtils.getPreference(this, Data_Key_SECURITY, "WPA2 PSK");
				wifiHotspot.setSsid(ssid);
				wifiHotspot.setPassword(password);
				wifiHotspot.setSecurity(security);
				
				LogUtil.d(TAG, "OTTSettingsService-----------ssid=" + ssid);
				LogUtil.d(TAG, "OTTSettingsService-----------password=" + password);
				LogUtil.d(TAG, "OTTSettingsService-----------security=" + security);
				
				wifiHotspotConnect(wifiHotspot);
				LogUtil.d(TAG, "OTTSettingsService-----------open wifi hotspot");			
			} else {
				LogUtil.d(TAG, "OTTSettingsService-----------wifi is opened!");
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				wifiManager.setWifiEnabled(true);
				// TODO:找到上次关机之前连接的ssid，在这看是否有此ssid，如果有就连接
				String connectSsid = wifiManager.getConnectionInfo().getSSID().toString();
				LogUtil.d(TAG, "OTTSettingsService-----------connectSsid = " +connectSsid);
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
	
	private class CheckUpgradeFileTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			return CheckUpgradeFile();
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {				
				Intent intent = new Intent();
				intent.putExtra("packge_file", "/cache/upgrade.zip");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDUpgradeActivity");
				startActivity(intent);
				LogUtil.d("OTTSettingsService", "-----to  GDUpgradeActivity!");
			}
		}
	}
	
	private boolean CheckUpgradeFile() {
		// 检测是否有升级包，如果有则将文件显示出来，否则弹出一个提示“未检测到升级包”
		// 打开文件，判断sda1、sdb1、sdb2、sdcard1是否存在，
		// 如果存在就检测看看dbstar-upgrade.zip是否存在
		// TODO:文件名是写死的，千万不能写错
		File file = new File("/cache/");
		LogUtil.d("OTTSettingsService", "---------file.exists() = " + file.exists());
		
		if (file.exists()) {
			File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String fileNmae = pathname.getName();
					String filePath = pathname.getPath();
					if (fileNmae.startsWith("command")) {
						LogUtil.d("OTTSettingsService", "-----accept()----filePath = " + filePath);
						LogUtil.d("OTTSettingsService", "-----accept()----fileNmae = " + fileNmae);
						LogUtil.d("OTTSettingsService", "-----command file exists! ");

						// 如果检测到/cache/command0或/cache/command1存在，则返回true
						return true;
					} else {
						LogUtil.d("OTTSettingsService", "-----command file is not exists! ");						
						return false;
					}
				}
			});	
			
			if (files != null && files.length > 0)
				return true;				
			else 
				return false;
		} else {			
			LogUtil.d("OTTSettingsService", "-----exists command file! ");
			return false;
		}
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
