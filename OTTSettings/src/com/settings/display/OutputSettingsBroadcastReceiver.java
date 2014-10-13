package com.settings.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.util.Log;

import com.settings.utils.LogUtil;
import com.settings.utils.SettingsCommon;
import com.settings.utils.Utils;
import android.app.SystemWriteManager;

public class OutputSettingsBroadcastReceiver extends BroadcastReceiver {

	private final String OUTPUT_MODE = "output_mode";

	private final String OUTPUT_POSITION_X = "output_position_x";
	private final String OUTPUT_POSITION_Y = "output_position_y";
	private final String OUTPUT_POSITION_W = "output_position_w";
	private final String OUTPUT_POSITION_H = "output_position_h";
	private final String OUTPUT_POSITION_MODE = "output_position_mode";

	private final String DISP_W = "disp_w";
	private final String DISP_H = "disp_h";

	private final String OutputModeFile = "/sys/class/display/mode";
	private final String PpscalerFile = "/sys/class/ppmgr/ppscaler";
	private final String PpscalerRectFile = "/sys/class/ppmgr/ppscaler_rect";
	private final String DispFile = "/sys/class/ppmgr/disp";
	private final String FreescaleFb0File = "/sys/class/graphics/fb0/free_scale";
	private final String FreescaleFb1File = "/sys/class/graphics/fb1/free_scale";
	private final String VideoAxisFile = "/sys/class/video/axis";
	private final String request2XScaleFile = "/sys/class/graphics/fb0/request2XScale";
	private final String scaleAxisOsd1File = "/sys/class/graphics/fb1/scale_axis";
	private final String scaleOsd1File = "/sys/class/graphics/fb1/scale";
	private final String blankFb0File = "/sys/class/graphics/fb0/blank";
	private final static String sel_480ioutput_x = "ubootenv.var.480ioutputx";
	private final static String sel_480ioutput_y = "ubootenv.var.480ioutputy";
	private final static String sel_480ioutput_width = "ubootenv.var.480ioutputwidth";
	private final static String sel_480ioutput_height = "ubootenv.var.480ioutputheight";
	private final static String sel_480poutput_x = "ubootenv.var.480poutputx";
	private final static String sel_480poutput_y = "ubootenv.var.480poutputy";
	private final static String sel_480poutput_width = "ubootenv.var.480poutputwidth";
	private final static String sel_480poutput_height = "ubootenv.var.480poutputheight";
	private final static String sel_576ioutput_x = "ubootenv.var.576ioutputx";
	private final static String sel_576ioutput_y = "ubootenv.var.576ioutputy";
	private final static String sel_576ioutput_width = "ubootenv.var.576ioutputwidth";
	private final static String sel_576ioutput_height = "ubootenv.var.576ioutputheight";
	private final static String sel_576poutput_x = "ubootenv.var.576poutputx";
	private final static String sel_576poutput_y = "ubootenv.var.576poutputy";
	private final static String sel_576poutput_width = "ubootenv.var.576poutputwidth";
	private final static String sel_576poutput_height = "ubootenv.var.576poutputheight";
	private final static String sel_720poutput_x = "ubootenv.var.720poutputx";
	private final static String sel_720poutput_y = "ubootenv.var.720poutputy";
	private final static String sel_720poutput_width = "ubootenv.var.720poutputwidth";
	private final static String sel_720poutput_height = "ubootenv.var.720poutputheight";
	private final static String sel_1080ioutput_x = "ubootenv.var.1080ioutputx";
	private final static String sel_1080ioutput_y = "ubootenv.var.1080ioutputy";
	private final static String sel_1080ioutput_width = "ubootenv.var.1080ioutputwidth";
	private final static String sel_1080ioutput_height = "ubootenv.var.1080ioutputheight";
	private final static String sel_1080poutput_x = "ubootenv.var.1080poutputx";
	private final static String sel_1080poutput_y = "ubootenv.var.1080poutputy";
	private final static String sel_1080poutput_width = "ubootenv.var.1080poutputwidth";
	private final static String sel_1080poutput_height = "ubootenv.var.1080poutputheight";
	private static final int OUTPUT480_FULL_WIDTH = 720;
	private static final int OUTPUT480_FULL_HEIGHT = 480;
	private static final int OUTPUT576_FULL_WIDTH = 720;
	private static final int OUTPUT576_FULL_HEIGHT = 576;
	private static final int OUTPUT720_FULL_WIDTH = 1280;
	private static final int OUTPUT720_FULL_HEIGHT = 720;
	private static final int OUTPUT1080_FULL_WIDTH = 1920;
	private static final int OUTPUT1080_FULL_HEIGHT = 1080;
	
