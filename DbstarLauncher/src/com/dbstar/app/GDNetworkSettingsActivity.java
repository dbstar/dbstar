package com.dbstar.app;

import com.dbstar.R;

import android.content.Intent;
import android.os.Bundle;

public class GDNetworkSettingsActivity extends GDBaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_networkview);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void initializeView() {
		super.initializeView();

	}
}
