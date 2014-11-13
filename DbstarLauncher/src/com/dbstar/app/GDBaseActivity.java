package com.dbstar.app;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.dbstar.R;
import com.dbstar.app.alert.GDAlertDialog;
import com.dbstar.app.alert.GDDiskInitDialog;
import com.dbstar.app.alert.NotificationDialog;
import com.dbstar.guodian.data.CtrlNo;
import com.dbstar.guodian.data.LoginData;
import com.dbstar.model.EventData;
import com.dbstar.model.GDCommon;
import com.dbstar.service.ClientObserver;
import com.dbstar.service.GDAudioController;
import com.dbstar.service.GDDataProviderService;
import com.dbstar.service.GDDataProviderService.DataProviderBinder;
import com.dbstar.util.LogUtil;
import com.dbstar.util.upgrade.RebootUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GDBaseActivity extends Activity implements ClientObserver, TaskController{
	private static final String TAG = "GDBaseActivity";

	protected static final int DLG_ID_ALERT = 0;
	protected static final int DLG_ID_SMARTCARD = 1;
	protected static final int DLG_ID_DRMINFO = 2;
	protected static final int DLG_ID_DISK_INIT = 3;

	protected static final int DLG_TYPE_FILE_NOTEXIST = 0;
	protected static final int DLG_TYPE_SMARTCARD_INFO = 1;
	protected static final int DLG_TYPE_NEWMAIL_INFO = 2;
	protected static final int DLG_TYPE_NOTIFICATION = 3;

	protected static final int MSG_SMARTCARD_STATUSCHANGED = 0x80001;
	protected static final int MSG_NEW_MAIL = 0x80002;
	protected static final int MSG_DISP_NOTIFICATION = 0x80003;
	protected static final int MSG_HIDE_NOTIFICATION = 0x80004;
	protected static final int MSG_DISK_INIT = 0x80005;

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

	protected boolean mIsStarted = false; // true when this activity is not
											// visible
	protected boolean mBlockSmartcardPopup = false; // false to allow activity
													// show alert
	protected int mSmartcardState = GDCommon.SMARTCARD_STATE_NONE;

	protected boolean isSmartcardReady() {
		if (mService != null) {
			return mService.isSmartcardReady();
		}

		return false;
	}

	protected boolean isSmartcardPlugIn() {
		if (mService != null) {
			return mService.isSmartcardPlugIn();
		}

		return false;
	}

	// launcher will call this to check whether smartcard is plugged in.
	protected void checkSmartcardStatus() {
		if (mService == null)
			return;

		boolean isIn = mService.isSmartcardPlugIn();
		if (!isIn) {
			notifySmartcardStatusChanged();
		}
	}

	protected class MenuPathItem {
		TextView sTextView;
		ImageView sDelimiter;
	}

	protected boolean mBound = false;
	protected GDDataProviderService mService;

	private ProgressDialog mLoadingDialog = null;
	private String mLoadingText = null;

	GDAlertDialog mAlertDlg = null, mSmartcardDlg = null;
	int mAlertType = -1;
	GDDiskInitDialog mDiskInitDlg = null;
	ImageView mStatusIndicatorView = null;

	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SMARTCARD_STATUSCHANGED: {
				alertSmartcardInfo();
				break;
			}
			case MSG_NEW_MAIL: {
				alertNewMail();
				break;
			}
			case MSG_DISP_NOTIFICATION: {
				displayNotification((String) msg.obj);
				break;
			}
			case MSG_HIDE_NOTIFICATION: {
				hideNotification();
				break;
			}
			case MSG_DISK_INIT: {
				displayDiskInitMessage(msg.arg1, (String) msg.obj);
				break;
			}
			}
		}
	};

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

		mStatusIndicatorView = (ImageView) findViewById(R.id.status_indicator);
		if (mStatusIndicatorView != null) {
			mStatusIndicatorView.setVisibility(View.INVISIBLE);
		}
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

		Intent intent = new Intent(this, GDDataProviderService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStart() {
		super.onStart();

		mIsStarted = true;

		if (mBound) {
			mService.registerPageObserver(this);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		mIsStarted = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (mBound) {
			mService.unRegisterPageObserver(this);
			unbindService(mConnection);
			mBound = false;
		}

		if (mDlgTimer != null) {
			mDlgTimer.cancel();
		}
	}

	public void setMute(boolean mute) {
		Intent intent = new Intent(GDAudioController.ActionMute);
		intent.putExtra("key_mute", mute);
		sendBroadcast(intent);

		if (mStatusIndicatorView != null) {
			if (mute) {
				mStatusIndicatorView.setImageResource(R.drawable.sound_mute);
			} else {
				mStatusIndicatorView.setImageResource(R.drawable.sound_unmute);
			}

			mHandler.removeCallbacks(mHideMuteIconTask);
			mStatusIndicatorView.setVisibility(View.VISIBLE);
			mHandler.postDelayed(mHideMuteIconTask, 2000);
		}
	}

	Runnable mHideMuteIconTask = new Runnable() {
		public void run() {
			mStatusIndicatorView.setVisibility(View.INVISIBLE);
		}
	};

	public boolean isMute() {
		if (mService != null) {
			return mService.isMute();
		}

		return false;
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
			mService = null;

			onServiceStop();
		}
	};

	protected void onServiceStart() {
		LogUtil.d(TAG, "onServiceStart");
		
		mService.registerPageObserver(this);
		// get the init state of smart card.
		mSmartcardState = mService.getSmartcardState();
		
	}

	// this will not be called, only if when service is killed or crashed.
	protected void onServiceStop() {
		LogUtil.d(TAG, "onServiceStop");
	}

	public void updateData(int type, int param1, int param2, Object data) {

	}

	public void updateData(int type, Object key, Object data) {

	}

	public void notifyEvent(int type, Object event) {

		LogUtil.d(TAG, "======= notifyEvent ==== type " + type + " event " + event);

		if (type == EventData.EVENT_SMARTCARD_STATUS) {
			EventData.SmartcardStatus status = (EventData.SmartcardStatus) event;
			mSmartcardState = status.State;

			LogUtil.d(TAG, " === mIsStarted == " + mIsStarted
					+ " mBlockSmartcardPopup =" + mBlockSmartcardPopup);

			if (mIsStarted) {
				if (!mBlockSmartcardPopup) {
					notifySmartcardStatusChanged();
				}
			} else {
				// settings or guodian app is on top, so send message
				// and let them to show smard card state info.
				Intent intent = new Intent(GDCommon.ActionSDStateChange);
				intent.putExtra(GDCommon.KeySDState, mSmartcardState);
				sendBroadcast(intent);
			}

		} else if (type == EventData.EVENT_NEWMAIL) {
			notifyNewMail();
		} else if (type == EventData.EVENT_NOTIFICATION) {
			Message msg = mHandler.obtainMessage(MSG_DISP_NOTIFICATION);
			msg.obj = event;
			msg.sendToTarget();
		} else if (type == EventData.EVENT_HIDE_NOTIFICATION) {
			mHandler.sendEmptyMessage(MSG_HIDE_NOTIFICATION);
		} else if (type == EventData.EVENT_DISK_INIT) {
			EventData.DiskInitEvent diskInit = (EventData.DiskInitEvent) event;
			Message msg = mHandler.obtainMessage(MSG_DISK_INIT);
			msg.arg1 = diskInit.Type;
			msg.obj = diskInit.Message;
			msg.sendToTarget();
		} else if (type == EventData.EVENT_DISK_FORMAT && isShowFormatDisk) {
			EventData.DiskFormatEvent formatEvent = (EventData.DiskFormatEvent) event;
			Resources res = getResources();
			String msg = null;
			if (formatEvent.Successed) {
				msg = res.getString(R.string.format_disk_successed);
			} else {
				msg = String.format(res.getString(R.string.format_disk_failed), formatEvent.ErrorMessage);
			}
			
			if (mCurrentTask != null) {
				mCurrentTask.onFinished(formatEvent.Successed ? VALUE_TRUE : VALUE_FALSE, msg);
			}
		}
	}

	protected void showLoadingDialog(String loadingText) {

		if (mLoadingDialog == null || !mLoadingDialog.isShowing()) {
			LogUtil.d(TAG, "show loading dialog");
			mLoadingDialog = ProgressDialog.show(this, "", loadingText, true);
			mLoadingDialog.setCancelable(false);
			mLoadingDialog.setCanceledOnTouchOutside(false);
		}
	}

	protected void hideLoadingDialog() {
		if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
			LogUtil.d(TAG, "hide loading dialog");
			mLoadingDialog.dismiss();
			mLoadingDialog = null;
		}
	}

	protected void cancelRequests(ClientObserver observer) {
		LogUtil.d(TAG, "cancelRequests");

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

	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DLG_ID_ALERT: {
			mAlertDlg = new GDAlertDialog(this, id);
			mAlertDlg.setOnShowListener(mOnShowListener);
			dialog = mAlertDlg;
			break;
		}
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

	void stopTimer() {
		if (mTimeoutTask != null) {
			mTimeoutTask.cancel();
			mTimeoutTask = null;
		}

		if (mDlgTimer != null) {
			mDlgTimer.cancel();
			mDlgTimer = null;
		}
	}

	void displayAlertDlg(GDAlertDialog dialog, int type) {

		LogUtil.d(TAG, " ====  displayAlertDlg == " + type);

		switch (type) {
		case DLG_TYPE_FILE_NOTEXIST: {
			dialog.setTitle(R.string.error_title);
			dialog.setMessage(R.string.file_notexist);
			dialog.showSingleButton();
			break;
		}
		case DLG_TYPE_SMARTCARD_INFO: {
			dialog.setTitle(R.string.smartcard_status_title);
			dialog.showSingleButton();

			if (mSmartcardState == GDCommon.SMARTCARD_STATE_INSERTED
					|| mSmartcardState == GDCommon.SMARTCARD_STATE_INSERTING) {
				dialog.setMessage(R.string.smartcard_status_in);
			} else if (mSmartcardState == GDCommon.SMARTCARD_STATE_REMOVED
					|| mSmartcardState == GDCommon.SMARTCARD_STATE_REMOVING) {
				dialog.setMessage(R.string.smartcard_status_out);
			} else {
				dialog.setMessage(R.string.smartcard_status_invlid);
			}
			break;
		}
		case DLG_TYPE_NEWMAIL_INFO: {
			dialog.setTitle(R.string.alert_title);
			dialog.setMessage(R.string.email_newmail);
			dialog.showSingleButton();
			break;
		}
		}

		if (dialog != null) {
			dialog.mOkButton.requestFocus();
		}
	}

	private static final int DLG_TIMEOUT = 3000;

	Timer mDlgTimer = null;
	TimerTask mTimeoutTask = null;

	protected void notifySmartcardStatusChanged() {
		mHandler.sendEmptyMessage(MSG_SMARTCARD_STATUSCHANGED);
	}

	protected void notifyNewMail() {
		mHandler.sendEmptyMessage(MSG_NEW_MAIL);
	}

	protected void alertFileNotExist() {
		mAlertType = DLG_TYPE_FILE_NOTEXIST;

		if (mAlertDlg == null || !mAlertDlg.isShowing()) {
			showDialog(DLG_ID_ALERT);
		} else {
			displayAlertDlg(mAlertDlg, mAlertType);
		}
	}

	protected void alertNewMail() {
		mAlertType = DLG_TYPE_NEWMAIL_INFO;

		if (mAlertDlg == null || !mAlertDlg.isShowing()) {
			showDialog(DLG_ID_ALERT);
		} else {
			displayAlertDlg(mAlertDlg, mAlertType);
		}
	}

	protected void alertSmartcardInfo() {
		mAlertType = DLG_TYPE_SMARTCARD_INFO;

		if (mService != null) {
			mSmartcardState = mService.getSmartcardState();
		}

		LogUtil.d(TAG, " ======== display smartcard state ==== " + mSmartcardState);

		if (mSmartcardDlg == null || !mSmartcardDlg.isShowing()) {
			// if (mSmartcardDlg == null
			// && mSmartcardState == GDCommon.SMARTCARD_STATE_INSERTED) {
			// // not display insert ok dialog.
			// return;
			// }

			showDialog(DLG_ID_SMARTCARD);
		} else {
			displayAlertDlg(mSmartcardDlg, mAlertType);
		}

		if (mSmartcardState == GDCommon.SMARTCARD_STATE_INSERTED) {
			hideDlgDelay();
		} else {
			stopTimer();
		}
	}

	NotificationDialog mNotificationDialog = null;

	protected void displayNotification(String message) {
		LogUtil.d(TAG, " ======= displayNotification ============ " + message);

		if (message != null && !message.isEmpty()) {
			String[] data = message.split("\t");
			if (data.length > 1) {
				int type = Integer.valueOf(data[0]);
				int duration = GDCommon.OSDDISP_TIMEOUT;

				mNotificationDialog = new NotificationDialog(this, type,
						data[1], duration);
				mNotificationDialog.show();
			}
		}
	}

	protected void hideNotification() {

		if (mNotificationDialog != null) {
			mNotificationDialog.dismiss();
			mNotificationDialog = null;
		}
	}

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

	private void displayDiskInitMessage(int type, String message) {
		if (mDiskInitDlg == null) {
			mDiskInitDlg = new GDDiskInitDialog(this);
			mDiskInitDlg.show();
		}

		mDiskInitDlg.updateState(type, message);
	}

    protected CtrlNo getCtrlNo() {
        if(mService == null){
            return null;
        }
        LoginData loginData = mService.getLoginData();
        if (loginData == null)
            return null;
        if (loginData.CtrlNo == null)
            return null;

        return loginData.CtrlNo;
    }
    
    protected boolean isShowFormatDisk = true;
	private ArrayList<TaskEntity> mTasks = null;
	private static final int MSG_REBOOT_DELAYED = 0xE001;
	private int mTaskIndex = 0;
	private static final int VALUE_TRUE = 1;
	private static final int VALUE_FALSE = 0;
	private TaskObserver mCurrentTask = null;
    
    private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REBOOT_DELAYED:
				rebootSystem();
				break;
			}
		}
	};
	
	protected void rebootSystem() {
		// hideLoadingDialog();
		RebootUtils.rebootNormal(this);
	}
	
	void okButtonPressed() {
		if (isShowFormatDisk) {			
			if (mTasks == null) {
				mTasks = new ArrayList<TaskEntity>();
			} else {
				mTasks.clear();
			}
			
			FormatTaskEntity task = new FormatTaskEntity(this);
			mTasks.add(task);
			
			mTaskIndex = -1;
			scheduleTaskSequently();
		} 
	}

	private void scheduleTaskSequently() {
		if (mTasks.size() > 0) {
			mTaskIndex++;

			if (mTaskIndex < mTasks.size()) {
				TaskEntity task = mTasks.get(mTaskIndex);
				task.doTask();
			} else {
				hideLoadingDialog();
				// restart system here!
				String msg = getResources().getString(R.string.reboot_notes);
				showLoadingDialog(msg);
				handler.sendEmptyMessageDelayed(MSG_REBOOT_DELAYED, 3000);
			}
		}
	}
    
	public void taskFinished() {
		scheduleTaskSequently();
	}

	public void registerTask(TaskObserver observer) {
		mCurrentTask = observer;
	}
	
	protected class TaskEntity implements TaskObserver {
		public static final int TaskRestore = 1;
		public static final int TaskClear = 2;
		public static final int TaskFormat = 3;

		public int Type = 0;

		protected TaskController Controller = null;

		public TaskEntity(TaskController controller, int type) {
			Controller = controller;
			Type = type;
		}

		public void doTask() {

		}

		public void onFinished(int resultCode, Object result) {

		}
	}
	
	public class FormatTaskEntity extends TaskEntity {
		public FormatTaskEntity(TaskController controller) {
			super(controller, TaskFormat);
		}

		public void doTask() {
			Controller.registerTask(FormatTaskEntity.this);

			String loadingText = getResources().getString(R.string.format_progress_text);
			showLoadingDialog(loadingText);
//			RestoreFactoryUtil.formatDisk();
//			DiskFormatter mFormatter = new DiskFormatter();
//			mFormatter.startFormatDisk("/dev/block/sda1", handler, false);
			
			Context context = GDApplication.getAppContext();
			Intent intent = new Intent(GDCommon.ActionSystemRecovery);
			intent.putExtra(GDCommon.KeyRecoveryType, GDCommon.RecoveryTypeFormatDisk);
			intent.putExtra("format_uri", "/dev/block/sda1");
			context.sendBroadcast(intent);
		}

		public void onFinished(int resultCode, Object result) {
			// check format result here.
			hideLoadingDialog();
			String msg = (String) result;

			showLoadingDialog(msg);

			if (resultCode == VALUE_TRUE) {
				rebootDelayed(3000);
			} else {
				rebootDelayed(5000);
			}
		}

		void rebootDelayed(long delayMillis) {
			mHandler.postDelayed(new Runnable() {
				public void run() {
					hideLoadingDialog();

					Controller.taskFinished();
				}
			}, delayMillis);
		}
	}
}
