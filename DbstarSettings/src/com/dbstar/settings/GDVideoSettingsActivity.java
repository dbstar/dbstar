package com.dbstar.settings;

import java.util.List;

import com.dbstar.settings.util.Utils;

import android.content.pm.PackageManager;
import android.os.Bundle;

public class GDVideoSettingsActivity extends GDSettingsActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void buildHeaders(List<Header> headers) {
		loadHeadersFromResource(R.xml.display_settings_header, headers);
	}

	protected void updateHeadersList(List<Header> target) {
		Header header = target.get(0);
		int id = (int) header.id;
		if (mFirstHeader == null
				&& HeaderAdapter.getHeaderType(header) != HeaderAdapter.HEADER_TYPE_CATEGORY) {
			mFirstHeader = header;
		}
		mHeaderIndexMap.put(id, 0);
	}

}
