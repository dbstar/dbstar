package com.dbstar.app.settings;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.EventData;
import com.dbstar.util.LogUtil;
import com.dbstar.util.upgrade.RebootUtils;

//Tasks
interface TaskController {
	public void taskFinished();

	public void registerTask(TaskObserver observer);
}

interface TaskObserver {
	public void onFinished(int resultCode, Object result);
}

public class GDSystemMgrActivity extends GDBaseActivity implements
		TaskController {
	private static final String TAG = "GDSystemMgrActivity";

	static final int VALUE_TRUE = 1;
	static final int VALUE_FALSE = 0;

	private static final String DefaultSecurityCode = "4000300888";
	private CheckBox mRestoreChecker, mClearChecker, mFormatChecker;
	private Button mOkButton, mCancelButton;
	private EditText mInputBox;
	private TextView mSecurityCodeView;
	private String mSecurityCode = DefaultSecurityCode;

	private ArrayList<TaskEntity> mTasks = null;
	private int mTaskIndex = 0;
	TaskObserver mCurrentTask = null;

	static final int MSG_REBOOT_DELAYED = 0xE001;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REBOOT_DELAYED: {
				rebootSystem();
				break;
			}
			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.system_management_view);

		initializeView();

		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		LogUtil.d(TAG, "menu path = " + mMenuPath);

		if (mMenuPath != null) {
			String[] menuArray = mMenuPath.split(MENU_STRING_DELIMITER);
			showMenuPath(menuArray);
		}
	}

	protected void initializeView() {
		super.initializeView();

		mRestoreChecker = (CheckBox) findViewById(R.id.restore_checker);
		mClearChecker = (CheckBox) findViewById(R.id.clear_purchase_checker);
		mFormatChecker = (CheckBox) findViewById(R.id.format_disk_checker);
		mOkButton = (Button) findViewById(R.id.okbutton);
		mCancelButton = (Button) findViewById(R.id.cancelbutton);
		mInputBox = (EditText) findViewById(R.id.security_code_editor);
		mSecurityCodeView = (TextView) findViewById(R.id.security_code);

		mRestoreChecker.setOnKeyListener(mOnKeyListener);
		mFormatChecker.setOnKeyListener(mOnKeyListener);
		mClearChecker.setOnKeyListener(mOnKeyListener);

		mOkButton.setOnKeyListener(mOnKeyListener);
		mCancelButton.setOnKeyListener(mOnKeyListener);

		mInputBox.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				checkSecurityCode();
			}

		});

		mOkButton.setEnabled(false);
		mOkButton.setFocusable(false);
		mCancelButton.requestFocus();
		mSecurityCodeView.setText(mSecurityCode);
	}

	public void notifyEvent(int type, Object event) {
		super.notifyEvent(type, event);

		EventData.DiskFormatEvent formatEvent = (EventData.DiskFormatEvent) event;
		if (type == EventData.EVENT_DISK_FORMAT) {
			Resources res = getResources();
			String msg = null;
			if (formatEvent.Successed) {
				msg = res.getString(R.string.format_disk_successed);
			} else {
				msg = String.format(res.getString(R.string.format_disk_failed),
						formatEvent.ErrorMessage);
			}

			if (mCurrentTask != null) {
				mCurrentTask.onFinished(formatEvent.Successed ? VALUE_TRUE
						: VALUE_FALSE, msg);
			}
		}
	}

	void checkSecurityCode() {
		boolean valid = false;

		String code = mInputBox.getText().toString();
		if (code != null && code.length() == mSecurityCode.length()
				&& code.equals(mSecurityCode)) {
			if (mClearChecker.isChecked() || mFormatChecker.isChecked()
					|| mRestoreChecker.isChecked()) {
				valid = true;
			}
		}

		if (valid) {
			mOkButton.setEnabled(true);
			mOkButton.setFocusable(true);
		} else {
			mOkButton.setEnabled(false);
			mOkButton.setFocusable(false);
		}
	}

	void okButtonPressed() {

		//RestoreFactoryUtil.closeNetwork();

		if (mTasks == null) {
			mTasks = new ArrayList<TaskEntity>();
		} else {
			mTasks.clear();
		}

		if (mRestoreChecker.isChecked()) {
			RestoreTaskEntity task = new RestoreTaskEntity(this);
			mTasks.add(task);
		}

		if (mClearChecker.isChecked()) {
			ClearTaskEntity task = new ClearTaskEntity(this);
			mTasks.add(task);
		}

		if (mFormatChecker.isChecked()) {
			FormatTaskEntity task = new FormatTaskEntity(this);
			mTasks.add(task);
		}

		mTaskIndex = -1;
		scheduleTaskSequently();
	}

	void scheduleTaskSequently() {
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
				mHandler.sendEmptyMessageDelayed(MSG_REBOOT_DELAYED, 3000);
			}
		}
	}

	void rebootSystem() {
		// hideLoadingDialog();
		RebootUtils.rebootNormal(this);
	}

	public void taskFinished() {
		scheduleTaskSequently();
	}

	public void registerTask(TaskObserver observer) {
		mCurrentTask = observer;
	}

	void cancelButtonPressed() {
		finish();
	}

	View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			int action = event.getAction();
			if (action == KeyEvent.ACTION_DOWN) {
				switch (keyCode) {
				case KeyEvent.KEYCODE_DPAD_CENTER: {
					if (v instanceof CheckBox) {
						CheckBox checker = (CheckBox) v;
						checker.setChecked(!checker.isChecked());
						checkSecurityCode();
						return true;
					} else if (v instanceof Button) {
						Button button = (Button) v;
						if (button == mOkButton) {
							okButtonPressed();
						} else if (button == mCancelButton) {
							cancelButtonPressed();
						}

						return true;
					}

					break;
				}
				}
			}
			return false;
		}
	};

	class TaskEntity implements TaskObserver {
		public static final int TaskRestore = 1;
		public static final int TaskClear = 2;
		public static final int TaskFormat = 3;

		public int Type = 0;

		protected TaskController Controller = null;

		public TaskEntity(TaskController controller, int type) {
			Controller = controller;
			Type = type;
		}

		protected void doTask() {

		}

		public void onFinished(int resultCode, Object result) {

		}
	}

	class RestoreTaskEntity extends TaskEntity {
		public RestoreTaskEntity(TaskController controller) {
			super(controller, TaskRestore);
		}

		public void doTask() {
			String loadingText = getResources().getString(
					R.string.restore_progress_text);
			showLoadingDialog(loadingText);

			RestoreFactoryUtil.clearNetworkInfo();
			RestoreFactoryUtil.clearSystemSettings();
			RestoreFactoryUtil.clearPushSettings();

			mHandler.postDelayed(new Runnable() {
				public void run() {
					hideLoadingDialog();
					Controller.taskFinished();
				}
			}, 6000);
		}
	}

	class ClearTaskEntity extends TaskEntity {
		public ClearTaskEntity(TaskController controller) {
			super(controller, TaskClear);
		}

		public void doTask() {
			String loadingText = getResources().getString(
					R.string.clear_progress_text);
			showLoadingDialog(loadingText);
			RestoreFactoryUtil.clearDrmInfo();

			mHandler.postDelayed(new Runnable() {
				public void run() {
					hideLoadingDialog();
					Controller.taskFinished();
				}
			}, 6000);
		}
	}

	class FormatTaskEntity extends TaskEntity {
		public FormatTaskEntity(TaskController controller) {
			super(controller, TaskFormat);
		}

		public void doTask() {
			Controller.registerTask(FormatTaskEntity.this);

			String loadingText = getResources().getString(
					R.string.format_progress_text);
			showLoadingDialog(loadingText);
			RestoreFactoryUtil.formatDisk();
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
