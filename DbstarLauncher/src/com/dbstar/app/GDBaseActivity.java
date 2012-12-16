package com.dbstar.app;

import java.util.Timer;
import java.util.TimerTask;

import com.dbstar.R;
import com.dbstar.app.alert.GDAlertDialog;
import com.dbstar.model.EventData;
import com.dbstar.model.GDCommon;
import com.dbstar.service.ClientObserver;
import com.dbstar.service.GDDataProviderService;
import com.dbstar.service.GDDataProviderService.DataProviderBinder;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GDBaseActivity extends Activity implements ClientObserver {
	private static final String TAG = "GDBaseActivity";

	protected static final int DLG_FILE_NOTEXIST = 0;
	protected static final int DLG_SMARTCARD_INFO = 1;

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

	protected boolean mIsSmartcardIn = false;

	protected class MenuPathItem {
		TextView sTextView;
		ImageView sDelimiter;
	}

	protected boolean mBound = false;
	protected GDDataProviderService mService;

	private ProgressDialog mLoadingDialog = null;
	private String mLoadingText = null;

	protected GDResourceAccessor mResource;

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

		mResource = new GDResourceAccessor(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!mBound) {
			Intent intent = new Intent(this, GDDataProviderService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mBound) {
			mService.unRegisterPageObserver(this);
		}

		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}

		if (mDlgTimer != null) {
			mDlgTimer.cancel();
		}
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);

		// overridePendingTransition(R.anim.slide_in_right, 0);
		overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
	}

	public void startActivity(Intent intent, boolean animate) {
		super.startActivity(intent);

		int enterAnimateId = 0;
		int exitAnimateId = 0;
		if (animate) {
			// animateId = R.anim.slide_in_right;
			enterAnimateId = R.anim.fade_in_short;
			exitAnimateId = R.anim.fade_out_short;
		}

		overridePendingTransition(enterAnimateId, exitAnimateId);
	}

	@Override
	public void finish() {
		super.finish();

		// eliminate the animation between activities
		// enterAnim, exitAnim
		// overridePendingTransition(0, R.anim.slide_out_left);
		overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			DataProviderBinder binder = (DataProviderBinder) service;
			mService = binder.getService();
			mBound = true;

			onServiceStart();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBound = false;

			onServiceStop();
		}
	};

	protected void onServiceStart() {
		Log.d(TAG, "onServiceStart");

		mService.registerPageObserver(this);
		mIsSmartcardIn = mService.isSmartcardPlugIn();
	}

	protected void onServiceStop() {
		Log.d(TAG, "onServiceStop");

		mService.unRegisterPageObserver(this);
	}

	public void updateData(int type, int param1, int param2, Object data) {

	}

	public void updateData(int type, Object key, Object data) {

	}

	public void notifyEvent(int type, Object event) {
		if (type == EventData.EVENT_SMARTCARD_STATUS) {
			EventData.SmartcardStatus status = (EventData.SmartcardStatus) event;
			boolean plugIn = status.isPlugIn;

			notifySmartcardStatusChanged(plugIn);
		}
	}

	protected boolean checkLoadingIsFinished() {
		return true;
	}

	protected void showLoadingDialog() {

		if (mLoadingText == null) {
			mLoadingText = getResources().getString(R.string.loading_text);
		}

		if (mLoadingDialog == null || !mLoadingDialog.isShowing()) {
			Log.d(TAG, "show loading dialog");
			mLoadingDialog = ProgressDialog.show(this, "", mLoadingText, true);
			mLoadingDialog.setCancelable(true);
			mLoadingDialog.setCanceledOnTouchOutside(true);
			mLoadingDialog.setOnCancelListener(new LoadingCancelListener());
		}
	}

	protected void showAlertDialog(int dialogId) {
		showDialog(dialogId);
	}

	GDAlertDialog mSmartcardDlg = null;

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DLG_FILE_NOTEXIST:
		case DLG_SMARTCARD_INFO: {
			mSmartcardDlg = new GDAlertDialog(this, id);
			mSmartcardDlg.setOnCreatedListener(mOnCreatedListener);
			dialog = mSmartcardDlg;
			break;
		}
		}

		return dialog;
	}

	GDAlertDialog.OnCreatedListener mOnCreatedListener = new GDAlertDialog.OnCreatedListener() {

		@Override
		public void onCreated(GDAlertDialog dialog) {
			if (dialog.getId() == DLG_FILE_NOTEXIST) {
				dialog.setTitle(R.string.error_title);
				dialog.setMessage(R.string.file_notexist);
				dialog.showSingleButton();
			} else if (dialog.getId() == DLG_SMARTCARD_INFO) {
				dialog.setTitle(R.string.smartcard_status_title);
				if (mIsSmartcardIn) {
					dialog.setMessage(R.string.smartcard_status_in);
				} else {
					dialog.setMessage(R.string.smartcard_status_out);
				}
				dialog.showSingleButton();
			}
		}

	};

	protected void hideLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()
				&& checkLoadingIsFinished()) {
			Log.d(TAG, "hide loading dialog");
			mLoadingDialog.dismiss();
		}
	}

	private class LoadingCancelListener implements OnCancelListener {
		public void onCancel(DialogInterface dialog) {
			onLoadingCancelled();
		}
	}

	protected void onLoadingCancelled() {
		Log.d(TAG, "onLoadingCancelled");

		cancelRequests(this);
	}

	protected void cancelRequests(ClientObserver observer) {
		Log.d(TAG, "cancelRequests");

		mService.cancelRequests(observer);
	}

	protected String formPageText(int pageNumber) {
		String str = mResource.HanZi_Di;
		str += (pageNumber + 1) + mResource.HanZi_Ye;

		return str;
	}

	protected String formPageText(int pageNumber, int pageCount) {
		String str = pageNumber + "/" + pageCount;
		return str;
	}

	protected static final int MSG_SMARTCARD_STATUSCHANGED = 0x80001;
	protected static final int MSG_SMARTCARD_PLUGIN = 0;
	protected static final int MSG_SMARTCARD_PLUGOUT = 1;

	protected void notifySmartcardStatusChanged(boolean plugIn) {
		Message message = mHandler.obtainMessage(MSG_SMARTCARD_STATUSCHANGED);
		message.arg1 = plugIn ? MSG_SMARTCARD_PLUGIN : MSG_SMARTCARD_PLUGOUT;
		mHandler.sendMessage(message);
	}

	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SMARTCARD_STATUSCHANGED: {
				boolean plugIn = msg.arg1 == MSG_SMARTCARD_PLUGIN ? true
						: false;
				showSmartcardInfo(plugIn);
			}
			}
		}
	};

	Timer mDlgTimer = null;
	TimerTask mTimeoutTask = null;

	void hideDlgDelay() {
		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0x4ef:
					mTimeoutTask.cancel();
					mTimeoutTask = null;
					mDlgTimer.cancel();
					mDlgTimer = null;

					if (mSmartcardDlg != null && mSmartcardDlg.isShowing()) {
						mSmartcardDlg.dismiss();
					}
					break;
				}
				super.handleMessage(msg);
			}

		};

		if (mTimeoutTask != null) {
			mTimeoutTask.cancel();
		}

		mTimeoutTask = new TimerTask() {

			public void run() {
				Message message = Message.obtain();
				message.what = 0x4ef;
				handler.sendMessage(message);
			}
		};

		if (mDlgTimer != null) {
			mDlgTimer.cancel();
		}

		mDlgTimer = new Timer();
		mDlgTimer.schedule(mTimeoutTask, DLG_TIMEOUT);
	}

	private static final int DLG_TIMEOUT = 3000;

	protected void showSmartcardInfo(boolean plugIn) {
		mIsSmartcardIn = plugIn;
		if (mSmartcardDlg == null) {
			showDialog(DLG_SMARTCARD_INFO);

		} else {
			if (mIsSmartcardIn) {
				mSmartcardDlg.setMessage(R.string.smartcard_status_in);
			} else {
				mSmartcardDlg.setMessage(R.string.smartcard_status_out);
			}

			mSmartcardDlg.show();
		}

		hideDlgDelay();
	}
}
