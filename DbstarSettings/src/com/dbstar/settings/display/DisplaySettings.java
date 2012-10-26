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

package com.dbstar.settings.display;

import com.dbstar.settings.R;
import com.dbstar.settings.SettingsPreferenceFragment;
import com.dbstar.settings.R.array;
import com.dbstar.settings.R.string;
import com.dbstar.settings.R.xml;
import com.dbstar.settings.common.SettingsCommon;
import com.dbstar.settings.util.Utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.app.Activity;
import android.util.Log;

import android.os.SystemProperties;

public class DisplaySettings extends SettingsPreferenceFragment implements
		Preference.OnPreferenceChangeListener {
	private static final String TAG = "DisplaySettings";

	/** If there is output mode option, use this. */
	private ListPreference mOutputmodePref;
	private CharSequence[] mEntryValues;
	private int mSelectedModeIndex;
	private int mEntryIndex;
	private static final int GET_USER_OPERATION = 1;

	/** If there is cvbsput mode option, use this. */	
	private ListPreference mCVBSmodePref;
	private CharSequence[] mCVBSValues;
	private int mCVBSIndex;
	private boolean mHasCVBSOutput = Utils.hasCVBSMode();

	/** If there is no setting in the provider, use this. */
	private static final String KEY_OUTPUTMODE = "output_mode";
	private static final String KEY_CVBSMODE = "cvbs_mode";
	private static final String KEY_DISPLAY_POSITION = "display_position";
	private static final String KEY_DEFAULT_FREQUENCY = "default_frequency";

	/** If there is display position option, use this. */
	private final String OUTPUT_MODE = "output_mode";
	private Preference mDisplayposition;
	private int mSelectedItemPosition;

	private ListPreference mDefaultFrequency;
	private CharSequence[] mDefaultFrequencyEntries;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.display_settings);

		if (Utils.platformHasTvOutput()) {
			mOutputmodePref = (ListPreference) findPreference(KEY_OUTPUTMODE);
			mOutputmodePref.setOnPreferenceChangeListener(this);

			String valOutputmode = SystemProperties.get(SettingsCommon.STR_OUTPUT_VAR);
			mEntryValues = getResources().getStringArray(
					R.array.outputmode_entries);
			mEntryIndex = findIndexOfEntry(valOutputmode, mEntryValues);
			mOutputmodePref.setValueIndex(mEntryIndex);
		} else {
			getPreferenceScreen().removePreference(
					findPreference(KEY_OUTPUTMODE));

		}

		if (mHasCVBSOutput) {
			mCVBSmodePref = (ListPreference) findPreference(KEY_CVBSMODE);
			mCVBSmodePref.setOnPreferenceChangeListener(this);

			String valCVBSmode = SystemProperties.get(SettingsCommon.STR_CVBS_VAR);
			mCVBSValues = getResources().getStringArray(
					R.array.cvbsmode_entries);
			if (!valCVBSmode.equals("null")) {
				mCVBSIndex = findIndexOfEntry(valCVBSmode, mCVBSValues);
				mCVBSmodePref.setValueIndex(mCVBSIndex);
			} else {
				mCVBSmodePref.setEnabled(false);
			}
		} else {
			getPreferenceScreen()
					.removePreference(findPreference(KEY_CVBSMODE));
		}

		if (Utils.platformHasTvOutput()) {
			mDisplayposition = findPreference(KEY_DISPLAY_POSITION);
			mDisplayposition.setPersistent(false);
		} else {
			getPreferenceScreen().removePreference(
					findPreference(KEY_DISPLAY_POSITION));
		}

		if (Utils.platformHasDefaultTVFreq()) {
			mDefaultFrequency = (ListPreference) findPreference(KEY_DEFAULT_FREQUENCY);
			mDefaultFrequency.setOnPreferenceChangeListener(this);
			String valDefaultFrequency = SystemProperties
					.get(SettingsCommon.STR_DEFAULT_FREQUENCY_VAR);
			mDefaultFrequencyEntries = getResources().getStringArray(
					R.array.default_frequency_entries);
			if (valDefaultFrequency.equals("")) {
				valDefaultFrequency = getResources().getString(
						R.string.tv_default_frequency_summary);
			}
			int index_DF = findIndexOfEntry(valDefaultFrequency,
					mDefaultFrequencyEntries);
			mDefaultFrequency.setValueIndex(index_DF);
			mDefaultFrequency.setSummary(valDefaultFrequency);
		} else {
			getPreferenceScreen().removePreference(
					findPreference(KEY_DEFAULT_FREQUENCY));
		}

		try {
			Bundle bundle = new Bundle();
			mSelectedItemPosition = bundle.getInt("mSelectedItemPosition");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mHasCVBSOutput) {
			String valCVBSmode = SystemProperties.get(SettingsCommon.STR_CVBS_VAR);
			if (!valCVBSmode.equals("null")) {
				mCVBSmodePref.setEnabled(true);
				mCVBSIndex = findIndexOfEntry(valCVBSmode, mCVBSValues);
				// mCVBSmodePref.setValueIndex(mCVBSIndex);
			} else {
				mCVBSmodePref.setEnabled(false);
			}
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		if (preference == mDisplayposition) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			this.setMenuVisibility(false);
			intent.setComponent(new ComponentName("com.dbstar.settings",
					"com.dbstar.settings.display.PositionSetting"));
			intent.putExtras(bundle);
			startActivity(intent);
			DisplaySettings.this.finish();
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	public boolean onPreferenceChange(Preference preference, Object objValue) {
		final String key = preference.getKey();

		if (KEY_CVBSMODE.equals(key)) {
			mCVBSIndex = Integer.parseInt(objValue.toString());

			Intent cvbsChangeIntent = new Intent(SettingsCommon.ACTION_CVBSMODE_CHANGE);
			cvbsChangeIntent.putExtra("cvbs_mode", mCVBSIndex);
			getActivity().sendBroadcast(cvbsChangeIntent);
		}

		if (KEY_OUTPUTMODE.equals(key)) {
			try {
				mSelectedModeIndex = Integer.parseInt((String) objValue);
				if (mEntryIndex != mSelectedModeIndex) {
					Intent intent = new Intent(getActivity(),
							OutputSetConfirm.class);
					intent.putExtra("set_mode",
							mEntryValues[mSelectedModeIndex]);
					if (mHasCVBSOutput) {
						intent.putExtra("cvbs_mode", mCVBSIndex);
					}
					startActivityForResult(intent, GET_USER_OPERATION);
				}
			} catch (NumberFormatException e) {
				Log.e(TAG, "could not persist output mode setting", e);
			}
		}

		if (KEY_DEFAULT_FREQUENCY.equals(key)) {
			try {
				int frequency_index = Integer.parseInt((String) objValue);
				mDefaultFrequency
						.setSummary(mDefaultFrequencyEntries[frequency_index]);
				SystemProperties.set(SettingsCommon.STR_DEFAULT_FREQUENCY_VAR,
						mDefaultFrequencyEntries[frequency_index].toString());
			} catch (NumberFormatException e) {
				Log.e(TAG, "could not persist default TV frequency setting", e);
			}
		}

		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case (GET_USER_OPERATION):
			if (resultCode == Activity.RESULT_OK) {
				String[] values = getResources().getStringArray(
						R.array.outputmode_entries);
				String tv_outputmode = values[mSelectedModeIndex];
				Intent saveIntent = new Intent(
						SettingsCommon.ACTION_OUTPUTMODE_SAVE);
				saveIntent.putExtra(OUTPUT_MODE, tv_outputmode);
				DisplaySettings.this.getActivity().sendBroadcast(
						saveIntent);
				mEntryIndex = mSelectedModeIndex;
				mOutputmodePref.setValueIndex(mEntryIndex);
			} else if (resultCode == Activity.RESULT_CANCELED) {
				mOutputmodePref.setValueIndex(mEntryIndex);
			}

		}
	}

	private int findIndexOfEntry(String value, CharSequence[] entry) {
		if (value != null && entry != null) {
			for (int i = entry.length - 1; i >= 0; i--) {
				if (entry[i].equals(value)) {
					return i;
				}
			}
		}
		return 4; // set 720p as default
	}
}
