package com.dbstar.app.alert;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.model.GDCommon;
import com.dbstar.util.upgrade.RebootUtils;

public class GDForceUpgradeActivity extends Activity {

	private String mPackageFile = "";

	TextView mTimeoutView;

	private static final int MSG_UPDATETIMEOUT = 0;
	private static final int TimeoutTimeInMills = 5000;
	private static final int UpdatePeriodInMills = 1000;

	int mTimeoutTime = TimeoutTimeInMills;

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATETIMEOUT: {
				if (mTimeoutTime == 0) {
					rebootInstallPackage();
					return;
				}

				mTimeoutTime -= UpdatePeriodInMills;
				updateTimeoutView();
				return;
			}
			}
		}
	};

	Timer mTimer = new Timer();
	UpdateTimeoutTask mTask = null;

	class UpdateTimeoutTask extends TimerTask {
		public void run() {
			timeout();
		}
	}

	void timeout() {
		mUIHandler.sendEmptyMessage(MSG_UPDATETIMEOUT);
	}

	void rebootInstallPackage() {
		finish();
		RebootUtils.rebootInstallPackage(this, mPackageFile);
	}

	void updateTimeoutView() {
		mTimeoutView.setText(String.valueOf(mTimeoutTime / 1000));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forceupgrade_view);

		Intent in = getIntent();
		mPackageFile = in.getStringExtra(GDCommon.KeyPackgeFile);
		mTimeoutView = (TextView) findViewById(R.id.timeout_view);
		mTimeoutTime = TimeoutTimeInMills;
		updateTimeoutView();

		mTask = new UpdateTimeoutTask();
		mTimer.schedule(mTask, UpdatePeriodInMills, UpdatePeriodInMills);
	}

	public void onDestroy() {
		super.onDestroy();

		mTask.cancel();
		mTimer.cancel();
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		return true;
	}
}
