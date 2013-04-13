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
import android.os.UEventObserver;
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
	private static String mDownloadName = "";
	private static Context mContext = null;
	//private DbstarPM mDPM = new DbstarPM();

	private UEventObserver mHDMIObserver = new UEventObserver() {
		public void onUEvent(UEventObserver.UEvent event) {
			Log.d(TAG, "HDMI event: " + event.get("SWITCH_STATE"));
			String msg = "";
			if ("1".equals(event.get("SWITCH_STATE"))) {
				msg = DbstarServiceApi.ACTION_HDMI_IN;
			} else {
				msg = DbstarServiceApi.ACTION_HDMI_OUT;
			}
			sendObserverMessage(msg);
		}
	};

	private UEventObserver mSmartCardObserver = new UEventObserver() {
		public void onUEvent(UEventObserver.UEvent event) {
			Log.d(TAG, "SmartCard event:" + event.get("SWITCH_STATE"));
			String msg = "";
			if ("1".equals(event.get("SWITCH_STATE"))) {
				msg = DbstarServiceApi.ACTION_SMARTCARD_IN;
			} else {
				msg = DbstarServiceApi.ACTION_SMARTCARD_OUT;
			}
			sendObserverMessage(msg);
		}
	};

	private void sendObserverMessage(String msg) {
		Intent it = new Intent();
		it.setAction(msg);
		if (DbstarService.mContext != null) {
			Log.d(TAG, "sendMessage: " + msg);
			DbstarService.mContext.sendBroadcast(it);
		}
	}

	private void startObserving() {
		Log.d(TAG, "startObserving()");
		mHDMIObserver.startObserving("DEVPATH=/devices/virtual/switch/hdmi");
		mSmartCardObserver.startObserving("DEVPATH=/devices/virtual/switch/smartcard");
	}

	private void stopObserving() {
		Log.d(TAG, "stopObserving()");
		mHDMIObserver.stopObserving();
		mSmartCardObserver.stopObserving();
	}

	public void onCreate() {
		Log.d(TAG, "----- onCreate ----");
		//mDPM.acquirePartialWakeLock(this);
		startObserving();
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
		//mDPM.releaseWakeLock();
		stopObserving();
	}

	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return mBinder;
	}

	public boolean onUnbind(Intent intent) {
		Log.d(TAG, "onUnbind");
		return true;
	}

	private native int init();
	private native int uninit();
	private native byte[] command(int cmd, String buf, int len);

	private final IDbstarService.Stub mBinder = new IDbstarService.Stub() {
		public int initDvbpush() throws RemoteException {
			Log.d(TAG, "initDvbpush()");
			return init();
		}

		public int uninitDvbpush() throws RemoteException {
			Log.d(TAG, "stopDvbpush()");
			return uninit();
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
			if (bytes != null) {
				String buf = new String(bytes, "utf-8");
				Log.i(TAG, "postNotifyMessage(" + type + ", [" + buf + "].");
			} else {
				Log.i(TAG, "postNotifyMessage(" + type + " message==null");
			}
			Intent it = new Intent();
			it.setAction(DbstarServiceApi.ACTION_NOTIFY);
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
