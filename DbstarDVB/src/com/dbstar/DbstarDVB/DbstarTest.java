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

	private int mUpdateType = 0;
	private Toast mToast = null;
	private View Button01, Button02, Button03, Button04, Button05, Button06;
	// private TextView taskID = null;
	// private TextView taskName = null;
	// private TextView downloadSize = null;
	// private TextView totalSize = null;
	private TextView taskInfo = null;
	private TextView command = null;
	private ProgressBar progressBar = null;

	private Intent mIntent = new Intent();
	private IDbstarService mDbstarService = null;
	private ComponentName mComponentName = new ComponentName(
			"com.dbstar.DbstarDVB", "com.dbstar.DbstarDVB.DbstarService");
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

		registerReceiver();
		
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

		// taskID = (TextView) findViewById(R.id.taskID);
		// taskName = (TextView) findViewById(R.id.taskName);
		// downloadSize = (TextView) findViewById(R.id.downloadSize);
		// totalSize = (TextView) findViewById(R.id.totalSize);
		progressBar = (ProgressBar) findViewById(R.id.progress);
		taskInfo = (TextView) findViewById(R.id.taskInfo);
		command = (TextView) findViewById(R.id.command);

		/* start service */
		Log.d(TAG, "startService");
		mIntent.setComponent(mComponentName);
		startService(mIntent);

		/* register broadcast receiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction(DbstarServiceApi.ACTION_NOTIFY);
		registerReceiver(mReceiver, filter);

		// mThread.start();
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
					mDbstarService.initDvbpush();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			showToast("initDvbpush");
			break;
		case R.id.Button02:
			if (mDbstarService != null) {
				try {
					mDbstarService.uninitDvbpush();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			showToast("uninitDvbpush");
			break;
		case R.id.Button03:
			showToast("Button03");
			break;
		case R.id.Button04:
			showToast("Button04");
			break;
		case R.id.Button05:
			if (mDbstarService != null) {
				try {
					Intent it = mDbstarService.sendCommand(0x32, null, 0);
					byte[] bytes = it.getByteArrayExtra("result");
					if (bytes == null) {
						Log.e(TAG, "result: null");
					} else {
						try {
							String buf = new String(bytes, "utf-8");
							Log.d(TAG, "Result: " + buf);
							taskInfo.setText(this.getString(R.string.taskInfo)
									+ "\n" + buf);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
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
			it.setComponent(new ComponentName("com.dbstar.DbstarDVB",
					"com.dbstar.DbstarDVB.VideoPlayer.FileList"));
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

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(DbstarServiceApi.ACTION_NOTIFY);

		registerReceiver(mReceiver, filter);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			try {
				String action = intent.getAction();
				if (action.equals(DbstarServiceApi.ACTION_NOTIFY)) {
					int type = intent.getIntExtra("type", 0);
					byte[] bytes = intent.getByteArrayExtra("message");
					String msg = new String(bytes, "utf-8");
					Log.d(TAG, "got broadcast: ACTION=" + action);
					Log.d(TAG, "got broadcast: type=" + type);
					Log.d(TAG, "got broadcast: message=" + msg);
					Toast.makeText(context,
							action + "(" + type + ", " + msg + ")", 2000)
							.show();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
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
