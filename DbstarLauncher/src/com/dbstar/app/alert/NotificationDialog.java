package com.dbstar.app.alert;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import com.dbstar.R;
import com.dbstar.widget.GDMarqeeTextView;
import com.dbstar.widget.text.ScrollingMovementMethod;

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
			getWindow().setGravity(Gravity.TOP);
			setContentView(R.layout.osd_popup_type_1);
			break;
		}

		case StyleBottom: {
			getWindow().setGravity(Gravity.BOTTOM);
			setContentView(R.layout.osd_popup_type_1);
			break;
		}

		case StyleFullScreen: {
			getWindow().setGravity(Gravity.CENTER);
			setContentView(R.layout.osd_popup_type_3);
			break;
		}

		case StyleHalfScreen:
		default: {
			getWindow().setGravity(Gravity.TOP);
			WindowManager.LayoutParams p = getWindow().getAttributes();
			p.y = convertDIP2Pixel(180);
			getWindow().setAttributes(p);
			setContentView(R.layout.osd_popup_type_4);
			break;
		}

		}

		if (mStyleType == StyleTop || mStyleType == StyleBottom) {
			mMarqeeView = (GDMarqeeTextView) findViewById(R.id.marqeeView);
			mMarqeeView.addText(mMessage);
		} else {
			mInfoView = (TextView) findViewById(R.id.info);
			mInfoView.setMovementMethod(new ScrollingMovementMethod(true));
			mInfoView.setText(mMessage);
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
