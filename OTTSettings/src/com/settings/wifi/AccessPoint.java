/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.settings.wifi;

import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.settings.ottsettings.R;
import com.settings.utils.Utils;

class AccessPoint implements Comparable<AccessPoint> {
	static final String TAG = "Settings.AccessPoint";

	private static final String KEY_DETAILEDSTATE = "key_detailedstate";
	private static final String KEY_WIFIINFO = "key_wifiinfo";
	private static final String KEY_SCANRESULT = "key_scanresult";
	private static final String KEY_CONFIG = "key_config";

	private static final int[] STATE_SECURED = { R.attr.state_encrypted };
	private static final int[] STATE_NONE = {};

	/**
	 * These values are matched in string arrays -- changes must be kept in sync
	 */
	static final int SECURITY_NONE = 0;
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_EAP = 3;

	enum PskType {
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	String ssid;
	String bssid;
	int security;
	int networkId;
	boolean wpsAvailable = false;

	PskType pskType = PskType.UNKNOWN;

	private WifiConfiguration mConfig;

	ScanResult mScanResult;

	private int mRssi;
	private WifiInfo mInfo;
	private DetailedState mState;

	private Context mContext;
	private TextView mTitleView, mSummaryView, mHighlightTileView,
			mHighlightSummaryView;
	private ImageView mSignalView;

