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
import android.net.ConnectivityManager;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;

import java.io.UnsupportedEncodingException;

import com.dbstar.DbstarDVB.*;

public class BroadcastTest extends Activity {
	private static final String TAG = "NetworkTest";

	private Toast mToast = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.broadcast_test);

		/* register broadcast receiver */
		IntentFilter filter = new IntentFilter();

        /* DbstarService */
		filter.addAction(DbstarServiceApi.ACTION_NOTIFY);
		filter.addAction(DbstarServiceApi.ACTION_HDMI_IN);
		filter.addAction(DbstarServiceApi.ACTION_HDMI_OUT);
		filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_IN);
		filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_OUT);

        /* Disk */
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);

        /* Network */
		filter.addAction(EthernetManager.ETH_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

		registerReceiver(mReceiver, filter);
	}

	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
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
            String action = intent.getAction();
            Log.d(TAG, "******* get broadcast: " + action);
            showToast(action);
		}
	};
}
