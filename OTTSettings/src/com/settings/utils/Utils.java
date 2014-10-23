package com.settings.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import android.provider.Settings;

import android.app.admin.DevicePolicyManager;
import android.content.Context;

import com.android.internal.telephony.TelephonyProperties;
import com.android.internal.telephony.TelephonyIntents;
import android.net.ProxyProperties;
import android.net.LinkProperties;
import android.os.SystemProperties;

import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.util.Log;

public class Utils {

	private static final String TAG = "Utils";

	// Audio
	public static boolean platformHasDigitAudio() {
		return SystemProperties.getBoolean("ro.platform.has.digitaudio", false);
	}

	// Video
	public static boolean hasCVBSMode() {
		return SystemProperties.getBoolean("ro.amlogic.has.CvbsOutput", false);
	}

	public static boolean hwHasEthernet() {
		return SystemProperties.getBoolean("hw.hasethernet", false);
	}

	public static boolean platformHasSecurity() {
		return SystemProperties.getBoolean("ro.platform.has.security", true);
	}

	public static boolean platformHasEncrypt() {
		return SystemProperties.getBoolean("ro.platform.has.encrypt", false);
	}

	public static boolean platformHasTvOutput() {
		return SystemProperties.getBoolean("ro.screen.has.tvout", false);
	}

	public static boolean platformHasMbxUiMode() {
		return SystemProperties.getBoolean("ro.platform.has.mbxuimode", false);
	}

	public static int platformHas1080Scale() {
		return SystemProperties.getInt("ro.platform.has.1080scale", 0);
	}

	public static boolean platformHasDefaultTVFreq() {
		return SystemProperties.getBoolean("ro.platform.has.defaulttvfreq", false);
	}

	// TODO
	public static boolean isRadioAllowed(Context c, String type) {
		return true;
	}

	public static boolean isWifiOnly(Context context) {
		return SystemProperties.getBoolean("hw.nophone", true);
	}

	public static boolean isWPA_PSKAllowed(WifiConfiguration wifiConfig) {
		return wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK);
	}

	public static boolean isWPA2_PSKAllowed(WifiConfiguration wifiConfig) {
		return wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK);
	}

	public static void enableWPA2_PSK(WifiConfiguration config) {
		config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
	}

	public static void enableWPA_PSK(WifiConfiguration wifiConfig) {

	}

	/**
	 * Returns the WIFI IP Addresses, if any, taking into account IPv4 and IPv6
	 * style addresses.
	 * 
	 * @param context
	 *            the application context
	 * @return the formatted and comma-separated IP addresses, or null if none.
	 */
	public static String getWifiIpAddresses(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		LinkProperties prop = cm
				.getLinkProperties(ConnectivityManager.TYPE_WIFI);
		return formatIpAddresses(prop);
	}

	public static String getEtherProperties(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		LinkProperties prop = cm
				.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
		return prop.toString();
	}

	/**
	 * Returns the default link's IP addresses, if any, taking into account IPv4
	 * and IPv6 style addresses.
	 * 
	 * @param context
	 *            the application context
	 * @return the formatted and comma-separated IP addresses, or null if none.
	 */
	public static String getDefaultIpAddresses(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		LinkProperties prop = cm.getActiveLinkProperties();
		return formatIpAddresses(prop);
	}

	private static String formatIpAddresses(LinkProperties prop) {
		if (prop == null)
			return null;
		Iterator<InetAddress> iter = prop.getAddresses().iterator();
		// If there are no entries, return null
		if (!iter.hasNext())
			return null;
		// Concatenate all available addresses, comma separated
		String addresses = "";
		while (iter.hasNext()) {
			addresses += iter.next().getHostAddress();
			if (iter.hasNext())
				addresses += ", ";
		}
		return addresses;
	}

	public static boolean hwNoPhone() {
		return SystemProperties.getBoolean("hw.nophone", true);
	}

	public static boolean setValue(String fileName, String value) {
		boolean success = true;
		File file = new File(fileName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			} 
			LogUtil.d("setValue", fileName);
			LogUtil.d("setValue", file.getName() + "");
			FileWriter writer = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(writer, 32);
			try {
				out.write(value);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
			Log.e(TAG, "IOException when write " + fileName);
		}

		return success;
	}

	public static class Proxy {
		public String mHostname;
		public int mPort;
		public String mExclusionList;

		public String getHost() {
			return mHostname;
		}

		public int getPort() {
			return mPort;
		}

		public String getExclusionList() {
			return mExclusionList;
		}

		public static Proxy getProxy(ConnectivityManager cm) {
			ProxyProperties proxy = cm.getGlobalProxy();
			if (proxy != null) {
				Proxy p = new Proxy();
				p.mHostname = proxy.getHost();
				p.mPort = proxy.getPort();
				p.mExclusionList = proxy.getExclusionList();
				return p;
			}

			return null;
		}

		public static void setProxy(ConnectivityManager cm, String hostName,
				int port, String exclList) {

			ProxyProperties p = new ProxyProperties(hostName, port, exclList);
			cm.setGlobalProxy(p);
		}

	}

	public static boolean isUserSetGlobalProxy(DevicePolicyManager dpm) {
		return dpm.getGlobalProxyAdmin() == null;
	}

	public static boolean isInECMMode() {
		 boolean in = Boolean.parseBoolean(
		 SystemProperties.get(TelephonyProperties.PROPERTY_INECM_MODE));
		 return in;
	}

	public static class WifiConfigure {
		public static final int AP_STATEA_ENABLING = WifiManager.WIFI_AP_STATE_ENABLING;
		public static final int AP_STATEA_ENABLED = WifiManager.WIFI_AP_STATE_ENABLED;
		public static final int AP_STATEA_DISABLING = WifiManager.WIFI_AP_STATE_DISABLING;
		public static final int AP_STATEA_DISABLED = WifiManager.WIFI_AP_STATE_DISABLED;

		public static final int DISABLED_AUTH_FAILURE = WifiConfiguration.DISABLED_AUTH_FAILURE;
		public static final int DISABLED_DHCP_FAILURE = WifiConfiguration.DISABLED_DHCP_FAILURE;
		public static final int DISABLED_DNS_FAILURE = WifiConfiguration.DISABLED_DNS_FAILURE;
		public static final int DISABLED_UNKNOWN_REASON = WifiConfiguration.DISABLED_UNKNOWN_REASON;

		public static final int INVALID_NETWORK_ID = WifiConfiguration.INVALID_NETWORK_ID;

		public static int getDisabledReason(WifiConfiguration configure) {
			return configure.disableReason;
		}

		public static boolean isDualBandSupported(WifiManager mgr) {
			return mgr.isDualBandSupported();
		}

		public static int getFrequencyBand(WifiManager mgr) {
			return mgr.getFrequencyBand();
		}

		public static void setFrequencyBand(WifiManager mgr, int value,
				boolean ret) {
			mgr.setFrequencyBand(value, ret);
		}
	}
	
	public static int getInt(String value){
	    int v = 0;
	    try {
            v = Integer.parseInt(value.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return v;
	}
}
