package com.dbstar.app.settings.smartcard;

import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.base.FragmentObserver;
import com.dbstar.app.settings.GDSettings;
import com.dbstar.service.GDDataProviderService;

public class GDDrmVersionFragment extends GDSmartcardFragment {
	private static final String TAG = "GDDrmVersionFragment";

	TextView mHardwareVersionView, mSoftwareVersionView, mLoaderVersionView;
	private String mHardwareVersion, mSoftwareVersion, mLoaderVersion;
	private String[] mKeys;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.drm_version_view, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initializeView();
	}

	// Request data at this point
	public void serviceStart() {
		Log.d(TAG, "=== service is started ===");

		queryData();
	}

	void queryData() {
		mKeys = new String[3];
		mKeys[0] = GDSettings.SettingHardwareVersion;
		mKeys[1] = GDSettings.SettingSoftwareVersion;
		mKeys[2] = GDSettings.SettingLoaderVersion;

		mSmartcardEngine.queryDeviceInfo(this, mKeys);
	}

	// Receive data at this point
	public void updateData(FragmentObserver observer, int type, Object key,
			Object data) {

		if (observer != this || data == null)
			return;

		if (type != GDDataProviderService.REQUESTTYPE_GETDEVICEINFO)
			return;

		Map<String, String> properties = (Map<String, String>) data;

		updateSettings(properties);

	}

	void initializeView() {
		mHardwareVersionView = (TextView) mActivity
				.findViewById(R.id.hardware_verion);
		mSoftwareVersionView = (TextView) mActivity
				.findViewById(R.id.software_version);
		mLoaderVersionView = (TextView) mActivity
				.findViewById(R.id.loader_version);

		mHardwareVersion = getResources().getString(
				R.string.deviceinfo_hardware_version);
		mSoftwareVersion = getResources().getString(
				R.string.deviceinfo_software_version);
		mLoaderVersion = getResources().getString(
				R.string.deviceinfo_loader_version);
	}

	public void updateSettings(Map<String, String> properties) {
		for (int i = 0; i < properties.size(); i++) {
			updateSettings(mKeys[i], properties.get(mKeys[i]));
		}
	}

	public void updateSettings(String key, String value) {
		if (key.equals(GDSettings.SettingHardwareVersion)) {
			mHardwareVersion += value;
			mHardwareVersionView.setText(mHardwareVersion);
		} else if (key.equals(GDSettings.SettingSoftwareVersion)) {
			mSoftwareVersion += value;
			mSoftwareVersionView.setText(mSoftwareVersion);
		} else if (key.equals(GDSettings.SettingLoaderVersion)) {
			mLoaderVersion += value;
			mLoaderVersionView.setText(mLoaderVersion);
		}
	}
}
