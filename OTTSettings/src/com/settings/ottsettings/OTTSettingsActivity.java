package com.settings.ottsettings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.settings.bean.WifiHotspot;
import com.settings.components.AboutSettingsViewWrapper;
import com.settings.components.AudioSettingsViewWrapper;
import com.settings.components.HelpSettingsViewWrapper;
import com.settings.components.NetStatusViewWrapper;
import com.settings.components.NetworkWifiSettings;
import com.settings.components.ShowAdjustSettingsViewWrapper;
import com.settings.components.SysUpgradeSettingsViewWrapper;
import com.settings.components.VedioSettingsViewWrapper;
import com.settings.components.WifiHotspotSettingsViewWrapper;
import com.settings.components.WifiSettingsView;
import com.settings.components.WiredSettingsView;
import com.settings.utils.LogUtil;

public class OTTSettingsActivity extends Activity {

	private RadioGroup rgContainer;
	private LinearLayout dynamicPanel;
	
	private boolean isFromWired = false;
	private boolean isFromWifi = false;
	
	private VedioSettingsViewWrapper vedioSettingsViewWrapper = null;
	private WiredSettingsView wiredSettingsView = null;
	private WifiSettingsView wifiSettingsView = null;
	private WifiHotspot wifiHotspot = new WifiHotspot();
	
	private static int Ethernet_Network_Mode = 0;
	private static final String Ethernet_Mode = "ethernet_mode";
	private ShowAdjustSettingsViewWrapper showAdjustSettingsViewWrapper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置成无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.lt_page_settings);

		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

		findViews();
		switchToNetStatus();
		
		populateData();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		if (wiredSettingdViewWrapper == null) {
//			wiredSettingdViewWrapper = new WiredSettingsViewWrapper(this);
//			wiredSettingdViewWrapper.onPause();
//		}
	
		LogUtil.d(getLocalClassName(), "onPause<<<<<?????>>>>>");
		
		if (isFromWired) {	
			LogUtil.d(getLocalClassName(), "onPause<<<<<?????isFromWired>>>>>" + isFromWired);			
			
//			pauseOfWired();
		}
		if (isFromWifi) {					
			LogUtil.d(getLocalClassName(), "onPause<<<<<?????isFromWifi>>>>>" + isFromWifi);			
//			pauseOfWifi();
		}
	}
	
	private void populateData() {
		wifiHotspot.setSsid("DbstarAP");
		wifiHotspot.setPassword("123456789");
		rgContainer.setOnCheckedChangeListener(new RbCheckedChangeListener());
		
		Bundle bundle = getIntent().getBundleExtra("mode");
		Ethernet_Network_Mode = bundle.getInt(Ethernet_Mode);
		LogUtil.d(getLocalClassName(), "populateData<<<<<?????>>>>>" + Ethernet_Network_Mode);			
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		VedioSettingsViewWrapper vedioSettingsViewWrapper = null;
		if (vedioSettingsViewWrapper == null) {
			vedioSettingsViewWrapper = new VedioSettingsViewWrapper(this);
		}
		vedioSettingsViewWrapper.onActivityResult(requestCode, resultCode, data);
		
		if (isFromWifi) {					
			pauseOfWifi();
		}
	}
	
	/**
	 * 关于
	 */
	private void switchToAboutSettings() {
		AboutSettingsViewWrapper wrapper = null;
		View view = populateViewToDynamicPanel(R.layout.lt_page_about_settings);
		if (wrapper == null) {
			wrapper = new AboutSettingsViewWrapper(this);
			wrapper.initView(view);
		}
	}
	
	/**
	 * 帮助
	 */
	private void switchToHelpSettings() {
		HelpSettingsViewWrapper wrapper = null;
		View view = populateViewToDynamicPanel(R.layout.lt_page_help_settings);
		if (wrapper == null) {
			wrapper = new HelpSettingsViewWrapper(this);
			wrapper.initView(view);
		}
	}
	
	/**
	 * 系统升级
	 */
	private void switchToSysUpgradeSettings() {
		SysUpgradeSettingsViewWrapper wrapper = null;
		View view = populateViewToDynamicPanel(R.layout.lt_page_sys_upgrade_settings);
		if (wrapper == null) {
			wrapper = new SysUpgradeSettingsViewWrapper(this);
			wrapper.initView(view);
		}
	}
	
	/**
	 * 显示调整
	 */
	private void switchToShowAdjustSettings() {
		View view = populateViewToDynamicPanel(R.layout.lt_page_show_adjust_settings);
		if (showAdjustSettingsViewWrapper == null) {
			showAdjustSettingsViewWrapper = new ShowAdjustSettingsViewWrapper(this);
		}
		showAdjustSettingsViewWrapper.initView(view);
	}
	
	/**
	 * 视频设置
	 */
	private void switchToVedioSettings() {
//		VedioSettingsViewWrapper vedioSettingsViewWrapper = null;
//		View view = populateViewToDynamicPanel(R.layout.lt_page_vedio_settings);
		View view = populateViewToDynamicPanel(R.layout.lt_page_vedio_display_settings);
//		if (vedioSettingsViewWrapper == null) {
			vedioSettingsViewWrapper = new VedioSettingsViewWrapper(this);
			vedioSettingsViewWrapper.initView(view);
//		}
	}
	
	/**
	 * 音频设置
	 */
	private void switchToAudioSettings() {
		AudioSettingsViewWrapper wrapper = null;
//		View view = populateViewToDynamicPanel(R.layout.lt_page_audio_settings);
		View view = populateViewToDynamicPanel(R.layout.lt_page_audio_sound_settings);
		if (wrapper == null) {
			wrapper = new AudioSettingsViewWrapper(this);
			wrapper.initView(view);
		}
	}

	/**
	 * WiFi热点
	 */
	private void switchToWifiHotspotSettings() {
		WifiHotspotSettingsViewWrapper wrapper = null;
		View view = populateViewToDynamicPanel(R.layout.lt_page_wifi_hotspot_settings);
		if (wrapper == null) {
			wrapper = new WifiHotspotSettingsViewWrapper(this);
		}
		wrapper.initView(view);
	}

