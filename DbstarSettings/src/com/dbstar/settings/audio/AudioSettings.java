package com.dbstar.settings.audio;

import com.dbstar.settings.R;
import com.dbstar.settings.utils.SoundSettings;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

public class AudioSettings extends Fragment {
	
	private RadioButton mTransparent, mTranscode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.audio_settings, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mTransparent = (RadioButton) getActivity().findViewById(R.id.settings_audio_transparent);
		mTranscode = (RadioButton) getActivity().findViewById(R.id.settings_audio_transcode);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
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
