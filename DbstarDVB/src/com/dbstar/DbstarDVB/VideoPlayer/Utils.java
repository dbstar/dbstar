package com.dbstar.DbstarDVB.VideoPlayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.net.Uri;
import android.util.Log;
import android.os.SystemProperties;

public class Utils {
	private static final String TAG = "Utils";

	private static final String PpscalerFile = "/sys/class/ppmgr/ppscaler";
	private static final String PpscalerRectFile = "/sys/class/ppmgr/ppscaler_rect";
	private static final String FreescaleFb0File = "/sys/class/graphics/fb0/free_scale";
	private static final String FreescaleFb1File = "/sys/class/graphics/fb1/free_scale";
	private static final String request2XScaleFile = "/sys/class/graphics/fb0/request2XScale";
	private static final String scaleAxisOsd1File = "/sys/class/graphics/fb1/scale_axis";
	private static final String scaleOsd1File = "/sys/class/graphics/fb1/scale";
//	private static final String blankFb0File = "/sys/class/graphics/fb0/blank";
	private static final String VideoAxisFile = "/sys/class/video/axis";

	private static final String STR_OUTPUT_MODE = "ubootenv.var.outputmode";

	
	private static final String sel_480ioutput_x = "ubootenv.var.480ioutputx";
	private static final String sel_480ioutput_y = "ubootenv.var.480ioutputy";
	private static final String sel_480ioutput_width = "ubootenv.var.480ioutputwidth";
	private static final String sel_480ioutput_height = "ubootenv.var.480ioutputheight";
	private static final String sel_480poutput_x = "ubootenv.var.480poutputx";
	private static final String sel_480poutput_y = "ubootenv.var.480poutputy";
	private static final String sel_480poutput_width = "ubootenv.var.480poutputwidth";
	private static final String sel_480poutput_height = "ubootenv.var.480poutputheight";
	private static final String sel_576ioutput_x = "ubootenv.var.576ioutputx";
	private static final String sel_576ioutput_y = "ubootenv.var.576ioutputy";
	private static final String sel_576ioutput_width = "ubootenv.var.576ioutputwidth";
	private static final String sel_576ioutput_height = "ubootenv.var.576ioutputheight";
	private static final String sel_576poutput_x = "ubootenv.var.576poutputx";
	private static final String sel_576poutput_y = "ubootenv.var.576poutputy";
	private static final String sel_576poutput_width = "ubootenv.var.576poutputwidth";
	private static final String sel_576poutput_height = "ubootenv.var.576poutputheight";
	private static final String sel_720poutput_x = "ubootenv.var.720poutputx";
	private static final String sel_720poutput_y = "ubootenv.var.720poutputy";
	private static final String sel_720poutput_width = "ubootenv.var.720poutputwidth";
	private static final String sel_720poutput_height = "ubootenv.var.720poutputheight";
	private static final String sel_1080ioutput_x = "ubootenv.var.1080ioutputx";
	private static final String sel_1080ioutput_y = "ubootenv.var.1080ioutputy";
	private static final String sel_1080ioutput_width = "ubootenv.var.1080ioutputwidth";
	private static final String sel_1080ioutput_height = "ubootenv.var.1080ioutputheight";
	private static final String sel_1080poutput_x = "ubootenv.var.1080poutputx";
	private static final String sel_1080poutput_y = "ubootenv.var.1080poutputy";
	private static final String sel_1080poutput_width = "ubootenv.var.1080poutputwidth";
	private static final String sel_1080poutput_height = "ubootenv.var.1080poutputheight";

	private static final String[] mOutputModeList = { "480i", "480p", "576i", "576p",
			"720p", "1080i", "1080p" };

