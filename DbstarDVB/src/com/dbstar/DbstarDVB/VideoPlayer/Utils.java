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

public class Utils {
	private static final String TAG = "Utils";

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
