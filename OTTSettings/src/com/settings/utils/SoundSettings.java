package com.settings.utils;

import android.os.SystemProperties;
import android.util.Log;

public class SoundSettings {

	private static final String TAG = "SoundSettings";

	private static final String STR_DIGIT_AUDIO_OUTPUT = "ubootenv.var.digitaudiooutput";
	private static final String DigitalRawFile = "/sys/class/audiodsp/digital_raw";

	public static final String AudioModePCM = "PCM";
	public static final String AudioModeRAW = "RAW";

	// Mode: PCM, Value: 0
	// Mode: RAW, Value: 1
	public static boolean setAudioOutputMode(String mode, String value) {
		
		if (!Utils.platformHasDigitAudio()) {
			return false;
		}
		
		SystemProperties.set(STR_DIGIT_AUDIO_OUTPUT, mode);
		boolean success = Utils.setValue(DigitalRawFile, value);
		
		Log.i(TAG, "digit audio output set to " + mode + " value=" + value);
		
		return success;
	}
	
	public static String getAudioOutputMode() {
		return SystemProperties.get(STR_DIGIT_AUDIO_OUTPUT);
	}

}
