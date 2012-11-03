package com.dbstar.settings;

import java.util.List;

public class GDAudioSettingsActivity  extends MultiPanelActivity {
	public void onBuildHeaders(List<Header> target) {
        // Should be overloaded by subclasses
		
		Header ethernetHeader = new Header();
		ethernetHeader.fragment="com.dbstar.settings.audio.AudioSettings";
		ethernetHeader.titleRes = R.string.settings_audio;
		ethernetHeader.iconRes = R.drawable.ic_settings_sound;
		
		target.add(ethernetHeader);
    }
}
