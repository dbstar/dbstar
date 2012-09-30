package com.dbstar.guodian.widget;

import com.dbstar.guodian.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;

public class GDWebView extends WebView {

	private static final String TAG = "GDWebView";

	public GDWebView(Context context) {
		super(context);

		// initializeView();
	}

	public GDWebView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// initializeView();
	}

	public GDWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// initializeView();
	}

	public GDWebView(Context context, AttributeSet attrs, int defStyle,
			boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);

		// initializeView();
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		int code = 0;
		int keyCode = event.getKeyCode();

		Log.d(TAG,
				"dispatchKeyEvent " + keyCode + "action " + event.getAction());

		boolean transcode = true;

		switch (keyCode) {
//		case KeyEvent.KEYCODE_DPAD_UP:
//			code = GDKeyEvent.KEYCODE_UP;
//			break;
//		case KeyEvent.KEYCODE_DPAD_DOWN:
//			code = GDKeyEvent.KEYCODE_DOWN;
//			break;
//		case KeyEvent.KEYCODE_DPAD_LEFT:
//		case KeyEvent.KEYCODE_VOLUME_DOWN:
//			code = GDKeyEvent.KEYCODE_LEFT;
//			break;
//
//		case KeyEvent.KEYCODE_DPAD_RIGHT:
//		case KeyEvent.KEYCODE_VOLUME_UP:
//			code = GDKeyEvent.KEYCODE_RIGHT;
//			break;
//		case KeyEvent.KEYCODE_DPAD_CENTER:
//		case KeyEvent.KEYCODE_ENTER: {
//			code = GDKeyEvent.KEYCODE_ENTER;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_ESCAPE:
//		case KeyEvent.KEYCODE_BACK: {
//			code = GDKeyEvent.KEYCODE_EXIT;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_MENU:
//		case KeyEvent.KEYCODE_HOME: {
//			code = GDKeyEvent.KEYCODE_MENU;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_0: {
//			code = GDKeyEvent.KEYCODE_0;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_1: {
//			code = GDKeyEvent.KEYCODE_1;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_2: {
//			code = GDKeyEvent.KEYCODE_2;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_3: {
//			code = GDKeyEvent.KEYCODE_3;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_4: {
//			code = GDKeyEvent.KEYCODE_4;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_5: {
//			code = GDKeyEvent.KEYCODE_5;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_6: {
//			code = GDKeyEvent.KEYCODE_6;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_7: {
//			code = GDKeyEvent.KEYCODE_7;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_8: {
//			code = GDKeyEvent.KEYCODE_8;
//			break;
//		}
//		case KeyEvent.KEYCODE_9: {
//			code = GDKeyEvent.KEYCODE_9;
//			break;
//		}
//		case KeyEvent.KEYCODE_MEDIA_PLAY:
//		case KeyEvent.KEYCODE_MEDIA_PAUSE: {
//			code = GDKeyEvent.KEYCODE_PLAY;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
//			code = GDKeyEvent.KEYCODE_FAST_FORWARD;
//			break;
//		}
//
//		case KeyEvent.KEYCODE_MEDIA_REWIND: {
//			code = GDKeyEvent.KEYCODE_REWIND;
//			break;
//		}
		
		case KeyEvent.KEYCODE_DPAD_RIGHT: {
			code = KeyEvent.KEYCODE_TAB;
			break;
		}

		default:
			transcode = false;
			break;
		}

		KeyEvent transEvent = null;
		if (transcode) {
			transEvent = new KeyEvent(event.getDownTime(),
					event.getEventTime(), event.getAction(), code,
					event.getRepeatCount(), event.getMetaState(),
					event.getDeviceId(), event.getScanCode(), event.getFlags(),
					event.getSource());
		} else {
			transEvent = event;
		}

		return super.dispatchKeyEvent(transEvent);
	}

	/*
	 * private static final int MSG_MOTION_DOWN = 0; private static final int
	 * MSG_MOTION_UP = 1;
	 * 
	 * HandlerThread mBgThread = null; Handler mHandler = null;
	 * 
	 * private void init() { mBgThread = new
	 * HandlerThread("SimulateMouseEventThread",
	 * Process.THREAD_PRIORITY_BACKGROUND); mBgThread.start();
	 * 
	 * mHandler = new Handler(mBgThread.getLooper()) { public void
	 * handleMessage(Message msg) { switch (msg.what) { case MSG_MOTION_DOWN: {
	 * simulateMouseEvent(true); break; }
	 * 
	 * case MSG_MOTION_UP: { simulateMouseEvent(false); break; }
	 * 
	 * default: break; } } }; }
	 * 
	 * private void simulateMouseEvent(boolean down) { try { Instrumentation
	 * inst = new Instrumentation(); if (down) { Log.d(TAG, "mouse down");
	 * inst.sendPointerSync(MotionEvent.obtain( SystemClock.uptimeMillis(),
	 * SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 240, 400, 0)); }
	 * else { Log.d(TAG, "mouse up"); inst.sendPointerSync(MotionEvent.obtain(
	 * SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
	 * MotionEvent.ACTION_UP, 240, 400, 0)); } } catch (Exception e) {
	 * Log.e("Exception when sendPointerSync", e.toString()); } }
	 */

	// protected void onDraw(Canvas c) {
	// super.onDraw(c);
	//
	//
	//
	// // post(new Runnable() {
	// // public void run() {
	// // getRootView().setBackgroundDrawable(mDefaultBackground);
	// // }
	// // });
	// }
	//
	private BitmapDrawable mDefaultBackground = null;

	private void initializeView() {

		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.webview_background);
		mDefaultBackground = new BitmapDrawable(getResources(), bmp);

		setBackgroundDrawable(mDefaultBackground);
	}

}
