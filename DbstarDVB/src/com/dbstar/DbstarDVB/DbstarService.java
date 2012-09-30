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

	private static String mDownloadName = "";

	public void onCreate() {
		Log.d(TAG, "----- onCreate ----");
	}

	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "startId " + startId + ": " + intent);
		super.onStart(intent, startId);
		if (null == intent) {
			return;
		}
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
			return taskinfoStart();
		}

		public int stopTaskInfo() throws RemoteException {
			Log.d(TAG, "stopTaskInfoGet()");
			return taskinfoStop();
		}

        public Intent getTaskInfo() throws RemoteException { 
			Log.d(TAG, "getTaskInfo()");

			byte[] bytes = taskinfoGet();
			Intent it = new Intent();
			it.putExtra("taskinfo", bytes);

			return it;
		}
	};

	static {
		System.loadLibrary("dvbpushjni");
	};
}
