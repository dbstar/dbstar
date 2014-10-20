package com.settings.components;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.settings.bean.WifiHotspot;
import com.settings.bean.WifiHotspotConfig;
import com.settings.ottsettings.R;
import com.settings.utils.DataUtils;
import com.settings.utils.LogUtil;
import com.settings.wifihotspot.WifiAdmin;
import com.settings.wifihotspot.WifiApAdmin;

public class NetworkWifiSettings {
	private Context mContext;
	private Activity mActivity;
	private WifiManager mWifiManager;
	
	private LinearLayout dynamicPanel;
	private CheckBox wifiSwitch;
	private TextView txtSelect;
	private RadioGroup radioGroup;
	private RadioButton wifiMode, APMode;
	private Button btnNext;
	private WifiSettingsView wifiSettingsView;
	private boolean isFromWifi = false;
	private WifiHotspot wifiHotspot = new WifiHotspot();
	private WifiApAdmin wifiAp;
	private WifiAdmin wifiAdmin;
	
	private static final String Data_Wireless_Switch = "com.wifi.network.isOpen";
	private static final String Data_Key_IsOpenWifi = "com.wifi.isOpen";
	private static final String Data_Key_IsOpenWifiHotspot = "com.settings.isOpenWifiHotspot";
	private static final String Data_Key_SSID = "com.settings.ssid";
	private static final String Data_Key_SECURITY = "com.settings.security";
	private static final String Data_Key_PWD = "com.settings.password";
	
	public NetworkWifiSettings(Activity activity) {
		this.mActivity = activity;
		this.mContext = activity;
		this.mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	public void initView(View view) {
		dynamicPanel = (LinearLayout) view.findViewById(R.id.network_wifi_dynamicPanel);
		wifiSwitch = (CheckBox) view.findViewById(R.id.network_wifi_cb_switch);
		txtSelect = (TextView) view.findViewById(R.id.network_wifi_txt_select);
		radioGroup = (RadioGroup) view.findViewById(R.id.network_wifi_radiogroup);
		btnNext = (Button) view.findViewById(R.id.network_wifi_btn_next);
		wifiMode = (RadioButton) view.findViewById(R.id.network_wifi_scan);
		APMode = (RadioButton) view.findViewById(R.id.network_wifi_hotspot);		
		
		wifiSwitch.requestFocus();
		
		boolean wirelessIsOpen = DataUtils.getPreference(mContext, Data_Wireless_Switch, true);
		boolean isOpenWifiHotspot = DataUtils.getPreference(mContext, Data_Key_IsOpenWifiHotspot, false);
		boolean isOpenWifi = DataUtils.getPreference(mContext, Data_Key_IsOpenWifi, true);
		LogUtil.d("NetworkWifiSettings", "------wirelessIsOpen = " + wirelessIsOpen);
		LogUtil.d("NetworkWifiSettings", "------isOpenWifiHotspot = " + isOpenWifiHotspot);
		LogUtil.d("NetworkWifiSettings", "------isOpenWifi = " + isOpenWifi);
		
		wifiSwitch.setChecked(wirelessIsOpen);
		
		if (wirelessIsOpen) {
			enable();
			if (isOpenWifiHotspot) {
				txtSelect.setText(mContext.getResources().getString(R.string.network_wifi_setup_2));
				// 打开AP
				String ssid = DataUtils.getPreference(mContext, Data_Key_SSID, "DbstarAP");
				String password = DataUtils.getPreference(mContext, Data_Key_PWD, "12345678");
				String security = DataUtils.getPreference(mContext, Data_Key_SECURITY, "WPA2 PSK");
				wifiHotspot.setSsid(ssid);
				wifiHotspot.setPassword(password);
				wifiHotspot.setSecurity(security);
				wifiHotspotConnect(wifiHotspot);
			} else {
				txtSelect.setText(mContext.getResources().getString(R.string.network_wifi_setup_1));
				// 打开Wifi
				if (!mWifiManager.isWifiEnabled()) {						
					mWifiManager.setWifiEnabled(true);
				}
				mWifiManager.reassociate();
			}
		} else {
			txtSelect.setText(mContext.getResources().getString(R.string.network_wifi_switch_isClose));
			unEnable();
			WifiApAdmin.closeWifiAp(mContext);
			mWifiManager.setWifiEnabled(false);
		}
		
		wifiSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					enable();					
					txtSelect.setText(mContext.getResources().getString(R.string.network_wifi_switch_isOpen));
				} else {
					wifiSwitch.requestFocus();
					unEnable();
					txtSelect.setText(mContext.getResources().getString(R.string.network_wifi_switch_isClose));
					
					// 将Ap和wifi都关掉
					WifiApAdmin.closeWifiAp(mContext);
					mWifiManager.setWifiEnabled(false);
				}
				
				DataUtils.savePreference(mContext, Data_Wireless_Switch, isChecked);
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				DataUtils.savePreference(mContext, Data_Key_IsOpenWifi, wifiMode.isChecked());
				WifiHotspotConfig.getInstance(mContext).setRestoreWifiHotspot(mContext, APMode.isChecked());
				DataUtils.savePreference(mContext, Data_Key_IsOpenWifiHotspot, APMode.isChecked());
				
				if (APMode.isChecked()) {
					mWifiManager.setWifiEnabled(false);
					switchToWifiHotspotSettings();
				} else {
					WifiApAdmin.closeWifiAp(mContext);
					if (!mWifiManager.isWifiEnabled()) {						
						mWifiManager.setWifiEnabled(true);
					}
					switchToWifiSetting();
					isFromWifi = true;
				}
				
			}
		});
	}

	private void wifiHotspotConnect(WifiHotspot wifiHotspot) {
		wifiAp = new WifiApAdmin(mContext);
//				wifiAp.startWifiAp("\"HotSpot\"", "hhhhhh123");
		wifiAp.startWifiAp(wifiHotspot.getSsid(), wifiHotspot.getPassword());
		
		wifiAdmin = new WifiAdmin(mContext) {
			
			@Override
			public void onNotifyWifiConnected() {
				LogUtil.d("OTTSettingsService", "have connected success!");
				LogUtil.d("OTTSettingsService", "###############################");
				
			}
			
			@Override
			public void onNotifyWifiConnectFailed() {
				LogUtil.d("OTTSettingsService", "have connected failed!");
				LogUtil.d("OTTSettingsService", "###############################");
				
			}
			
			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
				mContext.registerReceiver(receiver, filter);
				return null;
			}
			
			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				mContext.unregisterReceiver(receiver);
			}					
		};
		
		wifiAdmin.openWifi();
		wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(), getTypeOfSecurity(wifiHotspot));
