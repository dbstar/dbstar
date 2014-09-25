package com.guodian.checkdevicetool;

import java.io.File;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.dbstar.DbstarDVB.IDbstarService;
import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.util.GLog;
import com.guodian.checkdevicetool.util.XmlParser;
import com.guodian.checkdevicetool.widget.InitDeviceDialog;

public class AgingTestActivity extends Activity {
    
    private static final int MESSAGE_WHAT_PREPARE_FINISH = 0x1003;
    private static final int MESSAGE_WHAT_TRT_AGIN_PREPARE = 0x1004;
    
    private WindowManager mWindowManager;
    private LayoutParams mParams;
    private View mSuspensionView;
    private TextView mHour,mMinutes,mSecond;
    private TextView mTestReuslt;
    private GLog mLog;
    private InitDeviceDialog mInitDeviceDialog;
    private int mSettingPlayTime = 72;
    private IDbstarService mDbstarService = null;
    private int second,minutes,hours;
    private int mTryPrepareCount;
    private boolean isStartTimer;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            if(MESSAGE_WHAT_PREPARE_FINISH == what){
                if(mInitDeviceDialog != null && mInitDeviceDialog.isShowing())
                    mInitDeviceDialog.dismiss();
                startPlayActivity();
            }else if(MESSAGE_WHAT_TRT_AGIN_PREPARE == what){
                if(mTryPrepareCount < 3){
                    waitting(5 * 1000);
                    mTryPrepareCount ++;
                    mLog.i("try agin prepare");
                }else{
                    if(mInitDeviceDialog != null && mInitDeviceDialog.isShowing())
                        mInitDeviceDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.test_video_file_not_found, Toast.LENGTH_SHORT).show();
                    mLog.i(" sda1 hava not mounted");
                }
            }
        };
    };
  
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.aging_test_view);
        mLog = GLog.getLogger("Futao");
        mTestReuslt = (TextView) findViewById(R.id.test_result);
        
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mSettingPlayTime = 72;
        SharedPreferences preferences = getSharedPreferences(Configs.TEST_TYPE_ORDER_FILE_NAME, Context.MODE_PRIVATE);
        if(preferences != null){
            mSettingPlayTime = Integer.parseInt(preferences.getString(Configs.PLAY_TIME, mSettingPlayTime+"").trim());
        }
        mInitDeviceDialog = InitDeviceDialog.getInstance(this, null);
        mInitDeviceDialog.show();
        
        bindDbservice();
        
        waitting(15 * 1000);
    }
    
    private void waitting(int delayed){
        mHandler.postDelayed(new Runnable() {
                  
                  @Override
                  public void run() {
                     if(isPrepared()){
                         mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_WHAT_PREPARE_FINISH), 3000);
                     }else{
                         mHandler.sendEmptyMessage(MESSAGE_WHAT_TRT_AGIN_PREPARE);
                     }
                  }
              }, delayed);
      }
    private boolean isPrepared(){
        File file = new File(Configs.TARGET_VIDEO_FILE);
        if(file.exists()){
            return true;
        }else{
            return false;
        }
    }
    private void startPlayActivity(){
        mLog.i("startPlayActivity");
        Intent intent = new Intent();
        Uri uri = Uri.parse("file://" + Configs.TARGET_VIDEO_FILE);
        intent.setData(uri);
        intent.putExtra("isLoop", true);
        
        showSuspensionView();
        intent.setComponent(new ComponentName("com.dbstar.DbstarDVB", "com.dbstar.DbstarDVB.VideoPlayer.PlayerMenu"));
        intent.setAction("android.intent.action.View");
        startActivity(intent);
        overridePendingTransition(0, 0);
        try {
            mDbstarService.sendCommand(0x00200, "0", 1);
            mLog.i("send comman = " + 0x00200  + " value = 0" + "length = 1");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
     @Override
    protected void onStart() {
        super.onStart();
        if(isStartTimer){
            mHandler.removeCallbacks(TimerRunable);
            String time = mHour.getText() .toString() + getString(R.string.test_hour) + mMinutes.getText().toString() +getString(R.string.test_minute) + mSecond.getText().toString() + getString(R.string.test_second);
            writeResultToFile(getString(R.string.test_test_playvideo), time);
            isStartTimer = false;
            if(hours < mSettingPlayTime){
                writeNextTestType(Configs.TYPE_AGING_TEST);
                mTestReuslt.setText(getString(R.string.test_title) + " : " + time  + getString( R.string.test_less_than_setting_time )+ mSettingPlayTime + "," + getString(R.string.test_statu_fail) +","+ getString(R.string.test_end_notify_1));
            }else{
                writeNextTestType(Configs.TYPE_SELECTOR_TEST);
                mTestReuslt.setText(getString(R.string.test_title) + " : " + time  + "," + getString(R.string.test_statu_seccuss) +","+ getString(R.string.test_end_notify_1));
            }
        }
        
    }
     private void bindDbservice(){
         Intent mIntent = new Intent();
         mIntent.setComponent(new ComponentName("com.dbstar.DbstarDVB", "com.dbstar.DbstarDVB.DbstarService"));
         bindService(mIntent, mConnection, Context.BIND_AUTO_CREATE);
     }
     
     private ServiceConnection mConnection = new ServiceConnection() {
         public void onServiceConnected(ComponentName className, IBinder service) {
             mDbstarService = IDbstarService.Stub.asInterface(service);
         }

         public void onServiceDisconnected(ComponentName className) {
             mDbstarService = null;
         }
     };
    protected  void writeResultToFile(String key,String value){
       SharedPreferences sharedPreferences = getSharedPreferences(Configs.TEST_RESULT_PATH, Context.MODE_PRIVATE);
      
        if(sharedPreferences !=null){
             Editor editor = sharedPreferences.edit();
             editor.putString(key, value);
             editor.commit();
        }
     }
    protected void writeNextTestType(int type){
      SharedPreferences  sharedPreferences = getSharedPreferences(Configs.TEST_TYPE_ORDER_FILE_NAME, Context.MODE_PRIVATE);
        if(sharedPreferences != null){
            Editor editor =  sharedPreferences.edit();
            editor.putInt(Configs.TEST_TYPE,type);
            editor.commit(); 
        }
    }
    private void showSuspensionView(){
        if(mSuspensionView != null)
            removeSuspensionView();
        
        mSuspensionView = LayoutInflater.from(this).inflate(R.layout.timer_view, null);
        mParams = new LayoutParams();
        mParams.type = LayoutParams.TYPE_PHONE;  
        mParams.format = PixelFormat.RGBA_8888;  
        mParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL  
                | LayoutParams.FLAG_NOT_FOCUSABLE;  
        mParams.gravity = Gravity.CENTER_HORIZONTAL| Gravity.TOP;  
        mParams.width = 500;  
        mParams.height = 100;  
        mParams.x = 0;  
        mParams.y = 0; 
        mWindowManager.addView(mSuspensionView, mParams);
        
        mHour = (TextView) mSuspensionView.findViewById(R.id.hours);
        mMinutes = (TextView) mSuspensionView.findViewById(R.id.minutes);
        mSecond = (TextView) mSuspensionView.findViewById(R.id.second);
        hours = 0;
        minutes=0;
        second = 0;
        isStartTimer = true;
        mHandler.postDelayed(TimerRunable, 1000);
     }
     
     private void removeSuspensionView(){
         if(mSuspensionView != null){
             mWindowManager.removeView(mSuspensionView);
             mSuspensionView = null;
         }
     }
     private Runnable TimerRunable = new Runnable() {
        
        @Override
        public void run() {
             second++;
            if(second >= 60){
                second = 0;
                minutes ++;
            }
            
            if(minutes >=60){
                minutes =0;
                hours ++;
            }
            mHour.setText(String.valueOf(hours));
            mMinutes.setText(String.valueOf(minutes));
            mSecond.setText(String.valueOf(second));
            mHandler.postDelayed(this, 1000);
            if(hours > 0 && hours % 8 ==0){
                String time = mHour.getText() + getString(R.string.test_hour) + mMinutes.getText().toString() +getString(R.string.test_minute) + mSecond.getText().toString() + getString(R.string.test_second);
                writeResultToFile(getString(R.string.test_test_playvideo), time);
            }
            
            if(hours >= mSettingPlayTime){
                AgingTestActivity.this.sendBroadcast(new Intent("com.guodian.checkdevice.tool.exit.player"));
            }
        }
    };
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
}
