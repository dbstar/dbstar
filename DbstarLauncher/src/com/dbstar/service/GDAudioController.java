package com.dbstar.service;

import com.dbstar.model.GDCommon;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class GDAudioController extends BroadcastReceiver {
	private static final String TAG = "GDAudioController";

	public static final String ActionMute = "dbstar.intent.action.MUTE";
	private AudioManager mAudioManager = null;

	private Handler mAppHandler = null;
	private boolean mIsMute = false;

	public GDAudioController(Context context, Handler appHandler) {
		mAppHandler = appHandler;
		mAudioManager = (AudioManager) context
				.getSystemService(Service.AUDIO_SERVICE);
		
		mIsMute = false;
		
		SystemUtils.clearAudioInfo();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionMute);

		context.registerReceiver(this, filter);
	}

	public void muteAudio(int mute) {
		mIsMute = mute == GDCommon.MUTE_TRUE ? true : false;
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, mIsMute);
		SystemUtils.saveMute(mute);
	}

	public boolean isMute() {
		return mIsMute;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "=== receive === " + action);

		if (action.equals(ActionMute)) {
			boolean mute = intent.getBooleanExtra("key_mute", false);
			if (mAppHandler != null) {
				Message msg = mAppHandler
						.obtainMessage(GDCommon.MSG_MUTE_AUDIO);
				msg.arg1 = mute ? GDCommon.MUTE_TRUE : GDCommon.MUTE_FALSE;
				msg.sendToTarget();
			}
		}
	}

}
