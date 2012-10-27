package com.dbstar.settings.display;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.dbstar.settings.GDVideoSettingsActivity;
import com.dbstar.settings.R;
import com.dbstar.settings.R.array;
import com.dbstar.settings.R.drawable;
import com.dbstar.settings.R.id;
import com.dbstar.settings.R.string;
import com.dbstar.settings.R.xml;
import com.dbstar.settings.common.SettingsCommon;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.SystemProperties;

public class PositionSetting extends Activity {
	private final String TAG = "Settings_PositionSetting";

	private final String OUTPUT_POSITION_X = "output_position_x";
	private final String OUTPUT_POSITION_Y = "output_position_y";
	private final String OUTPUT_POSITION_W = "output_position_w";
	private final String OUTPUT_POSITION_H = "output_position_h";
	private final String OUTPUT_POSITION_MODE = "output_position_mode";

	private static final String STR_OUTPUT_MODE = "ubootenv.var.outputmode";
	private final static String sel_480ioutput_x = "ubootenv.var.480ioutputx";
	private final static String sel_480ioutput_y = "ubootenv.var.480ioutputy";
	private final static String sel_480ioutput_width = "ubootenv.var.480ioutputwidth";
	private final static String sel_480ioutput_height = "ubootenv.var.480ioutputheight";
	private final static String sel_480poutput_x = "ubootenv.var.480poutputx";
	private final static String sel_480poutput_y = "ubootenv.var.480poutputy";
	private final static String sel_480poutput_width = "ubootenv.var.480poutputwidth";
	private final static String sel_480poutput_height = "ubootenv.var.480poutputheight";
	private final static String sel_576ioutput_x = "ubootenv.var.576ioutputx";
	private final static String sel_576ioutput_y = "ubootenv.var.576ioutputy";
	private final static String sel_576ioutput_width = "ubootenv.var.576ioutputwidth";
	private final static String sel_576ioutput_height = "ubootenv.var.576ioutputheight";
	private final static String sel_576poutput_x = "ubootenv.var.576poutputx";
	private final static String sel_576poutput_y = "ubootenv.var.576poutputy";
	private final static String sel_576poutput_width = "ubootenv.var.576poutputwidth";
	private final static String sel_576poutput_height = "ubootenv.var.576poutputheight";
	private final static String sel_720poutput_x = "ubootenv.var.720poutputx";
	private final static String sel_720poutput_y = "ubootenv.var.720poutputy";
	private final static String sel_720poutput_width = "ubootenv.var.720poutputwidth";
	private final static String sel_720poutput_height = "ubootenv.var.720poutputheight";
	private final static String sel_1080ioutput_x = "ubootenv.var.1080ioutputx";
	private final static String sel_1080ioutput_y = "ubootenv.var.1080ioutputy";
	private final static String sel_1080ioutput_width = "ubootenv.var.1080ioutputwidth";
	private final static String sel_1080ioutput_height = "ubootenv.var.1080ioutputheight";
	private final static String sel_1080poutput_x = "ubootenv.var.1080poutputx";
	private final static String sel_1080poutput_y = "ubootenv.var.1080poutputy";
	private final static String sel_1080poutput_width = "ubootenv.var.1080poutputwidth";
	private final static String sel_1080poutput_height = "ubootenv.var.1080poutputheight";

	private static int ZoomPixel = 2;
	private boolean mZoomFlag = false; // mZoomFlag is true: zoom in;mZoomFlag
										// is false: zoom out
	private static double SizeChangeStep = 0.08;
	private Outputsize mOutputsize = new Outputsize();
	private PositionCoor mPrePosition = new PositionCoor();
	private PositionCoor mCurPosition = new PositionCoor();
	private int mSelectedItemPosition;

	private String mCurOutputMode = "";
	private String mPreOutputX = "";
	private String mPreOutputY = "";
	private String mPreOutputWidth = "";
	private String mPreOutputHeight = "";
	private String[] mOutputModeArray;

	private ImageButton mZoomButton;
	private ImageButton mLeftButton;
	private ImageButton mRightButton;
	private ImageButton mUpButton;
	private ImageButton mDownButton;
	private static final int GET_USER_OPERATION = 1;
	private static final int GET_DEFAULT_OPERATION = 2;

