package com.dbstar.app.alert;

import com.dbstar.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GDAlertDialog extends Dialog {

	private TextView mTitleView, mMessageView;
	public Button mOkButton, mCancelButton;
	public View mCenterSpacer;
	private int mId = -1;
	private int mType = -1;

	public static interface OnCreatedListener {
		public void onCreated(GDAlertDialog dialog);
	}

	public OnCreatedListener mOnCreatedListener = null;

	public void setOnCreatedListener(OnCreatedListener l) {
		mOnCreatedListener = l;
	}

	public GDAlertDialog(Context context, int id) {
		super(context, R.style.GDAlertDialog);

		mId = id;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.alert_dialog);

		mTitleView = (TextView) findViewById(R.id.alertTitle);
		mMessageView = (TextView) findViewById(R.id.message);

		mOkButton = (Button) findViewById(R.id.buttonOK);
		mCancelButton = (Button) findViewById(R.id.buttonCancel);
		mCenterSpacer = findViewById(R.id.centerSpacer);

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

	public void setMessage(String message) {
		mMessageView.setText(message);
	}

	public void setMessage(int messageId) {
		mMessageView.setText(messageId);
	}

	public void showSingleButton() {
		mCancelButton.setVisibility(View.GONE);
		mCenterSpacer.setVisibility(View.GONE);
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

		@Override
		public void onClick(View v) {
			if (v == mOkButton) {

			} else if (v == mCancelButton) {

			}

			closeDialog();
		}
	};
}
