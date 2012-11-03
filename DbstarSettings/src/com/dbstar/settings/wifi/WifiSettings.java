package com.dbstar.settings.wifi;

import com.dbstar.settings.BaseFragment;
import com.dbstar.settings.R;
import com.dbstar.settings.wifi.WifiEnabler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.content.BroadcastReceiver;
import android.graphics.Bitmap;
import android.util.Log;

import static android.net.wifi.WifiConfiguration.INVALID_NETWORK_ID;

//invisible
import com.android.internal.util.AsyncChannel;

import android.security.Credentials;
import android.security.KeyStore;
import android.net.wifi.WpsResult;

import com.dbstar.settings.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiSettings extends BaseFragment implements View.OnClickListener {
	private static final String TAG = "WifiSettings";

	// Instance state keys
	private static final String SAVE_DIALOG_EDIT_MODE = "edit_mode";
	private static final String SAVE_DIALOG_ACCESS_POINT_STATE = "wifi_ap_state";
	// Combo scans can take 5-6s to complete - set to 10s.
	private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
	private static final int WIFI_DIALOG_ID = 1;
	View mWifiSwitchButton;
	CheckBox mWifiSwitchIndicator;
	TextView mWifiSwitchTitle;

	private final IntentFilter mFilter;
	private final BroadcastReceiver mReceiver;
	private final Scanner mScanner;

	private WifiManager mWifiManager;
	private WifiEnabler mWifiEnabler;

	private DetailedState mLastState;
	private WifiInfo mLastInfo;
	private AtomicBoolean mConnected = new AtomicBoolean(false);
	private int mKeyStoreNetworkId = Utils.WifiConfigure.INVALID_NETWORK_ID;

	ArrayList<AccessPoint> mAccessPointList = new ArrayList<AccessPoint>();
	AccessPointsAdapter mAPAdapter;
	ListView mAPListView;

	private WifiDialog mDialog;

	private AccessPoint mSelectedAccessPoint;

	// Save the dialog details
	private boolean mDlgEdit;
	private AccessPoint mDlgAccessPoint;
	private Bundle mAccessPointSavedState;

	public WifiSettings() {
		mFilter = new IntentFilter();
		mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

		mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
		mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
		mFilter.addAction(WifiManager.ERROR_ACTION);

		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				handleEvent(context, intent);
			}
		};

		mScanner = new Scanner();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.wifi_settings, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mWifiManager = (WifiManager) getActivity().getSystemService(
				Context.WIFI_SERVICE);
		mWifiManager.asyncConnect(getActivity(), new WifiServiceHandler());

		mWifiSwitchButton = (View) getActivity().findViewById(
				R.id.wifi_switch_button);
		mWifiSwitchButton.setOnClickListener(this);
		mWifiSwitchTitle = (TextView) getActivity().findViewById(
				R.id.wifi_switch_title);
		mWifiSwitchIndicator = (CheckBox) getActivity().findViewById(
				R.id.wifi_switch_indicator);

		mAPListView = (ListView) getActivity().findViewById(
				R.id.wifi_ap_list_view);

		mWifiEnabler = new WifiEnabler(getActivity(), mWifiSwitchIndicator);

		mAPAdapter = new AccessPointsAdapter(getActivity());
		mAPAdapter.setDataSet(mAccessPointList);
		mAPListView.setAdapter(mAPAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mWifiEnabler != null) {
			mWifiEnabler.resume();
		}

		getActivity().registerReceiver(mReceiver, mFilter);
		if (mKeyStoreNetworkId != Utils.WifiConfigure.INVALID_NETWORK_ID
				&& KeyStore.getInstance().state() == KeyStore.State.UNLOCKED) {
			mWifiManager.connectNetwork(mKeyStoreNetworkId);
		}
		mKeyStoreNetworkId = Utils.WifiConfigure.INVALID_NETWORK_ID;

		updateAccessPoints();

		if (mDialog != null)
			mDialog.updatePWShowState();

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mWifiEnabler != null) {
			mWifiEnabler.pause();
		}
		getActivity().unregisterReceiver(mReceiver);
		mScanner.pause();
	}

	/**
	 * Shows the latest access points available with supplimental information
	 * like the strength of network and the security for it.
	 */
	private void updateAccessPoints() {
		final int wifiState = mWifiManager.getWifiState();

		switch (wifiState) {
		case WifiManager.WIFI_STATE_ENABLED:
			// AccessPoints are automatically sorted with TreeSet.
			final Collection<AccessPoint> accessPoints = constructAccessPoints();
			// mAPListPref.removeAll();
			mAccessPointList.clear();
			for (AccessPoint accessPoint : accessPoints) {
				// mAPListPref.addPreference(accessPoint);
				mAccessPointList.add(accessPoint);
			}
			mAPAdapter.notifyDataSetChanged();
			break;

		case WifiManager.WIFI_STATE_ENABLING:
			// mAPListPref.removeAll();
			mAccessPointList.clear();
			mAPAdapter.notifyDataSetChanged();
			break;

		case WifiManager.WIFI_STATE_DISABLING:
			// addMessagePreference(R.string.wifi_stopping);
			mWifiSwitchTitle.setText(R.string.wifi_stopping);
			break;

		case WifiManager.WIFI_STATE_DISABLED:
			// addMessagePreference(R.string.wifi_empty_list_wifi_off);
			mWifiSwitchTitle.setText(R.string.wifi_empty_list_wifi_off);
			break;
		}
	}

	/** Returns sorted list of access points */
	private List<AccessPoint> constructAccessPoints() {
		ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
		/**
		 * Lookup table to more quickly update AccessPoints by only considering
		 * objects with the correct SSID. Maps SSID -> List of AccessPoints with
		 * the given SSID.
		 */
		Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

		final List<WifiConfiguration> configs = mWifiManager
				.getConfiguredNetworks();
		if (configs != null) {
			for (WifiConfiguration config : configs) {
				AccessPoint accessPoint = new AccessPoint(getActivity(), config);
				accessPoint.update(mLastInfo, mLastState);
				accessPoints.add(accessPoint);
				apMap.put(accessPoint.ssid, accessPoint);
			}
		}

		final List<ScanResult> results = mWifiManager.getScanResults();
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0
						|| result.capabilities.contains("[IBSS]")) {
					continue;
				}

				boolean found = false;
				for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
					if (accessPoint.update(result))
						found = true;
				}
				if (!found) {
					AccessPoint accessPoint = new AccessPoint(getActivity(),
							result);
					accessPoints.add(accessPoint);
					apMap.put(accessPoint.ssid, accessPoint);
				}
			}
		}

		// Pre-sort accessPoints to speed preference insertion
		Collections.sort(accessPoints);
		return accessPoints;
	}

	/** A restricted multimap for use in constructAccessPoints */
	private class Multimap<K, V> {
		private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

		/** retrieve a non-null list of values with key K */
		List<V> getAll(K key) {
			List<V> values = store.get(key);
			return values != null ? values : Collections.<V> emptyList();
		}

		void put(K key, V val) {
			List<V> curVals = store.get(key);
			if (curVals == null) {
				curVals = new ArrayList<V>(3);
				store.put(key, curVals);
			}
			curVals.add(val);
		}
	}

	private void handleEvent(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
			updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN));
		} else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
				|| WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION
						.equals(action)
				|| WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
			updateAccessPoints();
		} else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
			// Ignore supplicant state changes when network is connected
			// TODO: we should deprecate SUPPLICANT_STATE_CHANGED_ACTION and
			// introduce a broadcast that combines the supplicant and network
			// network state change events so the apps dont have to worry about
			// ignoring supplicant state change when network is connected
			// to get more fine grained information.
			if (!mConnected.get()) {
				updateConnectionState(WifiInfo
						.getDetailedStateOf((SupplicantState) intent
								.getParcelableExtra(WifiManager.EXTRA_NEW_STATE)));
			}

		} else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			mConnected.set(info.isConnected());
			// changeNextButtonState(info.isConnected());
			updateAccessPoints();
			updateConnectionState(info.getDetailedState());
		} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
			updateConnectionState(null);
		} else if (WifiManager.ERROR_ACTION.equals(action)) {
			int errorCode = intent.getIntExtra(WifiManager.EXTRA_ERROR_CODE, 0);
			switch (errorCode) {
			case WifiManager.WPS_OVERLAP_ERROR:
				Toast.makeText(context, R.string.wifi_wps_overlap_error,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	private void updateConnectionState(DetailedState state) {
		/* sticky broadcasts can call this when wifi is disabled */
		if (!mWifiManager.isWifiEnabled()) {
			mScanner.pause();
			return;
		}

		if (state == DetailedState.OBTAINING_IPADDR) {
			mScanner.pause();
		} else {
			mScanner.resume();
		}

		mLastInfo = mWifiManager.getConnectionInfo();
		if (state != null) {
			mLastState = state;
		}

		for (int i = mAccessPointList.size() - 1; i >= 0; --i) {
			final AccessPoint accessPoint = mAccessPointList.get(i);
			accessPoint.update(mLastInfo, mLastState);
		}

		mAPAdapter.notifyDataSetChanged();

	}

	private void updateWifiState(int state) {
		switch (state) {
		case WifiManager.WIFI_STATE_ENABLED:
			mScanner.resume();
			return; // not break, to avoid the call to pause() below

		case WifiManager.WIFI_STATE_ENABLING:
			// addMessagePreference(R.string.wifi_starting);
			mWifiSwitchTitle.setText(R.string.wifi_starting);
			break;

		case WifiManager.WIFI_STATE_DISABLED:
			// addMessagePreference(R.string.wifi_empty_list_wifi_off);
			mWifiSwitchTitle.setText(R.string.wifi_empty_list_wifi_off);
			break;
		}

		mLastInfo = null;
		mLastState = null;
		mScanner.pause();
	}

	private class Scanner extends Handler {
		private int mRetry = 0;

		void resume() {
			if (!hasMessages(0)) {
				sendEmptyMessage(0);
			}
		}

		void forceScan() {
			removeMessages(0);
			sendEmptyMessage(0);
		}

		void pause() {
			mRetry = 0;
			removeMessages(0);
		}

		@Override
		public void handleMessage(Message message) {
			if (mWifiManager.startScanActive()) {
				mRetry = 0;
			} else if (++mRetry >= 3) {
				mRetry = 0;
				Toast.makeText(getActivity(), R.string.wifi_fail_to_scan,
						Toast.LENGTH_LONG).show();
				return;
			}
			sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
		}
	}

	private class WifiServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AsyncChannel.CMD_CHANNEL_HALF_CONNECTED:
				if (msg.arg1 == AsyncChannel.STATUS_SUCCESSFUL) {
					// AsyncChannel in msg.obj
				} else {
					// AsyncChannel set up failure, ignore
					Log.e(TAG, "Failed to establish AsyncChannel connection");
				}
				break;
			case WifiManager.CMD_WPS_COMPLETED:
				WpsResult result = (WpsResult) msg.obj;
				if (result == null)
					break;
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						getActivity()).setTitle(R.string.wifi_wps_setup_title)
						.setPositiveButton(android.R.string.ok, null);
				switch (result.status) {
				case FAILURE:
					dialog.setMessage(R.string.wifi_wps_failed);
					dialog.show();
					break;
				case IN_PROGRESS:
					dialog.setMessage(R.string.wifi_wps_in_progress);
					dialog.show();
					break;
				default:
					if (result.pin != null) {
						dialog.setMessage(getResources().getString(
								R.string.wifi_wps_pin_output, result.pin));
						dialog.show();
					}
					break;
				}
				break;
			// TODO: more connectivity feedback
			default:
				// Ignore
				break;
			}
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() != R.id.wifi_switch_button)
			return;

		mWifiSwitchIndicator.toggle();
	}

	OnItemClickListener mOnAPSelectedListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			onAccessPointSelected(position);
		}

	};

	void onAccessPointSelected(int index) {
		mSelectedAccessPoint = mAccessPointList.get(index);
		/** Bypass dialog for unsecured, unsaved networks */
		if (mSelectedAccessPoint.security == AccessPoint.SECURITY_NONE
				&& mSelectedAccessPoint.networkId == INVALID_NETWORK_ID) {
			mSelectedAccessPoint.generateOpenNetworkConfig();
			mWifiManager.connectNetwork(mSelectedAccessPoint.getConfig());
		} else {
			showConfigUi(mSelectedAccessPoint, false);
		}
	}

	/**
	 * Shows an appropriate Wifi configuration component. Called when a user
	 * clicks "Add network" preference or one of available networks is selected.
	 */
	private void showConfigUi(AccessPoint accessPoint, boolean edit) {
		showDialog(accessPoint, edit);
	}

	private void showDialog(AccessPoint accessPoint, boolean edit) {
		if (mDialog != null) {
			removeDialog(WIFI_DIALOG_ID);
			mDialog = null;
		}

		// Save the access point and edit mode
		mDlgAccessPoint = accessPoint;
		mDlgEdit = edit;

		showDialog(WIFI_DIALOG_ID);
	}

	private class AccessPointsAdapter extends BaseAdapter {

		private ArrayList<AccessPoint> mDataSet = null;

		public AccessPointsAdapter(Context context) {
		}

		public void setDataSet(ArrayList<AccessPoint> dataSet) {
			mDataSet = dataSet;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (mDataSet != null) {
				count = mDataSet.size();
			}

			return count;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (null == convertView) {
				LayoutInflater inflater = getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.wifi_list_item, parent,
						false);
			}

			mDataSet.get(position).bindView(convertView);

			return convertView;
		}
	}
}
