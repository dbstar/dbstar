package com.dbstar.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dbstar.settings.R;
import com.dbstar.settings.ethernet.EthernetSettings;
import com.dbstar.settings.wifi.WifiSettings;

public class GDNetworkSettingsActivity extends GDBaseActivity implements
		OnSaveListener {

	private static final String TAG = "GDNetworkSettingsActivity";
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

	private EthernetSettings mEthSettings;
	private WifiSettings mWifiSettings;

	private Handler mHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.network_settings);

		registerGetInfoReceiver();

		initializeView();

		// Intent intent = getIntent();
		// mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		// showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));

		mEthSettings = new EthernetSettings(this, this);
		mWifiSettings = new WifiSettings(this);

		mWifiSettings.onActivityCreated(savedInstanceState);

		mHandler = new Handler();

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				onShow();
			}

		}, 300);
	}

	@Override
	public void onStart() {
		super.onStart();

		// mGatewaySerialNumber.requestFocus();
		getNetworkInfo();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// setNetworkInfo();
		unregisterReceiver(mReceiver);
	}

	private void onShow() {
		// hide soft keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public void initializeView() {
		super.initializeView();

		mGatewaySerialNumber = (TextView) findViewById(R.id.gateway_serialnumber);
		mMulticastIP = (TextView) findViewById(R.id.multicast_ip);
		mMulticastPort = (TextView) findViewById(R.id.multicast_port);
		mGatewayIP = (TextView) findViewById(R.id.gateway_ip);
		mGatewayPort = (TextView) findViewById(R.id.gateway_port);

		mGatewaySerialNumber.setOnFocusChangeListener(mFocusChangedListener);
		mMulticastIP.setOnFocusChangeListener(mFocusChangedListener);
		mMulticastPort.setOnFocusChangeListener(mFocusChangedListener);
		mGatewayIP.setOnFocusChangeListener(mFocusChangedListener);
		mGatewayPort.setOnFocusChangeListener(mFocusChangedListener);
	}

	void registerGetInfoReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionUpateNetworkInfo);
		registerReceiver(mReceiver, filter);
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
		sendBroadcast(intent);
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

		sendBroadcast(intent);
	}

	@Override
	public void onResume() {
		super.onResume();

		mEthSettings.onResume();

		mWifiSettings.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();

		mEthSettings.onPause();
		mWifiSettings.onPause();
	}

	@Override
	public void onSave() {
		setNetworkInfo();

		finish();
	}

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
