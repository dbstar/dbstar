package com.guodian.checkdevicetool.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

public class CMDExecute {
	public synchronized String run(String[] cmd, String workDir) {
		String result = "";
		try {
			ProcessBuilder builder = new ProcessBuilder(cmd);
			InputStream in = null;
			// 设置一个路径
			if (workDir != null) {
				builder.directory(new File(workDir));
				builder.redirectErrorStream(true);

				Process process = builder.start();
				in = process.getInputStream();
				byte[] re = new byte[1024 * 2];

				while (in.read(re) != -1) {
					result = result + new String(re);
				}

				if (in != null) {
					in.close();
				}
			}
		} catch (IOException e) {
			Log.d("CMDExecute", "in CheckDeviceTool, CMDExecute error and exception = " + e);
			e.printStackTrace();
		}
		return result;
	}
}
