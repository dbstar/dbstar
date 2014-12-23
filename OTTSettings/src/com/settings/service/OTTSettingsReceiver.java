package com.settings.service;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;

import com.settings.bean.UpgradeInfo;
import com.settings.bean.Vapks;
import com.settings.bean.WifiHotspot;
import com.settings.bean.WifiHotspotConfig;
import com.settings.http.HttpConnect;
import com.settings.http.SimpleWorkPool.ConnectWork;
import com.settings.http.SimpleWorkPool.SimpleWorkPoolInstance;
import com.settings.utils.Constants;
import com.settings.utils.DataUtils;
import com.settings.utils.DisplaySettings;
import com.settings.utils.LogUtil;
import com.settings.utils.MD5;
import com.settings.utils.SettingUtils;
import com.settings.utils.SettingsCommon;
import com.settings.wifihotspot.WifiAdmin;
import com.settings.wifihotspot.WifiApAdmin;

public class OTTSettingsReceiver extends BroadcastReceiver {

	private static final String TAG = "OTTSettingsReceiver";	
	private WifiHotspot wifiHotspot = new WifiHotspot();
	private static final String Data_Key_SSID = "com.settings.ssid";
	private static final String Data_Key_SECURITY = "com.settings.security";
	private static final String Data_Key_PWD = "com.settings.password";
	
	private static final String Data_Wireless_Switch = "com.wifi.network.isOpen";
	
	private int mCheckCount = 0;
	private String productSN = null, deviceModel = null, softVersion = null;
	protected UpgradeInfo mUpgradeInfo = null;
	public static long fileTotalSize;
	private boolean upgradeIsSuccess = false;
	private Context mContext;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 4:
				Log.d(TAG, " ------------in handler----------");
				
				// check /cache/command0, /cache/command1 is exist
				CheckUpgradeFileTask task = new CheckUpgradeFileTask();
				task.execute();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		
		if (intent.getAction().equalsIgnoreCase("com.settings.service.OTTSettingsReceiver")) {
			recoveryVideoMode(context);
			
			File file = new File(SettingUtils.IsUpgrading_File);
			if (file.exists()) {
				file.delete();
			}
			
			final Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
				
				@Override
				public void run() {
					if (mCheckCount < 2) {
						Log.d(TAG, " ------------in timerTask and mCheckCount = " + mCheckCount);
						Log.d(TAG, " ------------in timerTask and upgradeIsSuccess = " + upgradeIsSuccess);
						if (!upgradeIsSuccess) {						
							Message message = new Message();
							message.what = 4;
							handler.sendMessage(message);
							mCheckCount ++;
						}
					} else {
						timer.cancel();
					}
				}
				
			};
			
