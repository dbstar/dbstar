package com.settings.components;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.settings.adapter.CheckedWifiListAdapter;
import com.settings.ottsettings.R;
import com.settings.utils.ToastUtils;

public class WifiSettingsViewWrapper {
	
	private Context context;
	private WifiManager wifiManager;
	private List<ScanResult> list;
	private CheckedWifiListAdapter adapter;
	
	// 无线扫描按钮
	private Button btnScanWifi;
	// 扫描wifi开关
	private CheckBox checkBox;
	private ListView wifiList;
	
	public WifiSettingsViewWrapper(Context context) {
		this.context = context;
	}

	public void initView(View view) {
		btnScanWifi = (Button) view.findViewById(R.id.wifi_settings_btn_checkWifi);
		checkBox = (CheckBox)view.findViewById(R.id.wifi_settings_checkbox);
		wifiList = (ListView) view.findViewById(R.id.wifi_settings_listView);
		
		checkBox.setChecked(true);
		
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager == null) {
			ToastUtils.showToast(context, context.getString(R.string.page_wifi_close));
			return;
		}
		
		if (checkBox.isChecked()) {
			wifiManager .setWifiEnabled(true);
			populateListViewData();
			
		} else {
			wifiManager.setWifiEnabled(false);
			if (list != null) {
				list.clear();
				adapter.refresh(list);
			}
		}
		
		btnScanWifi.setOnClickListener(new OnClickListener() {
			private long lastClick = 0l;
			
			@Override
			public void onClick(View v) {
				if (System.currentTimeMillis() - lastClick < 500l) {
					return;
				}
				lastClick = System.currentTimeMillis();
				
				if (wifiManager.isWifiEnabled()) {
					populateListViewData();
				} else {
					if (checkBox.isChecked()) 
						ToastUtils.showToast(context, context.getString(R.string.page_wifi_is_scanning));
					else
						ToastUtils.showToast(context, context.getString(R.string.page_wifi_close));
				}
			}
		});
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					wifiManager.setWifiEnabled(true);
					populateListViewData();
				} else {
					wifiManager.setWifiEnabled(false);
					if (list != null) {
						list.clear();
						adapter.refresh(list);
						wifiList.setAdapter(adapter);						
					}
				}
			}
		});
	}

	private void populateListViewData() {
		list = wifiManager.getScanResults();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		} else {
			adapter = new CheckedWifiListAdapter(context, list);				
		}
		adapter.refresh(list); 
		wifiList.setAdapter(adapter);
	}
	
}
