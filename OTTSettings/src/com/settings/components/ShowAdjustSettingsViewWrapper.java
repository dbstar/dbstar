package com.settings.components;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.settings.display.ScreenPositionManager;
import com.settings.ottsettings.R;
import com.settings.utils.LogUtil;
import com.settings.utils.ScreenUtils;

public class ShowAdjustSettingsViewWrapper {
	
	private Context context;
	private TextView txtText;
	private Button btnShowAdjust;
	private LinearLayout container;
	private ImageButton btn_position_zoom_out;
	private ImageButton btn_position_zoom_in;
	private TextView screen_tip_01;
	private ImageView img_num_hundred = null;
	private ImageView img_num_ten = null;
	private ImageView img_num_unit = null;
	private ImageView img_progress_bg;
	
	public static int Num[] = { R.drawable.ic_num0, R.drawable.ic_num1,
		R.drawable.ic_num2, R.drawable.ic_num3, R.drawable.ic_num4,
		R.drawable.ic_num5, R.drawable.ic_num6, R.drawable.ic_num7,
		R.drawable.ic_num8, R.drawable.ic_num9 };
	public static int progressNum[] = { R.drawable.ic_per_81,
		R.drawable.ic_per_82, R.drawable.ic_per_83, R.drawable.ic_per_84,
		R.drawable.ic_per_85, R.drawable.ic_per_86, R.drawable.ic_per_87,
		R.drawable.ic_per_88, R.drawable.ic_per_89, R.drawable.ic_per_90,
		R.drawable.ic_per_91, R.drawable.ic_per_92, R.drawable.ic_per_93,
		R.drawable.ic_per_94, R.drawable.ic_per_95, R.drawable.ic_per_96,
		R.drawable.ic_per_97, R.drawable.ic_per_98, R.drawable.ic_per_99,
		R.drawable.ic_per_100 };
	
	private ScreenPositionManager mScreenPositionManager = null;
	private boolean isOpenAdjustScreenView = false;
    
    private final int MAX_Height = 100;
    private final int MIN_Height = 80;
	
    private int screen_rate = MIN_Height;
    private static int screen_rate_ok;
    
    
	public static int mCurrentContentNum = 0;	
	public final static int VIEW_SCREEN_ADJUST = 4;
	public final static int VIEW_DISPLAY = 1;
	
	private String TAG = "ShowAdjustSettingsViewWrapper";

	public ShowAdjustSettingsViewWrapper(Context context) {
		this.context = context;
	}

	private final static String sel_720poutput_x = "ubootenv.var.720poutputx";
	private final static String sel_720poutput_y = "ubootenv.var.720poutputy";
	private final static String sel_720poutput_width = "ubootenv.var.720poutputwidth";
	private final static String sel_720poutput_height = "ubootenv.var.720poutputheight";
	
