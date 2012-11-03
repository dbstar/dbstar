package com.dbstar.settings;

import com.dbstar.settings.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GDBaseActivity extends Activity {
	private static final String TAG = "GDBaseActivity";

	protected static final String INTENT_KEY_MENUPATH = "menu_path";

	protected static final int MENU_LEVEL_1 = 0;
	protected static final int MENU_LEVEL_2 = 1;
	protected static final int MENU_LEVEL_3 = 2;
	protected static final int MENU_LEVEL_COUNT = 3;
	protected static final String MENU_STRING_DELIMITER = ">";
	protected String mMenuPath;
	protected MenuPathItem[] mMenuPathItems = new MenuPathItem[MENU_LEVEL_COUNT];
	// Menu path container view
	protected ViewGroup mMenuPathContainer;

	protected class MenuPathItem {
		TextView sTextView;
		ImageView sDelimiter;
	}

	protected void initializeMenuPath() {

		mMenuPathContainer = (ViewGroup) findViewById(R.id.menupath_view);

		for (int i = 0; i < MENU_LEVEL_COUNT; i++) {
			mMenuPathItems[i] = new MenuPathItem();
		}

		TextView textView = (TextView) findViewById(R.id.menupath_level1);
		mMenuPathItems[0].sTextView = textView;
		textView = (TextView) findViewById(R.id.menupath_level2);
		mMenuPathItems[1].sTextView = textView;
		textView = (TextView) findViewById(R.id.menupath_level3);
		mMenuPathItems[2].sTextView = textView;

		ImageView delimiterView = (ImageView) findViewById(R.id.menupath_level1_delimiter);
		mMenuPathItems[0].sDelimiter = delimiterView;

		delimiterView = (ImageView) findViewById(R.id.menupath_level2_delimiter);
		mMenuPathItems[1].sDelimiter = delimiterView;

		delimiterView = (ImageView) findViewById(R.id.menupath_level3_delimiter);
		mMenuPathItems[2].sDelimiter = delimiterView;
	}

	protected void initializeView() {
		initializeMenuPath();
	}

	protected void showMenuPath(String[] menuPath) {

		for (int i = 0; i < mMenuPathItems.length; i++) {
			if (i < menuPath.length) {
				mMenuPathItems[i].sTextView.setVisibility(View.VISIBLE);
				mMenuPathItems[i].sTextView.setText(menuPath[i]);

				if (mMenuPathItems[i].sDelimiter != null) {
					mMenuPathItems[i].sDelimiter.setVisibility(View.VISIBLE);
				}
			} else {
				mMenuPathItems[i].sTextView.setVisibility(View.INVISIBLE);

				if (mMenuPathItems[i].sDelimiter != null) {
					mMenuPathItems[i].sDelimiter.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
