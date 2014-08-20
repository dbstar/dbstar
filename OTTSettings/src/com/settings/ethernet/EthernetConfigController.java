package com.settings.ethernet;

import java.util.List;

import com.settings.base.PageManager;
import com.settings.network.NetworkCommon;
import com.settings.ottsettings.R;
import com.settings.utils.LogUtil;
import com.settings.utils.SettingUtils;
import com.settings.utils.SettingsCommon;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetStateTracker;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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

	public static final int MSG_NETWORK_CONNECT = 0;
	public static final int MSG_NETWORK_DISCONNECT = 1;

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

//	Button mOkButton, mPrevButton;
	Button mOkButton;

	private EthernetManager mEthManager;
	private EthernetDevInfo mEthInfo;
	private ConnectivityManager mConnectManager;

	private IntentFilter mEthIntentFilter;
	private Handler mHandler;

	private boolean mEnablePending;

	private Context mContext;
	private Activity mActivity;

	String mDev = null;

	boolean mIsEthHWConnected = false;
	
	private int Ethernet_Network_Mode;

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int state = intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE, 
			EthernetStateTracker.EVENT_HW_DISCONNECTED);


			if (state == EthernetStateTracker.EVENT_HW_CONNECTED) {
				Log.d(TAG, " -------recv state EVENT_HW_CONNECTED===========" + state);
				handleEthStateChanged(true);
			} else if (state == EthernetStateTracker.EVENT_HW_DISCONNECTED) {
				handleEthStateChanged(false);
				Log.d(TAG, " -------recv state EVENT_HW_DISCONNECTED===========" + state);
			}
		}
	};

	private BroadcastReceiver mEthernetInfoReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Log.d("@@@", "onReceive msg " + action);

//			if (Ethernet_Network_Mode == 1) {
				DhcpInfo dhcpInfo = mEthManager.getDhcpInfo();
				String ipAddress = SettingUtils.getAddress(dhcpInfo.ipAddress);
				mIpaddr.setText(ipAddress);
				String mask = SettingUtils.getAddress(dhcpInfo.netmask);
				mMask.setText(mask);
				String gateway = SettingUtils.getAddress(dhcpInfo.gateway);
				mGw.setText(gateway);
				String dns = SettingUtils.getAddress(dhcpInfo.dns1);
				mDns.setText(dns);
				String backupDns = SettingUtils.getAddress(dhcpInfo.dns2);
				mBackupDns.setText(backupDns);
