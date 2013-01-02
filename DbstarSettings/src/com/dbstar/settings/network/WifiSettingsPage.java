package com.dbstar.settings.network;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbstar.settings.R;
import com.dbstar.settings.base.BaseFragment;
import com.dbstar.settings.utils.SettingsCommon;
import com.dbstar.settings.wifi.WifiSettings;

public class WifiSettingsPage extends BaseFragment {
	private static final String TAG = "WifiSettingsPage";

	WifiSettings mSettings;
	Button mNextButton, mPrevButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_wifi_settings, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mSettings = new WifiSettings(mActivity);

		mSettings.onActivityCreated(savedInstanceState);

		mNextButton = (Button) mActivity.findViewById(R.id.nextbutton);
		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);

		mNextButton.setOnClickListener(mOnClickListener);
		mPrevButton.setOnClickListener(mOnClickListener);
		
		mPrevButton.requestFocus();
	}

	public void onResume() {
		super.onResume();
		if (mSettings != null) {
			mSettings.onResume();
		}
	}

	public void onPause() {
		super.onPause();
		if (mSettings != null) {
			mSettings.onPause();
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.nextbutton) {
				mManager.nextPage(SettingsCommon.PAGE_WIFI,
						SettingsCommon.PAGE_ETHERNET2);
			} else if (v.getId() == R.id.prevbutton) {
				mManager.prevPage(SettingsCommon.PAGE_WIFI);
			}
		}
	};
}
