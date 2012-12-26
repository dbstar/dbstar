package com.dbstar.settings;

import java.io.FileNotFoundException;
import java.io.FileReader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DbstarNetSettingsReceiver extends BroadcastReceiver {

	private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

	public static final String filePath = "/data/data/com.dbstar.settings/files/flag";

	protected class MyException extends Exception {
		protected MyException(String msg) {
			super(msg);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			try {
				int count, i = 0;
				char[] buf = new char[100];
				FileReader rd = new FileReader(filePath);
				count = rd.read(buf, 0, 1);
				buf[count] = '\n';
				String vlues = new String(buf, 0, count);

				if ((vlues.compareTo("1")) == 0)
					throw new MyException("Default OutPutMode Detected, exit");
			} catch (FileNotFoundException e1) {
				Intent starterIntent = new Intent(context,
						GDNetworkSettingsActivity.class);
				starterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(starterIntent);
			} catch (Exception e2) {
				Log.e("OOBE Start Up Receiver: EXCEPTION ", e2.toString());
			}

		}
	}
}
