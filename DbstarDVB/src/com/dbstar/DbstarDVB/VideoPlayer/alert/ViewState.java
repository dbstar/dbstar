package com.dbstar.DbstarDVB.VideoPlayer.alert;

import android.app.Dialog;
import android.view.KeyEvent;

public class ViewState {

	protected Dialog mDialog = null;
	protected ViewStateManager mManager;
	protected ActionHandler mActionHandler;
	
	protected ViewState(Dialog dlg, ViewStateManager mgr) {
		mDialog = dlg;
		mManager = mgr;
	}

	protected void enter(Object args) {

	}

	protected void start() {

	}

	protected void stop() {

	}

	protected void exit() {

	}

	protected void keyEvent(int KeyCode, KeyEvent event) {

	}
}
