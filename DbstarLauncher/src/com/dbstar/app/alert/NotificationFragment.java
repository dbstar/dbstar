package com.dbstar.app.alert;

import java.util.Timer;
import java.util.TimerTask;

import com.dbstar.R;
import com.dbstar.widget.GDMarqeeTextView;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class NotificationFragment extends DialogFragment {

	public static final int StyleTop = 1;
	public static final int StyleBottom = 2;
	public static final int StyleFullScreen = 3;
	public static final int StyleHalfScreen = 4;

	private int mStyleType;
	private String mMessage;
	private int mDuration;
	Timer mTimer = null;
	TimeoutTask mTimeoutTask = null;

	private GDMarqeeTextView mMarqeeView;

	public static NotificationFragment newInstance(int style, String message,
			int duration) {
		NotificationFragment f = new NotificationFragment(style, message,
				duration);
		return f;
	}

	public NotificationFragment(int style, String message, int duration) {
		mStyleType = style;
		mMessage = message;
		mDuration = duration;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GDAlertDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		switch (mStyleType) {
		case StyleTop: {
			getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.width = LayoutParams.MATCH_PARENT;
			p.height = LayoutParams.WRAP_CONTENT;
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);
			break;
		}

		case StyleBottom: {

			getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.width = LayoutParams.MATCH_PARENT;
			p.height = LayoutParams.WRAP_CONTENT;
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);

			break;
		}

		case StyleFullScreen: {

			getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.width = LayoutParams.MATCH_PARENT;
			p.height = LayoutParams.MATCH_PARENT;
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);

			break;
		}

		case StyleHalfScreen: {

			getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.width = LayoutParams.MATCH_PARENT;
			p.height = convertDIP2Pixel(360);
			p.horizontalMargin = 0.01f;
			p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);

			break;
		}

		}

		View v = inflater.inflate(R.layout.osd_notification_view, container,
				false);
		mMarqeeView = (GDMarqeeTextView) v.findViewById(R.id.marqeeView);

		mMarqeeView.addText(mMessage);

		return v;
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
		
		mMarqeeView.startMarqee(GDMarqeeTextView.MarqeeForever);
	}

	@Override
	public void onStop() {
		super.onStop();

		if (mMarqeeView.isRunning())
			mMarqeeView.stopMarqee();

		stopTimer();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);

		if (mMarqeeView.isRunning())
			mMarqeeView.stopMarqee();

		stopTimer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mMarqeeView.isRunning())
			mMarqeeView.stopMarqee();

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
		Resources r = getResources();
		float pixelSize = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, size, r.getDisplayMetrics());

		return (int) pixelSize;
	}
}
