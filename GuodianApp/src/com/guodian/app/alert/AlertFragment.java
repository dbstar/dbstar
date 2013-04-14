package com.guodian.app.alert;

import com.guodian.R;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AlertFragment extends DialogFragment {

	private TextView mTitleView, mMessageView;
	public Button mOkButton, mCancelButton;
	public View mCenterSpacer;

	private String mTitle, mMessage;
	private boolean mIsSingleButton = false;

	private static final String KeyTitle = "title";
	private static final String KeyMessage = "message";
	private static final String KeySingle = "single";

	public static AlertFragment newInstance(String title,
			String message, boolean type) {

		Bundle args = new Bundle();
		args.putString(KeyTitle, title);
		args.putString(KeyMessage, message);
		args.putBoolean(KeySingle, type);

		AlertFragment f = new AlertFragment();
		f.setArguments(args);

		return f;
	}

	public AlertFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();

		mTitle = args.getString(KeyTitle, "");
		mMessage = args.getString(KeyMessage, "");
		mIsSingleButton = args.getBoolean(KeySingle, false);
		
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GDAlertDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.alert_dialog, container, false);

		mTitleView = (TextView) v.findViewById(R.id.alertTitle);
		mMessageView = (TextView) v.findViewById(R.id.message);

		mOkButton = (Button) v.findViewById(R.id.buttonOK);
		mCancelButton = (Button) v.findViewById(R.id.buttonCancel);
		mCenterSpacer = v.findViewById(R.id.centerSpacer);

		mOkButton.setOnClickListener(mButtonClickListener);
		mCancelButton.setOnClickListener(mButtonClickListener);

		return v;
	}

	@Override
	public void onStart() {
		super.onStart();

		mTitleView.setText(mTitle);
		mMessageView.setText(mMessage);

		if (mIsSingleButton) {
			mCancelButton.setVisibility(View.GONE);
			mCenterSpacer.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	private void closeDialog() {
		dismiss();
	}

	int convertDIP2Pixel(int size) {
		Resources r = getResources();
		float pixelSize = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics());

		return (int) pixelSize;
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
