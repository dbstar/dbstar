package com.guodian.app;

import java.util.Timer;
import java.util.TimerTask;

import com.guodian.R;
import com.guodian.app.alert.GDAlertDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GDBaseActivity extends Activity {
	private static final String TAG = "GDBaseActivity";

	protected static final String INTENT_KEY_MENUPATH = "menu_path";

	protected static final int MENU_LEVEL_1 = 0;
	protected static final int MENU_LEVEL_2 = 1;
	protected static final int MENU_LEVEL_3 = 2;
	protected static final int MENU_LEVEL_COUNT = 3;
	protected static final String MENU_STRING_DELIMITER = ">";
	protected String mMenuPath;
	protected MenuPathItem[] mMenuPathItems = new MenuPathItem[MENU_LEVEL_COUNT];
	// Menu path container view
	protected ViewGroup mMenuPathContainer;

	protected class MenuPathItem {
		TextView sTextView;
		ImageView sDelimiter;
	}

	protected void initializeMenuPath() {

		mMenuPathContainer = (ViewGroup) findViewById(R.id.menupath_view);

		for (int i = 0; i < MENU_LEVEL_COUNT; i++) {
			mMenuPathItems[i] = new MenuPathItem();
		}

		TextView textView = (TextView) findViewById(R.id.menupath_level1);
		mMenuPathItems[0].sTextView = textView;
		textView = (TextView) findViewById(R.id.menupath_level2);
		mMenuPathItems[1].sTextView = textView;
		textView = (TextView) findViewById(R.id.menupath_level3);
		mMenuPathItems[2].sTextView = textView;

		ImageView delimiterView = (ImageView) findViewById(R.id.menupath_level1_delimiter);
		mMenuPathItems[0].sDelimiter = delimiterView;

		delimiterView = (ImageView) findViewById(R.id.menupath_level2_delimiter);
		mMenuPathItems[1].sDelimiter = delimiterView;

		delimiterView = (ImageView) findViewById(R.id.menupath_level3_delimiter);
		mMenuPathItems[2].sDelimiter = delimiterView;
	}

	protected void initializeView() {
		initializeMenuPath();
	}

	protected void showMenuPath(String[] menuPath) {

		for (int i = 0; i < mMenuPathItems.length; i++) {
			if (i < menuPath.length) {
				mMenuPathItems[i].sTextView.setVisibility(View.VISIBLE);
				mMenuPathItems[i].sTextView.setText(menuPath[i]);

				if (mMenuPathItems[i].sDelimiter != null) {
					mMenuPathItems[i].sDelimiter.setVisibility(View.VISIBLE);
				}
			} else {
				mMenuPathItems[i].sTextView.setVisibility(View.INVISIBLE);

				if (mMenuPathItems[i].sDelimiter != null) {
					mMenuPathItems[i].sDelimiter.setVisibility(View.INVISIBLE);
				}
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
	}

	@Override
	protected void onStart() {
		super.onStart();

		registerMessageReceiver();
	}

	@Override
	protected void onStop() {
		super.onStop();

		unregisterMessageReceiver();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public static final int DLG_ID_SMARTCARD = 0;
	public static final int DLG_TYPE_SMARTCARD_INFO = 1;

	private static final String ActionSDStateChange = "com.dbstar.DbstarLauncher.SDSTATE_CHANGE";
	private static final String KeySDState = "state";
	private static final int MSG_SDSTATE_CHANGE = 0x1001;
	private static final int MSG_HIDE_DLG = 0x2001;

	// smart card state
	public static final int SMARTCARD_STATE_INSERTING = 0x1001;
	public static final int SMARTCARD_STATE_INSERTED = 0x1002;
	public static final int SMARTCARD_STATE_INVALID = 0x1003;
	public static final int SMARTCARD_STATE_REMOVING = 0x1004;
	public static final int SMARTCARD_STATE_REMOVED = 0x1005;
	public static final int SMARTCARD_STATE_NONE = 0x1000;

	private static final int DLG_TIMEOUT = 3000;
	private Timer mTimer = null;
	private TimerTask mTimeoutTask = null;
	GDAlertDialog mSmartcardDlg = null;
	int mSmartcardState = 0;
	int mAlertType = 0;

	private void registerMessageReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ActionSDStateChange);

		registerReceiver(mSystemMessageReceiver, filter);
	}

	private void unregisterMessageReceiver() {
		unregisterReceiver(mSystemMessageReceiver);
	}

	private BroadcastReceiver mSystemMessageReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			Log.d(TAG, "onReceive System msg " + action);

			if (action.equals(ActionSDStateChange)) {
				int state = intent.getIntExtra(KeySDState, 0);
				Message msg = mHandler.obtainMessage(MSG_SDSTATE_CHANGE);
				msg.arg1 = state;
				msg.sendToTarget();
			}
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int msgId = msg.what;
			switch (msgId) {
			case MSG_SDSTATE_CHANGE: {
				handSmartcardStateChange(msg.arg1);
				break;
			}
			case MSG_HIDE_DLG: {
				hideAlertDlg();
				break;
			}
			}
		}
	};

	DialogInterface.OnShowListener mOnShowListener = new DialogInterface.OnShowListener() {

		public void onShow(DialogInterface dialog) {
			if (dialog instanceof GDAlertDialog) {
				displayAlertDlg((GDAlertDialog) dialog, mAlertType);
			}
		}

	};

	DialogInterface.OnDismissListener mOnDismissListener = new DialogInterface.OnDismissListener() {

		public void onDismiss(DialogInterface dialog) {
			if (dialog instanceof GDAlertDialog) {
				stopTimer();
			}
		}

	};

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DLG_ID_SMARTCARD: {
			mSmartcardDlg = new GDAlertDialog(this, id);
			mSmartcardDlg.setOnShowListener(mOnShowListener);
			mSmartcardDlg.setOnDismissListener(mOnDismissListener);
			dialog = mSmartcardDlg;
			break;
		}
		}

		return dialog;
	}

	protected void handSmartcardStateChange(int state) {
		mSmartcardState = state;
		mAlertType = DLG_TYPE_SMARTCARD_INFO;

		Log.d(TAG, " ======== display smartcard state ==== " + mSmartcardState);

		if (mSmartcardDlg == null || !mSmartcardDlg.isShowing()) {
			showDialog(DLG_ID_SMARTCARD);
		} else {
			displayAlertDlg(mSmartcardDlg, mAlertType);
		}

		if (mSmartcardState == SMARTCARD_STATE_INSERTED) {
			hideDlgDelay();
		} else {
			stopTimer();
		}
	}

	void stopTimer() {
		if (mTimeoutTask != null) {
			mTimeoutTask.cancel();
			mTimeoutTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	void displayAlertDlg(GDAlertDialog dialog, int type) {

		Log.d(TAG, " ====  displayAlertDlg == " + type);

		switch (type) {

		case DLG_TYPE_SMARTCARD_INFO: {
			dialog.setTitle(R.string.smartcard_status_title);
			dialog.showSingleButton();

			if (mSmartcardState == SMARTCARD_STATE_INSERTED
					|| mSmartcardState == SMARTCARD_STATE_INSERTING) {
				dialog.setMessage(R.string.smartcard_status_in);
			} else if (mSmartcardState == SMARTCARD_STATE_REMOVED
					|| mSmartcardState == SMARTCARD_STATE_REMOVING) {
				dialog.setMessage(R.string.smartcard_status_out);
			} else {
				dialog.setMessage(R.string.smartcard_status_invlid);
			}
			break;
		}

		}

		if (dialog != null) {
			dialog.mOkButton.requestFocus();
		}
	}

	void hideAlertDlg() {
		mSmartcardDlg.dismiss();
	}

	void hideDlgDelay() {

		if (mTimeoutTask != null) {
			mTimeoutTask.cancel();
		}

		mTimeoutTask = new TimerTask() {

			public void run() {
				Message message = Message.obtain();
				message.what = MSG_HIDE_DLG;
				mHandler.sendMessage(message);
			}
		};

		if (mTimer != null) {
			mTimer.cancel();
		}

		mTimer = new Timer();
		mTimer.schedule(mTimeoutTask, DLG_TIMEOUT);
	}
}
