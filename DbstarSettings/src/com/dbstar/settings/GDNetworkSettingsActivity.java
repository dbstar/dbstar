package com.dbstar.settings;

import java.util.List;

import com.dbstar.settings.R;
import com.dbstar.settings.util.Utils;

import android.content.pm.PackageManager;
import android.os.Bundle;

public class GDNetworkSettingsActivity extends GDSettingsActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void buildHeaders(List<Header> headers) {
		loadHeadersFromResource(R.xml.network_settings_headers, headers);
	}

	protected void updateHeadersList(List<Header> target) {
		int i=0;
		while (i < target.size()) {
            Header header = target.get(i);
            // Ids are integers, so downcasting
            int id = (int) header.id;
            if (id == R.id.wifi_settings) {
                // Remove WiFi Settings if WiFi service is not available.
                if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI)) {
                    target.remove(header);
                }
            } else if (id == R.id.ethernet_settings) {
                if (!Utils.hwHasEthernet()) {
                    target.remove(header);
                }
            }


			if(i >= target.size())
				break;

            // Increment if the current one wasn't removed by the Utils code.
            if (target.get(i) == header) {
                // Hold on to the first header, when we need to reset to the top-level
                if (mFirstHeader == null &&
                        HeaderAdapter.getHeaderType(header) != HeaderAdapter.HEADER_TYPE_CATEGORY) {
                    mFirstHeader = header;
                }
                mHeaderIndexMap.put(id, i);
                i++;
            }
        }
	}
}
