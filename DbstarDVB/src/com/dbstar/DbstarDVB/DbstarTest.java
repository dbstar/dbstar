package com.dbstar.DbstarDVB;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class DbstarTest extends Activity implements OnClickListener {
	private static final String TAG = "DbsterTest";
	private static final String DOWNLOAD_FINISH_ACTION = "com.dbstar.DbstarDVB.DOWNLOAD_FINISHED";

	private int mUpdateType = 0;
	private Toast mToast = null;
	private View Button01, Button02, Button03, Button04, Button05, Button06;
	private TextView taskID = null;
	private TextView taskName = null;
	private TextView downloadSize = null;
	private TextView totalSize = null;
	private TextView taskInfo = null;
	private ProgressBar progressBar = null;

	private Intent mIntent = new Intent();
	private IDbstarService mDbstarService = null;
	private ComponentName mComponentName = new ComponentName("com.dbstar.DbstarDVB", "com.dbstar.DbstarDVB.DbstarService");
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mDbstarService = IDbstarService.Stub.asInterface(service);
		}
		public void onServiceDisconnected(ComponentName className) {
			mDbstarService = null;
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		Button01 = this.findViewById(R.id.Button01);
		Button01.setOnClickListener(this);
		Button02 = this.findViewById(R.id.Button02);
		Button02.setOnClickListener(this);
		Button03 = this.findViewById(R.id.Button03);
		Button03.setOnClickListener(this);
		Button04 = this.findViewById(R.id.Button04);
		Button04.setOnClickListener(this);
		Button04 = this.findViewById(R.id.Button04);
		Button04.setOnClickListener(this);
		Button05 = this.findViewById(R.id.Button05);
		Button05.setOnClickListener(this);
		Button06 = this.findViewById(R.id.Button06);
		Button06.setOnClickListener(this);

		taskID = (TextView) findViewById(R.id.taskID);
		taskName = (TextView) findViewById(R.id.taskName);
		downloadSize = (TextView) findViewById(R.id.downloadSize);
		totalSize = (TextView) findViewById(R.id.totalSize);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		taskInfo = (TextView) findViewById(R.id.taskInfo);

		/* start service */
		Log.d(TAG, "startService");
		mIntent.setComponent(mComponentName);
		startService(mIntent);

		/* register broadcast receiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction(DOWNLOAD_FINISH_ACTION);
		registerReceiver(mReceiver, filter);

		//mThread.start();
	}

	public void onResume() {
		super.onResume();
		/* bind service */
		Log.d(TAG, "bindService");
		mUpdateType = 1;
		bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public void onPause() {
		super.onPause();
		/* unbind service */
		Log.d(TAG, "unbindService");
		unbindService(mConnection);
	}

	public void onClick(View v) {
		int ret = 0;
		switch (v.getId()) {
		case R.id.Button01:
			if (mDbstarService != null) {
				try {
					mDbstarService.startDvbpush();
				} catch (RemoteException e) {
					 e.printStackTrace();
				}
			}
			showToast("startDvbpush");
			break;
		case R.id.Button02:
			if (mDbstarService != null) {
				try {
					mDbstarService.stopDvbpush();
				} catch (RemoteException e) {
					 e.printStackTrace();
				}
			}
			showToast("stopDvbpush");
			break;
		case R.id.Button03:
			if (mDbstarService != null) {
				try {
					mDbstarService.startTaskInfo();
				} catch (RemoteException e) {
					 e.printStackTrace();
				}
			}
			showToast("startTaskInfo");
			break;
		case R.id.Button04:
			if (mDbstarService != null) {
				try {
					mDbstarService.stopTaskInfo();
				} catch (RemoteException e) {
					 e.printStackTrace();
				}
			}
			showToast("stopTaskInfo");
			break;
		case R.id.Button05:
			if (mDbstarService != null) {
				try {
					Intent it = mDbstarService.getTaskInfo();
					byte[] bytes = it.getByteArrayExtra("taskinfo");
					try {
						String info = new String(bytes, "utf-8");
						String info2 = new String(bytes, "GB2312");
						Log.d(TAG, "TaskInfo: " + info);
						Log.d(TAG, "TaskInfo: " + info2);
						taskInfo.setText(this.getString(R.string.taskInfo) + ": "+ info);
					} catch (UnsupportedEncodingException e) {
						 e.printStackTrace();
					}
				} catch (RemoteException e) {
					 e.printStackTrace();
				}
			}
			showToast("getTaskInfo");
			break;
		case R.id.Button06:
			Log.d(TAG, "Exit()");
			Intent it = new Intent();
			it.setComponent(new ComponentName("com.dbstar.DbstarDVB", "com.dbstar.DbstarDVB.VideoPlayer.FileList"));
			it.setAction("android.intent.action.MAIN");
			startActivity(it);
			break;
		default:
			break;
		}
	}

	public void onDestroy() {
		super.onDestroy();
		mUpdateType = 0;
		unregisterReceiver(mReceiver);
	}

	private void showToast(String text) {
		if (mToast == null)
			mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		mToast.setText(text);
		mToast.show();
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "got broadcast: ACTION=" + action);
			Toast.makeText(context, "got broadcast:" + action, 2000).show();
		}
	};

	private Thread mThread = new Thread() {
		public void run() {
			while (true) {
				if (mUpdateType > 0) {
					Log.d(TAG, "MSG_TASKINFO");
				}
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
}
