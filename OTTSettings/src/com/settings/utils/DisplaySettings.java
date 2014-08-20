package com.settings.utils;

import android.os.SystemProperties;

public class DisplaySettings {

	public static String getOutpuMode() {
		return SystemProperties.get(SettingsCommon.STR_OUTPUT_VAR);
	}

	public static String getCVBSOutpuMode() {
		return SystemProperties.get(SettingsCommon.STR_CVBS_VAR);
	}

	public static String getDefaultFrequency() {
		return SystemProperties.get(SettingsCommon.STR_DEFAULT_FREQUENCY_VAR);
	}

	public static void setDefaultFrequency(String frequency) {
		SystemProperties.set(SettingsCommon.STR_DEFAULT_FREQUENCY_VAR,
				frequency);
	}

	public static int findIndexOfEntry(String value, CharSequence[] entry) {
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
