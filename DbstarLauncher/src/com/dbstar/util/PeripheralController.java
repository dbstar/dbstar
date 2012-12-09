/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dbstar.util;

import android.util.Log;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class PeripheralController {
	private static String TAG = "PeripheralController";
	private static String SYSFILE_GPIO_CMD = "/sys/class/gpio/cmd";
	private static String SYSFILE_HDMI_STATE = "/sys/class/switch/hdmi/state";
	private static String SYSFILE_SMARTCARD_STATE = "/sys/class/switch/smartcard/state";
	private static String CMD_SET_POWER_LED_ON = "w:o:10:1";
	private static String CMD_SET_POWER_LED_OFF = "w:o:10:0";
	private static String CMD_SET_NETWORK_LED_ON = "w:d:1:1";
	private static String CMD_SET_NETWORK_LED_OFF = "w:d:1:0";
	private static String CMD_SET_AUDIO_OUTPUT_ON = "w:c:4:1";
	private static String CMD_SET_AUDIO_OUTPUT_OFF = "w:c:4:0";

	public void setPowerLedOn() {
		Log.d(TAG, "setPowerLedOn");
		setGpio(CMD_SET_POWER_LED_OFF);
		return;
	}

	public void setPowerLedOff() {
		Log.d(TAG, "setPowerLedOff");
		setGpio(CMD_SET_POWER_LED_OFF);
		return;
	}

	public void setNetworkLedOn() {
		Log.d(TAG, "setNetworkLedOn");
		setGpio(CMD_SET_NETWORK_LED_ON);
		return;
	}

	public void setNetworkLedOff() {
		Log.d(TAG, "setNetworkLedOff");
		setGpio(CMD_SET_NETWORK_LED_OFF);
		return;
	}

	public void setAudioOutputOn() {
		Log.d(TAG, "setAudioOutputOn");
		setGpio(CMD_SET_AUDIO_OUTPUT_ON);
		return;
	}

	public void setAudioOutputOff() {
		Log.d(TAG, "setAudioOutputOff");
		setGpio(CMD_SET_AUDIO_OUTPUT_OFF);
		return;
	}

	public boolean isHdmiIn() {
		String state = readSysFile(SYSFILE_HDMI_STATE);
		if (state != null && "1".equals(state)) {
			return true;
		}
		return false;
	}

	public boolean isSmartCardIn() {
		String state = readSysFile(SYSFILE_SMARTCARD_STATE);
		if (state != null && "1".equals(state)) {
			return true;
		}
		return false;
	}

	private void setGpio(String cmd) {
		writeSysFile(SYSFILE_GPIO_CMD, cmd);
	}

	private String readSysFile(String file) {
		String buf = null;
		if (file == null) {
			Log.d(TAG, "writeSysFile ERROR!, file=null");
			return null;
		} else try {
			BufferedReader br = new BufferedReader(new FileReader(file), 64);
			try {
				buf = br.readLine();
			} finally {
				br.close();
			}
			return buf; 
		} catch (IOException e) {
			Log.e(TAG, "readSysFile error"); 
			return null; 
		}
	}

	private void writeSysFile(String file, String buf) {
		if (file == null || buf == null) {
			Log.d(TAG, "writeSysFile ERROR!, file=" + file + "buf=" + buf);
			return;
		} else try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file), 64);
			try {
				bw.write(buf);
			} finally {
				bw.close();
			}
			return; 
		} catch (IOException e) {
			Log.e(TAG, "setGpio error"); 
			return; 
		}
	}
}
