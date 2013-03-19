package com.dbstar.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

public class GDAudioController extends BroadcastReceiver {
	private static final String TAG = "GDAudioController";
	
	public static final String ActionMute = "dbstar.intent.action.MUTE";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "=== receive === " + action);
		
		if (action.equals(ActionMute)) {
			boolean mute = intent.getBooleanExtra("key_mute", false);
			AudioManager audioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
			audioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
		}
	}

}
