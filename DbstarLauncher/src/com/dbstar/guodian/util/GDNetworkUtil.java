package com.dbstar.guodian.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class GDNetworkUtil {
	
	private static final String TAG = "GDNetworkUtil";
	static public String getMacAddress(Context context, ConnectivityManager connMananger) {
		Log.d(TAG, "getMacAddress");
		
		String macAddress = "";

		NetworkInfo info = connMananger.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			int type = info.getType();
			
			Log.d(TAG, "connected type = " + type + " name " + info.getTypeName());

			if (type == ConnectivityManager.TYPE_WIFI) {
				WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				macAddress = wifiInfo.getMacAddress();
			} else if (type == ConnectivityManager.TYPE_ETHERNET || type == ConnectivityManager.TYPE_MOBILE) {
				String addressFileName = "/sys/class/net/eth0/address";
				File addressFile = new File(addressFileName);
				Log.d(TAG, "1");
				
				if (addressFile.exists()) {
					Log.d(TAG, "2");
					macAddress = readString(addressFile);
					Log.d(TAG, macAddress);
				} else {
					addressFileName = "/sys/class/net/eth1/address";
					addressFile = new File(addressFileName);
					if (addressFile.exists()) {
						macAddress = readString(addressFile);
						Log.d(TAG, macAddress);
					}
				}
			} else {
				//other type;
			}
		}
		return macAddress;
	}

	static private String readString(File file) {
		String value = "";
		try {
			int BUFFER_SIZE = 8192;
			String UTF8 = "utf8";

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), UTF8), BUFFER_SIZE);
			value = br.readLine();
			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}
