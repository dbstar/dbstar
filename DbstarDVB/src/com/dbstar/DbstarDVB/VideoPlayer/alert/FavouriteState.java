package com.dbstar.DbstarDVB.VideoPlayer.alert;

import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dbstar.DbstarDVB.R;

public class FavouriteState extends TimerViewState {

	private static final String TAG = "FavouriteState";

	public static final String ID = "Favourite";

	private static final int TIMEOUT_IN_MILLIONSECONDS = 3000;
	private static final int UpdatePeriodInMills = 1000;

	Button mCloseButton;

	public FavouriteState(Dialog dlg, ViewStateManager manager) {
		super(ID, dlg, manager);
		
		mTimeoutTotal = TIMEOUT_IN_MILLIONSECONDS;
		mTimeoutUpdateInterval = UpdatePeriodInMills;
		mDelay = UpdatePeriodInMills;
	}

	public void enter(Object args) {
		mDialog.setContentView(R.layout.favorite_confirm_view);
		initializeView(mDialog);
	}

	public void start() {
		resetTimer();
	}

	public void stop() {
		stopTimer();
	}

	public void exit() {
		stopTimer();
	}

	protected void keyEvent(int KeyCode, KeyEvent event) {
		if (mDialog != null && mDialog.isShowing()) {
			resetTimer();
		}
	}
	
	public void onTimeout() {
		closePopupView();
	}

	void initializeView(Dialog dlg) {
		mTimeoutView = (TextView) dlg.findViewById(R.id.timeout_view);
		mCloseButton = (Button) dlg.findViewById(R.id.ok_button);
		mCloseButton.setOnClickListener(mClickListener);
		mCloseButton.requestFocus();
	}

	View.OnClickListener mClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			closedButtonClicked();
		}
	};

	void closedButtonClicked() {
		stop();
		exit();
		closePopupView();
	}

	void closePopupView() {
		mDialog.dismiss();
	}

}
