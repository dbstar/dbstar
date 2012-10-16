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

public class DbstarTest extends Activity implements OnClickListener {
	private static final String TAG = "DbsterTest";

	private Toast mToast = null;
	private View Button01, Button02, Button03;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		Button01 = this.findViewById(R.id.TestButton01);
		Button01.setOnClickListener(this);
		Button02 = this.findViewById(R.id.TestButton02);
		Button02.setOnClickListener(this);
		Button03 = this.findViewById(R.id.TestButton03);
		Button03.setOnClickListener(this);

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
		switch (v.getId()) {
		case R.id.TestButton01:
			//showToast("DvbpushTest");
			it.setComponent(new ComponentName("com.dbstar.DbstarDVB",
					"com.dbstar.DbstarDVB.Test.DvbpushTest"));
			it.setAction("android.intent.action.MAIN");
			startActivity(it);
			break;
		case R.id.TestButton02:
			//showToast("PlayerTest");
			it.setComponent(new ComponentName("com.dbstar.DbstarDVB",
					"com.dbstar.DbstarDVB.Test.PlayerTest"));
			it.setAction("android.intent.action.MAIN");
			startActivity(it);
			break;
		case R.id.TestButton03:
			//showToast("UpgradeTest");
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
