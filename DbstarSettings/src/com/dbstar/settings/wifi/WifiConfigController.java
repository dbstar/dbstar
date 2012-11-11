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

package com.dbstar.settings.wifi;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

import android.content.Context;
import android.content.res.Resources;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkUtils;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WifiInfo;
import android.net.wifi.WpsInfo;
import android.security.Credentials;
import android.security.KeyStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.dbstar.settings.R;

import java.net.InetAddress;
import java.util.Iterator;

/**
 * The class for allowing UIs like {@link WifiDialog} and
 * {@link WifiConfigUiBase} to share the logic for controlling buttons, text
 * fields, etc.
 */
public class WifiConfigController {
	private static final String TAG = "WifiConfigController";

	private static final String KEYSTORE_SPACE = "keystore://";

	private final WifiDialog mConfigUi;
	private final View mView;

	private final AccessPoint mAccessPoint;

	// e.g. AccessPoint.SECURITY_NONE
	private int mAccessPointSecurity;
	private TextView mPasswordView;
	Button mConfirmButton;
	View.OnClickListener mClickListener;

	private IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;
	private ProxySettings mProxySettings = ProxySettings.UNASSIGNED;
	private LinkProperties mLinkProperties = new LinkProperties();

	// True when this instance is used in SetupWizard XL context.
	static boolean requireKeyStore(WifiConfiguration config) {
		if (config == null) {
			return false;
		}
		String values[] = { config.ca_cert.value(), config.client_cert.value(),
				config.private_key.value() };
		for (String value : values) {
			if (value != null && value.startsWith(KEYSTORE_SPACE)) {
				return true;
			}
		}
		return false;
	}

	public WifiConfigController(WifiDialog parent, View view,
			AccessPoint accessPoint, View.OnClickListener l) {
		mConfigUi = parent;

		mClickListener = l;
		mView = view;

		mPasswordView = (TextView) view.findViewById(R.id.wifi_password);
		mConfirmButton = (Button) view.findViewById(R.id.wifi_okbutton);
		mConfirmButton.setOnClickListener(mClickListener);

		mAccessPoint = accessPoint;
		mAccessPointSecurity = (accessPoint == null) ? AccessPoint.SECURITY_NONE
				: accessPoint.security;
	}

	WifiConfiguration getConfig() {

		WifiConfiguration config = new WifiConfiguration();

		if (mAccessPoint.networkId == INVALID_NETWORK_ID) {
			config.SSID = AccessPoint.convertToQuotedString(mAccessPoint.ssid);
		} else {
			config.networkId = mAccessPoint.networkId;
		}

		switch (mAccessPointSecurity) {
		case AccessPoint.SECURITY_NONE:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			break;

		case AccessPoint.SECURITY_WEP:
			config.allowedKeyManagement.set(KeyMgmt.NONE);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
			if (mPasswordView.length() != 0) {
				int length = mPasswordView.length();
				String password = mPasswordView.getText().toString();
				// WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
				if ((length == 10 || length == 26 || length == 58)
						&& password.matches("[0-9A-Fa-f]*")) {
					config.wepKeys[0] = password;
				} else {
					config.wepKeys[0] = '"' + password + '"';
				}
			}
			break;

		case AccessPoint.SECURITY_PSK:
			config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			if (mPasswordView.length() != 0) {
				String password = mPasswordView.getText().toString();
				if (password.matches("[0-9A-Fa-f]{64}")) {
					config.preSharedKey = password;
				} else {
					config.preSharedKey = '"' + password + '"';
				}
			}
			break;

		case AccessPoint.SECURITY_EAP:
			config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
			// config.eap.setValue((String)
			// mEapMethodSpinner.getSelectedItem());
			//
			// config.phase2
			// .setValue((mPhase2Spinner.getSelectedItemPosition() == 0) ? ""
			// : "auth=" + mPhase2Spinner.getSelectedItem());
			// config.ca_cert.setValue((mEapCaCertSpinner
			// .getSelectedItemPosition() == 0) ? "" : KEYSTORE_SPACE
			// + Credentials.CA_CERTIFICATE
			// + (String) mEapCaCertSpinner.getSelectedItem());
			// config.client_cert.setValue((mEapUserCertSpinner
			// .getSelectedItemPosition() == 0) ? "" : KEYSTORE_SPACE
			// + Credentials.USER_CERTIFICATE
			// + (String) mEapUserCertSpinner.getSelectedItem());
			// config.private_key.setValue((mEapUserCertSpinner
			// .getSelectedItemPosition() == 0) ? "" : KEYSTORE_SPACE
			// + Credentials.USER_PRIVATE_KEY
			// + (String) mEapUserCertSpinner.getSelectedItem());
			// config.identity.setValue((mEapIdentityView.length() == 0) ? ""
			// : mEapIdentityView.getText().toString());
			// config.anonymous_identity
			// .setValue((mEapAnonymousView.length() == 0) ? ""
			// : mEapAnonymousView.getText().toString());
			if (mPasswordView.length() != 0) {
				config.password.setValue(mPasswordView.getText().toString());
			}
			break;

		default:
			return null;
		}

		// config.proxySettings = mProxySettings;
		// config.ipAssignment = mIpAssignment;
		// config.linkProperties = new LinkProperties(mLinkProperties);

		return config;
	}
}
