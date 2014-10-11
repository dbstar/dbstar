package com.dbstar.app.settings;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.model.GDDataModel;
import com.dbstar.model.GDDiskInfo;
import com.dbstar.model.GDDiskInfo.DiskInfo;
import com.dbstar.model.GDSystemConfigure;
import com.dbstar.util.GDNetworkUtil;
import com.dbstar.util.ImageUtil;

public class GDGeneralInfoActivity extends GDSettingActivity {

	private RelativeLayout mContainer;
	private TextView mDeviceSerialNumberView;
	private TextView mHardwareTypeView;
	private TextView mSoftwareVersionView;
	private TextView mLoaderVersionView;
	private TextView mMacAddressView;

	private TextView mDiskSizeView, mDiskUsedView, mDiskSpaceView;
	private TextView mUpgradeView;

	private String mDeviceSerialNumber, mHardwareType, mSoftwareVersion,
			mLoaderVersion, mMacAddress;
	private String mUpgradeCount;
	private String mDiskSize, mDiskUsed, mDiskSpace;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.generalinfo_view);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		if (mMenuPath != null) {
			showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
		}
	}

	private String[] mKeys;
	
	public void querySettings() {
		mKeys = new String[5];
		mKeys[0] = GDSettings.SettingDeviceSerialNumber;
		mKeys[1] = GDSettings.SettingHardwareType;
		mKeys[2] = GDSettings.SettingSoftwareVersion;
		mKeys[3] = GDSettings.SettingLoaderVersion;
		mKeys[4] = GDSettings.SettingUpgradeCount;
		
		mService.getDeviceInfo(this, mKeys);

		mMacAddress += GDNetworkUtil.getMacAddress(this, true);
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
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure configure = new GDSystemConfigure();
		dataModel.initialize(configure);
		
		if (key.equals(GDSettings.SettingDeviceSerialNumber)) {
//			mDeviceSerialNumber += value;
//			mDeviceSerialNumberView.setText(mDeviceSerialNumber);
			mDeviceSerialNumberView.setText(mDeviceSerialNumber + dataModel.getDeviceSearialNumber());
		} else if (key.equals(GDSettings.SettingHardwareType)) {
//			mHardwareType += value;
//			mHardwareTypeView.setText(mHardwareType);
			mHardwareTypeView.setText(mHardwareType + dataModel.getHardwareType());
		} else if (key.equals(GDSettings.SettingSoftwareVersion)) {
//			mSoftwareVersion += value;
//			mSoftwareVersionView.setText(mSoftwareVersion);
			mSoftwareVersionView.setText(mSoftwareVersion + dataModel.getSoftwareVersion());
		} else if (key.equals(GDSettings.SettingLoaderVersion)) {
//			mLoaderVersion += value;
//			mLoaderVersionView.setText(mLoaderVersion);
			mLoaderVersionView.setText(mLoaderVersion + dataModel.getLoaderVersion());
		} else if (key.equals(GDSettings.SettingUpgradeCount)) {
			if (value != null && !value.isEmpty()) {
				mUpgradeCount += value;
				mUpgradeView.setText(mUpgradeCount);
				mUpgradeView.setVisibility(View.VISIBLE);
			}
		}
	}

	public void initializeView() {
		super.initializeView();

		mContainer = (RelativeLayout) findViewById(R.id.generalinfo_view);
		mDeviceSerialNumberView = (TextView) findViewById(R.id.device_serialnumber);
		mHardwareTypeView = (TextView) findViewById(R.id.device_hardwaretype);
		mSoftwareVersionView = (TextView) findViewById(R.id.device_softwareversion);
		mLoaderVersionView = (TextView) findViewById(R.id.device_loaderversion);
		mMacAddressView = (TextView) findViewById(R.id.device_macaddress);
		mUpgradeView = (TextView) findViewById(R.id.upgrade_count);

		mDiskSizeView = (TextView) findViewById(R.id.disk_totalsize);
		mDiskUsedView = (TextView) findViewById(R.id.disk_usedsize);
		mDiskSpaceView = (TextView) findViewById(R.id.disk_spacesize);

		mDeviceSerialNumber = getResources().getString(R.string.deviceinfo_device_serialnumber);
		mHardwareType = getResources().getString(R.string.deviceinfo_hardware_type);
		mSoftwareVersion = getResources().getString(R.string.deviceinfo_software_version);
		mLoaderVersion = getResources().getString(R.string.deviceinfo_loader_version);
		mMacAddress = getResources().getString(R.string.deviceinfo_mac_address);
		mUpgradeCount = getResources().getString(R.string.upgrade_count);

		mDiskSize = getResources().getString(R.string.disk_totalsize);
		mDiskUsed = getResources().getString(R.string.disk_usedsize);
		mDiskSpace = getResources().getString(R.string.disk_spacesize);
		
		HashMap<String, Bitmap> bitmaps = ImageUtil.parserXmlAndLoadPic();
		
		if (bitmaps == null || bitmaps.size() <= 0) {
			mContainer.setBackgroundResource(R.drawable.view_background);
			return;
		}
		
		if (bitmaps.containsKey(ImageUtil.App_Key)) {
			Drawable drawable = new BitmapDrawable(bitmaps.get(ImageUtil.App_Key));
			mContainer.setBackgroundDrawable(drawable);
		}
	}
	
	// when long press menu key more than 5 seconds,
	// show the system management page.
	private boolean mIsMenuKeyPressed = false;

	Runnable mCheckLongPressTask = new Runnable() {

		public void run() {

			if (mIsMenuKeyPressed) {
				Intent intent = new Intent();
				intent.setClass(GDGeneralInfoActivity.this, GDAdvancedToolsActivity.class);
				intent.putExtra(INTENT_KEY_MENUPATH, mMenuPath);
				startActivity(intent);
			}
		}

	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_NOTIFICATION:
			mIsMenuKeyPressed = true;
			mHandler.postDelayed(mCheckLongPressTask, 10000);
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_NOTIFICATION:
			mIsMenuKeyPressed = false;
			mHandler.removeCallbacks(mCheckLongPressTask);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}
