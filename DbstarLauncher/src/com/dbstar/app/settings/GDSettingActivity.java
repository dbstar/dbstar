package com.dbstar.app.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.service.GDDataProviderService;

public class GDSettingActivity extends GDBaseActivity {

	private static final String TAG = "GDSettingActivity";

	public void onServiceStart() {
		querySettings();
	}

	public void updateData(int type, Object key, Object data) {
		if (type == GDDataProviderService.REQUESTTYPE_GETSETTINGS) {
			String settingName = (String) key;
			String value = (String) data;
			updateSettings(settingName, value);
		}
	}

	protected void querySettings() {

	}

	protected void updateSettings(String key, String value) {

	}
	
	protected void saveSettings() {
		
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			saveSettings();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	private View mInputView = null;
	private EditText mInputTextView = null;
	private View mCurrentEditView = null;

	protected void showInputView(View v) {
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
						Log.d(TAG, "text=" + textView.getText() + " "
								+ mInputTextView.getEditableText().toString());
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
