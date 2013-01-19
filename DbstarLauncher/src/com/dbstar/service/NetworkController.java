package com.dbstar.service;

import com.dbstar.model.GDCommon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetStateTracker;
import android.os.SystemProperties;
import android.util.Log;

public class NetworkController extends BroadcastReceiver {
	private static final String TAG = "DBSTAR.NetworkController";

	private static final int ETHERNET_PHYNONE = 0x00;
	private static final int ETHERNET_PHYCONNECTED = 0x01;
	private static final int ETHERNET_PHYDISCONNECTED = 0x02;

	private Context mContext;
	private EthernetManager mEthManager;

	private int mEthernetPhyState = ETHERNET_PHYNONE;
	private boolean mEthernetWaitingDHCP;
	private boolean mEthernetPhyConnect = false;

	private Handler mHandler;

	public NetworkController(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;

		mEthManager = (EthernetManager) mContext
				.getSystemService(mContext.ETH_SERVICE);

		if (mEthManager.isEthDeviceAdded()) {
			mEthernetPhyState = ETHERNET_PHYCONNECTED;

			configEthernet();
		} else {
			mEthernetPhyState = ETHERNET_PHYDISCONNECTED;
		}

		Log.d(TAG, "=========== hw  ====== " + mEthernetPhyState);

		IntentFilter filter = new IntentFilter();
		filter.addAction(EthernetManager.ETH_STATE_CHANGED_ACTION);

		context.registerReceiver(this, filter);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if (action.equals(EthernetManager.ETH_STATE_CHANGED_ACTION)) {
			updateEth(intent);
		}
	}

	public boolean isEthernetPhyConnected() {
		Log.d(TAG, "========= isEthernetPhyConnected ==========="
				+ mEthernetPhyState);

		return mEthernetPhyState == ETHERNET_PHYCONNECTED;
	}

	private final void updateEth(Intent intent) {
		final int event = intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE,
				EthernetStateTracker.EVENT_HW_DISCONNECTED);

		Log.d(TAG, "============== ethernet event ===========" + event);

		switch (event) {
		case EthernetStateTracker.EVENT_HW_CONNECTED:
			Log.d(TAG, "============== ethernet EVENT_HW_CONNECTED ===========");
			return;
		case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_SUCCEEDED:
			Log.d(TAG,
					"============== ethernet EVENT_INTERFACE_CONFIGURATION_SUCCEEDED ===========");
			return;
		case EthernetStateTracker.EVENT_INTERFACE_CONFIGURATION_FAILED:
			Log.d(TAG,
					"============== ethernet EVENT_INTERFACE_CONFIGURATION_FAILED ===========");
			return;

		case EthernetStateTracker.EVENT_DHCP_START:
			Log.d(TAG, "============== ethernet EVENT_DHCP_START ===========");
			return;
		case EthernetStateTracker.EVENT_HW_PHYCONNECTED:
			Log.d(TAG, "============== ethernet connected ==========="
					+ mEthernetPhyState);
			if (mEthernetPhyState != ETHERNET_PHYCONNECTED) {
				mEthernetPhyState = ETHERNET_PHYCONNECTED;
				configEthernet();
				mHandler.sendEmptyMessage(GDCommon.MSG_ETHERNET_PHYCONECTED);
			}
			return;
		case EthernetStateTracker.EVENT_HW_PHYDISCONNECTED:
		case EthernetStateTracker.EVENT_HW_DISCONNECTED:
			Log.d(TAG, "========= ethernet EVENT_HW_DISCONNECTED ==========="
					+ mEthernetPhyState);
			boolean eth_onboard = SystemProperties.getBoolean(
					"ro.hw.ethernet.onboard", false);
			if (eth_onboard) {
				mEthernetPhyConnect = false;
				mEthernetWaitingDHCP = false;

				Log.d(TAG, "========= ethernet EVENT_HW_DISCONNECTED ======="
						+ mEthernetPhyState);
				int hwState = mEthManager.isEthDeviceAdded() ? ETHERNET_PHYCONNECTED
						: ETHERNET_PHYDISCONNECTED;
				if (mEthernetPhyState != hwState && hwState == ETHERNET_PHYDISCONNECTED) {
					mEthernetPhyState = hwState;
					mHandler.sendEmptyMessage(GDCommon.MSG_ETHERNET_PHYDISCONECTED);
				}
			}
			return;
		case EthernetStateTracker.EVENT_HW_CHANGED:
			Log.d(TAG, "============== ethernet EVENT_HW_CHANGED ===========");
			return;

		default:
			if (mEthernetWaitingDHCP)
				return;
			return;
		}
	}

	private void dhcpFailed() {
		DhcpInfo dhcpInfo = mEthManager.getDhcpInfo();

		boolean privateIpValide = false;
		if (dhcpInfo != null) {
			String ip = getAddress(dhcpInfo.ipAddress);
			privateIpValide = isValidIpAddress(ip);
		}

		if (privateIpValide) {
			mHandler.sendEmptyMessage(GDCommon.MSG_DHCP_PRIVATEIP_READY);
		}

	}

	public void configEthernet() {
		if (mEthManager.isEthConfigured()) {
			EthernetDevInfo ethInfo = mEthManager.getSavedEthConfig();
			if (ethInfo.getConnectMode().equals(
					EthernetDevInfo.ETH_CONN_MODE_DHCP)) {
				mEthManager.updateEthDevInfo(ethInfo);
			}
		}
	}

	private static String getAddress(int addr) {
		return NetworkUtils.intToInetAddress(addr).getHostAddress();
	}

	private static boolean isValidIpAddress(String value) {
		String[] items = value.split(".");

		if (items == null || items.length < 4) {
			return false;
		}

		int zeroCount = 0;

		for (int i = 0; i < items.length; i++) {
			int item = Integer.parseInt(items[i]);
			if (item < 0 || item > 255) {
				return false;
			} else {
				if (item == 0) {
					zeroCount++;
				}
			}
		}

		if (zeroCount == 4) {
			return false;
		}

		return true;
	}

}