//			} else {
//				
//				if (action.equals(NetworkCommon.ActionSetEthernetInfo)) {
//					String ethernetInfo = intent.getStringExtra(NetworkCommon.KeyEthernetInfo);
//					
//					Log.d(TAG, " == ethernet info == " + ethernetInfo);
//					
//					// "ip=%s,mask=%s,gw=%s,dns1=%s,dns2=%s,mac=%s",
//					if (ethernetInfo != null && ethernetInfo.length() > 0) {
//						String[] values = ethernetInfo.split(",");
//						
//						String[] properties = values[0].split("=");
//						String ip = properties[1];
//						Log.d(TAG, " =+++_____= ethernet info ip =+++____= " + ip);
//						mIpaddr.setText(properties[1]);
//						
//						properties = values[1].split("=");
//						Log.d(TAG, " =+++_____= ethernet info properties[1] mask =+++____= " + properties[1]);
//						mMask.setText(properties[1]);
//						
//						properties = values[2].split("=");
//						Log.d(TAG, " =+++_____= ethernet info properties[1] gw =+++____= " + properties[1]);
//						mGw.setText(properties[1]);
//						
//						properties = values[3].split("=");
//						mDns.setText(properties[1]);
//						
//						properties = values[4].split("=");
//						mBackupDns.setText(properties[1]);
//						
//						boolean connected = false;
//						if (ip != null && ip.length() > 0) {
//							if (!ip.equals("127.0.0.1") && !ip.equals("0.0.0.0")) {
//								connected = isIpAddress(ip, true);
//							}
//						}
//						
//						mIsEthHWConnected = connected;
//						if (connected) {
//							if (mDhcpSwitchIndicator.isChecked()) {
//								mDhcpConnectState.setVisibility(View.VISIBLE);
//								mManualConnectState.setVisibility(View.INVISIBLE);
//							} else {
//								mDhcpConnectState.setVisibility(View.INVISIBLE);
//								mManualConnectState.setVisibility(View.VISIBLE);
//							}
//							
//						}
//						
//					}
//				}
//			}
			
		}
	};

	private void reqisterSystemReceiver() {

		IntentFilter filter = new IntentFilter(
				NetworkCommon.ActionSetEthernetInfo);
		mActivity.registerReceiver(mEthernetInfoReceiver, filter);
	}

	private void unregisterSystemReceiver() {
		mActivity.unregisterReceiver(mEthernetInfoReceiver);
	}

	public boolean isNetworkConnected() {
		return mIsEthHWConnected;
	}

	void handleEthStateChanged(boolean ethHWConnected) {
		mIsEthHWConnected = ethHWConnected;
		mHandler.post(new Runnable() {
			public void run() {
				setConnectionStatus(mIsEthHWConnected);
			}
		});
	}

	void setConnectionStatus(boolean connected) {

		Log.d(TAG, " =================== network connected =  " + connected);

		if (connected) {
			if (mDhcpSwitchIndicator.isChecked()) {
				mDhcpConnectState.setVisibility(View.VISIBLE);

				updateDhcpInfo();
			}

			if (mManualSwitchIndicator.isChecked()) {
				mManualConnectState.setVisibility(View.VISIBLE);
			}

		} else {
			if (mDhcpSwitchIndicator.isChecked()) {
				Log.d(TAG, " =================== mDhcpSwitchIndicator =  " + mDhcpSwitchIndicator.isChecked());
				mDhcpConnectState.setVisibility(View.INVISIBLE);
			}

			if (mManualSwitchIndicator.isChecked()) {
				Log.d(TAG, " =================== mManualSwitchIndicator.isChecked() =  " + mManualSwitchIndicator.isChecked());
				mManualConnectState.setVisibility(View.INVISIBLE);
			}
		}
	}

	public EthernetConfigController(Activity activity, EthernetManager ethManager, int mode) {
		mActivity = activity;
		mEthManager = ethManager;
		mContext = activity;
		Ethernet_Network_Mode = mode;
		
		LogUtil.d("EthernetConfigController", "<<<mActivity>>>>>" + mActivity);
		LogUtil.d("EthernetConfigController", "<<<mEthManager>>>>>" + mEthManager);
		LogUtil.d("EthernetConfigController", "<<<mContext>>>>>" + mContext);
		
		mEthIntentFilter = new IntentFilter(EthernetManager.ETH_STATE_CHANGED_ACTION);
		mConnectManager = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		mHandler = new Handler();

		buildDialogContent(activity);
		enableAfterConfig();

		NetworkInfo ethernetInfo = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

		if (ethernetInfo.isConnected()) {
			int ip = mEthManager.getDhcpInfo().ipAddress;
			Log.d(TAG, "---mDhcpSwitchIndicator-----networkInfo IpAddress ===== " + NetworkUtils.intToInetAddress(ip).getHostAddress());
			String ipAddr = NetworkUtils.intToInetAddress(ip).getHostAddress();
			String ipAddress = NetworkUtils.intToInetAddress(mEthManager.getDhcpInfo().ipAddress).getHostAddress();
			// if (!ipAddr.equals(ipAddress)) {
			// mEthInfo.setIpAddress(ipAddress);
			// }
			Log.d(TAG, "---mDhcpSwitchIndicator-----mEthManager.getDhcpInfo().ipAddress===== " + ipAddress);
			Log.d(TAG, "---mDhcpSwitchIndicator-----mEthInfo IpAddress ===== " + ipAddr);
			
			
		}
	}

	public void resume() {
		LogUtil.d(TAG + "???resume()???", "-=-=-=-=-"+mReceiver+"=-=-=-=");
		getContext().registerReceiver(mReceiver, mEthIntentFilter);
		reqisterSystemReceiver();

		if (mEthManager.isEthDeviceAdded()) {
			Intent intent = new Intent(NetworkCommon.ActionGetEthernetInfo);
			mContext.sendBroadcast(intent);
		}
	}

	public void pause() {
		LogUtil.d(TAG + "???pause()???", "-=-=-=-=-"+mReceiver+"=-=-=-=");
		getContext().unregisterReceiver(mReceiver);
		unregisterSystemReceiver();
	}

	public Context getContext() {
		return mContext;
	}

	private static String getAddress(int addr) {
		return NetworkUtils.intToInetAddress(addr).getHostAddress();
	}

	private int buildDialogContent(Activity activity) {
		mDhcpSwitchButton = (View) activity.findViewById(R.id.dhcp_switch_button);
		mDhcpSwitchIndicator = (CheckBox) activity.findViewById(R.id.dhcp_switch_indicator);

		mDhcpSwitchTitle = (TextView) activity.findViewById(R.id.dhcp_switch_title);

		mDhcpConnectState = (TextView) activity.findViewById(R.id.dhcp_conncetion_state);

		mManualSwitchButton = (View) activity.findViewById(R.id.manual_switch_button);
		mManualSwitchIndicator = (CheckBox) activity.findViewById(R.id.manual_switch_indicator);
		mManualSwitchTitle = (TextView) activity.findViewById(R.id.manaul_switch_title);
		mManualConnectState = (TextView) activity.findViewById(R.id.manual_conncetion_state);

		mOkButton = (Button) activity.findViewById(R.id.eth_btn_confirm);
//		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);

		LogUtil.d(TAG, ">>>>>>>>>View<<<<<<<<<" + activity);
		LogUtil.d(TAG, ">>>>>>>>>mDhcpSwitchButton<<<<<<<<<" + mDhcpSwitchButton);
		
		mDhcpSwitchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				enableDhcp(true);
				LogUtil.d("EthernetConfigController", "<<<----mDhcpSwitchButton>>>>>" );
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

		mDhcpSwitchButton.requestFocus();

		mIpaddr = (EditText) activity.findViewById(R.id.eth_ip);
		mMask = (EditText) activity.findViewById(R.id.eth_mask);
		mDns = (EditText) activity.findViewById(R.id.eth_dns);
		mBackupDns = (EditText) activity.findViewById(R.id.eth_backup_dns);
		mGw = (EditText) activity.findViewById(R.id.eth_gateway);
		
		mIpaddr.setLongClickable(false);
		mMask.setLongClickable(false);
		mDns.setLongClickable(false);
		mBackupDns.setLongClickable(false);
		mGw.setLongClickable(false);
		
		mIpaddr.setOnFocusChangeListener(mFocusChangeListener);
		mMask.setOnFocusChangeListener(mFocusChangeListener);
		mDns.setOnFocusChangeListener(mFocusChangeListener);
		mBackupDns.setOnFocusChangeListener(mFocusChangeListener);
		mGw.setOnFocusChangeListener(mFocusChangeListener);
		
		enableDhcp(true);

		String[] Devs = mEthManager.getDeviceNameList();

		if (Devs != null) {
			Log.d(TAG, "Devices = " + Devs + " count " + Devs.length);
			if (mEthManager.isEthConfigured()) {
				mEthInfo = mEthManager.getSavedEthConfig();
				DhcpInfo dhcpInfo = mEthManager.getDhcpInfo();
				mDev = mEthInfo.getIfName();
				if(dhcpInfo != null){
    				mIpaddr.setText(getAddress(dhcpInfo.ipAddress));
    	            mMask.setText(getAddress(dhcpInfo.netmask));
    	            mGw.setText(getAddress(dhcpInfo.gateway));
    	            mDns.setText(getAddress(dhcpInfo.dns1));
				}
//				mIpaddr.setText(mEthInfo.getIpAddress());
//				mGw.setText(mEthInfo.getRouteAddr());
//				mDns.setText(mEthInfo.getDnsAddr());
//				mMask.setText(mEthInfo.getNetMask());

				if (mEthInfo.getConnectMode().equals(
						EthernetDevInfo.ETH_CONN_MODE_DHCP)) {

					enableDhcp(true);

				} else {
					enableManual(true);
				}
			} else {
				getEthernetDevice(Devs);
			}
		}
		return 0;
	}

	void updateDhcpInfo() {
		DhcpInfo dhcpInfo = mEthManager.getDhcpInfo();
		if (dhcpInfo != null) {
			mIpaddr.setText(getAddress(dhcpInfo.ipAddress));
			mMask.setText(getAddress(dhcpInfo.netmask));
			mGw.setText(getAddress(dhcpInfo.gateway));
			mDns.setText(getAddress(dhcpInfo.dns1));
			mBackupDns.setText(getAddress(dhcpInfo.dns2));
		}
	}

	View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus == true) {
				if (v.getId() == R.id.dhcp_switch_button) {
					mDhcpSwitchTitle.setTextColor(0xFFFFCC00);
				} else if (v.getId() == R.id.manual_switch_button) {
					mManualSwitchTitle.setTextColor(0xFFFFCC00);
					LogUtil.d("EthernetConfigController", "<<<onFocusChange----mManualSwitchTitle");
				} else if (v instanceof EditText) {
					EditText textView = (EditText) v;
					textView.setSelection(0);
				}
			} else {
				if (v.getId() == R.id.dhcp_switch_button) {
					mDhcpSwitchTitle.setTextColor(0xFF000000);
					LogUtil.d("EthernetConfigController", "<<<onFocusChange----mDhcpSwitchTitle");
				} else if (v.getId() == R.id.manual_switch_button) {
					mManualSwitchTitle.setTextColor(0xFF000000);
				} else if (v instanceof EditText) {
					EditText textView = (EditText) v;
					boolean isIP = false;
					if (textView == mIpaddr) {
						isIP = true;
					}
					checkIpAddress(textView.getEditableText(), isIP);
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

		if (isNetworkConnected()) {
			mDhcpConnectState.setVisibility(View.VISIBLE);
			mManualConnectState.setVisibility(View.GONE);
			LogUtil.d("EthernetConfigController", "<<<enableDhcp----isNetworkConnected()"  + isNetworkConnected());
		}

		LogUtil.d("EthernetConfigController", "<<<----enableDhcp" + enable);
		mManualSwitchButton.setNextFocusDownId(R.id.eth_btn_confirm);

//		mPrevButton.setNextFocusUpId(R.id.manual_switch_button);
		mOkButton.setNextFocusUpId(R.id.manual_switch_button);
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

		if (isNetworkConnected()) {
			mDhcpConnectState.setVisibility(View.GONE);
			mManualConnectState.setVisibility(View.VISIBLE);
			LogUtil.d("EthernetConfigController", "<<<enableManual----isNetworkConnected()"  + isNetworkConnected());
		}

		LogUtil.d("EthernetConfigController", "<<<----enableManual" + enable);
		mManualSwitchButton.setNextFocusDownId(R.id.eth_ip);
		mOkButton.setNextFocusUpId(R.id.eth_backup_dns);
//		mPrevButton.setNextFocusUpId(R.id.eth_backup_dns);
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

	public int saveConfigure() {
		Log.d(TAG, "device name = " + mDev);

		if (mDev == null || mDev.isEmpty())
			return 0;

		if (mEthInfo == null) {
			if (mEthManager.isEthConfigured()) {
				mEthInfo = mEthManager.getSavedEthConfig();
			}			
			Log.d(TAG, "--------mEthManager.isEthConfigured() = " + mEthManager.isEthConfigured());
			Log.d(TAG, "--------mEthInfo = " + mEthInfo);
		}

//		EthernetDevInfo info = new EthernetDevInfo();
		mEthInfo.setIfName(mDev);

		Log.d(TAG, "---mDhcpSwitchIndicator-----info.getConnectMode() ==in front of change=== " + mEthInfo.getConnectMode());
		if (mDhcpSwitchIndicator.isChecked()) {
			mEthInfo.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP);
			mEthInfo.setIpAddress(null);
			mEthInfo.setRouteAddr(null);
			mEthInfo.setDnsAddr(null);
			mEthInfo.setNetMask(null);			
			Log.d(TAG, "---mDhcpSwitchIndicator-----info IpAddress ===== " + mEthInfo.getIpAddress());
			mEthManager.updateEthDevInfo(mEthInfo);
			
			Log.d(TAG, "--------mEnablePending = " + mEnablePending);
			if (mEnablePending) {
				Log.d(TAG, "--------mEthManager.getEthState() = " + mEthManager.getEthState());
				Log.d(TAG, "--------mEthManager.ETH_STATE_ENABLED = " + mEthManager.ETH_STATE_ENABLED);
				if (mEthManager.getEthState() == mEthManager.ETH_STATE_ENABLED) {
					mEthManager.setEthEnabled(true);
				}
				mEnablePending = false;
			}
			
			return 1;
		} else {
			String ip = mIpaddr.getText().toString();
			String mask = mMask.getText().toString();
			String gateway = mGw.getText().toString();
			String dns = mDns.getText().toString();

			boolean valid = true;

			if (ip.isEmpty() || !isIpAddress(ip, true)) {
				valid = false;
			}

			if (mask.isEmpty() || !isIpAddress(mask,false)) {
				valid = false;
			}

			if (!gateway.isEmpty() && !isIpAddress(gateway, false)) {
				valid = false;
			}

			if (!dns.isEmpty() && !isIpAddress(dns, false)) {
				valid = false;
			}

			if (!valid) {
				displayErrorIpPopup();
				return 0;
			}

//			if (mEthInfo.getConnectMode().equals(EthernetDevInfo.ETH_CONN_MODE_DHCP)) {
//				
//			}
			
			mEthInfo.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_MANUAL);
			Log.d(TAG, "--------manual ConnectMode ===== " + mEthInfo.getConnectMode());
			
			mEthInfo.setIpAddress(ip);
			mEthInfo.setRouteAddr(gateway);
			mEthInfo.setDnsAddr(dns);
			mEthInfo.setNetMask(mask);
			
			mEthManager.updateEthDevInfo(mEthInfo);
			
			Log.d(TAG, "--------mEnablePending = " + mEnablePending);
			if (mEnablePending) {
				Log.d(TAG, "--------mEthManager.getEthState() = " + mEthManager.getEthState());
				Log.d(TAG, "--------mEthManager.ETH_STATE_ENABLED = " + mEthManager.ETH_STATE_ENABLED);
				if (mEthManager.getEthState() == mEthManager.ETH_STATE_ENABLED) {
					mEthManager.setEthEnabled(true);
				}
				mEnablePending = false;
			}
		}

		return 0;
	}
	
	private Toast mToast = null;

	private void checkIpAddress(Editable s, boolean isIP) {
		String ip = s.toString();
		
		if (ip.isEmpty()) {
			return;
		}
		
		if (!isIpAddress(ip, isIP)) {
			displayErrorIpPopup();	
			s.clear();
		}
	}
	
	private void displayErrorIpPopup() {
		if (mToast != null) {
			mToast.cancel();
		}

		mToast = Toast.makeText(mContext, R.string.page_wired_eth_settings_error, Toast.LENGTH_SHORT);
		mToast.show();
	}

	private boolean isIpAddress(String value, boolean isIP) {
		int start = 0;
		int end = value.indexOf('.');
		int numBlocks = 0;
		int zeroCount = 0;
		int ttfCount = 0;

		while (start < value.length()) {
			if (end == -1) {
				end = value.length();
			}

			try {
				String var = value.substring(start, end);
				for (int i = 0; i < var.length(); i++) {
					char c = var.charAt(i);
					if (c < '0' || c > '9') {
						return false;
					}
				}

				int block = Integer.parseInt(var);
				if ((block > 255) || (block < 0)) {
					return false;
				}
				
				if (block == 0) {
					zeroCount++;
				}
				
				if (block == 255) {
					ttfCount++;
				}
				
			} catch (NumberFormatException e) {
				return false;
			}

			numBlocks++;

			start = end + 1;
			end = value.indexOf('.', start);
		}
		
		if (isIP && (zeroCount == 4 || ttfCount == 4)) {
			// 0.0.0.0 or 255.255.255.255
			return false;
		}
		
		return numBlocks == 4;
	}

	public void enableAfterConfig() {
		mEnablePending = true;
	}
}
