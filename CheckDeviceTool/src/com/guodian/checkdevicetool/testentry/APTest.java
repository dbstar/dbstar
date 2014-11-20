package com.guodian.checkdevicetool.testentry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.guodian.checkdevicetool.util.APUtil;
import com.guodian.checkdevicetool.util.CMDExecute;
import com.guodian.checkdevicetool.util.MyTimerCheck;
import com.guodian.checkdevicetool.util.WifiAdmin;

public class APTest extends TestTask{

	private WifiManager mWifiManager;
	private static final int TYPE_WPA = 0x13;
	
	public APTest(Context context, Handler handler, int viewId, boolean isAuto) {
		super(context, handler, viewId, isAuto);
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	public void start() {
		super.start();
		
		APUtil.closeWifiAp(mWifiManager);
		startAP();
		
		WifiAdmin wifiAdmin = new WifiAdmin(context) {
			
			@Override
			public void onNotifyWifiConnected() {
			}
			
			@Override
			public void onNotifyWifiConnectFailed() {
			}
			
			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				context.unregisterReceiver(receiver);
			}
			
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
				context.registerReceiver(receiver, filter);
				return null;
			}
		};
		
//		wifiAdmin.openWifi();
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
		wifiAdmin.addNetwork("DbstarAP", "12345678", TYPE_WPA);
	}

	private void startAP() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
		
		stratWifiAP();
		
		MyTimerCheck timerCheck = new MyTimerCheck() {
			
			@Override
			public void doTimerCheckWork() {
				if (APUtil.isWifiAPEnable(mWifiManager)) {
					CMDExecute cmdExecute = new CMDExecute();
					String[] args = {"netcfg"};
					String result = cmdExecute.run(args, "/system/bin/");
					Log.d("APTest", "in fetchDiskInfo(), result = " + result);
					if (result != null && result.contains("wlan0    UP                                192.168.43")) {
						Log.v("APTest", " WifiAP enabled success!");
						sendSuccessMsg();
						this.exit();
					}
				} else {
					Log.v("APTest", " WifiAP enabled failed!");
				}
			}
			
			@Override
			public void doTimeOutWork() {	
				Log.v("APTest", " WifiAP enabled failed!");
				sendFailMsg(null);
				this.exit();
				
			}
		};

		timerCheck.start(15, 1000);
	}
	
	private void stratWifiAP() {
		try {
			Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
			WifiConfiguration netConfig = new WifiConfiguration();
			netConfig.SSID = "DbstarAP";
			netConfig.preSharedKey = "12345678";
			netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			
			// 激活热点
			boolean invokeAP = (Boolean) method.invoke(mWifiManager, netConfig, true);
			Log.d("APTest", "in stratWifiAp, invokeAP = " + invokeAP);
		} catch (IllegalArgumentException e) {
			Log.d("APTest", "in stratWifiAp found Exception = " + e);
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.d("APTest", "in stratWifiAp found Exception = " + e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			Log.d("APTest", "in stratWifiAp found Exception = " + e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.d("APTest", "in stratWifiAp found Exception = " + e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() {
		super.stop();
	}
	
}
