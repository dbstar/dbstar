package com.settings.components;

import android.app.Activity;
import android.view.View;

import com.settings.utils.LogUtil;
import com.settings.wifi.WifiSettings;

public class WifiSettingsView {
	private static final String TAG = "WifiSettingsPage";
	
	private WifiSettings mSettings;
	private Activity mActivity;
	
	public WifiSettingsView(Activity activity) {
		this.mActivity = activity;
		mSettings = new WifiSettings(activity);
	}

	public void initView(View view) {
		LogUtil.d(TAG, "initView++++--------->>>>mSettings<<<<" + mSettings);
		mSettings.initView(view);
		resume();
//		pause();
	}

	public void resume() {
		if (mSettings != null) {
			mSettings.resume();
			LogUtil.d(TAG, "WifiSettingsView++++---------onResume");
		}
	}

	public void pause() {
		if (mSettings != null) {
			LogUtil.d(TAG, "WifiSettingsView++++---------onPause");
			mSettings.onPause();
		}
	}

//	private View.OnClickListener mOnClickListener = new View.OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			if (v.getId() == R.id.nextbutton) {
//				mManager.nextPage(SettingsCommon.PAGE_WIFI,
//						SettingsCommon.PAGE_ETHERNET2);
//			} else if (v.getId() == R.id.prevbutton) {
//				mManager.prevPage(SettingsCommon.PAGE_WIFI);
//			}
//		}
//	};
	
}
