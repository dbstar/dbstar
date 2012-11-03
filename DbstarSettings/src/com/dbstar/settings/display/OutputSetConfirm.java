/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dbstar.settings.display;

import com.dbstar.settings.R;
import com.dbstar.settings.R.string;
import com.dbstar.settings.utils.SettingsCommon;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

public class OutputSetConfirm extends Activity {
	private static final String TAG = "OutputSetConfirm";

	private AlertDialog mConfirmDlg = null;
	private final static long set_delay = 15 * 1000;
	private Handler mProgressHandler;

	String mNewOutputMode = null;
	int mCVBSMode = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		mNewOutputMode = intent.getStringExtra("set_mode");
		mCVBSMode = intent.getIntExtra("cvbs_mode", 0);

		Intent changeIntent = new Intent(
				SettingsCommon.ACTION_OUTPUTMODE_CHANGE);
		changeIntent.putExtra(SettingsCommon.OUTPUT_MODE, mNewOutputMode);
		changeIntent.putExtra("cvbs_mode", mCVBSMode);
		sendBroadcast(changeIntent);

		showConfirmDlg();
	}

	private class SetconfirmHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Intent cancelIntent = new Intent(
					SettingsCommon.ACTION_OUTPUTMODE_CANCEL);
			sendBroadcast(cancelIntent);

			setResult(RESULT_CANCELED, null);
			mConfirmDlg.dismiss();
			finish();
		}
	}

	private void showConfirmDlg() {

		mProgressHandler = new SetconfirmHandler();
		mProgressHandler.sendEmptyMessageDelayed(0, set_delay);

		mConfirmDlg = new AlertDialog.Builder(this)
				.setTitle(R.string.tv_output_mode_dialog_title)
				.setMessage(R.string.tv_output_mode_dialog_notes)
				.setPositiveButton(R.string.button_text_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								mProgressHandler.removeMessages(0);
								setResult(RESULT_OK, null);
								finish();
							}
						})
				.setNegativeButton(R.string.button_text_no,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								mProgressHandler.removeMessages(0);
								Intent cancelIntent = new Intent(
										SettingsCommon.ACTION_OUTPUTMODE_CANCEL);
								OutputSetConfirm.this
										.sendBroadcast(cancelIntent);
								setResult(RESULT_CANCELED, null);
								finish();
							}
						})
				.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK
								&& event.getAction() == KeyEvent.ACTION_UP
								&& !event.isCanceled()) {
							dialog.cancel();
							mProgressHandler.removeMessages(0);
							Intent cancelIntent = new Intent(
									SettingsCommon.ACTION_OUTPUTMODE_CANCEL);
							OutputSetConfirm.this.sendBroadcast(cancelIntent);
							setResult(RESULT_CANCELED, null);
							finish();
							return true;
						}
						return false;
					}
				}).show();
		mConfirmDlg.getButton(-2).requestFocus();
	}
}
