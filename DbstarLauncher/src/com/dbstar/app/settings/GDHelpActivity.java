package com.dbstar.app.settings;

import android.content.Intent;
import android.os.Bundle;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;

public class GDHelpActivity extends GDBaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_helpview);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}
}
