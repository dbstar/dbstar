package com.dbstar.settings;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.dbstar.settings.network.NetworkCommon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class DbstarNetSettingsReceiver extends BroadcastReceiver {

	private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
	private static final String ActionClearSettings = "com.dbstar.Settings.Action.CLEAR_SETTINGS";
	
	public static final String flagFile = "flag";

	protected class MyException extends Exception {
		protected MyException(String msg) {
			super(msg);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			try {
				int count = 0;
				byte[] buf = new byte[100];
				FileInputStream in = context.openFileInput(flagFile);
				BufferedInputStream bIn = new BufferedInputStream(in);
				count = bIn.read(buf, 0, buf.length);
				bIn.close();

				boolean exist = false;
				if (count > 0) {
					String vlues = new String(buf, 0, count);

					if ((vlues.compareTo("1")) == 0) {
						exist = true;
					}
				}

				if (exist) {
					throw new MyException(
							"Default Network Settings Detected, exit");
				}
			} catch (FileNotFoundException e1) {
				Intent starterIntent = new Intent(context,
						GDNetworkSettingsActivity.class);
				starterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(starterIntent);
			} catch (Exception e2) {
				Log.e("OOBE Start Up Receiver: EXCEPTION ", e2.toString());
			}

		} else if (ActionClearSettings.equals(intent.getAction())) {
			context.deleteFile(flagFile);
			
			SharedPreferences settings = context.getSharedPreferences(
					NetworkCommon.PREF_NAME_NETWORK, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.clear();
			editor.commit();
		}
		
	}
}