	private SystemWriteManager sw = null;

	private boolean hasCvbsOutput = Utils.hasCVBSMode();

	private final String[] mOutputModeList = { "480i", "480p", "576i", "576p",
			"720p", "1080i", "1080p" };
	private final String[] mOutputModeList_50hz = { "480i", "480p", "576i",
			"576p", "720p50hz", "1080i50hz", "1080p50hz" };

	// static {
	// System.loadLibrary("outputsettings");
	// }

	@Override
	public void onReceive(Context context, Intent intent) {
		
		sw = (SystemWriteManager) context.getSystemService("system_write");
		
		// boot completed
		if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
			if (SystemProperties.getInt(SettingsCommon.STR_1080SCALE, 0) == 2) {
				Log.d("OutputSettingsBroadcastReceiver", "onReceive ---------intent---" + intent);
				Utils.setValue(VideoAxisFile, "0 0 1280 720");
				Utils.setValue(DispFile, "1280 720");
			}
		}
		// change output mode
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_OUTPUTMODE_CHANGE)) {
			int[] curPosition = { 0, 0, 1280, 720 };
			String outputMode = intent.getStringExtra(OUTPUT_MODE);

			if (hasCvbsOutput
					&& (outputMode.equals("576i") || outputMode.equals("480i"))) {
				disableVpp2();
			}

			for (int new_index = 0; new_index < mOutputModeList.length; new_index++) {
				if (outputMode.equalsIgnoreCase(mOutputModeList[new_index])) {
					Log.d("onReceive", "new mode is: "
							+ mOutputModeList[new_index]);

					String old_mode = SystemProperties
							.get(SettingsCommon.STR_OUTPUT_VAR);
					Log.d("onReceive", "old mode is: " + old_mode);

					for (int old_index = 0; old_index < mOutputModeList.length; old_index++) {
						if (old_mode
								.equalsIgnoreCase(mOutputModeList[old_index])) {
							if (new_index != old_index) {
								curPosition = getPosition(outputMode);
								String switch50Hz = SystemProperties
										.get(SettingsCommon.STR_DEFAULT_FREQUENCY_VAR);
								if (switch50Hz.equalsIgnoreCase("50Hz")) {
									Utils.setValue(OutputModeFile,
											mOutputModeList_50hz[new_index]);
								} else {
									Utils.setValue(OutputModeFile,
											mOutputModeList[new_index]);
								}
								Utils.setValue(PpscalerRectFile, curPosition[0]
										+ " " + curPosition[1] + " "
										+ (curPosition[2] + curPosition[0] - 1)
										+ " "
										+ (curPosition[3] + curPosition[1] - 1)
										+ " " + 0);
								Utils.setValue(FreescaleFb0File, "0");
								Utils.setValue(FreescaleFb1File, "0");
								Utils.setValue(FreescaleFb0File, "1");
								Utils.setValue(FreescaleFb1File, "1");
							}
						}
					}
				}
			}

			if (hasCvbsOutput
					&& (!(outputMode.equals("576i") || outputMode
							.equals("480i")))) {
				int cvbs_mode = intent.getIntExtra("cvbs_mode", 0);
				setCvbsMode(cvbs_mode, outputMode);
			}
			
			// save settings! add this, then no cancel action.
			SystemProperties.set(SettingsCommon.STR_OUTPUT_VAR, outputMode);

		}
		// cancel output mode change
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_OUTPUTMODE_CANCEL)) {
			int[] curPosition = { 0, 0, 1280, 720 };
			String old_mode = SystemProperties
					.get(SettingsCommon.STR_OUTPUT_VAR);
			Log.d("onReceive", "old mode is: " + old_mode);

			if (hasCvbsOutput
					&& (old_mode.equals("576i") || old_mode.equals("480i"))) {
				disableVpp2();
			}

			curPosition = getPosition(old_mode);
			Utils.setValue(OutputModeFile, old_mode);
			Utils.setValue(PpscalerRectFile, curPosition[0] + " "
					+ curPosition[1] + " "
					+ (curPosition[2] + curPosition[0] - 1) + " "
					+ (curPosition[3] + curPosition[1] - 1) + " " + 0);
			Utils.setValue(FreescaleFb0File, "0");
			Utils.setValue(FreescaleFb1File, "0");
			Utils.setValue(FreescaleFb0File, "1");
			Utils.setValue(FreescaleFb1File, "1");

			if (hasCvbsOutput
					&& (!(old_mode.equals("576i") || old_mode.equals("480i")))) {
				int cvbs_mode = intent.getIntExtra("cvbs_mode", 0);
				setCvbsMode(cvbs_mode, old_mode);
			}

		}
		// save output mode change
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_OUTPUTMODE_SAVE)) {
			String outputMode = intent.getStringExtra(OUTPUT_MODE);
			SystemProperties.set(SettingsCommon.STR_OUTPUT_VAR, outputMode);
		}
		// change output position
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_OUTPUTPOSITION_CHANGE)) {
			int x = intent.getIntExtra(OUTPUT_POSITION_X, -1);
			int y = intent.getIntExtra(OUTPUT_POSITION_Y, -1);
			int w = intent.getIntExtra(OUTPUT_POSITION_W, -1);
			int h = intent.getIntExtra(OUTPUT_POSITION_H, -1);
			int mode = intent.getIntExtra(OUTPUT_POSITION_MODE, -1);

			if ((x != -1) && (y != -1) && (w != -1) && (h != -1)
					&& (mode != -1)) {
				Utils.setValue(PpscalerRectFile, x + " " + y + " " + w + " "
						+ h + " " + mode);
			}
		}
		// cancel output position change
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_OUTPUTPOSITION_CANCEL)) {
			int[] curPosition = { 0, 0, 1280, 720 };
			String cur_mode = SystemProperties
					.get(SettingsCommon.STR_OUTPUT_VAR);
			curPosition = getPosition(cur_mode);
			Utils.setValue(PpscalerRectFile, curPosition[0] + " "
					+ curPosition[1] + " "
					+ (curPosition[2] + curPosition[0] - 1) + " "
					+ (curPosition[3] + curPosition[1] - 1) + " " + 0);
		}
		// save output position change
		else if (intent.getAction().equalsIgnoreCase(SettingsCommon.ACTION_OUTPUTPOSITION_SAVE)) {
			int x = intent.getIntExtra(OUTPUT_POSITION_X, -1);
			int y = intent.getIntExtra(OUTPUT_POSITION_Y, -1);
			int w = intent.getIntExtra(OUTPUT_POSITION_W, -1);
			int h = intent.getIntExtra(OUTPUT_POSITION_H, -1);
			Log.d("OutputSettingsBroadcastReceiver", "///////////x===" + x);
			Log.d("OutputSettingsBroadcastReceiver", "///////////y===" + y);
			Log.d("OutputSettingsBroadcastReceiver", "///////////w===" + w);
			Log.d("OutputSettingsBroadcastReceiver", "///////////h===" + h);
			if ((x != -1) && (y != -1) && (w != -1) && (h != -1)) {
				savePosition(String.valueOf(x), String.valueOf(y), String.valueOf(w), String.valueOf(h));
				Log.d("OutputSettingsBroadcastReceiver", "///////////savePosition in receiver!");
			}
		}
		// set and save output position to default values
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_OUTPUTPOSITION_DEFAULT_SAVE)) {
			int w = 1280, h = 720;
			String cur_mode = SystemProperties
					.get(SettingsCommon.STR_OUTPUT_VAR);
			if ((cur_mode.equals(mOutputModeList[0]))
					|| (cur_mode.equals(mOutputModeList[1]))) {
				w = OUTPUT480_FULL_WIDTH;
				h = OUTPUT480_FULL_HEIGHT;
			} else if ((cur_mode.equals(mOutputModeList[2]))
					|| (cur_mode.equals(mOutputModeList[3]))) {
				w = OUTPUT576_FULL_WIDTH;
				h = OUTPUT576_FULL_HEIGHT;
			} else if ((cur_mode.equals(mOutputModeList[5]))
					|| (cur_mode.equals(mOutputModeList[6]))) {
				w = OUTPUT1080_FULL_WIDTH;
				h = OUTPUT1080_FULL_HEIGHT;
			} else {
				w = OUTPUT720_FULL_WIDTH;
				h = OUTPUT720_FULL_HEIGHT;
			}
			Utils.setValue(PpscalerRectFile, "0 0 " + (w - 1) + " " + (h - 1)
					+ " " + 0);
			savePosition("0", "0", String.valueOf(w), String.valueOf(h));
		}
		// disp width height
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_DISP_CHANGE)) {
			int w = intent.getIntExtra(DISP_W, -1);
			int h = intent.getIntExtra(DISP_H, -1);

			if ((w != -1) && (h != -1)) {
				Utils.setValue(DispFile, w + " " + h);
			}
		}
		// real video on
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_REALVIDEO_ON)) {
			Utils.setValue(blankFb0File, "1"); //disable OSD.
			String cur_mode = SystemProperties
					.get(SettingsCommon.STR_OUTPUT_VAR);
			Utils.setValue(PpscalerFile, "0");
			Utils.setValue(FreescaleFb0File, "0");
			Utils.setValue(FreescaleFb1File, "0");
			if ((cur_mode.equals(mOutputModeList[0]))
					|| (cur_mode.equals(mOutputModeList[1]))) {
				Utils.setValue(request2XScaleFile, "16 720 480");
				Utils.setValue(scaleAxisOsd1File, "1280 720 720 480");
				Utils.setValue(scaleOsd1File, "0x10001");
			} else if ((cur_mode.equals(mOutputModeList[2]))
					|| (cur_mode.equals(mOutputModeList[3]))) {
				Utils.setValue(request2XScaleFile, "16 720 576");
				Utils.setValue(scaleAxisOsd1File, "1280 720 720 576");
				Utils.setValue(scaleOsd1File, "0x10001");
			} else if ((cur_mode.equals(mOutputModeList[5]))
					|| (cur_mode.equals(mOutputModeList[6]))) {
				Utils.setValue(request2XScaleFile, "8");
				Utils.setValue(scaleAxisOsd1File, "1280 720 1920 1080");
				Utils.setValue(scaleOsd1File, "0x10001");
			} else {
				// for setting blank to 0
				Utils.setValue(request2XScaleFile, "16 1280 720");
			}
			Utils.setValue(blankFb0File, "0"); //enable OSD again.
		}
		// real video off
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_REALVIDEO_OFF)) {
			int[] curPosition = { 0, 0, 1280, 720 };
			//disenable OSD display.
			Utils.setValue(blankFb0File, "1");
			String cur_mode = SystemProperties
					.get(SettingsCommon.STR_OUTPUT_VAR);
			curPosition = getPosition(cur_mode);
			Utils.setValue(VideoAxisFile, "0 0 1280 720");
			Utils.setValue(PpscalerFile, "1");
			Utils.setValue(PpscalerRectFile, curPosition[0] + " "
					+ curPosition[1] + " "
					+ (curPosition[2] + curPosition[0] - 1) + " "
					+ (curPosition[3] + curPosition[1] - 1) + " " + 0);
			Utils.setValue(FreescaleFb0File, "1");
			Utils.setValue(FreescaleFb1File, "1");
			Utils.setValue(request2XScaleFile, "2");
			Utils.setValue(scaleOsd1File, "0");
			Utils.setValue(PpscalerRectFile, curPosition[0] + " "
					+ curPosition[1] + " "
					+ (curPosition[2] + curPosition[0] - 1) + " "
					+ (curPosition[3] + curPosition[1] - 1) + " " + 0);
			//enable OSD display again.
			Utils.setValue(blankFb0File, "0");
		}
		// change video position when disable freescale
		else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_VIDEOPOSITION_CHANGE)) {
			int[] curPosition = { 0, 0, 0, 0 };
			String cur_mode = SystemProperties
					.get(SettingsCommon.STR_OUTPUT_VAR);
			curPosition = getPosition(cur_mode);
			Utils.setValue(VideoAxisFile, curPosition[0] + " " + curPosition[1]
					+ " " + (curPosition[2] + curPosition[0] - 1) + " "
					+ (curPosition[3] + curPosition[1] - 1));
		} else if (intent.getAction().equalsIgnoreCase(
				SettingsCommon.ACTION_CVBSMODE_CHANGE)) {
			String outputmode = SystemProperties
					.get(SettingsCommon.STR_OUTPUT_VAR);
			int cvbs_mode = intent.getIntExtra("cvbs_mode", 0);
			setCvbsMode(cvbs_mode, outputmode);
		}
		Log.d("OutputSettingsBroadcastReceiver", "Action:" + intent.getAction()
				+ "complete");
	}

	private int[] getPosition(String mode) {
		int[] curPosition = { 0, 0, 1280, 720 };
		int index = 4; // 720p
		for (int i = 0; i < mOutputModeList.length; i++) {
			if (mode.equalsIgnoreCase(mOutputModeList[i]))
				index = i;
		}
		switch (index) {
		case 0: // 480i
			curPosition[0] = SystemProperties.getInt(sel_480ioutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_480ioutput_y, 0);
			curPosition[2] = SystemProperties.getInt(sel_480ioutput_width, 720);
			curPosition[3] = SystemProperties
					.getInt(sel_480ioutput_height, 480);
			break;
		case 1: // 480p
			curPosition[0] = SystemProperties.getInt(sel_480poutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_480poutput_y, 0);
			curPosition[2] = SystemProperties.getInt(sel_480poutput_width, 720);
			curPosition[3] = SystemProperties
					.getInt(sel_480poutput_height, 480);
			break;
		case 2: // 576i
			curPosition[0] = SystemProperties.getInt(sel_576ioutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_576ioutput_y, 0);
			curPosition[2] = SystemProperties.getInt(sel_576ioutput_width, 720);
			curPosition[3] = SystemProperties
					.getInt(sel_576ioutput_height, 576);
			break;
		case 3: // 576p
			curPosition[0] = SystemProperties.getInt(sel_576poutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_576poutput_y, 0);
			curPosition[2] = SystemProperties.getInt(sel_576poutput_width, 720);
			curPosition[3] = SystemProperties
					.getInt(sel_576poutput_height, 576);
			break;
		case 4: // 720p
			curPosition[0] = SystemProperties.getInt(sel_720poutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_720poutput_y, 0);
			curPosition[2] = SystemProperties
					.getInt(sel_720poutput_width, 1280);
			curPosition[3] = SystemProperties
					.getInt(sel_720poutput_height, 720);
			break;
		case 5: // 1080i
			curPosition[0] = SystemProperties.getInt(sel_1080ioutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_1080ioutput_y, 0);
			curPosition[2] = SystemProperties.getInt(sel_1080ioutput_width,
					1920);
			curPosition[3] = SystemProperties.getInt(sel_1080ioutput_height,
					1080);
			break;
		case 6: // 1080p
			curPosition[0] = SystemProperties.getInt(sel_1080poutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_1080poutput_y, 0);
			curPosition[2] = SystemProperties.getInt(sel_1080poutput_width,
					1920);
			curPosition[3] = SystemProperties.getInt(sel_1080poutput_height,
					1080);
			break;
		default: // 720p
			curPosition[0] = SystemProperties.getInt(sel_720poutput_x, 0);
			curPosition[1] = SystemProperties.getInt(sel_720poutput_y, 0);
			curPosition[2] = SystemProperties
					.getInt(sel_720poutput_width, 1280);
			curPosition[3] = SystemProperties
					.getInt(sel_720poutput_height, 720);
			break;
		}
		return curPosition;
	}

	private void savePosition(String x, String y, String w, String h) {
		String cur_mode = SystemProperties.get(SettingsCommon.STR_OUTPUT_VAR);
		int index = 4; // 720p
		for (int i = 0; i < mOutputModeList.length; i++) {
			if (cur_mode.equalsIgnoreCase(mOutputModeList[i]))
				index = i;
		}
		
		Log.d("OutputSettingsBroadcastReceiver", "------index-----" + index);
		
		switch (index) {
		case 0: // 480i
			SystemProperties.set(sel_480ioutput_x, x);
			SystemProperties.set(sel_480ioutput_y, y);
			SystemProperties.set(sel_480ioutput_width, w);
			SystemProperties.set(sel_480ioutput_height, h);
			
			
			sw.setProperty(sel_480poutput_x, x);
			sw.setProperty(sel_480poutput_y, y);
			sw.setProperty(sel_480poutput_width, w);
			sw.setProperty(sel_480poutput_height, h);
			break;
		case 1: // 480p
			SystemProperties.set(sel_480poutput_x, x);
			SystemProperties.set(sel_480poutput_y, y);
			SystemProperties.set(sel_480poutput_width, w);
			SystemProperties.set(sel_480poutput_height, h);
			break;
		case 2: // 576i
			SystemProperties.set(sel_576ioutput_x, x);
			SystemProperties.set(sel_576ioutput_y, y);
			SystemProperties.set(sel_576ioutput_width, w);
			SystemProperties.set(sel_576ioutput_height, h);
			break;
		case 3: // 576p
			SystemProperties.set(sel_576poutput_x, x);
			SystemProperties.set(sel_576poutput_y, y);
			SystemProperties.set(sel_576poutput_width, w);
			SystemProperties.set(sel_576poutput_height, h);
			break;
		case 4: // 720p
			SystemProperties.set(sel_720poutput_x, x);
			SystemProperties.set(sel_720poutput_y, y);
			SystemProperties.set(sel_720poutput_width, w);
			SystemProperties.set(sel_720poutput_height, h);
			Log.d("OutputSettingsBroadcastReceiver", "----------------------indext is 4!");
			
			// TODO:test
			SystemProperties.set("sel_poutput", "50");
			LogUtil.d("OutputSettingsBroadcastReceiver", SystemProperties.get("sel_poutput"));
			
			sw.setProperty(sel_720poutput_x, x);
			sw.setProperty(sel_720poutput_y, y);
			sw.setProperty(sel_720poutput_width, w);
			sw.setProperty(sel_720poutput_height, h);
			break;
		case 5: // 1080i
			SystemProperties.set(sel_1080ioutput_x, x);
			SystemProperties.set(sel_1080ioutput_y, y);
			SystemProperties.set(sel_1080ioutput_width, w);
			SystemProperties.set(sel_1080ioutput_height, h);
			break;
		case 6: // 1080p
			SystemProperties.set(sel_1080poutput_x, x);
			SystemProperties.set(sel_1080poutput_y, y);
			SystemProperties.set(sel_1080poutput_width, w);
			SystemProperties.set(sel_1080poutput_height, h);
			break;
		}
		
		Log.d("OutputSettingsBroadcastReceiver", "------SystemProperties-----" + SystemProperties.get(sel_720poutput_x));
		Log.d("OutputSettingsBroadcastReceiver", "------SystemProperties-----" + SystemProperties.get(sel_720poutput_y));
		Log.d("OutputSettingsBroadcastReceiver", "------SystemProperties-----" + SystemProperties.get(sel_720poutput_width));
		Log.d("OutputSettingsBroadcastReceiver", "------SystemProperties-----" + SystemProperties.get(sel_720poutput_height));
		Log.d("OutputSettingsBroadcastReceiver", "------x-----" + x);
		Log.d("OutputSettingsBroadcastReceiver", "------y-----" + y);
		Log.d("OutputSettingsBroadcastReceiver", "------w-----" + w);
		Log.d("OutputSettingsBroadcastReceiver", "------h-----" + h);

	}

	public void disableVpp2() {
		Utils.setValue("/sys/class/display2/mode", "null");
		SystemProperties.set("ubootenv.var.cvbsmode", "null");
	}

	public void setCvbsMode(int mode, String valOutputMode) {

		if (!(valOutputMode.equals("576i") || valOutputMode.equals("480i"))) {
			if (mode == 0) {
				Utils.setValue("/sys/class/display2/mode", "null");
				Utils.setValue("/sys/class/display2/mode", "480cvbs");
				Utils.setValue("/sys/class/video2/screen_mode", "1");
				SystemProperties.set("ubootenv.var.cvbsmode", "480cvbs");

			} else {
				Utils.setValue("/sys/class/display2/mode", "null");
				Utils.setValue("/sys/class/display2/mode", "576cvbs");
				Utils.setValue("/sys/class/video2/screen_mode", "1");
				SystemProperties.set("ubootenv.var.cvbsmode", "576cvbs");

			}

			if (valOutputMode.equals("1080p")) {
				Utils.setValue("/sys/module/amvideo2/parameters/clone_frame_scale_width", "960");

			} else {
				Utils.setValue("/sys/module/amvideo2/parameters/clone_frame_scale_width", "0");
			}
		} else {
			disableVpp2();
		}

	}
}
