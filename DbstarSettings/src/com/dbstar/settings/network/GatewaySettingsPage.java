package com.dbstar.settings.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dbstar.settings.R;
import com.dbstar.settings.base.BaseFragment;
import com.dbstar.settings.utils.SettingsCommon;

public class GatewaySettingsPage extends BaseFragment {

	private static final String ActionGetNetworkInfo = "com.dbstar.DbstarLauncher.Action.GET_NETWORKINFO";
	private static final String ActionUpateNetworkInfo = "com.dbstar.DbstarLauncher.Action.UPDATE_NETWORKINFO";
	private static final String ActionSetNetworkInfo = "com.dbstar.DbstarLauncher.Action.SET_NETWORKINFO";

	public static final String PropertyGatewaySerialNumber = "SmarthomeSN";
	public static final String PropertyGatewayIP = "SmarthomeServerIP";
	public static final String PropertyGatewayPort = "SmarthomeServerPort";
	public static final String PropertyMulticastIP = "DBDataServerIP";
	public static final String PropertyMulticastPort = "DBDataServerPort";

	private TextView mGatewaySerialNumber, mMulticastIP, mMulticastPort,
			mGatewayIP, mGatewayPort;
	View mNullview;
	Button mNextButton;
	
	public GatewaySettingsPage () {
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_gateway_setupview, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initializeView();
		
		registerGetInfoReceiver();
	}

	@Override
	public void onStart() {
		super.onStart();

		getNetworkInfo();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mActivity.unregisterReceiver(mReceiver);
	}

	private void onToNextPage() {
		setNetworkInfo();
	}

	void registerGetInfoReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionUpateNetworkInfo);
		mActivity.registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Log.d("@@@", "onReceive msg " + action);

			if (action.equals(ActionUpateNetworkInfo)) {
				String gatewaySerialNumber = intent
						.getStringExtra(PropertyGatewaySerialNumber);
				String gatewayIP = intent.getStringExtra(PropertyGatewayIP);
				String gatewayPort = intent.getStringExtra(PropertyGatewayPort);
				String multicastIP = intent.getStringExtra(PropertyMulticastIP);
				String multicastPort = intent
						.getStringExtra(PropertyMulticastPort);

				mGatewaySerialNumber.setText(gatewaySerialNumber);
				mMulticastIP.setText(multicastIP);
				mMulticastPort.setText(multicastPort);
				mGatewayIP.setText(gatewayIP);
				mGatewayPort.setText(gatewayPort);
			}
		}
	};

	void getNetworkInfo() {
		Intent intent = new Intent();
		intent.setAction(ActionGetNetworkInfo);
		mActivity.sendBroadcast(intent);
	}

	void setNetworkInfo() {
		String gatewaySerialNumber = mGatewaySerialNumber.getText().toString();
		String gatewayIP = mGatewayIP.getText().toString();
		String gatewayPort = mGatewayPort.getText().toString();
		String multicastIP = mMulticastIP.getText().toString();
		String multicastPort = mMulticastPort.getText().toString();

		Intent intent = new Intent();
		intent.setAction(ActionSetNetworkInfo);

		intent.putExtra(PropertyGatewaySerialNumber, gatewaySerialNumber);
		intent.putExtra(PropertyGatewayIP, gatewayIP);
		intent.putExtra(PropertyGatewayPort, gatewayPort);
		intent.putExtra(PropertyMulticastIP, multicastIP);
		intent.putExtra(PropertyMulticastPort, multicastPort);

		mActivity.sendBroadcast(intent);
	}

	void initializeView() {
		mNullview = mActivity.findViewById(R.id.nullview);
		mGatewaySerialNumber = (TextView) mActivity
				.findViewById(R.id.gateway_serialnumber);
		mMulticastIP = (TextView) mActivity.findViewById(R.id.multicast_ip);
		mMulticastPort = (TextView) mActivity.findViewById(R.id.multicast_port);
		mGatewayIP = (TextView) mActivity.findViewById(R.id.gateway_ip);
		mGatewayPort = (TextView) mActivity.findViewById(R.id.gateway_port);
		mNextButton = (Button) mActivity.findViewById(R.id.nextbutton);

		mGatewaySerialNumber.setOnFocusChangeListener(mFocusChangedListener);
		mMulticastIP.setOnFocusChangeListener(mFocusChangedListener);
		mMulticastPort.setOnFocusChangeListener(mFocusChangedListener);
		mGatewayIP.setOnFocusChangeListener(mFocusChangedListener);
		mGatewayPort.setOnFocusChangeListener(mFocusChangedListener);

		mNextButton.setOnClickListener(mOnClickListener);

		mNullview.setFocusable(true);
		mNullview.requestFocus();
	}

	View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mManager.nextPage(SettingsCommon.PAGE_GATEWAY,
					SettingsCommon.PAGE_CHANNELSELECTOR);
			onToNextPage();
		}
	};

	View.OnFocusChangeListener mFocusChangedListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v instanceof EditText) {
				if (hasFocus) {
					EditText textView = (EditText) v;
					textView.setSelection(0);
				}
			}

		}
	};

}