	public void initView(View view) {
		findViews(view);
		
		btnShowAdjust.requestFocus();
		
		mScreenPositionManager = new ScreenPositionManager(context);
		
//		screen_rate = mScreenPositionManager.getRateValue();
//        
//        LogUtil.d(TAG, "------initView-----" + screen_rate);
		
		
//		Log.d(TAG, "-----initViews----SystemProperties-----" + SystemProperties.get(sel_720poutput_x));
//		Log.d(TAG, "------initViews----SystemProperties-----" + SystemProperties.get(sel_720poutput_y));
//		Log.d(TAG, "------initViews----SystemProperties-----" + SystemProperties.get(sel_720poutput_width));
//		Log.d(TAG, "------initViews----SystemProperties-----" + SystemProperties.get(sel_720poutput_height));
		
		btnShowAdjust.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openScreenAdjustLayout();
			}
		});
		
	}

	public boolean isOpenAdjustScreenView() {
		return isOpenAdjustScreenView;
	}

	public void setOpenAdjustScreenView(boolean isOpenAdjustScreenView) {
		this.isOpenAdjustScreenView = isOpenAdjustScreenView;
	}

	private void openScreenAdjustLayout() {
		isOpenAdjustScreenView = true ; 
		mCurrentContentNum = VIEW_SCREEN_ADJUST;
		btnShowAdjust.setVisibility(View.GONE);
		txtText.setVisibility(View.GONE);
		container.setVisibility(View.VISIBLE);
		
		BtnonClickListener btnonClickListener = new BtnonClickListener();
        btn_position_zoom_out.setOnClickListener(btnonClickListener);
        btn_position_zoom_in.setOnClickListener(btnonClickListener);
		screen_tip_01.requestFocus();
        screen_tip_01.requestFocusFromTouch();
        
        mScreenPositionManager.initPostion();
        screen_rate = mScreenPositionManager.getRateValue();
        screen_rate_ok = screen_rate;
        
        LogUtil.d(TAG, "----openScreenAdjustLayout--------" + screen_rate);
        
        showProgressUI(0);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (ScreenUtils.DEBUG) Log.d(TAG, "onKeyDown(),keyCode : " + keyCode);
        if (ScreenUtils.DEBUG) Log.d(TAG, "isOpenAdjustScreenView : " + isOpenAdjustScreenView);
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			btn_position_zoom_in.setBackgroundResource(R.drawable.minus_unfocus);
			btn_position_zoom_out.setBackgroundResource(R.drawable.plus_focus);
			if (screen_rate < MAX_Height) {
				if (ScreenUtils.DEBUG)
					Log.d(TAG, "==== zoomIn ,screen_rate=" + screen_rate);
				showProgressUI(1);
				// mScreenPositionManager.zoomIn();
				mScreenPositionManager.zoomByPercent(screen_rate);
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			if (screen_rate > MIN_Height) {
				if (ScreenUtils.DEBUG)
					Log.d(TAG, "==== zoomOut,screen_rate=" + screen_rate);
				showProgressUI(-1);
				// mScreenPositionManager.zoomOut();
				mScreenPositionManager.zoomByPercent(screen_rate);
			}
			
			btn_position_zoom_in.setBackgroundResource(R.drawable.minus_focus);
			btn_position_zoom_out.setBackgroundResource(R.drawable.plus_unfocus);
			
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
//			keyCode == KeyEvent.KEYCODE_BACK || 
			closeScreenAdjustLayout();
			screen_rate_ok = screen_rate;
			Log.d(TAG, "-----ok-------screen_rate = " + screen_rate_ok);
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			return true;
		} else {
			isOpenAdjustScreenView = false ;
			btnShowAdjust.setVisibility(View.VISIBLE);
			txtText.setVisibility(View.VISIBLE);
			container.setVisibility(View.GONE);
			
			btnShowAdjust.requestFocus();
			if(mScreenPositionManager.isScreenPositionChanged()){
				Log.d(TAG, "---back---------screen_rate_ok = " + screen_rate_ok);
				mScreenPositionManager.zoomByPercent(screen_rate_ok);
				ScreenPositionManager.mIsOriginWinSet = false;    //user has changed&save postion,reset this prop to default 
//	            restartActivitySelf();
	        }
			return true;
		}
		return true;

	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (ScreenUtils.DEBUG) Log.d(TAG,"===== onKeyUp(), keyCode : " + keyCode);

//        if (mOutPutModeManager.ifModeIsSetting()){
//            return true;
//        }
		if (mCurrentContentNum == VIEW_SCREEN_ADJUST) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_unfocus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_unfocus);
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				btn_position_zoom_in.setBackgroundResource(R.drawable.minus_unfocus);
				btn_position_zoom_out.setBackgroundResource(R.drawable.plus_unfocus);
			}
			return true;
		} else {
			return false;
		}

