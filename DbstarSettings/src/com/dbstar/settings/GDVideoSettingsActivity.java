package com.dbstar.settings;

import java.util.List;

import com.dbstar.settings.display.PositionSetting;
import com.dbstar.settings.utils.Utils;

import android.content.ComponentName;
import android.content.Intent;

public class GDVideoSettingsActivity extends MultiPanelActivity {
	public void onBuildHeaders(List<Header> target) {
		Header header = null;

		if (Utils.platformHasTvOutput()) {
			header = new Header();
			header.fragment = "com.dbstar.settings.display.TVOutputModeSettings";
			header.titleRes = R.string.tv_output_mode;
			header.iconRes = R.drawable.ic_settings_display;
			target.add(header);
		}

		if (Utils.hasCVBSMode()) {
			header = new Header();
			header.fragment = "com.dbstar.settings.display.CVBSOutputModeSettings";
			header.titleRes = R.string.cvbs_output_mode;
			header.iconRes = R.drawable.ic_settings_display;
			target.add(header);
		}

		if (Utils.platformHasTvOutput()) {
			header = new Header();
			header.titleRes = R.string.display_position;
			header.iconRes = R.drawable.ic_settings_display;
			header.intent = new Intent();
			header.intent.setComponent(new ComponentName("com.dbstar.settings",
					"com.dbstar.settings.display.PositionSetting"));
			target.add(header);
		}

		if (Utils.platformHasDefaultTVFreq()) {
			header = new Header();
			header.fragment = "com.dbstar.settings.display.FrequencySettings";
			header.titleRes = R.string.tv_default_frequency;
			header.iconRes = R.drawable.ic_settings_display;
			target.add(header);
		}
	}
}
