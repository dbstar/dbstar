package com.dbstar.settings.network;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dbstar.settings.R;
import com.dbstar.settings.base.BaseFragment;
import com.dbstar.settings.ethernet.EthernetConfigController;
import com.dbstar.settings.utils.SettingsCommon;

import android.content.Context;

import android.net.ethernet.EthernetManager;

public class EthernetSettingsPage extends BaseFragment {

	EthernetConfigController mController;
	Button mOkButton, mPrevButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_ethernet_settings, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mController = new EthernetConfigController(mActivity,
				(EthernetManager) mActivity
						.getSystemService(Context.ETH_SERVICE));

		mOkButton = (Button) mActivity.findViewById(R.id.okbutton);
		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);

		mOkButton.setOnClickListener(mOnClickListener);
		mPrevButton.setOnClickListener(mOnClickListener);
	}

	public void onResume() {
		super.onResume();
		if (mController != null) {
			mController.resume();
		}
	}

	public void onPause() {
		super.onPause();
		if (mController != null) {
			mController.pause();
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.okbutton) {
				mManager.nextPage(SettingsCommon.PAGE_ETHERNET,
						SettingsCommon.PAGE_FINISH);
				
				mController.saveConfigure();
			} else if (v.getId() == R.id.prevbutton) {
				mManager.prevPage(SettingsCommon.PAGE_ETHERNET);
			}
		}
	};

}
