package com.dbstar.app.settings;

import java.util.Map;

import com.dbstar.R;
import com.dbstar.model.GDDiskInfo;
import com.dbstar.model.GDDiskInfo.DiskInfo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GDGeneralInfoActivity extends GDSettingActivity {

	private TextView mDeviceSerialNumberView;
	private TextView mHardwareTypeView;
	private TextView mSoftwareVersionView;
	private TextView mLoaderVersionView;
	private TextView mMacAddressView;

	private TextView mDiskSizeView, mDiskUsedView, mDiskSpaceView;

	private String mDeviceSerialNumber, mHardwareType, mSoftwareVersion,
			mLoaderVersion, mMacAddress;
	private String mDiskSize, mDiskUsed, mDiskSpace;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.generalinfo_view);

		initializeView();

//		Intent intent = getIntent();
		// mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		// showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	private String[] mKeys;
	
	public void querySettings() {
		mKeys = new String[4];
		mKeys[0] = GDSettings.SettingDeviceSerialNumber;
		mKeys[1] = GDSettings.SettingHardwareType;
		mKeys[2] = GDSettings.SettingSoftwareVersion;
		mKeys[3] = GDSettings.SettingLoaderVersion;
		
		mService.getDeviceInfo(this, mKeys);

		mMacAddress += mService.getMacAddress();
		mMacAddressView.setText(mMacAddress);

		String disk = mService.getStorageDisk();
		if (disk != null && !disk.isEmpty()) {
			DiskInfo info = GDDiskInfo.getDiskInfo(disk, true);

			mDiskSize += info.DiskSize;
			mDiskUsed += info.DiskUsed;
			mDiskSpace += info.DiskSpace;

			mDiskSizeView.setText(mDiskSize);
			mDiskUsedView.setText(mDiskUsed);
			mDiskSpaceView.setText(mDiskSpace);
		}
	}

	public void updateSettings(Map<String, String> properties) {
		for (int i=0 ; i<properties.size() ; i++) {
			updateSettings(mKeys[i], properties.get(mKeys[i]));
		}
	}
	
	public void updateSettings(String key, String value) {
		if (key.equals(GDSettings.SettingDeviceSerialNumber)) {
			mDeviceSerialNumber += value;
			mDeviceSerialNumberView.setText(mDeviceSerialNumber);
		} else if (key.equals(GDSettings.SettingHardwareType)) {
			mHardwareType += value;
			mHardwareTypeView.setText(mHardwareType);
		} else if (key.equals(GDSettings.SettingSoftwareVersion)) {
			mSoftwareVersion += value;
			mSoftwareVersionView.setText(mSoftwareVersion);
		} else if (key.equals(GDSettings.SettingLoaderVersion)) {
			mLoaderVersion += value;
			mLoaderVersionView.setText(mLoaderVersion);
		}
	}

	public void initializeView() {
		// super.initializeView();

		mDeviceSerialNumberView = (TextView) findViewById(R.id.device_serialnumber);
		mHardwareTypeView = (TextView) findViewById(R.id.device_hardwaretype);
		mSoftwareVersionView = (TextView) findViewById(R.id.device_softwareversion);
		mLoaderVersionView = (TextView) findViewById(R.id.device_loaderversion);
		mMacAddressView = (TextView) findViewById(R.id.device_macaddress);

		mDiskSizeView = (TextView) findViewById(R.id.disk_totalsize);
		mDiskUsedView = (TextView) findViewById(R.id.disk_usedsize);
		mDiskSpaceView = (TextView) findViewById(R.id.disk_spacesize);

		mDeviceSerialNumber = getResources().getString(
				R.string.deviceinfo_device_serialnumber);
		mHardwareType = getResources().getString(
				R.string.deviceinfo_hardware_type);
		mSoftwareVersion = getResources().getString(
				R.string.deviceinfo_software_version);
		mLoaderVersion = getResources().getString(
				R.string.deviceinfo_loader_version);
		mMacAddress = getResources().getString(R.string.deviceinfo_mac_address);

		mDiskSize = getResources().getString(R.string.disk_totalsize);
		mDiskUsed = getResources().getString(R.string.disk_usedsize);
		mDiskSpace = getResources().getString(R.string.disk_spacesize);
	}
}