//	/**
//	 * 无线设置
//	 */
//	private void switchToWifiSetting() {
////		WifiSettingsView wifiSettingsView = null;
////		View view = populateViewToDynamicPanel(R.layout.lt_page_wifi_settings);
//		View view = populateViewToDynamicPanel(R.layout.network_wifi_settings);
//		if (wifiSettingsView == null) {
//			wifiSettingsView = new WifiSettingsView(this);
//		}
//		wifiSettingsView.initView(view);
////			wifiSettingsView.resume();
//	}
	
	/**
	 * 无线设置
	 */
	private void switchToWifiSetting() {
		NetworkWifiSettings networkWifiSettings = null;
//		View view = populateViewToDynamicPanel(R.layout.lt_page_wifi_settings);
		View view = populateViewToDynamicPanel(R.layout.lt_page_network_wifi_settings);
		if (networkWifiSettings == null) {
			networkWifiSettings = new NetworkWifiSettings(this);
		}
		networkWifiSettings.initView(view);
//			wifiSettingsView.resume();
	}
	
	/**
	 * 有线设置
	 */
	private void switchToWiredSettings() {
//		WiredSettingsView wiredSettingsView = null;
		View view = populateViewToDynamicPanel(R.layout.lt_page_network_ethernet_settings);
//		if (wiredSettingsView == null) {
			wiredSettingsView = new WiredSettingsView(this, view, Ethernet_Network_Mode);
//		}
		wiredSettingsView.initView(view);
//		wiredSettingsView.onResume();
	}
	
	/**
	 * 网络状态
	 */
	private void switchToNetStatus() {
		NetStatusViewWrapper wrapper = null;
		View view = populateViewToDynamicPanel(R.layout.lt_page_network_settings);

		if (wrapper == null) {
			wrapper = new NetStatusViewWrapper(this, Ethernet_Network_Mode);
			wrapper.initView(view);
		}
	}

	private View populateViewToDynamicPanel(int resId) {
		if (dynamicPanel.getChildCount() > 0) {
			View view = dynamicPanel.getChildAt(0);
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
			view.startAnimation(animation);
		}
		dynamicPanel.removeAllViews();
		// 构造器
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(resId, null);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		view.startAnimation(animation);
		dynamicPanel.addView(view);
		return view;
	}
	
	private void pauseOfWired() {
//		WiredSettingsView wiredSettingsView = null;
		if (wiredSettingsView == null) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.lt_page_network_ethernet_settings, null);
			wiredSettingsView = new WiredSettingsView(this, view, Ethernet_Network_Mode);
		}
		wiredSettingsView.onPause();
		isFromWired = false;
	}
	
	private void pauseOfWifi() {
		WifiSettingsView wifiSettingsView = null;
		if (wifiSettingsView == null) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.network_wifi_settings, null);
			wifiSettingsView = new WifiSettingsView(this);
			wifiSettingsView.initView(view);
		}
		wifiSettingsView.pause();
		isFromWifi = false;
	}
	
	private class RbCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.settings_net_status:
				switchToNetStatus();
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				break;
			case R.id.settings_wired:
				switchToWiredSettings();
				if (isFromWifi) {					
					pauseOfWifi();
				}
				isFromWired = true;
				break;
			case R.id.settings_wifi:
				switchToWifiSetting();
				
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				isFromWifi = true;
				break;
			case R.id.settings_wifi_hotspot:
				switchToWifiHotspotSettings();
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				break;
			case R.id.settings_audio:
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				switchToAudioSettings();
				break;
			case R.id.settings_vedio:
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				switchToVedioSettings();
				break;
			case R.id.settings_showAdjust:
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				switchToShowAdjustSettings();
				break;
			case R.id.settings_sysUpgrade:
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				switchToSysUpgradeSettings();
				break;
			case R.id.settings_help:
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				switchToHelpSettings();
				break;
			case R.id.settings_about:
//				if (isFromWired) {					
//					pauseOfWired();
//				}
				if (isFromWifi) {					
					pauseOfWifi();
				}
				switchToAboutSettings();
				break;
			default:
				break;
			}

		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		Log.d(getClass().getName(), "onKeyDown---------isOpenAdjustScreenView : " + showAdjustSettingsViewWrapper.isOpenAdjustScreenView());
		if (showAdjustSettingsViewWrapper != null && showAdjustSettingsViewWrapper.isOpenAdjustScreenView()) {
			return showAdjustSettingsViewWrapper.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		Log.d(getClass().getName(), "onKeyUp-------isOpenAdjustScreenView : " + showAdjustSettingsViewWrapper.isOpenAdjustScreenView());
		if (showAdjustSettingsViewWrapper != null && showAdjustSettingsViewWrapper.isOpenAdjustScreenView()) {			
			return showAdjustSettingsViewWrapper.onKeyUp(keyCode, event);
		}
		return super.onKeyUp(keyCode, event);
	}
	
	private void findViews() {
		rgContainer = (RadioGroup) findViewById(R.id.settings_rg_container);
		dynamicPanel = (LinearLayout) findViewById(R.id.settings_dynamic_container);
	}
}
