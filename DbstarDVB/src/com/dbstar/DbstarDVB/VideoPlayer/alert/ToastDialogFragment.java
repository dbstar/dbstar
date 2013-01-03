package com.dbstar.DbstarDVB.VideoPlayer.alert;

import java.util.Timer;
import java.util.TimerTask;

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

import com.dbstar.DbstarDVB.R;

public class ToastDialogFragment extends DialogFragment {

	private static final String PARAMETER_MSGTYPE = "msg_type";
	private static final String PARAMETER_MSGID = "msg_id";

	private ImageView mMsgImageView = null;
	private int mMsgId;
	private int mMsgType;
	private int mDuration;
	Timer mTimer = null;
	ToastDialogListener mListener = null;

	public interface ToastDialogListener {
		public void onShow(ImageView view, int msgType, int msgId);
	}

	public static ToastDialogFragment newInstance() {
		ToastDialogFragment f = new ToastDialogFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// int style = DialogFragment.STYLE_NORMAL, theme = 0;
		// setStyle(style, theme);

		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DbDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().setGravity(Gravity.RIGHT | Gravity.TOP);
		WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
		p.width = LayoutParams.WRAP_CONTENT;
		p.height = LayoutParams.WRAP_CONTENT;
		p.horizontalMargin = 0.1f;
		p.verticalMargin = 0.1f;
		getDialog().getWindow().setAttributes(p);

		View v = inflater.inflate(R.layout.toast_layout, container, false);
		mMsgImageView = (ImageView) v.findViewById(R.id.message_image);

		return v;
	}
	
	int convertDIP2Pixel(int size) {
		Resources r = getResources();
		float pixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				size, r.getDisplayMetrics());

		return (int) pixelSize;
	}

	@Override
	public void onStart() {
		super.onStart();

		if (mListener != null) {
			mListener.onShow(mMsgImageView, mMsgType, mMsgId);
		}

		mTimer = new Timer();
		mTimer.schedule(mTimeoutTask, mDuration);
	}

	@Override
	public void onStop() {
		super.onStop();

		stopTimer();
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);

		stopTimer();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		stopTimer();
	}

	public void setListener(ToastDialogListener l) {
		mListener = l;
	}

	public void setMessage(int msgType, int msgId) {
		mMsgType = msgType;
		mMsgId = msgId;
	}

	public void setDuration(int duration) {
		mDuration = duration;
	}

	public void stopTimer() {
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

	TimerTask mTimeoutTask = new TimerTask() {

		public void run() {
			Message message = Message.obtain();
			message.what = 0xdee;
			mHandler.sendMessage(message);
		}
	};
}
