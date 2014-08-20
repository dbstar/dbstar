package com.settings.components;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.settings.ottsettings.R;

public class OttAlertDialog extends Dialog {

	private TextView mTitleView;
	private EditText mEtPassword;
	private CheckBox mCbShowPwd;
	public Button mOkButton, mCancelButton;
	private int mId = -1;
	private int mType = -1;

	public static interface OnCreatedListener {
		public void onCreated(OttAlertDialog dialog);
	}

	public OnCreatedListener mOnCreatedListener = null;

	public void setOnCreatedListener(OnCreatedListener l) {
		mOnCreatedListener = l;
	}

	public OttAlertDialog(Context context, int id) {
		super(context, R.style.GDAlertDialog);

		mId = id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lt_common_wifi_dialog);

		mTitleView = (TextView) findViewById(R.id.dialog_wifi_name);
		mEtPassword = (EditText) findViewById(R.id.dialog_wifi_et_password);
		mCbShowPwd = (CheckBox) findViewById(R.id.dialog_wifi_cb_show_password);
		mOkButton = (Button) findViewById(R.id.dialog_wifi_btn_ok);
		mCancelButton = (Button) findViewById(R.id.dialog_wifi_btn_cancle);

		mOkButton.setOnClickListener(mButtonClickListener);
		mCancelButton.setOnClickListener(mButtonClickListener);

		if (mOnCreatedListener != null) {
			mOnCreatedListener.onCreated(this);
		}
	}

	public void setTitle(String title) {
		mTitleView.setText(title);
	}

	public void setTitle(int titleId) {
		mTitleView.setText(titleId);
	}

	public int getId() {
		return mId;
	}
	
	public int getType() {
		return mType;
	}
	
	public void setType(int type) {
		mType = type;
	}

	private void closeDialog() {
		dismiss();
	}

	View.OnClickListener mButtonClickListener = new View.OnClickListener() {

		public void onClick(View v) {
			if (v == mOkButton) {
				// TODO:
				
			} else if (v == mCancelButton) {
				
			}

			closeDialog();
		}
	};
}
