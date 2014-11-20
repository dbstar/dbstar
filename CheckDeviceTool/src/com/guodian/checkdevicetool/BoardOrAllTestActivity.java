package com.guodian.checkdevicetool;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dbstar.DbstarDVB.IDbstarService;
import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.testentry.APTest;
import com.guodian.checkdevicetool.testentry.AudioTest;
import com.guodian.checkdevicetool.testentry.CopyVideoFileTest;
import com.guodian.checkdevicetool.testentry.DefaultDiskTest;
import com.guodian.checkdevicetool.testentry.Disk;
import com.guodian.checkdevicetool.testentry.EthernetTest;
import com.guodian.checkdevicetool.testentry.NetWorkSignallampTest;
import com.guodian.checkdevicetool.testentry.SdcardTest;
import com.guodian.checkdevicetool.testentry.SmartCardTest;
import com.guodian.checkdevicetool.testentry.TestTask;
import com.guodian.checkdevicetool.testentry.USB1Test;
import com.guodian.checkdevicetool.testentry.USB2Test;
import com.guodian.checkdevicetool.testentry.WifiTest;


public class BoardOrAllTestActivity extends BaseActivity {
    
    private ScrollView mMainLayout;
    private LayoutInflater mInflater;
    private LinearLayout mCurrentTestView;
    private TextView mResultView;
    private TextView mReasonView;
    private Button mButtonYes;
    private Button mButtonNo;
    private IDbstarService mDbstarService = null;
    private ArrayList<TestTask> mTaskList ;
    private TestTask mCurrentTask;
    private int mSuccessfullyCount;
    
    
    private int mFailCount; 
    private int mTotalCount;
    private int mTestTyep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        mInflater = LayoutInflater.from(this);
        mMainLayout = (ScrollView) mInflater.inflate(R.layout.activity_main, null);
        setContentView(mMainLayout);
        mTestTyep = getIntent().getIntExtra(Configs.TEST_TYPE, Configs.TYPE_BOARD_TEST);
        
