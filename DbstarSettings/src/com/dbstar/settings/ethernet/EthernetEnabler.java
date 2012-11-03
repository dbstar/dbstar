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
import android.net.ethernet.EthernetManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.dbstar.settings.R;

public class EthernetEnabler implements OnCheckedChangeListener {
	private static final String TAG = "SettingsEthEnabler";

	private static final boolean LOCAL_LOGD = false;
	private EthernetManager mEthManager;
	private CheckBox mSwitch;
	private boolean mStateMachineEvent;

	public EthernetEnabler(EthernetManager ethernetManager,
			CheckBox _switch) {
		mEthManager = ethernetManager;
		mSwitch = _switch;

		if (mEthManager.getEthState() == ETH_STATE_ENABLED) {
			setSwitchChecked(true);
		}
	}

	public void resume() {
		mSwitch.setOnCheckedChangeListener(this);
	}

	public void pause() {
		mSwitch.setOnCheckedChangeListener(null);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (mStateMachineEvent) {
			return;
		}

		setEthEnabled(isChecked);
		setSwitchChecked(isChecked);
	}
}
