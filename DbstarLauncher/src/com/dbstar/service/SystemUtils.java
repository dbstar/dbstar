package com.dbstar.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.os.SystemProperties;
import android.util.Log;

public class SystemUtils {

	private static final String SmartHomePrepertyName = "service.smarthome.started";

	public static void startSmartHomeServer() {
		SystemProperties.set(SmartHomePrepertyName, "1");
	}

	public static void stopSmartHomeServer() {
		SystemProperties.set(SmartHomePrepertyName, "0");
	}

	private static final String MuteFile = "/data/dbstar/mute";
	private static final String VolumeFile = "/data/dbstar/volume";
	private static final String TAG =  "SystemUtils";

	
	public static void clearAudioInfo() {
		deleteFile(MuteFile);
//		deleteFile(VolumeFile);
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
		
		Log.d(TAG, " save value " + value + " to file: " + path);

		try {
			if (!file.exists()) {
				if (!file.createNewFile())
					return;
			}

			OutputStream fo = new FileOutputStream(file);
			DataOutputStream dos = new DataOutputStream(fo);
			dos.writeInt(value);
			dos.close();
			
			Log.d(TAG, " write success !");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
