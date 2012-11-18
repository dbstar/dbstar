package com.dbstar.DbstarDVB.VideoPlayer.alert;

import android.app.Dialog;
import android.util.Log;
import android.view.KeyEvent;

public class ViewState {

	private static final String TAG = "ViewState";
	protected String mId;
	protected Dialog mDialog = null;
	protected ViewStateManager mManager;
	protected ActionHandler mActionHandler;

	protected ViewState(String id, Dialog dlg, ViewStateManager mgr) {
		mId = id;
		mDialog = dlg;
		mManager = mgr;
	}

	protected String getId() {
		return mId;
	}

	protected void enter(Object args) {
		Log.d(TAG, "enter " + mId);
	}

	protected void start() {
		Log.d(TAG, "start " + mId);
	}

	protected void stop() {
		Log.d(TAG, "stop " + mId);
	}

	protected void exit() {
		Log.d(TAG, "exit " + mId);
	}

	protected void keyEvent(int KeyCode, KeyEvent event) {
		Log.d(TAG, "keyEvent " + mId);
	}
}
