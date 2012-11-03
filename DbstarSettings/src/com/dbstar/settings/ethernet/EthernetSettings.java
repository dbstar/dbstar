package com.dbstar.settings.ethernet;

import com.dbstar.settings.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.content.Context;

import android.net.ethernet.EthernetManager;

public class EthernetSettings extends Fragment implements View.OnClickListener {

	View mEthernetSwitchButton;
	CheckBox mEthernetSwitchIndicator;
	TextView mEthernetSwitchTitle;

	EthernetEnabler mEthEnabler;
	EthernetConfigController mController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ethernet_settings, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mEthernetSwitchButton = (View) getActivity().findViewById(
				R.id.eth_switch_button);
		mEthernetSwitchButton.setOnClickListener(this);
		mEthernetSwitchTitle = (TextView) getActivity().findViewById(
				R.id.eth_switch_title);
		mEthernetSwitchIndicator = (CheckBox) getActivity().findViewById(
				R.id.eth_switch_indicator);
		
		mEthEnabler = new EthernetEnabler((EthernetManager) getActivity()
				.getSystemService(Context.ETH_SERVICE),
				mEthernetSwitchIndicator);

		mController = new EthernetConfigController(getActivity(),
				(EthernetManager) getActivity().getSystemService(
						Context.ETH_SERVICE));
	}

	@Override
	public void onStart() {
		super.onStart();
		
		mController.start();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mEthEnabler != null) {
			mEthEnabler.resume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mEthEnabler != null) {
			mEthEnabler.pause();
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		mController.stop();
		mController.saveConfigure();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() != R.id.eth_switch_button)
			return;

		mEthernetSwitchIndicator.toggle();
	}
}
