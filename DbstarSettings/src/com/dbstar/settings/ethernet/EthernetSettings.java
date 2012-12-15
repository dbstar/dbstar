package com.dbstar.settings.ethernet;

import com.dbstar.settings.OnSaveListener;
import com.dbstar.settings.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.content.Context;

import android.net.ethernet.EthernetManager;

public class EthernetSettings implements View.OnClickListener {

	View mEthernetSwitchButton;
	CheckBox mEthernetSwitchIndicator;
	TextView mEthSwitchTitle;
	Button mSaveButton;
	CheckBox mWifiSwitchIndicator;

	EthernetEnabler mEthEnabler;
	EthernetConfigController mController;

	View mEthConfigView;
	View mWifiConfigView;

	OnSaveListener mSaveListener;

	public boolean isEthernetOn() {
		return mEthernetSwitchIndicator.isChecked();
	}

	public EthernetSettings(Activity activity, OnSaveListener callback) {
		mSaveListener = callback;
		mEthConfigView = (View) activity.findViewById(R.id.eth_config);
		mWifiConfigView = (View) activity.findViewById(R.id.wifi_aplist);

		mEthernetSwitchButton = (View) activity
				.findViewById(R.id.eth_switch_button);
		mEthernetSwitchButton.setOnClickListener(this);
		mEthernetSwitchIndicator = (CheckBox) activity
				.findViewById(R.id.eth_switch_indicator);
		mWifiSwitchIndicator = (CheckBox) activity
				.findViewById(R.id.wifi_switch_indicator);

		mEthSwitchTitle = (TextView) activity
				.findViewById(R.id.eth_switch_title);

		mSaveButton = (Button) activity.findViewById(R.id.eth_savebutton);
		mSaveButton.setOnClickListener(this);

		mEthEnabler = new EthernetEnabler(
				(EthernetManager) activity
						.getSystemService(Context.ETH_SERVICE),
				mEthernetSwitchIndicator, mEthConfigView);

		mController = new EthernetConfigController(activity,
				(EthernetManager) activity
						.getSystemService(Context.ETH_SERVICE));

		mEthernetSwitchButton.setOnFocusChangeListener(mFocusChangeListener);
	}

	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus == true) {
				if (v.getId() == R.id.eth_switch_button) {
					mEthSwitchTitle.setTextColor(0xFFFFCC00);
				}
			} else {
				if (v.getId() == R.id.eth_switch_button) {
					mEthSwitchTitle.setTextColor(0xFF000000);
				}
			}

		}
	};

	public void onResume() {

		if (mEthEnabler != null) {
			mEthEnabler.resume();
		}

		if (mController != null) {
			mController.resume();
		}
	}

	public void onPause() {
		if (mEthEnabler != null) {
			mEthEnabler.pause();
		}

		if (mController != null) {
			mController.pause();
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.eth_switch_button) {
			mEthernetSwitchIndicator.toggle();
			mWifiSwitchIndicator.setChecked(!mEthernetSwitchIndicator
					.isChecked());

		} else if (view.getId() == R.id.eth_savebutton) {
			mController.saveConfigure();
			mSaveListener.onSave();
		}
	}
}
