package com.settings.components;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.settings.ottsettings.R;
import com.settings.wifihotspot.WifiApAdmin;

public class NetworkWifiSettings {
	private Context mContext;
	private Activity mActivity;
	private WifiManager mWifiManager;
	
	private LinearLayout dynamicPanel;
	private RadioGroup radioGroup;
	private RadioButton wifiMode, APMode;
	private Button btnNext;
	private WifiSettingsView wifiSettingsView;
	private boolean isFromWifi = false;
	
	public NetworkWifiSettings(Activity activity) {
		this.mActivity = activity;
		this.mContext = activity;
		this.mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	public void initView(View view) {
		dynamicPanel = (LinearLayout) view.findViewById(R.id.network_wifi_dynamicPanel);
		radioGroup = (RadioGroup) view.findViewById(R.id.network_wifi_radiogroup);
		btnNext = (Button) view.findViewById(R.id.network_wifi_btn_next);
		wifiMode = (RadioButton) view.findViewById(R.id.network_wifi_scan);
		APMode = (RadioButton) view.findViewById(R.id.network_wifi_hotspot);		
		
		btnNext.requestFocus();
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.network_wifi_scan) {
					WifiApAdmin.closeWifiAp(mContext);
					if (!mWifiManager.isWifiEnabled()) {						
						mWifiManager.setWifiEnabled(true);
					}					
				} else if (checkedId == R.id.network_wifi_hotspot) {
					mWifiManager.setWifiEnabled(false);
				}
				
			}
		});
		
		btnNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (APMode.isChecked()) {
					switchToWifiHotspotSettings();
				} else {
					switchToWifiSetting();
					isFromWifi = true;
				}
				
			}
		});
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
