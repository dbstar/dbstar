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

import static android.net.ethernet.EthernetManager.ETH_STATE_DISABLED;
import static android.net.ethernet.EthernetManager.ETH_STATE_ENABLED;
import static android.net.ethernet.EthernetManager.ETH_STATE_UNKNOWN;

import com.dbstar.settings.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.ethernet.EthernetManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.Switch;

public class EthernetEnabler implements OnPreferenceChangeListener {
	private static final String TAG = "SettingsEthEnabler";

	private static final boolean LOCAL_LOGD = false;
	private EthernetManager mEthManager;
	private CheckBoxPreference mSwitch;
	private boolean mStateMachineEvent;

	public EthernetEnabler(EthernetManager ethernetManager,
			CheckBoxPreference _switch) {
		mEthManager = ethernetManager;
		mSwitch = _switch;

		if (mEthManager.getEthState() == ETH_STATE_ENABLED) {
			setSwitchChecked(true);
		}
	}

	public void resume() {
		mSwitch.setOnPreferenceChangeListener(this);
	}

	public void pause() {
		mSwitch.setOnPreferenceChangeListener(null);
	}

	private void setSwitchChecked(boolean checked) {
		if (checked != mSwitch.isChecked()) {
			mStateMachineEvent = true;
			mSwitch.setChecked(checked);
			mStateMachineEvent = false;
		}
	}

	private void setEthEnabled(final boolean enable) {
		int state = mEthManager.getEthState();

		mEthManager.setEthEnabled(enable);
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object value) {
		if (mStateMachineEvent) {
			return false;
		}

		boolean isChecked = (Boolean) value;
		setEthEnabled(isChecked);
		setSwitchChecked(isChecked);

		return false;
	}
}
