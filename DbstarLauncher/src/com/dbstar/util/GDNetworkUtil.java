package com.dbstar.util;

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

	static public String getMacAddress(Context context, boolean isEthernet) {
		Log.d(TAG, "getMacAddress");

		String macAddress = "";
		if (isEthernet) {
			String addressFileName = "/sys/class/net/eth0/address";
			File addressFile = new File(addressFileName);
			if (addressFile.exists()) {
				macAddress = readString(addressFile);
				Log.d(TAG, macAddress);
			}
		} else {
//			WifiManager wifiManager = (WifiManager) context
//					.getSystemService(Context.WIFI_SERVICE);
//			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//			macAddress = wifiInfo.getMacAddress();

			String addressFileName = "/sys/class/net/wlan0/address";
			File addressFile = new File(addressFileName);
			if (addressFile.exists()) {
				macAddress = readString(addressFile);
				Log.d(TAG, macAddress);
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
