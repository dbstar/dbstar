package com.dbstar.app.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.RadioButton;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;

public class GDAudioSettingsActivity extends GDBaseActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_audioview);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void initializeView() {
		super.initializeView();

	}

	public void onRadioButtonClicked(View view) {

		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
		case R.id.settings_audio_transparent:
			if (checked)
				break;
		case R.id.settings_audio_transcode:
			if (checked)
				break;
		}
	}
}
