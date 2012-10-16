package com.dbstar.DbstarDVB.Test;

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

import com.dbstar.DbstarDVB.*;


public class DvbpushTest extends Activity implements OnClickListener {
	private static final String TAG = "DbsterTest";

	private Toast mToast = null;
	private View Button01, Button02, Button03, Button04, Button05;
	private TextView command = null;
	//private ProgressBar progressBar = null;

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
		setContentView(R.layout.dvbpush_test);

		Button01 = this.findViewById(R.id.DvbButton01);
		Button01.setOnClickListener(this);
		Button02 = this.findViewById(R.id.DvbButton02);
		Button02.setOnClickListener(this);
		Button03 = this.findViewById(R.id.DvbButton03);
		Button03.setOnClickListener(this);
		Button04 = this.findViewById(R.id.DvbButton04);
		Button04.setOnClickListener(this);
		Button04 = this.findViewById(R.id.DvbButton04);
		Button04.setOnClickListener(this);
		Button05 = this.findViewById(R.id.DvbButton05);
		Button05.setOnClickListener(this);

		//progressBar = (ProgressBar) findViewById(R.id.Progress);
		command = (TextView)findViewById(R.id.Command);

		/* start service */
		Log.d(TAG, "startService");
		mIntent.setComponent(mComponentName);
		startService(mIntent);

		/* register broadcast receiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction(DbstarServiceApi.ACTION_NOTIFY);
		registerReceiver(mReceiver, filter);
	}

	public void onResume() {
		super.onResume();
		/* bind service */
		Log.d(TAG, "bindService");
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
		case R.id.DvbButton01:
			if (mDbstarService != null) {
				try {
					mDbstarService.initDvbpush();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			showToast("initDvbpush");
			break;
		case R.id.DvbButton02:
			showToast("start getTaskinfo");
			if (mDbstarService != null) {
				try {
					Intent it = mDbstarService.sendCommand(DbstarServiceApi.CMD_DVBPUSH_GETINFO_START, null, 0);
					byte[] bytes = it.getByteArrayExtra("result");
					if (bytes == null) {
						Log.e(TAG, "result: null");
						command.setText(this.getString(R.string.Command));
					} else {
						try {
							String buf = new String(bytes, "utf-8");
							Log.d(TAG, "Result: " + buf);
							command.setText(this.getString(R.string.Command)
									+ "\n" + buf);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.DvbButton03:
			if (mDbstarService != null) {
				try {
					Intent it = mDbstarService.sendCommand(DbstarServiceApi.CMD_DVBPUSH_GETINFO, null, 0);
					byte[] bytes = it.getByteArrayExtra("result");
					if (bytes == null) {
						Log.e(TAG, "result: null");
						command.setText(this.getString(R.string.Command));
					} else {
						try {
							String buf = new String(bytes, "utf-8");
							Log.d(TAG, "Result: " + buf);
							command.setText(this.getString(R.string.Command)
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
		case R.id.DvbButton04:
			showToast("stop getTaskinfo");
			if (mDbstarService != null) {
				try {
					Intent it = mDbstarService.sendCommand(DbstarServiceApi.CMD_DVBPUSH_GETINFO_STOP, null, 0);
					byte[] bytes = it.getByteArrayExtra("result");
					if (bytes == null) {
						Log.e(TAG, "result: null");
						command.setText(this.getString(R.string.Command));
					} else {
						try {
							String buf = new String(bytes, "utf-8");
							Log.d(TAG, "Result: " + buf);
							command.setText(this.getString(R.string.Command)
									+ "\n" + buf);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.DvbButton05:
			if (mDbstarService != null) {
				try {
					mDbstarService.uninitDvbpush();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			showToast("uninitDvbpush");
			break;
		default:
			break;
		}
	}

	public void onDestroy() {
		super.onDestroy();
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
			try {
				String action = intent.getAction();
				if (action.equals(DbstarServiceApi.ACTION_NOTIFY)) {
					int type = intent.getIntExtra("type", 0);
					byte[] bytes = intent.getByteArrayExtra("message");
					Log.d(TAG, "got broadcast: ACTION=" + action);
					Log.d(TAG, "got broadcast: type=" + type);
					if (bytes != null) {
						String msg = new String(bytes, "utf-8");
						Log.d(TAG, "got broadcast: message=" + msg);
						Toast.makeText(context,
								action + "(" + type + ", " + msg + ")", 2000)
								.show();
					} else {
						Toast.makeText(context,
								action + "(" + type + ")", 2000)
								.show();
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	};
}
