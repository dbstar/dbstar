package com.dbstar.guodian.app.base;

import android.util.Log;

import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.EventData;

public class GDSmartActivity extends GDBaseActivity {
	
	private static final String TAG = "GDSmartActivity";
	
	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);
		
		if (type == EventData.EVENT_GUODIAN_DISCONNECTED) {
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
		if (mService != null) {
			mService.reconnect();
		}
	}
	
	protected void handleConnectFailed() {
		Log.d(TAG, "handleConnectFailed");
		if (mService != null) {
			mService.reconnect();
		}
	}
	
	protected void handleConnected() {
		Log.d(TAG, "handleConnected");
	}
	
	protected void handleLoginSuccessed() {
		Log.d(TAG, "handleLoginSuccessed");
	}
	
	protected void handleReconnecting() {
		Log.d(TAG, "handleReconnecting");
	}
}
