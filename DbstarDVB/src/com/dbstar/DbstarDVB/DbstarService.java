package com.dbstar.DbstarDVB;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.ArrayList;

public class DbstarService extends Service {
	private static final String TAG = "DbstarService";
	private static final String NOTIFY_ACTION = "com.dbstar.DbstarDVB.NOTIFY";
	private static String mDownloadName = "";
	private static Context mContext = null;

	public void onCreate() {
		Log.d(TAG, "----- onCreate ----");
	}

	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "startId " + startId + ": " + intent);
		super.onStart(intent, startId);
		if (null == intent) {
			return;
		}
		DbstarService.mContext = this.getApplicationContext();
		Bundle extras = intent.getExtras();
		if (null != extras) {
			DbstarService.mDownloadName = extras.getString("extrasInfo");
		}
	}

	public void onDestroy() {
		Log.d(TAG, "----- onDestroy ----");
	}

	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return true;
	}

	private native int dvbpushStart();

	private native int dvbpushStop();

	private native int taskinfoStart();

	private native int taskinfoStop();

	private native byte[] taskinfoGet();

	private native byte[] command(int cmd, String buf, int len);

	private final IDbstarService.Stub mBinder = new IDbstarService.Stub() {
		public int startDvbpush() throws RemoteException {
			Log.d(TAG, "startDvbpush()");
			return dvbpushStart();
		}

		public int stopDvbpush() throws RemoteException {
			Log.d(TAG, "stopDvbpush()");
			return dvbpushStop();
		}

		public int startTaskInfo() throws RemoteException {
			Log.d(TAG, "startTaskInfoGet()");
			byte[] bytes = command(1, null, 0);
			int ret = 0;
			if (bytes != null) {
				ret = Integer.valueOf(new String(bytes));
			}
			return ret;
		}

		public int stopTaskInfo() throws RemoteException {
			Log.d(TAG, "stopTaskInfoGet()");
			byte[] bytes = command(2, null, 0);
			int ret = 0;
			if (bytes != null) {
				ret = Integer.valueOf(new String(bytes));
			}
			return ret;
		}

		public Intent getTaskInfo() throws RemoteException {
			Log.d(TAG, "getTaskInfo()");

			byte[] bytes = command(3, null, 0);
			Intent it = new Intent();
			it.putExtra("result", bytes);

			return it;
		}

		public Intent sendCommand(int cmd, String buf, int len)
				throws RemoteException {
			Log.d(TAG, "sendCommand()");
			byte[] bytes = command(cmd, buf, len);
			Intent it = new Intent();
			it.putExtra("result", bytes);

			return it;
		}
	};

	public static void postNotifyMessage(int type, byte[] bytes) {
		try {
			String buf = new String(bytes, "utf-8");
			Log.i(TAG, "postNotifyMessage(" + type + ", [" + buf + "].");
			Intent it = new Intent();
			it.setAction(NOTIFY_ACTION);
			it.putExtra("type", type);
			it.putExtra("message", bytes);
			if (DbstarService.mContext != null) {
				DbstarService.mContext.sendBroadcast(it);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	static {
		System.loadLibrary("dvbpushjni");
	};
}
