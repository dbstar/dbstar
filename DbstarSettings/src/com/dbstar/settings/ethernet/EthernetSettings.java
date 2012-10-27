/*
 * Copyright (C) 2010 The Android-x86 Open Source Project
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
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */

package com.dbstar.settings.ethernet;

import com.dbstar.settings.R;
import com.dbstar.settings.SettingsPreferenceFragment;
import com.dbstar.settings.common.SettingsCommon;
import com.dbstar.settings.util.Utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ethernet.EthernetManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Gravity;
import android.widget.Switch;
import android.util.Log;

public class EthernetSettings extends SettingsPreferenceFragment {
	private static final String LOG_TAG = "Ethernet";
	private static final String KEY_CONF_ETH = "ETHERNET_config";
	private static final String KEY_SWITCH_ETH = "ETHERNET_switch";

	private EthernetEnabler mEthEnabler;
	private EthernetConfigDialog mEthConfigDialog;
	private Preference mEthConfigPref;
	private CheckBoxPreference mEthSwitchPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.ethernet_settings);

		final PreferenceScreen preferenceScreen = getPreferenceScreen();
		mEthConfigPref = preferenceScreen.findPreference(KEY_CONF_ETH);
		mEthSwitchPref = (CheckBoxPreference) preferenceScreen
				.findPreference(KEY_SWITCH_ETH);
	}

	@Override
	public void onStart() {
		super.onStart();

		initToggles();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mEthEnabler != null) {
			mEthEnabler.resume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mEthEnabler != null) {
			mEthEnabler.pause();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		if (preference == mEthConfigPref) {
			mEthConfigDialog.show();
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	private void initToggles() {
		mEthEnabler = new EthernetEnabler(
				(EthernetManager) getSystemService(Context.ETH_SERVICE),
				mEthSwitchPref);
		mEthConfigDialog = new EthernetConfigDialog(getActivity(),
				(EthernetManager) getSystemService(Context.ETH_SERVICE));
	}
}