        bindDbservice();
        
      
    }
    
    @Override
    protected void startTest() {
        super.startTest();
        TestTask.count =0;
        mTaskList =new ArrayList<TestTask>();
        constructTaskList();
        mTotalCount = mTaskList.size();
        testNext();
    }
    /**
     * construct different task list depending on test type
     */
    private void constructTaskList() {
        if(Configs.TYPE_BOARD_TEST == mTestTyep){
            
            mTaskList.add(new EthernetTest(this, handler,R.layout.test_ethernet,true));
            mTaskList.add(new WifiTest(this, handler,R.layout.test_wifi,true));
            mTaskList.add(new APTest(this, handler, R.layout.test_wifi_ap, true));
            // 出厂的时候虽然不带硬盘，但是仍需检测硬盘
            mTaskList.add(new DefaultDiskTest(this, handler,R.layout.test_disk,true));
            mTaskList.add(new USB1Test(this, handler, R.layout.test_usb1, true));
            mTaskList.add(new USB2Test(this, handler, R.layout.test_usb2, true));
            mTaskList.add(new SdcardTest(this, handler,R.layout.test_sdcard,true));
//            mTaskList.add(new PlayerTest(this, handler, R.layout.test_video,true));
            
        }else if(Configs.TYPE_ALL_TEST == mTestTyep){
        	// mTaskList.add(new GDModuleTest(this, handler,R.layout.test_gdmodule,false));
            mTaskList.add(new AudioTest(this, handler, R.layout.test_audio, false));
            // 检测的是机顶盒盖上的电源指示灯
            // mTaskList.add(new PowerLightTest(this, handler,R.layout.test_power_light,false));
            mTaskList.add(new NetWorkSignallampTest(this, handler,R.layout.test_network_signal_lamp,false));
            // 盒盖上的休眠灯
            // mTaskList.add(new SleepKeyTest(this, handler, R.layout.test_sleep_key, false));
            mTaskList.add(new EthernetTest(this, handler,R.layout.test_ethernet,true));
            mTaskList.add(new WifiTest(this, handler,R.layout.test_wifi,true));
            mTaskList.add(new APTest(this, handler, R.layout.test_wifi_ap, true));
            mTaskList.add(new SmartCardTest(this, handler,R.layout.test_smartcard,true));
            mTaskList.add(new DefaultDiskTest(this, handler,R.layout.test_disk,true));
            mTaskList.add(new CopyVideoFileTest(this, handler, R.layout.test_copy_video, true));
            mTaskList.add(new USB1Test(this, handler, R.layout.test_usb1, true));
            mTaskList.add(new USB2Test(this, handler, R.layout.test_usb2, true));
            mTaskList.add(new SdcardTest(this, handler,R.layout.test_sdcard,true));
            
        }else if(Configs.TYPE_SELECTOR_TEST == mTestTyep){
        	// mTaskList.add(new GDModuleTest(this, handler,R.layout.test_gdmodule,false));
            mTaskList.add(new AudioTest(this, handler, R.layout.test_audio, false));
            // mTaskList.add(new PowerLightTest(this, handler,R.layout.test_power_light,false));
            mTaskList.add(new NetWorkSignallampTest(this, handler,R.layout.test_network_signal_lamp,false));
            // mTaskList.add(new SleepKeyTest(this, handler, R.layout.test_sleep_key, false));
            mTaskList.add(new EthernetTest(this, handler,R.layout.test_ethernet,true));
            mTaskList.add(new SmartCardTest(this, handler,R.layout.test_smartcard,true));
            mTaskList.add(new WifiTest(this, handler,R.layout.test_wifi,true));
            mTaskList.add(new APTest(this, handler, R.layout.test_wifi_ap, true));
            mTaskList.add(new DefaultDiskTest(this, handler,R.layout.test_disk,true));
            mTaskList.add(new USB1Test(this, handler, R.layout.test_usb1, true));
            mTaskList.add(new USB2Test(this, handler, R.layout.test_usb2, true));
            mTaskList.add(new SdcardTest(this, handler,R.layout.test_sdcard,true));
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
  
   private Handler handler = new Handler(){
        public  synchronized void  handleMessage(android.os.Message msg) {
            int what = msg.what;
            mReasonView =  (TextView) mCurrentTestView.findViewById(R.id.test_fail_reason);
            mResultView = (TextView) mCurrentTestView.findViewById(R.id.test_disk_sda1_statu);
            mButtonYes = (Button) mCurrentTestView.findViewById(R.id.btn_yes);
            mButtonNo = (Button) mCurrentTestView.findViewById(R.id.btn_no);
            boolean isShowResult = true;
            boolean isAutoNext = true;
            
            if(msg.getData() != null){
                isShowResult = msg.getData().getBoolean(Configs.MSG_SHOW_RESULT);
                isAutoNext = msg.getData().getBoolean(Configs.MSG_SHOW_AUTO_NEXT);
            }
            if(isShowResult){
                if(Configs.TEST_SUCCESS == what){
                    mResultView.setTextColor(Color.WHITE);
                    mResultView.setText(R.string.test_statu_seccuss);
                    mSuccessfullyCount ++;
                    mCurrentTask.setResult(getString(R.string.test_statu_seccuss));
                }else if(Configs.TEST_FAIL == what){
                    mResultView.setTextColor(Color.RED);
                    mResultView.setText(R.string.test_statu_fail);
                    mFailCount ++;
                    mCurrentTask.setResult(getString(R.string.test_statu_fail));
                }
                
            }
            String args = null;
            if(msg.getData() != null)
                args = msg.getData().getString(Configs.MESSAGE);
            if(args != null){
                if(mReasonView != null){
                    mReasonView.setVisibility(View.VISIBLE);
                    mReasonView.setText(args);
                }
            }else{
                if(mReasonView != null && mReasonView.isShown()){
                    mReasonView.setVisibility(View.GONE);
                }
            }
            if(isAutoNext){
                mCurrentTestView.findViewById(R.id.test_pb).setVisibility(View.INVISIBLE);
                mCurrentTask.stop();
                mTaskList.remove(mCurrentTask);
                testNext();
            }else{
                if(mButtonYes != null && mButtonYes.isShown() && mButtonNo != null && mButtonNo.isShown()){
                    mButtonYes.requestFocus();
                    mButtonYes.setOnClickListener(clickListener);
                    mButtonNo.setOnClickListener(clickListener);
                }
            }
        };
    };
    
    OnClickListener clickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_yes){
                mResultView.setTextColor(Color.WHITE);
                mResultView.setText(R.string.test_statu_seccuss);
                mSuccessfullyCount ++;
                mCurrentTask.setResult(getString(R.string.test_statu_seccuss));
            }else if(v.getId() == R.id.btn_no){
                mResultView.setTextColor(Color.RED);
                mResultView.setText(R.string.test_statu_fail);
                mFailCount ++;
                mCurrentTask.setResult(getString(R.string.test_statu_fail));
            }
            if(mReasonView != null && mReasonView.isShown()){
                mReasonView.setVisibility(View.GONE);
            }
            
            mCurrentTestView.findViewById(R.id.test_pb).setVisibility(View.INVISIBLE);
            mButtonYes.setVisibility(View.INVISIBLE);
            mButtonNo.setVisibility(View.INVISIBLE);
            mCurrentTask.stop();
            mTaskList.remove(mCurrentTask);
            testNext();
        }
    }; 
    
    
    public void setCurrentTestView(LinearLayout layout){
        mCurrentTestView = layout;
        handler.post(ScrollRunnable);
    }     
    
    
    private Runnable ScrollRunnable= new Runnable() {
        @Override
        public void run() {
            int off = mMainLayout.getChildAt(0).getMeasuredHeight() - mMainLayout.getHeight() + 50;
            mLog.i("ScrollRunnable = " + off);
            mMainLayout.scrollBy(0, off);
                }
        };

    private void testNext(){
       mLog.i("testNext");
        if(mCurrentTask != null){
            if(mResult != null){
                mResult.put(mCurrentTask.getName(), mCurrentTask.getResult());
            }
        }
        if(mTaskList.size() > 0){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                	if (mTaskList.size() > 0) {
                		mCurrentTask = mTaskList.get(0);
                		mCurrentTask.start();                		
                	}
                }
            }, 1000);
        }else{
            testFinish();
        }
    }
   private void testFinish(){
       LinearLayout parentView = (LinearLayout) findViewById(R.id.parent);
       LinearLayout finishView = (LinearLayout) mInflater.inflate(R.layout.test_finish, null);
       parentView.addView(finishView);
       TextView textView = (TextView) finishView.findViewById(R.id.end_text);
       String str = "";
       if(mTestTyep == Configs.TYPE_BOARD_TEST){
           str = getString(R.string.test_end_notify_1);
           if(mFailCount == 0 && (mSuccessfullyCount == mTotalCount)){
               writeNextTestType( Configs.TYPE_ALL_TEST);
           }else{
               writeNextTestType( Configs.TYPE_BOARD_TEST);
           }
           clearAllResult();
       }else if(mTestTyep == Configs.TYPE_ALL_TEST){
           str = getString(R.string.test_end_notify_1);
           if(mFailCount == 0 && (mSuccessfullyCount == mTotalCount)){
               writeNextTestType( Configs.TYPE_AGING_TEST);
           }else{
               writeNextTestType( Configs.TYPE_ALL_TEST);
           }
       }else if(mTestTyep == Configs.TYPE_SELECTOR_TEST){
           str = getString(R.string.test_end_notify_2);
           writeNextTestType( Configs.TYPE_SELECTOR_TEST);
       }
       textView.setText(getString(R.string.test_total) + " " + mTotalCount + getString(R.string.test_xiang) +"，"+ getString(R.string.test_statu_seccuss) + " " + mSuccessfullyCount +"，"+ getString(R.string.test_statu_fail) + " "  + mFailCount + "，" + str );
       writeResultToFile();
       mCurrentTask.release();
       handler.post(ScrollRunnable);
   }
    public ArrayList<Disk> getDisks(){
        return mDisks;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(mTestTyep == Configs.TYPE_BOARD_TEST || mTestTyep == Configs.TYPE_ALL_TEST){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
    public Configs getConfig(){
        return mConfig;
    }
    
    public IDbstarService getDbService(){
        return mDbstarService;
    }
    
    @Override
    protected void onStop() {
        super.onStop();
       
    }
    protected void onDestroy() {
       super.onDestroy();
        unbindService(mConnection);
    };
   
}
