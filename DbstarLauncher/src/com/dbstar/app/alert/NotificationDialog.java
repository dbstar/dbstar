package com.dbstar.app.alert;

import java.util.Timer;
import java.util.TimerTask;

import com.dbstar.R;
import com.dbstar.app.alert.NotificationFragment.TimeoutTask;
import com.dbstar.widget.GDMarqeeTextView;
import com.dbstar.widget.text.ScrollingMovementMethod;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class NotificationDialog extends Dialog implements
		DialogInterface.OnDismissListener {
	public static final int StyleTop = 1;
	public static final int StyleBottom = 2;
	public static final int StyleFullScreen = 3;
	public static final int StyleHalfScreen = 4;

	private int mStyleType;
	private String mMessage;
	private int mDuration;
	Timer mTimer = null;
	TimeoutTask mTimeoutTask = null;

	private GDMarqeeTextView mMarqeeView = null;
	private TextView mInfoView = null;
	private Context mContext = null;

	public NotificationDialog(Context context, int style, String message,
			int duration) {
		super(context, R.style.GDAlertDialog);
		mContext = context;
		mStyleType = style;
		mMessage = message;
		mDuration = duration;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		switch (mStyleType) {
		case StyleTop: {
			getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
			WindowManager.LayoutParams p = getWindow().getAttributes();
			p.width = convertDIP2Pixel(1120); //LayoutParams.MATCH_PARENT;
			p.height = convertDIP2Pixel(80);
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getWindow().setAttributes(p);
			break;
		}

		case StyleBottom: {
			getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
			WindowManager.LayoutParams p = getWindow().getAttributes();
			p.width = convertDIP2Pixel(1120);
			p.height = convertDIP2Pixel(80);
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getWindow().setAttributes(p);
			break;
		}

		case StyleFullScreen: {
			getWindow().setGravity(Gravity.CENTER);
			WindowManager.LayoutParams p = getWindow().getAttributes();
			p.width = convertDIP2Pixel(1120);
			p.height = convertDIP2Pixel(680);
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getWindow().setAttributes(p);
			break;
		}

		case StyleHalfScreen: {
			getWindow().setGravity(Gravity.CENTER);
			WindowManager.LayoutParams p = getWindow().getAttributes();
			p.width = convertDIP2Pixel(1120);
			p.height = convertDIP2Pixel(360);
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getWindow().setAttributes(p);
			break;
		}

		}

		if (mStyleType == StyleFullScreen || mStyleType == StyleHalfScreen) {
			setContentView(R.layout.osd_popup_view);
			mInfoView = (TextView) findViewById(R.id.info);

			mInfoView.setMovementMethod(new ScrollingMovementMethod(true));
			mInfoView.setText(mMessage);
		} else {
			setContentView(R.layout.osd_notification_view);
			mMarqeeView = (GDMarqeeTextView) findViewById(R.id.marqeeView);

			mMarqeeView.addText(mMessage);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mTimeoutTask != null) {
			mTimeoutTask.cancel();
			mTimeoutTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		mTimer = new Timer();
		mTimeoutTask = new TimeoutTask();
		mTimer.schedule(mTimeoutTask, mDuration);

		if (mStyleType == StyleBottom || mStyleType == StyleTop) {
			mMarqeeView.startMarqee(GDMarqeeTextView.MarqeeForever);
		}
	}

	@Override
	public void onStop() {
		super.onStop();

		if (mStyleType == StyleBottom || mStyleType == StyleTop) {
			if (mMarqeeView.isRunning())
				mMarqeeView.stopMarqee();
		}

		stopTimer();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {

		if (mStyleType == StyleBottom || mStyleType == StyleTop) {
			if (mMarqeeView.isRunning())
				mMarqeeView.stopMarqee();
		}

		stopTimer();
	}

	public void stopTimer() {
		if (mTimeoutTask != null) {
			mTimeoutTask.cancel();
			mTimeoutTask = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0xdee:
				dismiss();
				break;
			}
			super.handleMessage(msg);
		}

	};

	class TimeoutTask extends TimerTask {
		public void run() {
			Message message = Message.obtain();
			message.what = 0xdee;
			mHandler.sendMessage(message);
		}
	}

	int convertDIP2Pixel(int size) {
		Resources r = mContext.getResources();
		float pixelSize = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics());

		return (int) pixelSize;
	}
}
