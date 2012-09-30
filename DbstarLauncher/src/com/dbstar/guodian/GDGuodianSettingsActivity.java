package com.dbstar.guodian;

import com.dbstar.guodian.model.GDDataProviderService;
import com.dbstar.guodian.model.GDSettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GDGuodianSettingsActivity extends GDBaseActivity {

	private static final String TAG = "GDGuodianSettingsActivity";

	private TextView mVersionText;
	private TextView mServerIPText;
	private TextView mPortText;
	private TextView mUsernameText;
	private TextView mPasswordText;
	private TextView mSerialNumberText;
	private Button mOkButton;

	private TextView[] mEditors;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.guodian_settingsview);

		mVersionText = (TextView) findViewById(R.id.editText_version);
		mServerIPText = (TextView) findViewById(R.id.editText_serverIP);
		mPortText = (TextView) findViewById(R.id.editText_port);
		mUsernameText = (TextView) findViewById(R.id.editText_username);
		mPasswordText = (TextView) findViewById(R.id.editText_password);
		mSerialNumberText = (TextView) findViewById(R.id.editText_serialnumber);

		mEditors = new TextView[5];
		mEditors[0] = mServerIPText;
		mEditors[1] = mPortText;
		mEditors[2] = mUsernameText;
		mEditors[3] = mPasswordText;
		mEditors[4] = mSerialNumberText;
		for (int i = 0; i < mEditors.length; i++) {
			mEditors[i].setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					Log.d(TAG, "onKey " + keyCode);
					boolean ret = false;
					int action = event.getAction();
					if (action == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {

						case KeyEvent.KEYCODE_DPAD_CENTER:
						case KeyEvent.KEYCODE_ENTER:
							ret = true;
							showInputView(v);
							break;
						default:
							break;
						}
					}
					return ret;
				}
			});
		}

		mOkButton = (Button) findViewById(R.id.button_ok);

		mOkButton.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.d(TAG, "onKey " + keyCode);
				boolean ret = false;
				int action = event.getAction();
				if (action == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {

					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						ret = true;
						saveSettings();
						break;
					default:
						break;
					}
				}
				return ret;
			}
		});
		
		initializeView();
		
		Intent intent = getIntent();
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	public void onServiceStart() {
		retriveSettings();
	}

	public void updateData(int type, Object key, Object data) {
		if (type == GDDataProviderService.REQUESTTYPE_GETSETTINGS) {
			String settingName = (String) key;
			String value = (String) data;

			Log.d(TAG, settingName + "=" + value);
			if (settingName.equals(GDSettings.SettingVersion)) {
				mVersionText.setText(value);
			} else if (settingName.equals(GDSettings.SettingServerIP)) {
				mServerIPText.setText(value);
			} else if (settingName.equals(GDSettings.SettingServerPort)) {
				mPortText.setText(value);
			} else if (settingName.equals(GDSettings.SettingUserName)) {
				mUsernameText.setText(value);
			} else if (settingName.equals(GDSettings.SettingPasswrod)) {
				mPasswordText.setText(value);
			} else if (settingName.equals(GDSettings.SettingSerialNumber)) {
				mSerialNumberText.setText(value);
			} else {
			}
		}
	}

	private void retriveSettings() {
		if (!mBound)
			return;

		mService.getSettingsValue(this, GDSettings.SettingVersion);
		mService.getSettingsValue(this, GDSettings.SettingServerIP);
		mService.getSettingsValue(this, GDSettings.SettingServerPort);
		mService.getSettingsValue(this, GDSettings.SettingUserName);
		mService.getSettingsValue(this, GDSettings.SettingPasswrod);
		mService.getSettingsValue(this, GDSettings.SettingSerialNumber);
	}

	private void saveSettings() {
		if (!mBound)
			return;

		// String version = mVersionText.getText().toString();
		String ip = mServerIPText.getText().toString();
		String port = mPortText.getText().toString();
		String username = mUsernameText.getText().toString();
		String passwd = mPasswordText.getText().toString();
		String serialNumber = mSerialNumberText.getText().toString();

		/*
		 * if (!version.isEmpty()) {
		 * mService.setSettingsValue(GDSettings.SettingVersion, version); }
		 */

		if (!ip.isEmpty()) {
			mService.setSettingsValue(GDSettings.SettingServerIP, ip);
		}

		if (!port.isEmpty()) {
			mService.setSettingsValue(GDSettings.SettingServerPort, port);
		}

		if (!username.isEmpty()) {
			mService.setSettingsValue(GDSettings.SettingUserName, username);
		}

		if (!passwd.isEmpty()) {
			mService.setSettingsValue(GDSettings.SettingPasswrod, passwd);
		}

		if (!serialNumber.isEmpty()) {
			mService.setSettingsValue(GDSettings.SettingSerialNumber,
					serialNumber);
		}
	}

	private View mInputView = null;
	private EditText mInputTextView = null;
	private View mCurrentEditView = null;

	void showInputView(View v) {
		mCurrentEditView = v;

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setIcon(R.drawable.icon);
		builder.setTitle(R.string.inputview_title);

		builder.setCancelable(false);
		mInputView = getLayoutInflater().inflate(R.layout.input_view, null);
		mInputTextView = (EditText) mInputView.findViewById(R.id.inputEdit);
		builder.setView(mInputView);

		builder.setPositiveButton(R.string.button_text_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						TextView textView = (TextView) mCurrentEditView;
						textView.setText(mInputTextView.getEditableText()
								.toString());
						Log.d(TAG, "text=" + textView.getText() + " " + mInputTextView.getEditableText()
								.toString());
					}
				});
		builder.setNegativeButton(R.string.button_text_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});

		builder.show();
	}

}
