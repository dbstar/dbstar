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
import android.net.Uri;

import java.io.UnsupportedEncodingException;

import com.dbstar.DbstarDVB.*;

public class PlayerTest extends Activity implements OnClickListener {
	private static final String TAG = "DbsterTest";

	private static final String mNormalFile = "/mnt/sdb1/test_ok.ts";
	private static final String mTSFile = "/mnt/sdb1/drm/test.ts";
	private static final String mDRMFile = "/mnt/sdb1/drm/1.drm";

	private Toast mToast = null;
	private View Button01, Button02;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_test);

		Button01 = this.findViewById(R.id.PlayerButton01);
		Button01.setOnClickListener(this);
		Button02 = this.findViewById(R.id.PlayerButton02);
		Button02.setOnClickListener(this);

		/* register broadcast receiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction(DbstarServiceApi.ACTION_NOTIFY);
		registerReceiver(mReceiver, filter);
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	}

	public void onClick(View v) {
		int ret = 0;
		Intent it = new Intent();
		Uri uri = null;
		String header = "file://";
		switch (v.getId()) {
		case R.id.PlayerButton01:
			//showToast("Normal TS Play Test");
			uri = Uri.parse(header + mNormalFile);
			it.setData(uri);
			it.setComponent(new ComponentName("com.dbstar.DbstarDVB",
					"com.dbstar.DbstarDVB.VideoPlayer.PlayerMenu"));
			it.setAction("android.intent.action.View");
			startActivity(it);
			break;
		case R.id.PlayerButton02:
			//showToast("DRM TS Play Test");
			uri = Uri.parse(header + mTSFile + "|" + mDRMFile);
			it.setData(uri);
			it.setComponent(new ComponentName("com.dbstar.DbstarDVB",
					"com.dbstar.DbstarDVB.VideoPlayer.PlayerMenu"));
			it.setAction("android.intent.action.View");
			startActivity(it);
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
