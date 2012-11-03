package com.dbstar.settings;

import java.util.List;

import com.dbstar.settings.R;

public class GDNetworkSettingsActivity extends MultiPanelActivity {
	
	
	public void onBuildHeaders(List<Header> target) {
        // Should be overloaded by subclasses
		
		Header ethernetHeader = new Header();
		ethernetHeader.fragment="com.dbstar.settings.ethernet.EthernetSettings";
		ethernetHeader.titleRes = R.string.eth_setting;
		ethernetHeader.iconRes = R.drawable.ic_settings_ethernet3;
		
		target.add(ethernetHeader);
		
		Header wifiHeader = new Header();
		wifiHeader.fragment="com.dbstar.settings.wifi.WifiSettings";
		wifiHeader.titleRes = R.string.wifi_settings_title;
		wifiHeader.iconRes = R.drawable.ic_settings_wireless;
		
		target.add(wifiHeader);
    }

}
