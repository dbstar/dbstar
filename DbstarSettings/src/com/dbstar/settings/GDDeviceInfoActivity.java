package com.dbstar.settings;


import com.dbstar.settings.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

public class GDDeviceInfoActivity extends GDSettingsActivity {
	private static final String TAG = "GDDeviceInfoActivity";
	
	private TextView mDeviceSerialNumberView;
	private TextView mHardwareTypeView;
	private TextView mSoftwareVersionView;
	private TextView mLoaderVersionView;
	private TextView mMacAddressView;
	private TextView mGatewaySerialNumberView;
	private TextView mServerIPView;
	private TextView mPortView;
	
	private String mOriginalGatewaySerialNumber, mOriginalServerIP, mOriginalPort;

	private TextView[] mEditors;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.deviceinfo_view);

		initializeView();

		Intent intent = getIntent();
//		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
//		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

//	public void querySettings() {
//		mService.getSettingsValue(this, GDSettings.SettingDeviceSerialNumber);
//		mService.getSettingsValue(this, GDSettings.SettingHardwareType);
//		mService.getSettingsValue(this, GDSettings.SettingSoftwareVersion);
//		mService.getSettingsValue(this, GDSettings.SettingLoaderVersion);
//		mService.getSettingsValue(this, GDSettings.SettingGatewaySerialNumber);
//		mService.getSettingsValue(this, GDSettings.SettingServerIP);
//		mService.getSettingsValue(this, GDSettings.SettingServerPort);
//		
//		String macAddress = mService.getMacAddress();
//		mMacAddressView.setText(macAddress);
//	}
//
//	public void updateSettings(String key, String value) {
//		if (key.equals(GDSettings.SettingDeviceSerialNumber)) {
//			mDeviceSerialNumberView.setText(value);
//		} else if (key.equals(GDSettings.SettingHardwareType)) {
//			mHardwareTypeView.setText(value);
//		} else if (key.equals(GDSettings.SettingSoftwareVersion)) {
//			mSoftwareVersionView.setText(value);
//		} else if (key.equals(GDSettings.SettingLoaderVersion)) {
//			mLoaderVersionView.setText(value);
//		} else if (key.equals(GDSettings.SettingGatewaySerialNumber)) {
//			mOriginalGatewaySerialNumber= value; 
//			mGatewaySerialNumberView.setText(value);
//		} else if (key.equals(GDSettings.SettingServerIP)) {
//			mOriginalServerIP = value;
//			mServerIPView.setText(value);
//		} else if (key.equals(GDSettings.SettingServerPort)) {
//			mOriginalPort = value;
//			mPortView.setText(value);
//		} else {
//		}
//	}	
	
//	public void saveSettings() {
//		if (!mBound)
//			return;
//
//		String gatewaySerialNumber = mGatewaySerialNumberView.getText().toString();
//		String serverIP = mServerIPView.getText().toString();
//		String serverPort = mPortView.getText().toString();
//
//		/*
//		 * if (!version.isEmpty()) {
//		 * mService.setSettingsValue(GDSettings.SettingVersion, version); }
//		 */
//
//		if (!gatewaySerialNumber.isEmpty() && !gatewaySerialNumber.equals(mOriginalGatewaySerialNumber)) {
//			mService.setSettingsValue(GDSettings.SettingGatewaySerialNumber, gatewaySerialNumber);
//		}
//
//		if (!serverIP.isEmpty() && !serverIP.equals(mOriginalServerIP)) {
//			mService.setSettingsValue(GDSettings.SettingServerIP, serverIP);
//		}
//
//		if (!serverPort.isEmpty() && !serverPort.equals(mOriginalPort)) {
//			mService.setSettingsValue(GDSettings.SettingServerPort, serverPort);
//		}
//	}
//	

	public void initializeView () {
//		super.initializeView();
		
		mDeviceSerialNumberView = (TextView) findViewById(R.id.text_device_serialnumber);
		mHardwareTypeView = (TextView) findViewById(R.id.text_hardware_type);
		mSoftwareVersionView = (TextView) findViewById(R.id.text_software_version);
		mLoaderVersionView = (TextView) findViewById(R.id.text_loader_version);
		mMacAddressView = (TextView) findViewById(R.id.text_mac_address);
		mGatewaySerialNumberView = (TextView) findViewById(R.id.text_gateway_serialnumber);
		mServerIPView = (TextView) findViewById(R.id.text_serverip);
		mPortView = (TextView) findViewById(R.id.text_serverport);

		mEditors = new TextView[3];
		mEditors[0] = mGatewaySerialNumberView;
		mEditors[1] = mServerIPView;
		mEditors[2] = mPortView;

		for (int i = 0; i < mEditors.length; i++) {
			mEditors[i].setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					Log.d(TAG, "onKey " + keyCode);
					boolean ret = false;
					int action = event.getAction();
					if (action == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {

						case KeyEvent.KEYCODE_DPAD_CENTER:
						case KeyEvent.KEYCODE_ENTER:
							ret = true;
//							showInputView(v);
							break;
						default:
							break;
						}
					}
					return ret;
				}
			});
		}
	}
}
