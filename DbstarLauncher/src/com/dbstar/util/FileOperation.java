package com.dbstar.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
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

	public static void saveValueToFile(Context context, String path, String value) {
		Log.d(TAG, "save " + value + " to " + path);
		try {
			byte[] setflag = value.getBytes();
			FileOutputStream fos = context.openFileOutput(path,
			Context.MODE_PRIVATE);
			fos.write(setflag);
			fos.close();
		} catch (Exception e) {
			Log.e(TAG,"write file error:" + "path");
			e.printStackTrace();
		}
	}

	public static String readValueFromFile(Context context, String path) {
		String value = null;

		try {
            int count = 0;
            byte[] buf = new byte[100];
            FileInputStream in = context.openFileInput(path);
            BufferedInputStream bIn = new BufferedInputStream(in);
            count = bIn.read(buf, 0, buf.length);
			if (count>0) {
				value = new String(buf, 0, count);
			}
            bIn.close();
			Log.d(TAG, " === read value == " + value + " " + path);
        } catch (FileNotFoundException e1) {
            ;
        } catch (IOException e) {
            e.printStackTrace();
            context.deleteFile(path);
        }
		
		return value;
	}

}