	public static void setVideoOn() {
//		writeSysfs(blankFb0File, "1");
		// surfaceflinger will set back to 0

		String cur_mode = SystemProperties.get(STR_OUTPUT_MODE);
		writeSysfs(PpscalerFile, "0");
		writeSysfs(FreescaleFb0File, "0");
		writeSysfs(FreescaleFb1File, "0");
		if ((cur_mode.equals(mOutputModeList[0]))
				|| (cur_mode.equals(mOutputModeList[1]))) {
			writeSysfs(request2XScaleFile, "16 720 480");
			writeSysfs(scaleAxisOsd1File, "1280 720 720 480");
			writeSysfs(scaleOsd1File, "0x10001");
		} else if ((cur_mode.equals(mOutputModeList[2]))
				|| (cur_mode.equals(mOutputModeList[3]))) {
			writeSysfs(request2XScaleFile, "16 720 576");
			writeSysfs(scaleAxisOsd1File, "1280 720 720 576");
			writeSysfs(scaleOsd1File, "0x10001");
		} else if ((cur_mode.equals(mOutputModeList[5]))
				|| (cur_mode.equals(mOutputModeList[6]))) {
			writeSysfs(request2XScaleFile, "8");
			writeSysfs(scaleAxisOsd1File, "1280 720 1920 1080");
			writeSysfs(scaleOsd1File, "0x10001");
		} else {
			// for setting blank to 0
			writeSysfs(request2XScaleFile, "16 1280 720");
		}

	}

	public static void setVideoOff() {
//		writeSysfs(blankFb0File, "1");
		// surfaceflinger will set back to 0

		int[] curPosition = { 0, 0, 1280, 720 };
		String cur_mode = SystemProperties.get(STR_OUTPUT_MODE);
		curPosition = getPosition(cur_mode);

		writeSysfs(VideoAxisFile, "0 0 1280 720");

		writeSysfs(PpscalerFile, "1");
		writeSysfs(PpscalerRectFile, curPosition[0] + " "
				+ curPosition[1] + " " + (curPosition[2] + curPosition[0] - 1)
				+ " " + (curPosition[3] + curPosition[1] - 1) + " " + 0);
		writeSysfs(FreescaleFb0File, "1");
		writeSysfs(FreescaleFb1File, "1");
		writeSysfs(request2XScaleFile, "2");
		writeSysfs(scaleOsd1File, "0");
		writeSysfs(PpscalerRectFile, curPosition[0] + " "
				+ curPosition[1] + " " + (curPosition[2] + curPosition[0] - 1)
				+ " " + (curPosition[3] + curPosition[1] - 1) + " " + 0);

	}

	public static void setVideoPositionChange() {
		int[] curPosition = { 0, 0, 0, 0 };
		String cur_mode = SystemProperties.get(STR_OUTPUT_MODE);
		curPosition = getPosition(cur_mode);
		writeSysfs(VideoAxisFile, curPosition[0] + " " + curPosition[1]
				+ " " + (curPosition[2] + curPosition[0] - 1) + " "
				+ (curPosition[3] + curPosition[1] - 1));
	}

	private static int[] getPosition(String mode) {
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
	
	public static String getFilePath(Uri uri) {
		String path = uri.getPath();
		int idx = -1;
		
		idx = path.lastIndexOf("|");
		if (idx > 0) {
			path = path.substring(0, idx);
		}
		
		return path;
	}
	
	public static int writeSysfs(String path, String val) {
		if (!new File(path).exists()) {
			Log.e(TAG, "File not found: " + path);
			return 1;
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path), 64);
			try {
				writer.write(val);
			} finally {
				writer.close();
			}
			return 0;

		} catch (IOException e) {
			Log.e(TAG, "IO Exception when write: " + path, e);
			return 1;
		}
	}
	
	public static String readSysfs(String path) {
		String buf = null;

		File file = new File(path);
		if (!file.exists()) {
			Log.d(TAG, "file " + path + "not exist");
			return buf;
		}

		// read
		try {
			BufferedReader in = new BufferedReader(new FileReader(path),
					32);
			try {
				buf = in.readLine();
				Log.d(TAG, "file content:" + buf);
			} finally {
				in.close();
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException when read " + path);
		}
		
		return buf;
	}

	public static String do_exec(String[] cmd) {
		String s = "\n";
		try {
			java.lang.Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				s += line + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cmd.toString();
	}

	public static String secToTime(int i, Boolean isTotalTime) {
		String retStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (i <= 0) {
			return "00:00:00";
		} else {
			minute = i / 60;
			if (minute < 60) {
				second = i % 60;
				retStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
			} else {
				hour = minute / 60;
				if (hour > 99)
					return "99:59:59";
				minute = minute % 60;
				second = i % 60;
				retStr = unitFormat(hour) + ":" + unitFormat(minute) + ":"
						+ unitFormat(second);
			}
		}
		return retStr;
	}

	private static String unitFormat(int i) {
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = Integer.toString(i);
		return retStr;
	}
}