//		return super.onKeyUp(keyCode, event);
	}
	
	private void closeScreenAdjustLayout() {
        isOpenAdjustScreenView = false ; 
		
		mCurrentContentNum = VIEW_DISPLAY;
		
		btnShowAdjust.setVisibility(View.VISIBLE);
		txtText.setVisibility(View.VISIBLE);
		container.setVisibility(View.GONE);
		
		btnShowAdjust.requestFocus();
		
		
        if(mScreenPositionManager.isScreenPositionChanged()){
            mScreenPositionManager.savePostion();
            ScreenPositionManager.mIsOriginWinSet = false;    //user has changed&save postion,reset this prop to default 
            //restartActivitySelf();
        }
//       
//        btn_position_zoom_out.setEnabled(false);
//        btn_position_zoom_in.setEnabled(false);
	}
	
	private void showProgressUI(int step) {
        screen_rate = screen_rate + step;
        if(screen_rate > MAX_Height){
            screen_rate = MAX_Height;
        }
        if(screen_rate < MIN_Height){
            screen_rate = MIN_Height ;
        }
        if (ScreenUtils.DEBUG) Log.d(TAG,"===== showProgressUI() ,screen_rate="+ screen_rate);
		if (screen_rate ==100) {
			int hundred = Num[(int) screen_rate / 100];
			img_num_hundred.setVisibility(View.VISIBLE);
			img_num_hundred.setBackgroundResource(hundred);
            int ten = Num[(screen_rate -100)/10] ;
			img_num_ten.setBackgroundResource(ten);
            int unit = Num[(screen_rate -100)%10];
			img_num_unit.setBackgroundResource(unit);
			if (screen_rate - MIN_Height>= 0 && screen_rate - MIN_Height <= 20)
				img_progress_bg.setBackgroundResource(progressNum[screen_rate - MIN_Height-1]);
		} else if (screen_rate >= 10 && screen_rate <= 99) {
			img_num_hundred.setVisibility(View.GONE);
			int ten = Num[(int) (screen_rate / 10)];
			int unit = Num[(int) (screen_rate % 10)];
			img_num_ten.setBackgroundResource(ten);
			img_num_unit.setBackgroundResource(unit);
			if (screen_rate - MIN_Height >= 0 && screen_rate - MIN_Height <= 19)
				img_progress_bg.setBackgroundResource(progressNum[screen_rate - MIN_Height]);
		} else if (screen_rate >= 0 && screen_rate <= 9) {
			int unit = Num[screen_rate];
			img_num_unit.setBackgroundResource(unit);
		}

	}

	private class BtnonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v instanceof ImageButton) {
				int id = v.getId();
				if (id == R.id.btn_position_zoom_in) {
					if (screen_rate > MIN_Height) {
						showProgressUI(-1);
						// mScreenPositionManager.zoomOut();
						mScreenPositionManager.zoomByPercent(screen_rate);
					}
				} else if (id == R.id.btn_position_zoom_out) {
					if (screen_rate < MAX_Height) {
						showProgressUI(1);
						// mScreenPositionManager.zoomIn();
						mScreenPositionManager.zoomByPercent(screen_rate);
					}
				}
			}

		}
	}

	private void findViews(View view) {
		txtText = (TextView) view.findViewById(R.id.showAdjust_settings_text);
		btnShowAdjust = (Button) view.findViewById(R.id.showAdjust_settings_btn);
		container = (LinearLayout) view.findViewById(R.id.showAdjust_settings_content_postion);
		btn_position_zoom_out = (ImageButton) view.findViewById(R.id.btn_position_zoom_out);
		btn_position_zoom_in = (ImageButton) view.findViewById(R.id.btn_position_zoom_in);
		screen_tip_01 = (TextView)view.findViewById(R.id.screen_tip_01);
		
		img_num_hundred = (ImageView) view.findViewById(R.id.img_num_hundred);
		img_num_ten = (ImageView) view.findViewById(R.id.img_num_ten);
		img_num_unit = (ImageView) view.findViewById(R.id.img_num_unit);
		img_progress_bg = (ImageView) view.findViewById(R.id.img_progress_bg);
	}

}
