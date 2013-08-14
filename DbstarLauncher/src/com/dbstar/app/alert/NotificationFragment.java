package com.dbstar.app.alert;

import java.util.Timer;
import java.util.TimerTask;

import com.dbstar.R;
import com.dbstar.util.LogUtil;
import com.dbstar.widget.GDMarqeeTextView;
import com.dbstar.widget.text.ScrollingMovementMethod;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
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
import android.widget.TextView;

public class NotificationFragment extends DialogFragment {

	public static final int StyleTop = 1;
	public static final int StyleBottom = 2;
	public static final int StyleFullScreen = 3;
	public static final int StyleHalfScreen = 4;
	private static final String TAG = "NotificationFragment";

	private int mStyleType;
	private String mMessage;
	private int mDuration;
	Timer mTimer = null;
	TimeoutTask mTimeoutTask = null;

	private GDMarqeeTextView mMarqeeView = null;
	private TextView mInfoView = null;

	public static NotificationFragment newInstance(int style, String message,
			int duration) {
		NotificationFragment f = new NotificationFragment();

		Bundle args = new Bundle();
		args.putInt("style", style);
		args.putString("message", message);
		args.putInt("duration", duration);
		f.setArguments(args);

		return f;
	}

	public NotificationFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		mStyleType = args.getInt("style");
		mMessage = args.getString("message");
		mDuration = args.getInt("duration");
		
		LogUtil.d(TAG, " style = " + mStyleType + " message=" + mMessage);

		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.GDAlertDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		switch (mStyleType) {
		case StyleTop: {
			getDialog().getWindow().setGravity(
					Gravity.LEFT | Gravity.TOP);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.type = LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;
			p.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			p.width = convertDIP2Pixel(1120);
			p.height = convertDIP2Pixel(80);
			p.x = convertDIP2Pixel(80);
			p.y = convertDIP2Pixel(20);
			LogUtil.d(TAG, " w h x y " + p.width + " " + p.height + " " + p.x + " " + p.y);
			// p.horizontalMargin = 0.01f;
			// p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);
			break;
		}

		case StyleBottom: {
			getDialog().getWindow().setGravity(
					Gravity.LEFT | Gravity.BOTTOM);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.width = convertDIP2Pixel(1120);
			p.height = convertDIP2Pixel(80);
			p.x = convertDIP2Pixel(80);
			p.y = convertDIP2Pixel(620);
			LogUtil.d(TAG, " w h x y " + p.width + " " + p.height + " " + p.x + " " + p.y);
//			p.horizontalMargin = 0.01f;
//			p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);
			break;
		}

		case StyleFullScreen: {
			getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.width = convertDIP2Pixel(1120);
			p.height = convertDIP2Pixel(680);
			p.x = convertDIP2Pixel(80);
			p.y = convertDIP2Pixel(20);
			LogUtil.d(TAG, " w h x y " + p.width + " " + p.height + " " + p.x + " " + p.y);
//			p.horizontalMargin = 0.01f;
//			p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);
			break;
		}

		case StyleHalfScreen: {
			getDialog().getWindow().setGravity(Gravity.LEFT | Gravity.BOTTOM);
			WindowManager.LayoutParams p = getDialog().getWindow()
					.getAttributes();
			p.width = convertDIP2Pixel(1120);
			p.height = convertDIP2Pixel(360);
			p.x = convertDIP2Pixel(80);
			p.y = convertDIP2Pixel(180);
			LogUtil.d(TAG, " w h x y " + p.width + " " + p.height + " " + p.x + " " + p.y);
//			p.horizontalMargin = 0.01f;
//			p.verticalMargin = 0.005f;
			getDialog().getWindow().setAttributes(p);
			break;
		}

		}

		View v = null;

		if (mStyleType == StyleFullScreen || mStyleType == StyleHalfScreen) {
			v = inflater.inflate(R.layout.osd_popup_view, container, false);
			mInfoView = (TextView) v.findViewById(R.id.info);

			mInfoView.setMovementMethod(new ScrollingMovementMethod(true));
			mInfoView.setText(mMessage);
		} else {
			v = inflater.inflate(R.layout.osd_notification_view, container,
					false);
			mMarqeeView = (GDMarqeeTextView) v.findViewById(R.id.marqeeView);

			mMarqeeView.addText(mMessage);
		}

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
		super.onDismiss(dialog);

		if (mStyleType == StyleBottom || mStyleType == StyleTop) {
			if (mMarqeeView.isRunning())
				mMarqeeView.stopMarqee();
		}

		stopTimer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

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
