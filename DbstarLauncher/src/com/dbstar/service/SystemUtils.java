package com.dbstar.service;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import com.dbstar.util.LogUtil;

import android.os.SystemProperties;

public class SystemUtils {

	private static final String TAG =  "SystemUtils";

	private static final String SystemStatusProperty = "persist.sys.status";
	private static final String SmartHomeProperty = "service.smarthome.started";
	private static final String MuteFile = "/data/dbstar/mute";
	private static final String VolumeFile = "/data/dbstar/volume";
	private static final String STR_1080SCALE = "ro.platform.has.1080scale";
	private static final String VideoAxisFile = "/sys/class/video/axis";
	private static final String DispFile = "/sys/class/ppmgr/disp";

	public static void setSystemStatus(String status) {
		if (status == null)
			return;
		LogUtil.d(TAG, "+++++++++++ setSystemStatus("+ status + ")");
		SystemProperties.set(SystemStatusProperty, status);
		String state = SystemProperties.get(SystemStatusProperty, "");
		LogUtil.d(TAG, "+++++++++++ getSystemStatus()=" + state);

		return;
	}

	public static String getSystemStatus() {
		String state = SystemProperties.get(SystemStatusProperty, "");
		LogUtil.d(TAG, "+++++++++++ getSystemStatus()=" + state);
		return state;
	}

	public static void startSmartHomeServer() {
		SystemProperties.set(SmartHomeProperty, "1");
	}

	public static void stopSmartHomeServer() {
		SystemProperties.set(SmartHomeProperty, "0");
	}

	public static void clearAudioInfo() {
		deleteFile(MuteFile);
	}
	
	public static void saveMute(int mute) {
		saveValueToFile(MuteFile, mute);
	}
	
	public static void saveVolume(int volume) {
		saveValueToFile(VolumeFile, volume);
	}

	public static void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void saveValueToFile(String path, int value) {
		File file = new File(path);
		
		LogUtil.d(TAG, " save value " + value + " to file: " + path);

		try {
			if (!file.exists()) {
				if (!file.createNewFile())
					return;
			}

			OutputStream fo = new FileOutputStream(file);
			DataOutputStream dos = new DataOutputStream(fo);
			dos.writeInt(value);
			dos.close();
			
			LogUtil.d(TAG, " write success !");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int writeSysfs(String path, String val) {
		if (!new File(path).exists()) {
			LogUtil.e(TAG, "File not found: " + path);
			return 1;
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path), 64);
			try {
				writer.write(val);
				LogUtil.d(TAG, " write osd black ok!");
			} finally {
				writer.close();
			}
			return 0;

		} catch (IOException e) {
			LogUtil.e(TAG, "IO Exception when write: " + path, e);
			return 1;
		}
	}
	
	public static void setVideoSettings() {
		if(SystemProperties.getInt(STR_1080SCALE, 0) == 2){
            LogUtil.d(TAG, "BOOT_COMPLETED - set video axis");
            writeSysfs(VideoAxisFile, "0 0 1280 720");
            writeSysfs(DispFile, "1280 720");
        }
	}
}
