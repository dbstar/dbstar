package com.settings.service;

import com.settings.utils.DataUtils;
import com.settings.utils.DisplaySettings;
import com.settings.utils.SettingsCommon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class OTTSettingsModeService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		String mDefaultFrequency = DataUtils.getPreference(this, "modeFrequecy", "");
		String videoMode = DataUtils.getPreference(this, "modeValue", DisplaySettings.getOutpuMode());
		Log.d("OTTSettingsModeService", "videoMode = " + videoMode + " mDefaultFrequency = " + mDefaultFrequency);
		Intent saveIntent = new Intent(SettingsCommon.ACTION_OUTPUTMODE_CHANGE);
		saveIntent.putExtra(SettingsCommon.OUTPUT_MODE, videoMode);
		sendBroadcast(saveIntent);
	}

}