	private static final int OUTPUT480_FULL_WIDTH = 720;
	private static final int OUTPUT480_FULL_HEIGHT = 480;
	private static final int OUTPUT576_FULL_WIDTH = 720;
	private static final int OUTPUT576_FULL_HEIGHT = 576;
	private static final int OUTPUT720_FULL_WIDTH = 1280;
	private static final int OUTPUT720_FULL_HEIGHT = 720;
	private static final int OUTPUT1080_FULL_WIDTH = 1920;
	private static final int OUTPUT1080_FULL_HEIGHT = 1080;

	private static String VideoDisbaleFile = "/sys/class/video/disable_video";
	private static String VideoEnableFile = "/sys/class/display/wr_reg";
	private static String FreeScaleAxisFile = "/sys/class/graphics/fb0/free_scale_axis";
	private static String FreeScaleOsd0File = "/sys/class/graphics/fb0/free_scale";
	private static String FreeScaleOsd1File = "/sys/class/graphics/fb1/free_scale";
	private static String VideoAxisFile = "/sys/class/video/axis";
	private static String DisplayAxisFile = "/sys/class/display/axis";
	private static String PpscalerFile = "/sys/class/ppmgr/ppscaler";
	private static String BlankFb0File = "/sys/class/graphics/fb0/blank";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.xml.position_setting);

		TextView mHelp = (TextView) findViewById(R.id.positionsetting_help);
		mHelp.setText(R.string.position_help_noreboot);

		mZoomButton = (ImageButton) findViewById(R.id.btn_position_changeZoom);
		mZoomButton.setOnTouchListener(new mZoomButtonOnTouchListener());
		mZoomButton.setOnKeyListener(new mZoomButtonOnKeyListener());
		mLeftButton = (ImageButton) findViewById(R.id.btn_position_left);
		mLeftButton.setOnTouchListener(new mLeftButtonOnTouchListener());
		mRightButton = (ImageButton) findViewById(R.id.btn_position_right);
		mRightButton.setOnTouchListener(new mRightButtonOnTouchListener());
		mUpButton = (ImageButton) findViewById(R.id.btn_position_top);
		mUpButton.setOnTouchListener(new mUpButtonOnTouchListener());
		mDownButton = (ImageButton) findViewById(R.id.btn_position_bottom);
		mDownButton.setOnTouchListener(new mDownButtonOnTouchListener());

		mCurOutputMode = SystemProperties.get(STR_OUTPUT_MODE);
		mOutputModeArray = getResources().getStringArray(
				R.array.position_entries);
		getOutputsize();
		getOutput(mCurOutputMode);

		mPrePosition.width = 0;
		mPrePosition.height = 0;
		mPrePosition.left = 0;
		mPrePosition.top = 0;
		mPrePosition.right = 0;
		mPrePosition.bottom = 0;
		mCurPosition.width = Integer.valueOf(mPreOutputWidth).intValue();
		mCurPosition.height = Integer.valueOf(mPreOutputHeight).intValue();
		mCurPosition.left = Integer.valueOf(mPreOutputX).intValue();
		mCurPosition.top = Integer.valueOf(mPreOutputY).intValue();
		mCurPosition.right = mCurPosition.width + mCurPosition.left - 1;
		mCurPosition.bottom = mCurPosition.height + mCurPosition.top - 1;

		writeFile(FreeScaleOsd0File, "1");
		writeFile(FreeScaleOsd1File, "1");

		try {
			Bundle bundle = new Bundle();
			bundle = this.getIntent().getExtras();
			mSelectedItemPosition = bundle
					.getInt(SettingsCommon.KEY_SELECTED_ITEM);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class mZoomButtonOnTouchListener implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mZoomButton
						.setBackgroundResource(R.drawable.position_button_zoom_hl);
				if (mZoomFlag == true) {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_right);
					mRightButton
							.setBackgroundResource(R.drawable.position_button_left);
					mUpButton
							.setBackgroundResource(R.drawable.position_button_down);
					mDownButton
							.setBackgroundResource(R.drawable.position_button_up);
					mZoomFlag = false;
				} else {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_left);
					mRightButton
							.setBackgroundResource(R.drawable.position_button_right);
					mUpButton
							.setBackgroundResource(R.drawable.position_button_up);
					mDownButton
							.setBackgroundResource(R.drawable.position_button_down);
					mZoomFlag = true;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mZoomButton
						.setBackgroundResource(R.drawable.position_button_zoom);
			}
			return false;
		}
	}

	class mZoomButtonOnKeyListener implements OnKeyListener {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				mZoomButton
						.setBackgroundResource(R.drawable.position_button_zoom_hl);
				if (mZoomFlag == true) {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_right);
					mRightButton
							.setBackgroundResource(R.drawable.position_button_left);
					mUpButton
							.setBackgroundResource(R.drawable.position_button_down);
					mDownButton
							.setBackgroundResource(R.drawable.position_button_up);
					mZoomFlag = false;
				} else {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_left);
					mRightButton
							.setBackgroundResource(R.drawable.position_button_right);
					mUpButton
							.setBackgroundResource(R.drawable.position_button_up);
					mDownButton
							.setBackgroundResource(R.drawable.position_button_down);
					mZoomFlag = true;
				}
			} else if (event.getAction() == KeyEvent.ACTION_UP
					&& keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				mZoomButton
						.setBackgroundResource(R.drawable.position_button_zoom);
			}
			return false;
		}
	}

	class mLeftButtonOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (mZoomFlag) {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_left_hl);
				} else {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_right_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (mZoomFlag) {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_left);
					if (mCurPosition.left > (-mPrePosition.left)) {
						mCurPosition.left -= ZoomPixel;
						if (mCurPosition.left < (-mPrePosition.left)) {
							mCurPosition.left = -mPrePosition.left;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				} else {
					mLeftButton
							.setBackgroundResource(R.drawable.position_button_right);
					if (mCurPosition.left < (mOutputsize.width_min - mPrePosition.left)) {
						mCurPosition.left += ZoomPixel;
						if (mCurPosition.left > (mOutputsize.width_min - mPrePosition.left)) {
							mCurPosition.left = mOutputsize.width_min
									- mPrePosition.left;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				}
			}
			return false;
		}
	}

	class mRightButtonOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (mZoomFlag) {
					mRightButton
							.setBackgroundResource(R.drawable.position_button_right_hl);
				} else {
					mRightButton
							.setBackgroundResource(R.drawable.position_button_left_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (mZoomFlag) {
					mRightButton
							.setBackgroundResource(R.drawable.position_button_right);
					if (mCurPosition.right < (mOutputsize.width - mPrePosition.left)) {
						mCurPosition.right += ZoomPixel;
						if (mCurPosition.right > (mOutputsize.width - mPrePosition.left)) {
							mCurPosition.right = mOutputsize.width
									- mPrePosition.left;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				} else {
					mRightButton
							.setBackgroundResource(R.drawable.position_button_left);
					if (mCurPosition.right > (mOutputsize.width
							- mPrePosition.left - mOutputsize.width_min)) {
						mCurPosition.right -= ZoomPixel;
						if (mCurPosition.right < (mOutputsize.width
								- mPrePosition.left - mOutputsize.width_min)) {
							mCurPosition.right = mOutputsize.width
									- mPrePosition.left - mOutputsize.width_min;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				}
			}
			return false;
		}
	}

	class mUpButtonOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (mZoomFlag) {
					mUpButton
							.setBackgroundResource(R.drawable.position_button_up_hl);
				} else {
					mUpButton
							.setBackgroundResource(R.drawable.position_button_down_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (mZoomFlag) {
					mUpButton
							.setBackgroundResource(R.drawable.position_button_up);
					if (mCurPosition.top > (-mPrePosition.top)) {
						mCurPosition.top -= ZoomPixel;
						if (mCurPosition.top < (-mPrePosition.top)) {
							mCurPosition.top = -mPrePosition.top;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				} else {
					mUpButton
							.setBackgroundResource(R.drawable.position_button_down);
					if (mCurPosition.top < (mOutputsize.height_min - mPrePosition.top)) {
						mCurPosition.top += ZoomPixel;
						if (mCurPosition.top > (mOutputsize.height_min - mPrePosition.top)) {
							mCurPosition.top = mOutputsize.height_min
									- mPrePosition.top;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				}
			}
			return false;
		}
	}

	class mDownButtonOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (mZoomFlag) {
					mDownButton
							.setBackgroundResource(R.drawable.position_button_down_hl);
				} else {
					mDownButton
							.setBackgroundResource(R.drawable.position_button_up_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (mZoomFlag) {
					mDownButton
							.setBackgroundResource(R.drawable.position_button_down);
					if (mCurPosition.bottom < (mOutputsize.height - mPrePosition.top)) {
						mCurPosition.bottom += ZoomPixel;
						if (mCurPosition.bottom > (mOutputsize.height - mPrePosition.top)) {
							mCurPosition.bottom = mOutputsize.height
									- mPrePosition.top;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				} else {
					mDownButton
							.setBackgroundResource(R.drawable.position_button_up);
					if (mCurPosition.bottom > (mOutputsize.height
							- mPrePosition.top - mOutputsize.height_min)) {
						mCurPosition.bottom -= ZoomPixel;
						if (mCurPosition.bottom < (mOutputsize.height
								- mPrePosition.top - mOutputsize.height_min)) {
							mCurPosition.bottom = mOutputsize.height
									- mPrePosition.top - mOutputsize.height_min;
						}
						setPosition(mCurPosition.left, mCurPosition.top,
								mCurPosition.right, mCurPosition.bottom, 0);
					}
				}
			}
			return false;
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent msg) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (mZoomFlag == true) {
				mZoomButton
						.setBackgroundResource(R.drawable.position_button_zoom);
				mLeftButton
						.setBackgroundResource(R.drawable.position_button_left);
				mRightButton
						.setBackgroundResource(R.drawable.position_button_right);
				mUpButton.setBackgroundResource(R.drawable.position_button_up);
				mDownButton
						.setBackgroundResource(R.drawable.position_button_down);
			} else {
				mZoomButton
						.setBackgroundResource(R.drawable.position_button_zoom);
				mLeftButton
						.setBackgroundResource(R.drawable.position_button_right);
				mRightButton
						.setBackgroundResource(R.drawable.position_button_left);
				mUpButton
						.setBackgroundResource(R.drawable.position_button_down);
				mDownButton
						.setBackgroundResource(R.drawable.position_button_up);
			}
			break;
		}
		return true;
	}

	private void backToParentActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt(SettingsCommon.KEY_SELECTED_ITEM, mSelectedItemPosition);
		intent.setClass(this, GDVideoSettingsActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		Log.d(TAG, " onKeyDown " + keyCode);
		
		mZoomButton.requestFocus();
		mZoomButton.requestFocusFromTouch();
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU 
				|| keyCode == KeyEvent.KEYCODE_ESCAPE) {
			int x, y;
			x = mCurPosition.left + mPrePosition.left;
			if (x < 0)
				x = 0;
			y = mCurPosition.top + mPrePosition.top;
			if (y < 0)
				y = 0;
			mCurPosition.width = mCurPosition.right - mCurPosition.left + 1;
			mCurPosition.height = mCurPosition.bottom - mCurPosition.top + 1;
			if ((mCurPosition.width % 2) == 1) {
				mCurPosition.width--;
			}
			if ((mCurPosition.height % 2) == 1) {
				mCurPosition.height--;
			}
			if ((String.valueOf(x).equals(mPreOutputX))
					&& (String.valueOf(y).equals(mPreOutputY))
					&& (String.valueOf(mCurPosition.width)
							.equals(mPreOutputWidth))
					&& (String.valueOf(mCurPosition.height)
							.equals(mPreOutputHeight))) {
				backToParentActivity();
			} else {
				Intent intent = new Intent(PositionSetting.this,
						DisplayPositionSetConfirm.class);
				Bundle bundle = new Bundle();
				bundle.putInt("get_operation", GET_USER_OPERATION);
				intent.putExtras(bundle);
				startActivityForResult(intent, GET_USER_OPERATION);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (mZoomFlag) {
				mUpButton
						.setBackgroundResource(R.drawable.position_button_up_hl);
				if (mCurPosition.top > (-mPrePosition.top)) {
					mCurPosition.top -= ZoomPixel;
					if (mCurPosition.top < (-mPrePosition.top)) {
						mCurPosition.top = -mPrePosition.top;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			} else {
				mUpButton
						.setBackgroundResource(R.drawable.position_button_down_hl);
				if (mCurPosition.top < (mOutputsize.height_min - mPrePosition.top)) {
					mCurPosition.top += ZoomPixel;
					if (mCurPosition.top > (mOutputsize.height_min - mPrePosition.top)) {
						mCurPosition.top = mOutputsize.height_min
								- mPrePosition.top;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if (mZoomFlag) {
				mDownButton
						.setBackgroundResource(R.drawable.position_button_down_hl);
				if (mCurPosition.bottom < (mOutputsize.height - mPrePosition.top)) {
					mCurPosition.bottom += ZoomPixel;
					if (mCurPosition.bottom > (mOutputsize.height - mPrePosition.top)) {
						mCurPosition.bottom = mOutputsize.height
								- mPrePosition.top;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			} else {
				mDownButton
						.setBackgroundResource(R.drawable.position_button_up_hl);
				if (mCurPosition.bottom > (mOutputsize.height
						- mPrePosition.top - mOutputsize.height_min)) {
					mCurPosition.bottom -= ZoomPixel;
					if (mCurPosition.bottom < (mOutputsize.height
							- mPrePosition.top - mOutputsize.height_min)) {
						mCurPosition.bottom = mOutputsize.height
								- mPrePosition.top - mOutputsize.height_min;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (mZoomFlag) {
				mLeftButton
						.setBackgroundResource(R.drawable.position_button_left_hl);
				if (mCurPosition.left > (-mPrePosition.left)) {
					mCurPosition.left -= ZoomPixel;
					if (mCurPosition.left < (-mPrePosition.left)) {
						mCurPosition.left = -mPrePosition.left;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			} else {
				mLeftButton
						.setBackgroundResource(R.drawable.position_button_right_hl);
				if (mCurPosition.left < (mOutputsize.width_min - mPrePosition.left)) {
					mCurPosition.left += ZoomPixel;
					if (mCurPosition.left > (mOutputsize.width_min - mPrePosition.left)) {
						mCurPosition.left = mOutputsize.width_min
								- mPrePosition.left;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (mZoomFlag) {
				mRightButton
						.setBackgroundResource(R.drawable.position_button_right_hl);
				if (mCurPosition.right < (mOutputsize.width - mPrePosition.left)) {
					mCurPosition.right += ZoomPixel;
					if (mCurPosition.right > (mOutputsize.width - mPrePosition.left)) {
						mCurPosition.right = mOutputsize.width
								- mPrePosition.left;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			} else {
				mRightButton
						.setBackgroundResource(R.drawable.position_button_left_hl);
				if (mCurPosition.right > (mOutputsize.width - mPrePosition.left - mOutputsize.width_min)) {
					mCurPosition.right -= ZoomPixel;
					if (mCurPosition.right < (mOutputsize.width
							- mPrePosition.left - mOutputsize.width_min)) {
						mCurPosition.right = mOutputsize.width
								- mPrePosition.left - mOutputsize.width_min;
					}
					setPosition(mCurPosition.left, mCurPosition.top,
							mCurPosition.right, mCurPosition.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			mZoomButton
					.setBackgroundResource(R.drawable.position_button_zoom_hl);
			if (mZoomFlag == true) {
				mLeftButton
						.setBackgroundResource(R.drawable.position_button_right);
				mRightButton
						.setBackgroundResource(R.drawable.position_button_left);
				mUpButton
						.setBackgroundResource(R.drawable.position_button_down);
				mDownButton
						.setBackgroundResource(R.drawable.position_button_up);
				mZoomFlag = false;
			} else {
				mLeftButton
						.setBackgroundResource(R.drawable.position_button_left);
				mRightButton
						.setBackgroundResource(R.drawable.position_button_right);
				mUpButton.setBackgroundResource(R.drawable.position_button_up);
				mDownButton
						.setBackgroundResource(R.drawable.position_button_down);
				mZoomFlag = true;
			}
		}
		return true;
	}

	private class PositionCoor {
		private int left;
		private int top;
		private int right;
		private int bottom;
		private int width;
		private int height;
	}

	private class Outputsize {
		private int width_min;
		private int height_min;
		private int width_max;
		private int height_max;
		private int width;
		private int height;
	}

	private void setPosition(int l, int t, int r, int b, int mode) {
		Intent intent_output_position = new Intent(
				SettingsCommon.ACTION_OUTPUTPOSITION_CHANGE);
		intent_output_position.putExtra(OUTPUT_POSITION_X, l);
		intent_output_position.putExtra(OUTPUT_POSITION_Y, t);
		intent_output_position.putExtra(OUTPUT_POSITION_W, r);
		intent_output_position.putExtra(OUTPUT_POSITION_H, b);
		intent_output_position.putExtra(OUTPUT_POSITION_MODE, mode);
		PositionSetting.this.sendBroadcast(intent_output_position);
	}

	public void writeFile(String file, String value) {
		File OutputFile = new File(file);
		if (!OutputFile.exists()) {
			return;
		}
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile),
					32);
			try {
				Log.d(TAG, "set" + file + ": " + value);
				out.write(value);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write " + OutputFile);
		}
	}

	private void getOutputsize() {
		if ((mCurOutputMode.equals(mOutputModeArray[0]))
				|| (mCurOutputMode.equals(mOutputModeArray[1]))) {
			mOutputsize.width_min = (int) (720 * SizeChangeStep);
			mOutputsize.width_max = (int) (720 * (1 + SizeChangeStep));
			mOutputsize.width = OUTPUT480_FULL_WIDTH;
			mOutputsize.height_min = (int) (480 * SizeChangeStep);
			mOutputsize.height_max = (int) (480 * (1 + SizeChangeStep));
			mOutputsize.height = OUTPUT480_FULL_HEIGHT;
		} else if ((mCurOutputMode.equals(mOutputModeArray[2]))
				|| (mCurOutputMode.equals(mOutputModeArray[3]))) {
			mOutputsize.width_min = (int) (720 * SizeChangeStep);
			mOutputsize.width_max = (int) (720 * (1 + SizeChangeStep));
			mOutputsize.width = OUTPUT576_FULL_WIDTH;
			mOutputsize.height_min = (int) (576 * SizeChangeStep);
			mOutputsize.height_max = (int) (576 * (1 + SizeChangeStep));
			mOutputsize.height = OUTPUT576_FULL_HEIGHT;
		} else if (mCurOutputMode.equals(mOutputModeArray[4])) {
			mOutputsize.width_min = (int) (1280 * SizeChangeStep);
			mOutputsize.width_max = (int) (1280 * (1 + SizeChangeStep));
			mOutputsize.width = OUTPUT720_FULL_WIDTH;
			mOutputsize.height_min = (int) (720 * SizeChangeStep);
			mOutputsize.height_max = (int) (720 * (1 + SizeChangeStep));
			mOutputsize.height = OUTPUT720_FULL_HEIGHT;
		} else if ((mCurOutputMode.equals(mOutputModeArray[5]))
				|| (mCurOutputMode.equals(mOutputModeArray[6]))) {
			mOutputsize.width_min = (int) (1920 * SizeChangeStep);
			mOutputsize.width_max = (int) (1920 * (1 + SizeChangeStep));
			mOutputsize.width = OUTPUT1080_FULL_WIDTH;
			mOutputsize.height_min = (int) (1080 * SizeChangeStep);
			mOutputsize.height_max = (int) (1080 * (1 + SizeChangeStep));
			mOutputsize.height = OUTPUT1080_FULL_HEIGHT;
		}
	}

	private void getOutput(String mode) {
		if (mode.equals(mOutputModeArray[0])) {
			mPreOutputX = SystemProperties.get(sel_480ioutput_x);
			mPreOutputY = SystemProperties.get(sel_480ioutput_y);
			mPreOutputWidth = SystemProperties.get(sel_480ioutput_width);
			mPreOutputHeight = SystemProperties.get(sel_480ioutput_height);
			if (mPreOutputX.equals(""))
				mPreOutputX = "0";
			if (mPreOutputY.equals(""))
				mPreOutputY = "0";
			if (mPreOutputWidth.equals(""))
				mPreOutputWidth = String.valueOf(OUTPUT480_FULL_WIDTH);
			if (mPreOutputHeight.equals(""))
				mPreOutputHeight = String.valueOf(OUTPUT480_FULL_HEIGHT);
		} else if (mode.equals(mOutputModeArray[1])) {
			mPreOutputX = SystemProperties.get(sel_480poutput_x);
			mPreOutputY = SystemProperties.get(sel_480poutput_y);
			mPreOutputWidth = SystemProperties.get(sel_480poutput_width);
			mPreOutputHeight = SystemProperties.get(sel_480poutput_height);
			if (mPreOutputX.equals(""))
				mPreOutputX = "0";
			if (mPreOutputY.equals(""))
				mPreOutputY = "0";
			if (mPreOutputWidth.equals(""))
				mPreOutputWidth = String.valueOf(OUTPUT480_FULL_WIDTH);
			if (mPreOutputHeight.equals(""))
				mPreOutputHeight = String.valueOf(OUTPUT480_FULL_HEIGHT);
		} else if (mode.equals(mOutputModeArray[2])) {
			mPreOutputX = SystemProperties.get(sel_576ioutput_x);
			mPreOutputY = SystemProperties.get(sel_576ioutput_y);
			mPreOutputWidth = SystemProperties.get(sel_576ioutput_width);
			mPreOutputHeight = SystemProperties.get(sel_576ioutput_height);
			if (mPreOutputX.equals(""))
				mPreOutputX = "0";
			if (mPreOutputY.equals(""))
				mPreOutputY = "0";
			if (mPreOutputWidth.equals(""))
				mPreOutputWidth = String.valueOf(OUTPUT576_FULL_WIDTH);
			if (mPreOutputHeight.equals(""))
				mPreOutputHeight = String.valueOf(OUTPUT576_FULL_HEIGHT);
		} else if (mode.equals(mOutputModeArray[3])) {
			mPreOutputX = SystemProperties.get(sel_576poutput_x);
			mPreOutputY = SystemProperties.get(sel_576poutput_y);
			mPreOutputWidth = SystemProperties.get(sel_576poutput_width);
			mPreOutputHeight = SystemProperties.get(sel_576poutput_height);
			if (mPreOutputX.equals(""))
				mPreOutputX = "0";
			if (mPreOutputY.equals(""))
				mPreOutputY = "0";
			if (mPreOutputWidth.equals(""))
				mPreOutputWidth = String.valueOf(OUTPUT576_FULL_WIDTH);
			if (mPreOutputHeight.equals(""))
				mPreOutputHeight = String.valueOf(OUTPUT576_FULL_HEIGHT);
		} else if (mode.equals(mOutputModeArray[4])) {
			mPreOutputX = SystemProperties.get(sel_720poutput_x);
			mPreOutputY = SystemProperties.get(sel_720poutput_y);
			mPreOutputWidth = SystemProperties.get(sel_720poutput_width);
			mPreOutputHeight = SystemProperties.get(sel_720poutput_height);
			if (mPreOutputX.equals(""))
				mPreOutputX = "0";
			if (mPreOutputY.equals(""))
				mPreOutputY = "0";
			if (mPreOutputWidth.equals(""))
				mPreOutputWidth = String.valueOf(OUTPUT720_FULL_WIDTH);
			if (mPreOutputHeight.equals(""))
				mPreOutputHeight = String.valueOf(OUTPUT720_FULL_HEIGHT);
		} else if (mode.equals(mOutputModeArray[5])) {
			mPreOutputX = SystemProperties.get(sel_1080ioutput_x);
			mPreOutputY = SystemProperties.get(sel_1080ioutput_y);
			mPreOutputWidth = SystemProperties.get(sel_1080ioutput_width);
			mPreOutputHeight = SystemProperties.get(sel_1080ioutput_height);
			if (mPreOutputX.equals(""))
				mPreOutputX = "0";
			if (mPreOutputY.equals(""))
				mPreOutputY = "0";
			if (mPreOutputWidth.equals(""))
				mPreOutputWidth = String.valueOf(OUTPUT1080_FULL_WIDTH);
			if (mPreOutputHeight.equals(""))
				mPreOutputHeight = String.valueOf(OUTPUT1080_FULL_HEIGHT);
		} else if (mode.equals(mOutputModeArray[6])) {
			mPreOutputX = SystemProperties.get(sel_1080poutput_x);
			mPreOutputY = SystemProperties.get(sel_1080poutput_y);
			mPreOutputWidth = SystemProperties.get(sel_1080poutput_width);
			mPreOutputHeight = SystemProperties.get(sel_1080poutput_height);
			if (mPreOutputX.equals(""))
				mPreOutputX = "0";
			if (mPreOutputY.equals(""))
				mPreOutputY = "0";
			if (mPreOutputWidth.equals(""))
				mPreOutputWidth = String.valueOf(OUTPUT1080_FULL_WIDTH);
			if (mPreOutputHeight.equals(""))
				mPreOutputHeight = String.valueOf(OUTPUT1080_FULL_HEIGHT);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		SystemProperties.set("vplayer.hideStatusBar.enable", "true");
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		SystemProperties.set("vplayer.hideStatusBar.enable", "false");
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		int x, y;
		x = mCurPosition.left + mPrePosition.left;
		if (x < 0)
			x = 0;
		y = mCurPosition.top + mPrePosition.top;
		if (y < 0)
			y = 0;
		if ((mCurPosition.width % 2) == 1) {
			mCurPosition.width--;
		}
		if ((mCurPosition.height % 2) == 1) {
			mCurPosition.height--;
		}
		switch (requestCode) {
		case (GET_USER_OPERATION):
			if (resultCode == Activity.RESULT_OK) {
				try {
					Intent intent_output_position = new Intent(
							SettingsCommon.ACTION_OUTPUTPOSITION_SAVE);
					intent_output_position.putExtra(OUTPUT_POSITION_X, x);
					intent_output_position.putExtra(OUTPUT_POSITION_Y, y);
					intent_output_position.putExtra(OUTPUT_POSITION_W,
							mCurPosition.width);
					intent_output_position.putExtra(OUTPUT_POSITION_H,
							mCurPosition.height);
					PositionSetting.this.sendBroadcast(intent_output_position);
					Log.i(TAG, "--------------------------------position Set");
					Log.d(TAG,
							"--------------------------------set display axis x = "
									+ x);
					Log.d(TAG,
							"--------------------------------set display axis y = "
									+ y);
					Log.d(TAG,
							"--------------------------------set display axis width = "
									+ mCurPosition.width);
					Log.d(TAG,
							"--------------------------------set display axis height = "
									+ mCurPosition.height);

					backToParentActivity();
				} catch (Exception e) {
					Log.i(TAG,
							"--------------------------------setOutput_position No set");
					Log.e(TAG,
							"Exception Occured: Trying to add set setflag : "
									+ e.toString());

					backToParentActivity();
					Log.e(TAG, "Finishing the Application");
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Intent intent_output_position = new Intent(
						SettingsCommon.ACTION_OUTPUTPOSITION_CANCEL);
				PositionSetting.this.sendBroadcast(intent_output_position);

				backToParentActivity();
				Log.i(TAG, "----------------------no");
			}
			break;
		case (GET_DEFAULT_OPERATION):
			if (resultCode == Activity.RESULT_OK) {
				try {
					Intent intent_output_position = new Intent(
							SettingsCommon.ACTION_OUTPUTPOSITION_DEFAULT_SAVE);
					PositionSetting.this.sendBroadcast(intent_output_position);
					Log.i(TAG,
							"--------------------------------default position Set");

					backToParentActivity();
				} catch (Exception e) {
					Log.i(TAG,
							"--------------------------------setOutput_position No set");
					Log.e(TAG,
							"Exception Occured: Trying to add set setflag : "
									+ e.toString());

					backToParentActivity();
					Log.e(TAG, "Finishing the Application");
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Intent intent_output_position = new
				// Intent(ACTION_OUTPUTPOSITION_CANCEL);
				// PositionSetting.this.sendBroadcast(intent_output_position);

				backToParentActivity();
				Log.i(TAG, "----------------------no");
			}
			break;
		}
	}
}