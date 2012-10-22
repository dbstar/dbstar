package com.dbstar.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.dbstar.settings.R;
import com.dbstar.settings.util.SoundSettings;

public class GDAudioSettingsActivity extends GDBaseActivity {

	private RadioButton mTransparent, mTranscode;

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
		mTransparent = (RadioButton) findViewById(R.id.settings_audio_transparent);
		mTranscode = (RadioButton) findViewById(R.id.settings_audio_transcode);
		String mode = SoundSettings.getAudioOutputMode();
		if (mode != null && !mode.isEmpty()) {
			if (mode.equals(SoundSettings.AudioModePCM)) {
				mTranscode.setChecked(true);
				mTransparent.setChecked(false);
			} else if (mode.equals(SoundSettings.AudioModeRAW)) {
				mTranscode.setChecked(false);
				mTransparent.setChecked(true);
			}
		}
	}

	public void onRadioButtonClicked(View view) {

		boolean checked = ((RadioButton) view).isChecked();

		switch (view.getId()) {
		case R.id.settings_audio_transparent: {
			if (checked) {
				SoundSettings.setAudioOutputMode(SoundSettings.AudioModeRAW,
						"1");
			}
			break;
		}
		case R.id.settings_audio_transcode: {
			if (checked) {
				SoundSettings.setAudioOutputMode(SoundSettings.AudioModePCM,
						"0");
			}
			break;
		}
		}
	}
}
