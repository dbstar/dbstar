package com.dbstar.settings.display;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	private static int zoom_pixel = 2;
	private boolean zoom_flag = false; // zoom_flag is true: zoom in;zoom_flag
										// is false: zoom out
	private static double outputsize_per = 0.08;
	private Outputsize outputsize = new Outputsize();
	private PositionCoor position_per = new PositionCoor();
	private PositionCoor position_cur = new PositionCoor();
	private int selectedItemPosition;


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
	private String curOutputmode = "";
	private String pre_output_x = "";
	private String pre_output_y = "";
	private String pre_output_width = "";
	private String pre_output_height = "";
	private String[] outputmode_array;

	private ImageButton mchangeZoomBtn;
	private ImageButton mleftBtn;
	private ImageButton mrightBtn;
	private ImageButton mtopBtn;
	private ImageButton mbottomBtn;
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

		mchangeZoomBtn = (ImageButton) findViewById(R.id.btn_position_changeZoom);
		mchangeZoomBtn
				.setOnTouchListener(new mpositionChangeZoomBtnOnTouchistener());
		mchangeZoomBtn
				.setOnKeyListener(new mpositionChangeZoomBtnOnKeyistener());
		mleftBtn = (ImageButton) findViewById(R.id.btn_position_left);
		mleftBtn.setOnTouchListener(new mpositionLeftBtnOnTouchListener());
		mrightBtn = (ImageButton) findViewById(R.id.btn_position_right);
		mrightBtn.setOnTouchListener(new mpositionRightBtnOnTouchListener());
		mtopBtn = (ImageButton) findViewById(R.id.btn_position_top);
		mtopBtn.setOnTouchListener(new mpositionTopBtnOnTouchListener());
		mbottomBtn = (ImageButton) findViewById(R.id.btn_position_bottom);
		mbottomBtn.setOnTouchListener(new mpositionBottomBtnOnTouchListener());

		curOutputmode = SystemProperties.get(STR_OUTPUT_MODE);
		outputmode_array = getResources().getStringArray(
				R.array.position_entries);
		getOutputsize();
		getOutput(curOutputmode);

		position_per.width = 0;
		position_per.height = 0;
		position_per.left = 0;
		position_per.top = 0;
		position_per.right = 0;
		position_per.bottom = 0;
		position_cur.width = Integer.valueOf(pre_output_width).intValue();
		position_cur.height = Integer.valueOf(pre_output_height).intValue();
		position_cur.left = Integer.valueOf(pre_output_x).intValue();
		position_cur.top = Integer.valueOf(pre_output_y).intValue();
		position_cur.right = position_cur.width + position_cur.left - 1;
		position_cur.bottom = position_cur.height + position_cur.top - 1;

		writeFile(FreeScaleOsd0File, "1");
		writeFile(FreeScaleOsd1File, "1");

		try {
			Bundle bundle = new Bundle();
			bundle = this.getIntent().getExtras();
			selectedItemPosition = bundle.getInt("selectedItemPosition");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class mpositionChangeZoomBtnOnTouchistener implements OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mchangeZoomBtn
						.setBackgroundResource(R.drawable.position_button_zoom_hl);
				if (zoom_flag == true) {
					mleftBtn.setBackgroundResource(R.drawable.position_button_right);
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_left);
					mtopBtn.setBackgroundResource(R.drawable.position_button_down);
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_up);
					zoom_flag = false;
				} else {
					mleftBtn.setBackgroundResource(R.drawable.position_button_left);
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_right);
					mtopBtn.setBackgroundResource(R.drawable.position_button_up);
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_down);
					zoom_flag = true;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mchangeZoomBtn
						.setBackgroundResource(R.drawable.position_button_zoom);
			}
			return false;
		}
	}

	class mpositionChangeZoomBtnOnKeyistener implements OnKeyListener {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				mchangeZoomBtn
						.setBackgroundResource(R.drawable.position_button_zoom_hl);
				if (zoom_flag == true) {
					mleftBtn.setBackgroundResource(R.drawable.position_button_right);
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_left);
					mtopBtn.setBackgroundResource(R.drawable.position_button_down);
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_up);
					zoom_flag = false;
				} else {
					mleftBtn.setBackgroundResource(R.drawable.position_button_left);
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_right);
					mtopBtn.setBackgroundResource(R.drawable.position_button_up);
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_down);
					zoom_flag = true;
				}
			} else if (event.getAction() == KeyEvent.ACTION_UP
					&& keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
				mchangeZoomBtn
						.setBackgroundResource(R.drawable.position_button_zoom);
			}
			return false;
		}
	}

	class mpositionLeftBtnOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (zoom_flag) {
					mleftBtn.setBackgroundResource(R.drawable.position_button_left_hl);
				} else {
					mleftBtn.setBackgroundResource(R.drawable.position_button_right_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (zoom_flag) {
					mleftBtn.setBackgroundResource(R.drawable.position_button_left);
					if (position_cur.left > (-position_per.left)) {
						position_cur.left -= zoom_pixel;
						if (position_cur.left < (-position_per.left)) {
							position_cur.left = -position_per.left;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
					}
				} else {
					mleftBtn.setBackgroundResource(R.drawable.position_button_right);
					if (position_cur.left < (outputsize.width_min - position_per.left)) {
						position_cur.left += zoom_pixel;
						if (position_cur.left > (outputsize.width_min - position_per.left)) {
							position_cur.left = outputsize.width_min
									- position_per.left;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
					}
				}
			}
			return false;
		}
	}

	class mpositionRightBtnOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (zoom_flag) {
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_right_hl);
				} else {
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_left_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (zoom_flag) {
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_right);
					if (position_cur.right < (outputsize.width - position_per.left)) {
						position_cur.right += zoom_pixel;
						if (position_cur.right > (outputsize.width - position_per.left)) {
							position_cur.right = outputsize.width
									- position_per.left;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
					}
				} else {
					mrightBtn
							.setBackgroundResource(R.drawable.position_button_left);
					if (position_cur.right > (outputsize.width
							- position_per.left - outputsize.width_min)) {
						position_cur.right -= zoom_pixel;
						if (position_cur.right < (outputsize.width
								- position_per.left - outputsize.width_min)) {
							position_cur.right = outputsize.width
									- position_per.left - outputsize.width_min;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
					}
				}
			}
			return false;
		}
	}

	class mpositionTopBtnOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (zoom_flag) {
					mtopBtn.setBackgroundResource(R.drawable.position_button_up_hl);
				} else {
					mtopBtn.setBackgroundResource(R.drawable.position_button_down_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (zoom_flag) {
					mtopBtn.setBackgroundResource(R.drawable.position_button_up);
					if (position_cur.top > (-position_per.top)) {
						position_cur.top -= zoom_pixel;
						if (position_cur.top < (-position_per.top)) {
							position_cur.top = -position_per.top;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
					}
				} else {
					mtopBtn.setBackgroundResource(R.drawable.position_button_down);
					if (position_cur.top < (outputsize.height_min - position_per.top)) {
						position_cur.top += zoom_pixel;
						if (position_cur.top > (outputsize.height_min - position_per.top)) {
							position_cur.top = outputsize.height_min
									- position_per.top;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
					}
				}
			}
			return false;
		}
	}

	class mpositionBottomBtnOnTouchListener implements OnTouchListener {
		// @Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (zoom_flag) {
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_down_hl);
				} else {
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_up_hl);
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (zoom_flag) {
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_down);
					if (position_cur.bottom < (outputsize.height - position_per.top)) {
						position_cur.bottom += zoom_pixel;
						if (position_cur.bottom > (outputsize.height - position_per.top)) {
							position_cur.bottom = outputsize.height
									- position_per.top;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
					}
				} else {
					mbottomBtn
							.setBackgroundResource(R.drawable.position_button_up);
					if (position_cur.bottom > (outputsize.height
							- position_per.top - outputsize.height_min)) {
						position_cur.bottom -= zoom_pixel;
						if (position_cur.bottom < (outputsize.height
								- position_per.top - outputsize.height_min)) {
							position_cur.bottom = outputsize.height
									- position_per.top - outputsize.height_min;
						}
						setPosition(position_cur.left, position_cur.top,
								position_cur.right, position_cur.bottom, 0);
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
			if (zoom_flag == true) {
				mchangeZoomBtn
						.setBackgroundResource(R.drawable.position_button_zoom);
				mleftBtn.setBackgroundResource(R.drawable.position_button_left);
				mrightBtn
						.setBackgroundResource(R.drawable.position_button_right);
				mtopBtn.setBackgroundResource(R.drawable.position_button_up);
				mbottomBtn
						.setBackgroundResource(R.drawable.position_button_down);
			} else {
				mchangeZoomBtn
						.setBackgroundResource(R.drawable.position_button_zoom);
				mleftBtn.setBackgroundResource(R.drawable.position_button_right);
				mrightBtn
						.setBackgroundResource(R.drawable.position_button_left);
				mtopBtn.setBackgroundResource(R.drawable.position_button_down);
				mbottomBtn.setBackgroundResource(R.drawable.position_button_up);
			}
			break;
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		mchangeZoomBtn.requestFocus();
		mchangeZoomBtn.requestFocusFromTouch();
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			int x, y;
			x = position_cur.left + position_per.left;
			if (x < 0)
				x = 0;
			y = position_cur.top + position_per.top;
			if (y < 0)
				y = 0;
			position_cur.width = position_cur.right - position_cur.left + 1;
			position_cur.height = position_cur.bottom - position_cur.top + 1;
			if ((position_cur.width % 2) == 1) {
				position_cur.width--;
			}
			if ((position_cur.height % 2) == 1) {
				position_cur.height--;
			}
			if ((String.valueOf(x).equals(pre_output_x))
					&& (String.valueOf(y).equals(pre_output_y))
					&& (String.valueOf(position_cur.width)
							.equals(pre_output_width))
					&& (String.valueOf(position_cur.height)
							.equals(pre_output_height))) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("selectedItemPosition", selectedItemPosition);
				intent.setClass(PositionSetting.this, DisplaySettings.class);
				intent.putExtras(bundle);
				startActivity(intent);
				PositionSetting.this.finish();
			} else {
				Intent intent = new Intent(PositionSetting.this,
						DisplayPositionSetConfirm.class);
				Bundle bundle = new Bundle();
				bundle.putInt("get_operation", GET_USER_OPERATION);
				intent.putExtras(bundle);
				startActivityForResult(intent, GET_USER_OPERATION);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (zoom_flag) {
				mtopBtn.setBackgroundResource(R.drawable.position_button_up_hl);
				if (position_cur.top > (-position_per.top)) {
					position_cur.top -= zoom_pixel;
					if (position_cur.top < (-position_per.top)) {
						position_cur.top = -position_per.top;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			} else {
				mtopBtn.setBackgroundResource(R.drawable.position_button_down_hl);
				if (position_cur.top < (outputsize.height_min - position_per.top)) {
					position_cur.top += zoom_pixel;
					if (position_cur.top > (outputsize.height_min - position_per.top)) {
						position_cur.top = outputsize.height_min
								- position_per.top;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if (zoom_flag) {
				mbottomBtn
						.setBackgroundResource(R.drawable.position_button_down_hl);
				if (position_cur.bottom < (outputsize.height - position_per.top)) {
					position_cur.bottom += zoom_pixel;
					if (position_cur.bottom > (outputsize.height - position_per.top)) {
						position_cur.bottom = outputsize.height
								- position_per.top;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			} else {
				mbottomBtn
						.setBackgroundResource(R.drawable.position_button_up_hl);
				if (position_cur.bottom > (outputsize.height - position_per.top - outputsize.height_min)) {
					position_cur.bottom -= zoom_pixel;
					if (position_cur.bottom < (outputsize.height
							- position_per.top - outputsize.height_min)) {
						position_cur.bottom = outputsize.height
								- position_per.top - outputsize.height_min;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			if (zoom_flag) {
				mleftBtn.setBackgroundResource(R.drawable.position_button_left_hl);
				if (position_cur.left > (-position_per.left)) {
					position_cur.left -= zoom_pixel;
					if (position_cur.left < (-position_per.left)) {
						position_cur.left = -position_per.left;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			} else {
				mleftBtn.setBackgroundResource(R.drawable.position_button_right_hl);
				if (position_cur.left < (outputsize.width_min - position_per.left)) {
					position_cur.left += zoom_pixel;
					if (position_cur.left > (outputsize.width_min - position_per.left)) {
						position_cur.left = outputsize.width_min
								- position_per.left;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			if (zoom_flag) {
				mrightBtn
						.setBackgroundResource(R.drawable.position_button_right_hl);
				if (position_cur.right < (outputsize.width - position_per.left)) {
					position_cur.right += zoom_pixel;
					if (position_cur.right > (outputsize.width - position_per.left)) {
						position_cur.right = outputsize.width
								- position_per.left;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			} else {
				mrightBtn
						.setBackgroundResource(R.drawable.position_button_left_hl);
				if (position_cur.right > (outputsize.width - position_per.left - outputsize.width_min)) {
					position_cur.right -= zoom_pixel;
					if (position_cur.right < (outputsize.width
							- position_per.left - outputsize.width_min)) {
						position_cur.right = outputsize.width
								- position_per.left - outputsize.width_min;
					}
					setPosition(position_cur.left, position_cur.top,
							position_cur.right, position_cur.bottom, 0);
				}
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			mchangeZoomBtn
					.setBackgroundResource(R.drawable.position_button_zoom_hl);
			if (zoom_flag == true) {
				mleftBtn.setBackgroundResource(R.drawable.position_button_right);
				mrightBtn
						.setBackgroundResource(R.drawable.position_button_left);
				mtopBtn.setBackgroundResource(R.drawable.position_button_down);
				mbottomBtn.setBackgroundResource(R.drawable.position_button_up);
				zoom_flag = false;
			} else {
				mleftBtn.setBackgroundResource(R.drawable.position_button_left);
				mrightBtn
						.setBackgroundResource(R.drawable.position_button_right);
				mtopBtn.setBackgroundResource(R.drawable.position_button_up);
				mbottomBtn
						.setBackgroundResource(R.drawable.position_button_down);
				zoom_flag = true;
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
		Intent intent_output_position = new Intent(SettingsCommon.ACTION_OUTPUTPOSITION_CHANGE);
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
		if ((curOutputmode.equals(outputmode_array[0]))
				|| (curOutputmode.equals(outputmode_array[1]))) {
			outputsize.width_min = (int) (720 * outputsize_per);
			outputsize.width_max = (int) (720 * (1 + outputsize_per));
			outputsize.width = OUTPUT480_FULL_WIDTH;
			outputsize.height_min = (int) (480 * outputsize_per);
			outputsize.height_max = (int) (480 * (1 + outputsize_per));
			outputsize.height = OUTPUT480_FULL_HEIGHT;
		} else if ((curOutputmode.equals(outputmode_array[2]))
				|| (curOutputmode.equals(outputmode_array[3]))) {
			outputsize.width_min = (int) (720 * outputsize_per);
			outputsize.width_max = (int) (720 * (1 + outputsize_per));
			outputsize.width = OUTPUT576_FULL_WIDTH;
			outputsize.height_min = (int) (576 * outputsize_per);
			outputsize.height_max = (int) (576 * (1 + outputsize_per));
			outputsize.height = OUTPUT576_FULL_HEIGHT;
		} else if (curOutputmode.equals(outputmode_array[4])) {
			outputsize.width_min = (int) (1280 * outputsize_per);
			outputsize.width_max = (int) (1280 * (1 + outputsize_per));
			outputsize.width = OUTPUT720_FULL_WIDTH;
			outputsize.height_min = (int) (720 * outputsize_per);
			outputsize.height_max = (int) (720 * (1 + outputsize_per));
			outputsize.height = OUTPUT720_FULL_HEIGHT;
		} else if ((curOutputmode.equals(outputmode_array[5]))
				|| (curOutputmode.equals(outputmode_array[6]))) {
			outputsize.width_min = (int) (1920 * outputsize_per);
			outputsize.width_max = (int) (1920 * (1 + outputsize_per));
			outputsize.width = OUTPUT1080_FULL_WIDTH;
			outputsize.height_min = (int) (1080 * outputsize_per);
			outputsize.height_max = (int) (1080 * (1 + outputsize_per));
			outputsize.height = OUTPUT1080_FULL_HEIGHT;
		}
	}

	private void getOutput(String get_outputmode) {
		if (get_outputmode.equals(outputmode_array[0])) {
			pre_output_x = SystemProperties.get(sel_480ioutput_x);
			pre_output_y = SystemProperties.get(sel_480ioutput_y);
			pre_output_width = SystemProperties.get(sel_480ioutput_width);
			pre_output_height = SystemProperties.get(sel_480ioutput_height);
			if (pre_output_x.equals(""))
				pre_output_x = "0";
			if (pre_output_y.equals(""))
				pre_output_y = "0";
			if (pre_output_width.equals(""))
				pre_output_width = String.valueOf(OUTPUT480_FULL_WIDTH);
			if (pre_output_height.equals(""))
				pre_output_height = String.valueOf(OUTPUT480_FULL_HEIGHT);
		} else if (get_outputmode.equals(outputmode_array[1])) {
			pre_output_x = SystemProperties.get(sel_480poutput_x);
			pre_output_y = SystemProperties.get(sel_480poutput_y);
			pre_output_width = SystemProperties.get(sel_480poutput_width);
			pre_output_height = SystemProperties.get(sel_480poutput_height);
			if (pre_output_x.equals(""))
				pre_output_x = "0";
			if (pre_output_y.equals(""))
				pre_output_y = "0";
			if (pre_output_width.equals(""))
				pre_output_width = String.valueOf(OUTPUT480_FULL_WIDTH);
			if (pre_output_height.equals(""))
				pre_output_height = String.valueOf(OUTPUT480_FULL_HEIGHT);
		} else if (get_outputmode.equals(outputmode_array[2])) {
			pre_output_x = SystemProperties.get(sel_576ioutput_x);
			pre_output_y = SystemProperties.get(sel_576ioutput_y);
			pre_output_width = SystemProperties.get(sel_576ioutput_width);
			pre_output_height = SystemProperties.get(sel_576ioutput_height);
			if (pre_output_x.equals(""))
				pre_output_x = "0";
			if (pre_output_y.equals(""))
				pre_output_y = "0";
			if (pre_output_width.equals(""))
				pre_output_width = String.valueOf(OUTPUT576_FULL_WIDTH);
			if (pre_output_height.equals(""))
				pre_output_height = String.valueOf(OUTPUT576_FULL_HEIGHT);
		} else if (get_outputmode.equals(outputmode_array[3])) {
			pre_output_x = SystemProperties.get(sel_576poutput_x);
			pre_output_y = SystemProperties.get(sel_576poutput_y);
			pre_output_width = SystemProperties.get(sel_576poutput_width);
			pre_output_height = SystemProperties.get(sel_576poutput_height);
			if (pre_output_x.equals(""))
				pre_output_x = "0";
			if (pre_output_y.equals(""))
				pre_output_y = "0";
			if (pre_output_width.equals(""))
				pre_output_width = String.valueOf(OUTPUT576_FULL_WIDTH);
			if (pre_output_height.equals(""))
				pre_output_height = String.valueOf(OUTPUT576_FULL_HEIGHT);
		} else if (get_outputmode.equals(outputmode_array[4])) {
			pre_output_x = SystemProperties.get(sel_720poutput_x);
			pre_output_y = SystemProperties.get(sel_720poutput_y);
			pre_output_width = SystemProperties.get(sel_720poutput_width);
			pre_output_height = SystemProperties.get(sel_720poutput_height);
			if (pre_output_x.equals(""))
				pre_output_x = "0";
			if (pre_output_y.equals(""))
				pre_output_y = "0";
			if (pre_output_width.equals(""))
				pre_output_width = String.valueOf(OUTPUT720_FULL_WIDTH);
			if (pre_output_height.equals(""))
				pre_output_height = String.valueOf(OUTPUT720_FULL_HEIGHT);
		} else if (get_outputmode.equals(outputmode_array[5])) {
			pre_output_x = SystemProperties.get(sel_1080ioutput_x);
			pre_output_y = SystemProperties.get(sel_1080ioutput_y);
			pre_output_width = SystemProperties.get(sel_1080ioutput_width);
			pre_output_height = SystemProperties.get(sel_1080ioutput_height);
			if (pre_output_x.equals(""))
				pre_output_x = "0";
			if (pre_output_y.equals(""))
				pre_output_y = "0";
			if (pre_output_width.equals(""))
				pre_output_width = String.valueOf(OUTPUT1080_FULL_WIDTH);
			if (pre_output_height.equals(""))
				pre_output_height = String.valueOf(OUTPUT1080_FULL_HEIGHT);
		} else if (get_outputmode.equals(outputmode_array[6])) {
			pre_output_x = SystemProperties.get(sel_1080poutput_x);
			pre_output_y = SystemProperties.get(sel_1080poutput_y);
			pre_output_width = SystemProperties.get(sel_1080poutput_width);
			pre_output_height = SystemProperties.get(sel_1080poutput_height);
			if (pre_output_x.equals(""))
				pre_output_x = "0";
			if (pre_output_y.equals(""))
				pre_output_y = "0";
			if (pre_output_width.equals(""))
				pre_output_width = String.valueOf(OUTPUT1080_FULL_WIDTH);
			if (pre_output_height.equals(""))
				pre_output_height = String.valueOf(OUTPUT1080_FULL_HEIGHT);
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
		x = position_cur.left + position_per.left;
		if (x < 0)
			x = 0;
		y = position_cur.top + position_per.top;
		if (y < 0)
			y = 0;
		if ((position_cur.width % 2) == 1) {
			position_cur.width--;
		}
		if ((position_cur.height % 2) == 1) {
			position_cur.height--;
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
							position_cur.width);
					intent_output_position.putExtra(OUTPUT_POSITION_H,
							position_cur.height);
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
									+ position_cur.width);
					Log.d(TAG,
							"--------------------------------set display axis height = "
									+ position_cur.height);

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("selectedItemPosition", selectedItemPosition);
					intent.setClass(PositionSetting.this, DisplaySettings.class);
					intent.putExtras(bundle);
					startActivity(intent);
					PositionSetting.this.finish();
				} catch (Exception e) {
					Log.i(TAG,
							"--------------------------------setOutput_position No set");
					Log.e(TAG,
							"Exception Occured: Trying to add set setflag : "
									+ e.toString());
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("selectedItemPosition", selectedItemPosition);
					intent.setClass(PositionSetting.this, DisplaySettings.class);
					intent.putExtras(bundle);
					startActivity(intent);
					PositionSetting.this.finish();
					Log.e(TAG, "Finishing the Application");
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Intent intent_output_position = new Intent(
						SettingsCommon.ACTION_OUTPUTPOSITION_CANCEL);
				PositionSetting.this.sendBroadcast(intent_output_position);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("selectedItemPosition", selectedItemPosition);
				intent.setClass(PositionSetting.this, DisplaySettings.class);
				intent.putExtras(bundle);
				startActivity(intent);
				PositionSetting.this.finish();
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
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("selectedItemPosition", selectedItemPosition);
					intent.setClass(PositionSetting.this, DisplaySettings.class);
					intent.putExtras(bundle);
					startActivity(intent);
					PositionSetting.this.finish();
				} catch (Exception e) {
					Log.i(TAG,
							"--------------------------------setOutput_position No set");
					Log.e(TAG,
							"Exception Occured: Trying to add set setflag : "
									+ e.toString());
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putInt("selectedItemPosition", selectedItemPosition);
					intent.setClass(PositionSetting.this, DisplaySettings.class);
					intent.putExtras(bundle);
					startActivity(intent);
					PositionSetting.this.finish();
					Log.e(TAG, "Finishing the Application");
				}
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// Intent intent_output_position = new
				// Intent(ACTION_OUTPUTPOSITION_CANCEL);
				// PositionSetting.this.sendBroadcast(intent_output_position);
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("selectedItemPosition", selectedItemPosition);
				intent.setClass(PositionSetting.this, DisplaySettings.class);
				intent.putExtras(bundle);
				startActivity(intent);
				PositionSetting.this.finish();
				Log.i(TAG, "----------------------no");
			}
			break;
		}
	}
}