//				wifiAdmin.addNetwork(wifiHotspot.getSsid(), wifiHotspot.getPassword(),  WifiAdmin.TYPE_WPA);
	}
	
	private int getTypeOfSecurity(WifiHotspot wifiHotspot) {
		int type = WifiAdmin.TYPE_WPA;
		if (wifiHotspot != null) {
			if (wifiHotspot.getSecurity().equals("Open")) {
				type = WifiAdmin.TYPE_NO_PASSWD;
			} else if (wifiHotspot.getSecurity().equals("WPA PSK")) {
				type = WifiAdmin.TYPE_WEP;								
			} else {				
				type = WifiAdmin.TYPE_WPA;				
			}
		}
		return type;
	}
	
	private void enable() {
		radioGroup.setEnabled(true);
		radioGroup.setFocusable(true);
		btnNext.setEnabled(true);
	}

	private void unEnable() {
		radioGroup.setEnabled(false);
		radioGroup.setFocusable(false);
		btnNext.setEnabled(false);
	}
	
	public void onKeyDown(int keyCode, KeyEvent event) {
		if (isFromWifi) {
			wifiSettingsView.pause();
		}
	}
	
	/**
	 * AP模式
	 * @param wifiHotspot
	 * @param isOpenWifiHotspot
	 */
	private void switchToWifiHotspotSettings() {
		WifiHotspotSettingsViewWrapper wrapper = null;
//		View view = populateViewToDynamicPanel(R.layout.wifi_hotspot_settings);
		View view = populateViewToDynamicPanel(R.layout.lt_page_wifi_hotspot_settings);
		if (wrapper == null) {
			wrapper = new WifiHotspotSettingsViewWrapper(mContext);
		}
		wrapper.initView(view);
	}
	

	/**
	 * 网卡模式
	 */
	private void switchToWifiSetting() {
//		View view = populateViewToDynamicPanel(R.layout.lt_page_wifi_settings);
		View view = populateViewToDynamicPanel(R.layout.network_wifi_settings);
		wifiSettingsView = new WifiSettingsView(mActivity);
		wifiSettingsView.initView(view);
	}
	
	private View populateViewToDynamicPanel(int resId) {
		if (dynamicPanel.getChildCount() > 0) {
			View view = dynamicPanel.getChildAt(0);
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
			view.startAnimation(animation);
		}
		dynamicPanel.removeAllViews();
		// 构造器
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(resId, null);
		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
		view.startAnimation(animation);
		dynamicPanel.addView(view);
		return view;
	}
}
