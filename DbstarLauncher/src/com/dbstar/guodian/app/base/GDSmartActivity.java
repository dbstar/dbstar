package com.dbstar.guodian.app.base;

import android.util.Log;

import com.dbstar.app.GDBaseActivity;
import com.dbstar.guodian.engine.GDConstract;
import com.dbstar.model.EventData;

public class GDSmartActivity extends GDBaseActivity {
	
	private static final String TAG = "GDSmartActivity";
	private static final long DefaultTimeout = 5000;
	private static final int MaxReconnectCount = 3;
	private long mTimeout = DefaultTimeout;
	private int mReconnectCount = 0;
	private boolean mStartReconnect = false;

	private Runnable mTimeoutTask = new Runnable() {

		public void run() {
			handleRequestTimeout();
		}
		
	};
	
	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);
		
		if (type == EventData.EVENT_GUODIAN_DATA) {
			handleRequestFinished();
		} else if (type == EventData.EVENT_GUODIAN_DISCONNECTED) {
			handleDisconnected();
		} else if (type == EventData.EVENT_GUODIAN_CONNECT_FAILED) {
			handleConnectFailed();
		} else if (type == EventData.EVENT_GUODIAN_CONNECTED) {
			handleConnected();
		} else if (type == EventData.EVENT_LOGIN_SUCCESSED) {
			handleLoginSuccessed();
		} else if (type == EventData.EVENT_GUODIAN_RECONNECTTING) {
			handleReconnecting();
		} else {
			
		}
	}
	
	protected void handleDisconnected() {
		Log.d(TAG, "handleDisconnected");
		
		mHandler.removeCallbacks(mTimeoutTask);
		
		if (!mStartReconnect) {
			mStartReconnect = true;
			mReconnectCount = 0;
		}

		mReconnectCount++;
		if (mReconnectCount > MaxReconnectCount) {
			Log.d(TAG, "reach max reconnect count");
			
			mStartReconnect = false;
			connectFailed();
			return;
		}

		mService.reconnect();
	}
	
	protected void handleConnectFailed() {
		Log.d(TAG, "handleConnectFailed");
		
		mHandler.removeCallbacks(mTimeoutTask);
		
		if (!mStartReconnect) {
			mStartReconnect = true;
			mReconnectCount = 0;
		}
		
		mReconnectCount++;
		if (mReconnectCount > MaxReconnectCount) {
			Log.d(TAG, "reach max reconnect count");
			
			mStartReconnect = false;
			connectFailed();
			return;
		}

		mService.reconnect();
	}
	
	protected void handleConnected() {
		Log.d(TAG, "handleConnected");
	}
	
	protected void handleLoginSuccessed() {
		Log.d(TAG, "handleLoginSuccessed");
		
		if (mStartReconnect) {
			mStartReconnect = false;
			mReconnectCount = 0;
		}
	}
	
	protected void handleReconnecting() {
		Log.d(TAG, "handleReconnecting");
	}
	
	protected void handleRequestTimeout() {
		Log.d(TAG, "handleReconnecting");
		mStartReconnect = true;
		mReconnectCount = 0;
		mService.disconnect();
	}
	
	protected void handleRequestFinished() {
		mHandler.removeCallbacks(mTimeoutTask);
	}
	
	private void connectFailed() {
		Log.d(TAG, "connectFailed");
	}
	
	public void requestData(int type, Object args) {
		mService.requestPowerData(type,	args);
		
		mHandler.postDelayed(mTimeoutTask, mTimeout);
	}
}