	static int getSecurity(WifiConfiguration config) {
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return SECURITY_PSK;
		}
		if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
				|| config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
			return SECURITY_EAP;
		}
		return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
	}

	private static int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	public String getSecurityString(boolean concise) {
		Context context = mContext;
		switch (security) {
		case SECURITY_EAP:
			return concise ? context.getString(R.string.wifi_security_short_eap) : context.getString(R.string.wifi_security_eap);
		case SECURITY_PSK:
			switch (pskType) {
			case WPA:
				return concise ? context.getString(R.string.wifi_security_short_wpa) : context.getString(R.string.wifi_security_wpa);
			case WPA2:
				return concise ? context.getString(R.string.wifi_security_short_wpa2) : context.getString(R.string.wifi_security_wpa2);
			case WPA_WPA2:
				return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) : context.getString(R.string.wifi_security_wpa_wpa2);
			case UNKNOWN:
			default:
				return concise ? context.getString(R.string.wifi_security_short_psk_generic) : context.getString(R.string.wifi_security_psk_generic);
			}
		case SECURITY_WEP:
			return concise ? context.getString(R.string.wifi_security_short_wep) : context.getString(R.string.wifi_security_wep);
		case SECURITY_NONE:
		default:
			return concise ? "" : context.getString(R.string.wifi_security_none);
		}
	}

	private static PskType getPskType(ScanResult result) {
		boolean wpa = result.capabilities.contains("WPA-PSK");
		boolean wpa2 = result.capabilities.contains("WPA2-PSK");
		if (wpa2 && wpa) {
			return PskType.WPA_WPA2;
		} else if (wpa2) {
			return PskType.WPA2;
		} else if (wpa) {
			return PskType.WPA;
		} else {
			Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
			return PskType.UNKNOWN;
		}
	}

	AccessPoint(Context context, WifiConfiguration config) {
		mContext = context;
		loadConfig(config);
	}

	AccessPoint(Context context, ScanResult result) {
		mContext = context;
		loadResult(result);
	}

	AccessPoint(Context context, Bundle savedState) {
		mContext = context;

		mConfig = savedState.getParcelable(KEY_CONFIG);
		if (mConfig != null) {
			loadConfig(mConfig);
		}

		mScanResult = (ScanResult) savedState.getParcelable(KEY_SCANRESULT);
		if (mScanResult != null) {
			loadResult(mScanResult);
		}

		mInfo = (WifiInfo) savedState.getParcelable(KEY_WIFIINFO);
		if (savedState.containsKey(KEY_DETAILEDSTATE)) {
			mState = DetailedState.valueOf(savedState
					.getString(KEY_DETAILEDSTATE));
		}

		update(mInfo, mState);
	}

	public void saveWifiState(Bundle savedState) {
		savedState.putParcelable(KEY_CONFIG, mConfig);
		savedState.putParcelable(KEY_SCANRESULT, mScanResult);
		savedState.putParcelable(KEY_WIFIINFO, mInfo);

		if (mState != null) {
			savedState.putString(KEY_DETAILEDSTATE, mState.toString());
		}
	}

	private void loadConfig(WifiConfiguration config) {
		ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
		bssid = config.BSSID;
		security = getSecurity(config);
		networkId = config.networkId;

		mRssi = Integer.MAX_VALUE;
		mConfig = config;
	}

	private void loadResult(ScanResult result) {
		ssid = result.SSID;
		bssid = result.BSSID;
		security = getSecurity(result);
		wpsAvailable = security != SECURITY_EAP
				&& result.capabilities.contains("WPS");
		if (security == SECURITY_PSK)
			pskType = getPskType(result);

		networkId = -1;
		mRssi = result.level;
		mScanResult = result;
	}

	public void bindView(View view) {
		Log.d(TAG, "bindView view=" + view);

		mTitleView = (TextView) view.findViewById(R.id.title);
		mSummaryView = (TextView) view.findViewById(R.id.summary);
		mHighlightTileView = (TextView) view.findViewById(R.id.highlight_title);
		mHighlightSummaryView = (TextView) view.findViewById(R.id.highlight_summary);

		mSignalView = (ImageView) view.findViewById(R.id.signal);
		ImageView signal = mSignalView;
		if (mRssi == Integer.MAX_VALUE) {
			signal.setImageDrawable(null);
		} else {
			signal.setImageLevel(getLevel());
			signal.setImageResource(R.drawable.wifi_signal);
			signal.setImageState((security != SECURITY_NONE) ? STATE_SECURED : STATE_NONE, true);
		}
	}

	private void setTitle(String title) {
		mTitleView.setText(title);
		mHighlightTileView.setText(title);
	}

	private void setSummary(String summary) {
		mSummaryView.setText(summary);
		mHighlightSummaryView.setText(summary);
	}

	public int compareTo(AccessPoint other) {

		// Active one goes first.
		if (mInfo != other.mInfo) {
			return (mInfo != null) ? -1 : 1;
		}
		// Reachable one goes before unreachable one.
		if ((mRssi ^ other.mRssi) < 0) {
			return (mRssi != Integer.MAX_VALUE) ? -1 : 1;
		}
		// Configured one goes before unconfigured one.
		if ((networkId ^ other.networkId) < 0) {
			return (networkId != -1) ? -1 : 1;
		}
		// Sort by signal strength.
		int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
		if (difference != 0) {
			return difference;
		}
		// Sort by ssid.
		return ssid.compareToIgnoreCase(other.ssid);
	}

	boolean update(ScanResult result) {
		if (ssid.equals(result.SSID) && security == getSecurity(result)) {
			if (WifiManager.compareSignalLevel(result.level, mRssi) > 0) {
				int oldLevel = getLevel();
				mRssi = result.level;
				if (getLevel() != oldLevel) {
					// notifyChanged();
				}
			}
			// This flag only comes from scans, is not easily saved in config
			if (security == SECURITY_PSK) {
				pskType = getPskType(result);
			}
			return true;
		}
		return false;
	}

	void update(WifiInfo info, DetailedState state) {
		if (info != null && networkId != Utils.WifiConfigure.INVALID_NETWORK_ID
				&& networkId == info.getNetworkId()) {
			mRssi = info.getRssi();
			mInfo = info;
			mState = state;
		} else if (mInfo != null) {
			mInfo = null;
			mState = null;
		}

	}

	int getLevel() {
		if (mRssi == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 4);
	}

	WifiConfiguration getConfig() {
		return mConfig;
	}

	WifiInfo getInfo() {
		return mInfo;
	}

	DetailedState getState() {
		return mState;
	}

	static String removeDoubleQuotes(String string) {
		int length = string.length();
		if ((length > 1) && (string.charAt(0) == '"')
				&& (string.charAt(length - 1) == '"')) {
			return string.substring(1, length - 1);
		}
		return string;
	}

	static String convertToQuotedString(String string) {
		return "\"" + string + "\"";
	}

	/** Updates the title and summary; may indirectly call notifyChanged() */
	public void refresh() {

		Log.d(TAG, "ssid=" + ssid + " mTitleView =" + mTitleView);
		Log.d(TAG, "ssid=" + ssid);

		setTitle(ssid);

		Context context = mContext;
		if (mState != null) { // This is the active connection
			setSummary(Summary.get(context, mState));
		} else if (mRssi == Integer.MAX_VALUE) { // Wifi out of range
			setSummary(context.getString(R.string.wifi_not_in_range));
		} else if (mConfig != null
				&& mConfig.status == WifiConfiguration.Status.DISABLED) {
			switch (Utils.WifiConfigure.getDisabledReason(mConfig)) {
			case Utils.WifiConfigure.DISABLED_AUTH_FAILURE:
				setSummary(context
						.getString(R.string.wifi_disabled_password_failure));
				break;
			case Utils.WifiConfigure.DISABLED_DHCP_FAILURE:
			case Utils.WifiConfigure.DISABLED_DNS_FAILURE:
				setSummary(context
						.getString(R.string.wifi_disabled_network_failure));
				break;
			case Utils.WifiConfigure.DISABLED_UNKNOWN_REASON:
				setSummary(context.getString(R.string.wifi_disabled_generic));
			}
		} else { // In range, not disabled.
			StringBuilder summary = new StringBuilder();
			if (mConfig != null) { // Is saved network
				summary.append(context.getString(R.string.wifi_remembered));
			}

			if (security != SECURITY_NONE) {
				String securityStrFormat;
				if (summary.length() == 0) {
					securityStrFormat = context
							.getString(R.string.wifi_secured_first_item);
				} else {
					securityStrFormat = context
							.getString(R.string.wifi_secured_second_item);
				}
				summary.append(String.format(securityStrFormat,
						getSecurityString(true)));
			}

			if (mConfig == null && wpsAvailable) { // Only list WPS available
													// for unsaved networks
				if (summary.length() == 0) {
					summary.append(context.getString(R.string.wifi_wps_available_first_item));
				} else {
					summary.append(context.getString(R.string.wifi_wps_available_second_item));
				}
			}
			setSummary(summary.toString());
		}
	}

	/**
	 * Generate and save a default wifiConfiguration with common values. Can
	 * only be called for unsecured networks.
	 * 
	 * @hide
	 */
	protected void generateOpenNetworkConfig() {
		if (security != SECURITY_NONE)
			throw new IllegalStateException();
		if (mConfig != null)
			return;
		mConfig = new WifiConfiguration();
		mConfig.SSID = AccessPoint.convertToQuotedString(ssid);
		mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
	}
}
