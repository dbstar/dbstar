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
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class GpioController {
	private static String TAG = "GPIOController";
	private static String GPIO_CMD_FILE = "/sys/class/gpio/cmd";
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

	private void setGpio(String cmd) {
		try { 
			BufferedWriter bw = new BufferedWriter(new FileWriter(GPIO_CMD_FILE), 32); 
			try {
				bw.write(cmd); 
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