			if (timer != null) {
				timer.schedule(timerTask, 60 * 1000);
			}
		} else if (intent.getAction().equalsIgnoreCase("com.settings.service.action.OTTSettingsService")) {
			// 如果上次退出程序之前设置过wifi热点，并且没有关闭，则启动wifi热点，并将上次设置的信息显示在上面
			boolean wirelessIsOpen = DataUtils.getPreference(context, Data_Wireless_Switch, true);
			
			if (wirelessIsOpen) {
				if (WifiHotspotConfig.getInstance(context).shouldRestoreWifiHotspot()) {
					LogUtil.d(TAG, "in OTTSettingsReceiver, wifi hotspot is opened");
					String ssid = DataUtils.getPreference(context, Data_Key_SSID, "DbstarAP");
					String password = DataUtils.getPreference(context, Data_Key_PWD, "12345678");
					String security = DataUtils.getPreference(context, Data_Key_SECURITY, "WPA2 PSK");
					wifiHotspot.setSsid(ssid);
					wifiHotspot.setPassword(password);
					wifiHotspot.setSecurity(security);
					
//					LogUtil.d(TAG, "OTTSettingsReceiver-----------ssid=" + ssid);
//					LogUtil.d(TAG, "OTTSettingsReceiver-----------password=" + password);
//					LogUtil.d(TAG, "OTTSettingsReceiver-----------security=" + security);
					
					wifiHotspotConnect(context, wifiHotspot);
					LogUtil.d(TAG, "OTTSettingsReceiver-----------open wifi hotspot");			
				} else {
					LogUtil.d(TAG, "OTTSettingsReceiver-----------wifi is opened!");
					WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					wifiManager.setWifiEnabled(true);
					// 找到上次关机之前连接的ssid，在这看是否有此ssid，如果有就连接
					String connectSsid = wifiManager.getConnectionInfo().getSSID().toString();
					LogUtil.d(TAG, "OTTSettingsReceiver-----------connectSsid = " + connectSsid);
					
					if (connectSsid != null) {
						wifiManager.reassociate();
					}
				}
			} else {
				LogUtil.d(TAG, "wireless is closed! and eth0 should stract!");	
				EthernetManager ethernetManager = (EthernetManager) context.getSystemService(Context.ETH_SERVICE);
				ethernetManager.setEthEnabled(true);
			}
		}
		
	}
	private void wifiHotspotConnect(final Context context, WifiHotspot wifiHotspot) {
		WifiApAdmin wifiAp = new WifiApAdmin(context);
//				wifiAp.startWifiAp("\"HotSpot\"", "hhhhhh123");
		wifiAp.startWifiAp(wifiHotspot.getSsid(), wifiHotspot.getPassword());
		
		WifiAdmin wifiAdmin = new WifiAdmin(context) {
			
			@Override
			public void onNotifyWifiConnected() {
				LogUtil.d(TAG, "have connected success!");
				LogUtil.d(TAG, "###############################");
				
			}
			
			@Override
			public void onNotifyWifiConnectFailed() {
				LogUtil.d(TAG, "have connected failed!");
				LogUtil.d(TAG, "###############################");
				
			}
			
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
				context.getApplicationContext().registerReceiver(receiver, filter);
				return null;
			}
			
			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				context.getApplicationContext().unregisterReceiver(receiver);
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

	private void recoveryVideoMode(Context context) {
		String mDefaultFrequency = DataUtils.getPreference(context, "modeFrequecy", "");
		String videoMode = DataUtils.getPreference(context, "modeValue", DisplaySettings.getOutpuMode());
		Log.d("OTTSettingsModeService", "videoMode = " + videoMode + " mDefaultFrequency = " + mDefaultFrequency);
		Intent saveIntent = new Intent(SettingsCommon.ACTION_OUTPUTMODE_CHANGE);
		saveIntent.putExtra(SettingsCommon.OUTPUT_MODE, videoMode);
		context.sendBroadcast(saveIntent);
	}

	private void checkSysUpgradeFile() {
		String content = DataUtils.getCacheContent();
		if (content != null && content.length() > 0) {
			String[] splits = content.split("\n");
			if (splits != null && splits.length >= 2) {
				productSN = splits[0];
				softVersion = splits[1];
				deviceModel = splits[2];
			}
		}

		if (softVersion == null || softVersion.equals("")) {
			softVersion = "2.0.3.1";
		}

		if (productSN == null || productSN.equals("")) {
			// TODO: this sn is not standard
			productSN = "0000000000000066";
		}

		if (deviceModel == null || deviceModel.equals("")) {
			deviceModel = "02";
		}

		String mac = SettingUtils.getLocalMacAddress(true);

		String string = "OEM$" + deviceModel + "$" + productSN + "$" + mac;

		LogUtil.d(TAG, "before encrpt = " + string);
		// md5 calculate
		String md5String = MD5.getMD5(string);

		// put parameters into List, then encode as URL
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
		paramsList.add(new BasicNameValuePair("VAPK", "system"));
		paramsList.add(new BasicNameValuePair("CURVERSION", softVersion));
		// encode
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		String url = Constants.Server_Url_Upgrade + param;

		LogUtil.d(TAG, "url = " + url);

		ConnectWork<UpgradeInfo> work = new ConnectWork<UpgradeInfo>(HttpConnect.POST, url, paramsList) {

			@Override
			public UpgradeInfo processResult(HttpEntity entity) {
				return SettingUtils.parseUpgradeEntity(entity);
			}

			@Override
			public void connectComplete(final UpgradeInfo upgradeInfo) {
				if (upgradeInfo == null || upgradeInfo.getVapksList() == null || upgradeInfo.getVapksList().size() <= 0) {
					return;
				}

				boolean isNeedUpgrade = false;

				if (upgradeInfo.getRc() == 0) {

					mUpgradeInfo = upgradeInfo;

					LogUtil.d("UpgradeTask", "success");

					LogUtil.d(TAG, "softVersion = " + softVersion);

					isNeedUpgrade = SettingUtils.compareSoftVersionAndIsOrNotUpgrade(softVersion, upgradeInfo, isNeedUpgrade);

					LogUtil.d(TAG, "isNeedUpgrade = " + isNeedUpgrade);
					
					if (isNeedUpgrade) {
						upgradeIsSuccess = true;
						upgrade();
					} else // if no need upgrade, write '0' into "/data/dbstar/isupgrade.upgrade"
						SettingUtils.save0ToFile();
					
				} else if (upgradeInfo.getRc() == -9001) {
					LogUtil.d("UpgradeTask", "no upgrade package");
				} else if (upgradeInfo.getRc() == -2101) {
					LogUtil.d("UpgradeTask", "no regist");
				} else if (upgradeInfo.getRc() == -2113) {
					LogUtil.d("UpgradeTask", "MAC is invalid");
				}
			}
		};

		SimpleWorkPoolInstance.instance().execute(work);
	}

	private void upgrade() {
		if (mUpgradeInfo == null) {
			return;
		}

		List<Vapks> list = mUpgradeInfo.getVapksList();
		if (list != null && !list.isEmpty()) {
			for (final Vapks vapks : list) {
				final String upgradeUrl = vapks.getProfileUrl();
				LogUtil.d(TAG, " upgradeUrl = " + upgradeUrl);
				ConnectWork<Boolean> work = new ConnectWork<Boolean>(HttpConnect.GET, upgradeUrl, null) {

					@Override
					public Boolean processResult(HttpEntity entity) {
						boolean success = false;
						
						if (entity == null)
							return false;
						
						try {
							InputStream is = entity.getContent();
							fileTotalSize = entity.getContentLength();
							// save zip file
							success = SettingUtils.SaveFile(mContext, is, fileTotalSize, vapks.getVersion(), false);
						} catch (IOException e) {
							LogUtil.d(TAG, " download upgrade file failed!" + e);
							SettingUtils.save0ToFile();
							fileTotalSize = 0;
						}
						return success;
					}

					@Override
					public void connectComplete(Boolean success) {
						if (success) {
							int mode = vapks.getUpgradeMode();
							// upgrade forced
							Intent intent = new Intent();
							if (mode == 1) {
								intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDForceUpgradeActivity");
							} else {
								intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDUpgradeActivity");
							}
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packge_file", "/cache/upgrade.zip");
							mContext.startActivity(intent);
							upgradeIsSuccess = true;
						} else {
							LogUtil.d(TAG, " save file failed!");
							fileTotalSize = 0;
							upgradeIsSuccess = false;
							
							Intent intent = new Intent(SettingUtils.Sys_Upgrade_Settings_Upgrade_Failed);
							mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
						}
					}
				};
				SimpleWorkPoolInstance.instance().execute(work);
			}
		}
		;
	}
	
	private class CheckUpgradeFileTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean existsCommandFile = CheckUpgradeFile();
			if (existsCommandFile)
				LogUtil.d(TAG, "-----command file exists! ");
			else {
				LogUtil.d(TAG, "-----command file is not exists! ");						
			}
				
			return existsCommandFile;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {				
				Intent intent = new Intent();
				intent.putExtra("packge_file", "/cache/upgrade.zip");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setClassName("com.dbstar", "com.dbstar.app.alert.GDUpgradeActivity");
				mContext.startActivity(intent);
				LogUtil.d(TAG, "-----to  GDUpgradeActivity!");
			} else {
				boolean isNetworkAvailable = SettingUtils.isNetworkAvailable(mContext);
				boolean isUpgrading = SettingUtils.readIsUpgrade();
				LogUtil.d(TAG, "SettingUtils.readUpgradeFile() = " + isUpgrading);
				
				if (isNetworkAvailable) {
					if (!isUpgrading) {
						checkSysUpgradeFile();
						fileTotalSize = 1024000;						
						LogUtil.d(TAG, "-----isUpgrading = " + isNetworkAvailable + ", fileTotalSize" + fileTotalSize);
						
						Intent intent = new Intent(SettingUtils.Sys_Upgrade_Settings_Upgrade);
						intent.putExtra("isUpgrading", isUpgrading);
						intent.putExtra("fileTotalSize", fileTotalSize);
						mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
					}
				} else
					SettingUtils.save0ToFile();
				
			}
		}
	}
	
	private boolean CheckUpgradeFile() {
		// check upgrade package is exist, if not, show a pop message: detect no upgrade package
		// open file, check sda1, sdb1, sdb2, sdcard1 is exist
		// if exist, check dbstar-upgrade.zip is exist
		// TODO: file name is static
		File file = new File("/cache/");
		LogUtil.d(TAG, "---------file.exists() = " + file.exists());
		
		if (file.exists()) {
			File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String fileNmae = pathname.getName();
					String filePath = pathname.getPath();
					if (fileNmae.startsWith("command")) {
//						LogUtil.d(TAG, "-----accept()----filePath = " + filePath);
//						LogUtil.d(TAG, "-----accept()----fileNmae = " + fileNmae);
//						LogUtil.d(TAG, "-----command file exists! ");

						// if /cache/command0 or /cache/command1 exist, return true
						File file = new File(filePath);
						if (file.exists()) {
							if (file.length() > 0) {
								LogUtil.d(TAG, "upgrade command exists!");						
								return true;
							} else {
								LogUtil.d(TAG, "upgrade command exists but length=0");		
								return false;
							}
						} else
							return false;
					} else {
						LogUtil.d(TAG, "upgrade command is not exists!");						
						return false;
					}
				}
			});	
			
			if (files != null && files.length > 0)
				return true;				
			else 
				return false;
		} else {			
			LogUtil.d(TAG, "-----do not exists command file! ");
			return false;
		}
	}
}
