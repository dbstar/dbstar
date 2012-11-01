package com.dbstar.app.alert;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.model.GDCommon;
import com.dbstar.util.upgrade.RebootUtils;

public class GDForceUpgradeActivity extends Activity {

	private String mPackageFile = "";
	Timer mTimer = new Timer();
	UpdateTimeoutTask mTask = null;

	class UpdateTimeoutTask extends TimerTask {
		public void run() {
			timeout();
		}
	}

	void timeout() {
		rebootInstallPackage();
	}

	void rebootInstallPackage() {
		finish();
		RebootUtils.rebootInstallPackage(this, mPackageFile);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent in = getIntent();
		mPackageFile = in.getStringExtra(GDCommon.KeyPackgeFile);

		setContentView(R.layout.alert_upgrade);
		
		TextView messageView = (TextView) findViewById(R.id.message);
		messageView.setText(R.string.popup_forceupgrade_notes);
		
		ViewGroup buttonPanel = (ViewGroup) findViewById(R.id.buttonPanel);
		buttonPanel.setVisibility(View.GONE);
		
		mTask = new UpdateTimeoutTask();
		mTimer.schedule(mTask, 5000);
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
