package com.dbstar.app.settings;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dbstar.R;
import com.dbstar.util.LogUtil;

public class GDAdvancedToolsActivity extends Activity {
	private static final String TAG = "GDAdvancedToolsActivity";
	private static final String INTENT_KEY_MENUPATH = "menu_path";

	private Button btnSettings;
	private Button btnSysManager;
	private Button btnWebBrowser;
	private Button btnFileBrowser;
	private Button btnFactoryTest;
	
	private class Item {
		public String component;
		public String activity;
	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.advanced_tools_view);

		Intent intent = getIntent();
		String mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		
		initializeView(mMenuPath);
	}
	
	private void initializeView(final String menuPath) {
		Item item = new Item();
		PackageManager manager = getPackageManager();
		
		//
		btnSettings = (Button) findViewById(R.id.btn_settings);
		Intent intent = new Intent();
		String componentName = "com.android.settings.Settings";
		intent.setComponent(new ComponentName("com.android.settings", componentName));
		intent.setAction("android.intent.action.VIEW");
		List<ResolveInfo> list = manager.queryIntentActivities(intent, 0);
		if (list != null && list.size() > 0)		
			btnSettings.setVisibility(View.VISIBLE);
		else 
			btnSettings.setVisibility(View.GONE);
		
		btnSettings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startComponent("com.android.settings", "Settings");
			}
		});
		
		//
		btnWebBrowser = (Button) findViewById(R.id.btn_webbrowser);
		final Intent webIntent = new Intent(Intent.ACTION_VIEW);
		webIntent.setData(Uri.parse("http://www.baidu.com"));
		List<ResolveInfo> webBrowserList = manager.queryIntentActivities(webIntent, 0);
		if (webBrowserList != null && webBrowserList.size() > 0)		
			btnWebBrowser.setVisibility(View.VISIBLE);
		else 
			btnWebBrowser.setVisibility(View.GONE);
			
		btnWebBrowser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(webIntent);
			}
		});

		//
		btnFileBrowser = (Button) findViewById(R.id.btn_filebrowser);
		btnFileBrowser.setOnClickListener(mListener);
		item = new Item();
		btnFileBrowser.setTag(item);
		item.component = "com.fb.FileBrower";
		item.activity = "FileBrower";
		
		//
		btnFactoryTest = (Button) findViewById(R.id.btn_factorytest);
		Intent testIntent = new Intent();
		String testComponentName = "com.guodian.checkdevicetool.SelectTestActivity";
		testIntent.setComponent(new ComponentName("com.guodian.checkdevicetool", testComponentName));
		testIntent.setAction("android.intent.action.VIEW");
		List<ResolveInfo> factoryTestlist = manager.queryIntentActivities(testIntent, 0);
		if (factoryTestlist != null && factoryTestlist.size() > 0)
			btnFactoryTest.setVisibility(View.VISIBLE);						
		else
			btnFactoryTest.setVisibility(View.GONE);
		
		btnFactoryTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startComponent("com.guodian.checkdevicetool", "SelectTestActivity");
			}
		});
	
		//
		btnSysManager = (Button) findViewById(R.id.btn_system_manager);
		btnSysManager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GDAdvancedToolsActivity.this, GDSystemMgrActivity.class);
				intent.putExtra(INTENT_KEY_MENUPATH, menuPath);
				startActivity(intent);
			}
		});
		
	}
	
	View.OnClickListener mListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if (v instanceof Button) {
				Button btn = (Button) v;
				
				Item item = (Item) btn.getTag();
				startComponent(item.component, item.activity);
			}
		}
	};
	
	private void startComponent(String packageName, String activityName) {
		Intent intent = new Intent();
		String componentName = packageName + "." + activityName;
		intent.setComponent(new ComponentName(packageName, componentName));
		intent.setAction("android.intent.action.VIEW");
		
		LogUtil.d(TAG, "start " + componentName);
		startActivity(intent);			
	}
	
}
