package com.dbstar.DbstarDVB.VideoPlayer.alert;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class TimerViewState extends ViewState {

	private static final String TAG = "TimerViewState";

	protected TextView mTimeoutView;
	protected int mCurrentTimeout = 0, mTimeoutTotal = 0,
			mTimeoutUpdateInterval = 0, mDelay = 0;

	static Handler mUIHandler = new Handler();
	Timer mTimer = new Timer();
	TimeoutTask mTask = null;
	UpdateTask mUpdateTask = new UpdateTask();

	class TimeoutTask extends TimerTask {
		public void run() {
			Log.d(TAG, " --===========================  timeout mUIHandler = "
					+ mUIHandler);
			mUIHandler.post(mUpdateTask);
		}
	}

	class UpdateTask implements Runnable {

		@Override
		public void run() {
			Log.d(TAG, " ++++++++++ timeout = " + mCurrentTimeout);

			if (mCurrentTimeout == 0) {
				onTimeout();
				return;
			}

			mCurrentTimeout -= mTimeoutUpdateInterval;
			updateTimeoutView();
		}

	}

	protected void resetTimer() {
		Log.d(TAG, "+++reset timer++");

		if (mTask != null) {
			mTask.cancel();
		}

		mCurrentTimeout = mTimeoutTotal;
		updateTimeoutView();

		mTask = new TimeoutTask();

		if (mTimer == null) {
			mTimer = new Timer();
		}
		mTimer.schedule(mTask, mDelay, mTimeoutUpdateInterval);
	}

	protected void stopTimer() {
		if (mTask != null) {
			mTask.cancel();
			mTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	void updateTimeoutView() {
		String timeout = String.valueOf(mCurrentTimeout/1000);
		mTimeoutView.setText(timeout);
	}

	protected TimerViewState(String id, Dialog dlg, ViewStateManager mgr) {
		super(id, dlg, mgr);
	}
	
	protected void onTimeout() {

	}

}
