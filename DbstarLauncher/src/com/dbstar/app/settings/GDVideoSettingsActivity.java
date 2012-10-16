package com.dbstar.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;

public class GDVideoSettingsActivity extends GDBaseActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_videoview);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void initializeView() {
		super.initializeView();

	}

	public void onCheckboxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();

		switch (view.getId()) {
		case R.id.video_resolution_1080p60:
			if (checked)
				break;
		case R.id.video_resolution_1080p25:
			if (checked)
				break;
		case R.id.video_resolution_1080i50:
			if (checked)
				break;
		case R.id.video_resolution_720p:
			if (checked)
				break;
		}
	}
}
