package com.dbstar.settings.ethernet;

import java.util.List;

import com.dbstar.settings.R;
import com.dbstar.settings.utils.Utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetStateTracker;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EthernetConfigController {
	private final String TAG = "EthernetConfigController";

	public static final String DefaultEthernetDeviceName = "eth0";
	private View mDhcpSwitchButton;
	private CheckBox mDhcpSwitchIndicator;
	private TextView mDhcpConnectState, mDhcpSwitchTitle;

	private View mManualSwitchButton;
	private CheckBox mManualSwitchIndicator;
	private TextView mManualConnectState, mManualSwitchTitle;

	private EditText mIpaddr;
	private EditText mDns, mBackupDns;
	private EditText mGw;
	private EditText mMask;

	private EthernetManager mEthManager;
	private EthernetDevInfo mEthInfo;

	private final IntentFilter mIntentFilter;
	private Handler mHandler;

	private boolean mEnablePending;
	Button mSaveButton;

	private Context mContext;
	private Activity mActivity;

	String mDev = null;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE,
					EthernetStateTracker.EVENT_HW_DISCONNECTED);
			Log.d(TAG, " recv state=" + state);
			if (state == EthernetStateTracker.EVENT_HW_CONNECTED
					|| state == EthernetStateTracker.EVENT_HW_PHYCONNECTED) {
				handleNetConnected(true);
			} else if (state == EthernetStateTracker.EVENT_HW_DISCONNECTED) {
				// || state == EthernetStateTracker.EVENT_HW_CHANGED) {
				// Unfortunately, the interface will still be listed when this
				// intent is sent, so delay updating.
				handleNetConnected(false);
			}
		}
	};

	boolean mIsNetConnected = false;

	void handleNetConnected(boolean connected) {

		Log.d(TAG, " =================== network connected =  " + connected);

		mIsNetConnected = connected;
		mHandler.post(new Runnable() {
			public void run() {
				setConnectionStatus();
			}
		});
	}

	void setConnectionStatus() {
		if (mIsNetConnected) {
			if (mDhcpSwitchIndicator.isChecked()) {
				mDhcpConnectState.setVisibility(View.VISIBLE);
			}
			if (mManualSwitchIndicator.isChecked()) {
				mManualConnectState.setVisibility(View.VISIBLE);
			}

		} else {
			if (mDhcpSwitchIndicator.isChecked()) {
				mDhcpConnectState.setVisibility(View.INVISIBLE);
			}
			if (mManualSwitchIndicator.isChecked()) {
				mManualConnectState.setVisibility(View.INVISIBLE);
			}
		}
	}

	public EthernetConfigController(Activity activity,
			EthernetManager ethManager) {
		mActivity = activity;
		mEthManager = ethManager;
		mContext = activity;

		mIntentFilter = new IntentFilter(
				EthernetManager.ETH_STATE_CHANGED_ACTION);
		mHandler = new Handler();

		buildDialogContent(activity);
		enableAfterConfig();

	}

	public void resume() {
		getContext().registerReceiver(mReceiver, mIntentFilter);
	}

	public void pause() {
		getContext().unregisterReceiver(mReceiver);
	}

	public Context getContext() {
		return mContext;
	}

	private static String getAddress(int addr) {
		return NetworkUtils.intToInetAddress(addr).getHostAddress();
	}

	public int buildDialogContent(Context context) {
		mSaveButton = (Button) mActivity.findViewById(R.id.eth_savebutton);
		mDhcpSwitchButton = (View) mActivity
				.findViewById(R.id.dhcp_switch_button);
		mDhcpSwitchIndicator = (CheckBox) mActivity
				.findViewById(R.id.dhcp_switch_indicator);

		mDhcpSwitchTitle = (TextView) mActivity
				.findViewById(R.id.dhcp_switch_title);

		mDhcpConnectState = (TextView) mActivity
				.findViewById(R.id.dhcp_conncetion_state);

		mManualSwitchButton = (View) mActivity
				.findViewById(R.id.manual_switch_button);
		mManualSwitchIndicator = (CheckBox) mActivity
				.findViewById(R.id.manual_switch_indicator);
		mManualSwitchTitle = (TextView) mActivity
				.findViewById(R.id.manaul_switch_title);
		mManualConnectState = (TextView) mActivity
				.findViewById(R.id.manual_conncetion_state);

		mDhcpSwitchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				enableDhcp(true);
			}
		});

		mManualSwitchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				enableManual(true);
			}
		});

		mDhcpSwitchButton.setOnFocusChangeListener(mFocusChangeListener);
		mManualSwitchButton.setOnFocusChangeListener(mFocusChangeListener);

		mIpaddr = (EditText) mActivity.findViewById(R.id.eth_ip);
		mMask = (EditText) mActivity.findViewById(R.id.eth_mask);
		mDns = (EditText) mActivity.findViewById(R.id.eth_dns);
		mBackupDns = (EditText) mActivity.findViewById(R.id.eth_backup_dns);
		mGw = (EditText) mActivity.findViewById(R.id.eth_gateway);

		enableDhcp(true);

		String[] Devs = mEthManager.getDeviceNameList();
		Log.d(TAG, "Devices = " + Devs + " count " + Devs.length);
		if (Devs != null) {
			if (mEthManager.isEthConfigured()) {
				mEthInfo = mEthManager.getSavedEthConfig();

				mDev = mEthInfo.getIfName();
				mIpaddr.setText(mEthInfo.getIpAddress());
				mGw.setText(mEthInfo.getRouteAddr());
				mDns.setText(mEthInfo.getDnsAddr());
				mMask.setText(mEthInfo.getNetMask());

				if (mEthInfo.getConnectMode().equals(
						EthernetDevInfo.ETH_CONN_MODE_DHCP)) {

					enableDhcp(true);

					DhcpInfo dhcpInfo = mEthManager.getDhcpInfo();
					mIpaddr.setText(getAddress(dhcpInfo.ipAddress));
					mMask.setText(getAddress(dhcpInfo.netmask));
					mGw.setText(getAddress(dhcpInfo.gateway));
					mDns.setText(getAddress(dhcpInfo.dns1));
					mBackupDns.setText(getAddress(dhcpInfo.dns2));

				} else {
					enableManual(true);
				}
			} else {
				getEthernetDevice(Devs);
			}
		}
		return 0;
	}

	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus == true) {
				if (v.getId() == R.id.dhcp_switch_button) {
					mDhcpSwitchTitle.setTextColor(0xFFFFCC00);
				} else if (v.getId() == R.id.manual_switch_button) {
					mManualSwitchTitle.setTextColor(0xFFFFCC00);
				}
			} else {
				if (v.getId() == R.id.dhcp_switch_button) {
					mDhcpSwitchTitle.setTextColor(0xFF000000);
				} else if (v.getId() == R.id.manual_switch_button) {
					mManualSwitchTitle.setTextColor(0xFF000000);
				}
			}

		}
	};

	private void enableDhcp(boolean enable) {
		mDhcpSwitchIndicator.setChecked(enable);
		mManualSwitchIndicator.setChecked(!enable);

		mIpaddr.setEnabled(!enable);
		mDns.setEnabled(!enable);
		mBackupDns.setEnabled(!enable);
		mGw.setEnabled(!enable);
		mMask.setEnabled(!enable);

		mIpaddr.setFocusable(!enable);
		mDns.setFocusable(!enable);
		mBackupDns.setFocusable(!enable);
		mGw.setFocusable(!enable);
		mMask.setFocusable(!enable);

		mDhcpConnectState.setVisibility(View.VISIBLE);
		mManualConnectState.setVisibility(View.GONE);

		mManualSwitchButton.setNextFocusDownId(R.id.eth_savebutton);
		mSaveButton.setNextFocusUpId(R.id.manual_switch_button);
	}

	private void enableManual(boolean enable) {
		mDhcpSwitchIndicator.setChecked(!enable);
		mManualSwitchIndicator.setChecked(enable);

		mIpaddr.setEnabled(enable);
		mDns.setEnabled(enable);
		mBackupDns.setEnabled(enable);
		mGw.setEnabled(enable);
		mMask.setEnabled(enable);

		mIpaddr.setFocusable(enable);
		mDns.setFocusable(enable);
		mBackupDns.setFocusable(enable);
		mGw.setFocusable(enable);
		mMask.setFocusable(enable);
		
		mIpaddr.setNextFocusLeftId(R.id.gateway_serialnumber);
		mDns.setNextFocusLeftId(R.id.gateway_serialnumber);
		mBackupDns.setNextFocusLeftId(R.id.gateway_serialnumber);
		mGw.setNextFocusLeftId(R.id.gateway_serialnumber);
		mMask.setNextFocusLeftId(R.id.gateway_serialnumber);

		mDhcpConnectState.setVisibility(View.GONE);
		mManualConnectState.setVisibility(View.VISIBLE);

		mManualSwitchButton.setNextFocusDownId(R.id.eth_ip);
		mSaveButton.setNextFocusUpId(R.id.eth_backup_dns);
	}

	private void getEthernetDevice(String[] Devs) {
		for (int i = 0; i < Devs.length; i++) {
			if (Devs[i].equalsIgnoreCase(DefaultEthernetDeviceName)) {
				mDev = Devs[i];
				Log.d(TAG, " device = " + mDev);
				break;
			}
		}
	}

	public void saveConfigure() {
		Log.d(TAG, "device name = " + mDev);

		if (mDev == null || mDev.isEmpty())
			return;

		EthernetDevInfo info = new EthernetDevInfo();
		info.setIfName(mDev);

		if (mDhcpSwitchIndicator.isChecked()) {
			info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP);
			info.setIpAddress(null);
			info.setRouteAddr(null);
			info.setDnsAddr(null);
			info.setNetMask(null);
		} else {
			String ip = mIpaddr.getText().toString();
			String mask = mMask.getText().toString();
			String gateway = mGw.getText().toString();
			String dns = mDns.getText().toString();

			boolean valid = true;

			if (ip.isEmpty() || !isIpAddress(ip)) {
				valid = false;
			}

			if (mask.isEmpty() || !isIpAddress(mask)) {
				valid = false;
			}

			if (!gateway.isEmpty() && !isIpAddress(gateway)) {
				valid = false;
			}

			if (!dns.isEmpty() && !isIpAddress(dns)) {
				valid = false;
			}

			if (!valid) {
				Toast.makeText(mContext, R.string.eth_settings_error,
						Toast.LENGTH_LONG).show();
				return;
			}

			info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_MANUAL);
			info.setIpAddress(ip);
			info.setRouteAddr(gateway);
			info.setDnsAddr(dns);
			info.setNetMask(mask);
		}

		mEthManager.updateEthDevInfo(info);
		if (mEnablePending) {
			if (mEthManager.getEthState() == mEthManager.ETH_STATE_ENABLED) {
				mEthManager.setEthEnabled(true);
			}
			mEnablePending = false;
		}
	}

	private boolean isIpAddress(String value) {
		int start = 0;
		int end = value.indexOf('.');
		int numBlocks = 0;

		while (start < value.length()) {
			if (end == -1) {
				end = value.length();
			}

			try {
				int block = Integer.parseInt(value.substring(start, end));
				if ((block > 255) || (block < 0)) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}

			numBlocks++;

			start = end + 1;
			end = value.indexOf('.', start);
		}
		return numBlocks == 4;
	}

	public void enableAfterConfig() {
		mEnablePending = true;
	}
}
