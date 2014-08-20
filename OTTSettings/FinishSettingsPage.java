package com.dbstar.settings.network;

import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dbstar.settings.R;
import com.dbstar.settings.base.BaseFragment;
import com.dbstar.settings.utils.SettingsCommon;

public class FinishSettingsPage extends BaseFragment {

	private static final String TAG = "FinishSettingsPage";

	TextView mStateView;
	Button mOkButton, mPrevButton;

	boolean mIsChecked = false;
	boolean mFirstDisconnectInfo = true;

	private Handler mHandler;
	ConnectivityManager mConnectManager;
	private IntentFilter mConnectIntentFilter;

	private Timer mTimer = null;
	private TimerTask mTask = null;

	class TimeoutTask implements Runnable {

		@Override
		public void run() {
			Log.d(TAG, "=== timeout === ");
			configureTimeout();
		}

	}

	void configureTimeout() {
		mStateView.setText(R.string.network_setup_failed);
		
		stopTimer();
	}

	void stopTimer() {
	    Log.d(TAG, "============== stop Timer" );
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {

			if (!mIsChecked)
				return;

			String action = intent.getAction();

			if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION))
				return;

			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

			Log.d(TAG, "noConnectivity = " + noConnectivity);
			if (noConnectivity) {
				if (mFirstDisconnectInfo) {
					// we will first receive a disconnect message, so skip it
					// here.
					mFirstDisconnectInfo = false;
					return;
				}

				// There are no connected networks at all
				handleNetConnected();
				return;
			}

			// case 1: attempting to connect to another network, just wait for
			// another broadcast
			// case 2: connected
			// NetworkInfo networkInfo = (NetworkInfo) intent
			// .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

			NetworkInfo networkInfo = mConnectManager.getActiveNetworkInfo();

			if (networkInfo != null) {
				Log.d(TAG, "getTypeName() = " + networkInfo.getTypeName());
				Log.d(TAG, "isConnected() = " + networkInfo.isConnected());

				if ((networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET || networkInfo
						.getType() == ConnectivityManager.TYPE_WIFI)
						&& networkInfo.isConnected()) {
					handleNetConnected();
				}
			}
		}

	};

	public boolean isNetworkConnected() {
		NetworkInfo networkInfo = mConnectManager.getActiveNetworkInfo();
		return networkInfo != null
				&& (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET || networkInfo
						.getType() == ConnectivityManager.TYPE_WIFI)
				&& networkInfo.isConnected();
	}

	void handleNetConnected() {

		mHandler.post(new Runnable() {
			public void run() {
				handleNetworkConnectStatus();
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.network_setup_endview, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initializeView();

		mHandler = new Handler();

		mConnectIntentFilter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);

		mConnectManager = (ConnectivityManager) mActivity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	public void onStart() {
		super.onStart();

		reqisterConnectReceiver();

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				checkConfigResult();
			}

		}, 2000);
	}

	public void onStop() {
		super.onStop();

		stopTimer();
		unregisterConnectReceiver();
	}

	void initializeView() {
		mStateView = (TextView) mActivity.findViewById(R.id.state_view);

		mOkButton = (Button) mActivity.findViewById(R.id.okbutton);
		mPrevButton = (Button) mActivity.findViewById(R.id.prevbutton);

		mOkButton.setOnClickListener(mOnClickListener);
		mPrevButton.setOnClickListener(mOnClickListener);

		mPrevButton.requestFocus();
	}

	private void reqisterConnectReceiver() {
		mActivity.registerReceiver(mNetworkReceiver, mConnectIntentFilter);
	}

	private void unregisterConnectReceiver() {
		mActivity.unregisterReceiver(mNetworkReceiver);
	}

	void checkConfigResult() {

		mIsChecked = true;

		NetworkInfo netInfo = mConnectManager.getActiveNetworkInfo();
		Log.d(TAG, "============== checkConfigResult " + netInfo);
		Log.d(TAG,
				"============== checkConfigResult "
						+ mConnectManager
								.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET));

		if (netInfo != null) {

			Log.d(TAG, "============== checkConfigResult " + netInfo.getState()
					+ " " + netInfo.getDetailedState());

			if (netInfo.getState() == NetworkInfo.State.CONNECTED) {
				mStateView.setText(R.string.network_setup_success);
			} else {
				if (netInfo.getState() == NetworkInfo.State.CONNECTING
						|| netInfo.getState() == NetworkInfo.State.DISCONNECTING) {
				    scheduleTimeoutTask();
					return;
				}

				if (netInfo.getState() == NetworkInfo.State.DISCONNECTED
						|| netInfo.getState() == NetworkInfo.State.SUSPENDED
						|| netInfo.getState() == NetworkInfo.State.UNKNOWN) {
					mStateView.setText(R.string.network_setup_failed);
					return;
				}
			}
		}
		 else {
		 //there is no connect now, so just wait the message, and handle it
		 //there.
		     scheduleTimeoutTask();
		 }
	}
	 
	void scheduleTimeoutTask(){
	    mTimer = new Timer();
        mTask = new TimerTask() {
            public void run() {
                mHandler.post(new TimeoutTask());
            }
        };

        mTimer.schedule(mTask, 120000);
        Log.d(TAG, "============== schedule TimeOut ");
	}
	void handleNetworkConnectStatus() {
		stopTimer();

		boolean connected = isNetworkConnected();
		if (connected) {
			mStateView.setText(R.string.network_setup_success);
		} else {
			mStateView.setText(R.string.network_setup_failed);
		}
	}

	void finishNetsettings() {
		try {
			String setflagValues = "1";
			byte[] setflag = setflagValues.getBytes();
			FileOutputStream fos = mActivity.openFileOutput(
					NetworkCommon.FlagFile, Context.MODE_WORLD_READABLE);
			fos.write(setflag);

			fos.close();
		} catch (Exception e) {
			Log.e(TAG,
					"Exception Occured: Trying to add set setflag : "
							+ e.toString());
			Log.e(TAG, "Finishing the Application");
		}
	}

	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.okbutton) {
				finishNetsettings();
				mActivity.finish();
			} else if (v.getId() == R.id.prevbutton) {
				mManager.prevPage(SettingsCommon.PAGE_FINISH);
			}
		}
	};
}
