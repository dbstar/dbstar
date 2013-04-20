package com.dbstar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.util.Log;

public class FileOperation {
	private static final String TAG = "FileOperation";

	private static String readString(File file) {
		String value = "";
		try {
			int BUFFER_SIZE = 8192;
			String UTF8 = "utf8";
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), UTF8), BUFFER_SIZE);
			value = br.readLine();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	private static void writeString(File file, String value) {
		try {
            byte[] bytes = value.getBytes();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static String read(String filename) {
		String value = "";
        if (filename == null) {
            Log.e(TAG, "filename null!");
            return value;
        }

        File file = new File(filename);
        if (file.exists()) {
            value = readString(file);
        }
        Log.d(TAG, "readFile(" + filename + ")=" + value);

        return value;
    }

    public static void write(String filename, String value) {
        if (filename == null) {
            Log.e(TAG, "filename null!");
        }

        Log.d(TAG, "writeFile(" + filename + ", " + value + ")");
        File file = new File(filename);
        if (file.exists()) {
            writeString(file, value);
        }
    }
}
