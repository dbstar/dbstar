package com.dbstar.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.service.GDDataProviderService;

public class GDHelpActivity extends GDBaseActivity {
	
	TextView mHelpContent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_helpview);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}
	
//	public void onServiceStart() {
//		mService.getHelpInfo(this);
//	}
//	
//	public void updateData(int type, String key, Object data) {
//		if (type == GDDataProviderService.REQUESTTYPE_GETHELPINFO) {
//			if (data != null) {
//				String helpInfo = (String) data;
//				if (!helpInfo.isEmpty()) {
//					mHelpContent.setText(helpInfo);
//				}
//			}
//		}
//	}
	
	public void initializeView() {
		super.initializeView();
		
//		mHelpContent = (TextView) findViewById(R.id.settings_help_content);
//		mHelpContent.setText("");
	}
}
