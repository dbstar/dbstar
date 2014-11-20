package com.guodian.checkdevicetool;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.testentry.Disk;
import com.guodian.checkdevicetool.util.DeviceInfoProvider;
import com.guodian.checkdevicetool.util.GLog;
import com.guodian.checkdevicetool.util.XmlParser;
import com.guodian.checkdevicetool.widget.InitDeviceDialog;

public class BaseActivity extends Activity{
    
    private static final int MESSAGE_WHAT_PREPARE_FINISH = 0x1001;
    private static final int MESSAGE_WHAT_TRT_AGIN_PREPARE = 0x1002;
    
    protected static Map<String, String> mResult = new HashMap<String, String>();
    protected GLog mLog;
    private SharedPreferences mTestResultPf;
    private SharedPreferences mTestTypePf;
    private int mTryPrepareCount;
    private InitDeviceDialog mInitDeviceDialog;
    protected Configs mConfig;
    protected ArrayList<Disk> mDisks;
    
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            int what = msg.what;
            if(MESSAGE_WHAT_PREPARE_FINISH == what){
                if(mInitDeviceDialog != null && mInitDeviceDialog.isShowing())
                    mInitDeviceDialog.dismiss();
                String path = CheckTestConfigFilePath();
                
                mConfig = XmlParser.parseConfig(path);
                if(mConfig == null){
                    Toast.makeText(getApplication(), R.string.test_read_configfile_fail, 1).show(); 
                }else{
                    mLog.i(mConfig.toString());
                 
                }
                startTest();
            }else if(MESSAGE_WHAT_TRT_AGIN_PREPARE == what){
                if(mTryPrepareCount < 3){
                    waitting(5 * 1000);
                    mTryPrepareCount ++;
                    mLog.i("try agin prepare");
                }else{
                    
                   mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_WHAT_PREPARE_FINISH), 3000);
                    
                    mLog.i("test config file in not found or sda1 hava not mounted");
                }
            }
        }

    };
   
    private String CheckTestConfigFilePath() {
    	String path = Configs.TEST_CONFIG_FILE_PAHT_SDA1;
    	String[] filePaths = {Configs.TEST_CONFIG_FILE_PAHT_SDA1, Configs.TEST_CONFIG_FILE_PAHT_SDB1, 
    			Configs.TEST_CONFIG_FILE_PAHT_SDC1, Configs.TEST_CONFIG_FILE_PAHT_SDCARD1};
    	for (int i = 0; i < filePaths.length; i++) {
    		File file = new File(filePaths[i]);
    		if (file.exists()) {
    			path = filePaths[i];
    			Log.d("BaseActivity", "in CheckDeviceTool, TEST_CONFIG_FILE_PAHT = " + filePaths[i]);
    			break;
    		}
    	}
    	return path;
    };

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
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         mTestResultPf = getSharedPreferences(Configs.TEST_RESULT_PATH, Context.MODE_PRIVATE);
         mTestTypePf = getSharedPreferences(Configs.TEST_TYPE_ORDER_FILE_NAME, Context.MODE_PRIVATE);
         mLog =  GLog.getLogger("Futao");
         
         mInitDeviceDialog = InitDeviceDialog.getInstance(this, null);
         mInitDeviceDialog.show();
         waitting(15 * 1000);
    };
    
    protected void startTest(){
        if(mTestTypePf != null){
            Editor editor = mTestTypePf.edit();
            if(mConfig == null || mConfig.mPlayTime == null || mConfig.mPlayTime.isEmpty()){
                editor.putString(Configs.PLAY_TIME, "72");
            }else{
                editor.putString(Configs.PLAY_TIME, mConfig.mPlayTime);
            }
            editor.commit();
        }
    }
    protected void writeNextTestType(int type){
        if(mTestTypePf != null){
            Editor editor =  mTestTypePf.edit();
            editor.putInt(Configs.TEST_TYPE,type);
            editor.commit(); 
        }
    }
    protected  void writeResultToFile(){
       if(mResult == null || mTestResultPf == null)
           return;
        try {
            Iterator<String> iterator = mResult.keySet().iterator();
            String key = null;
            String value = null;
            Editor editor = mTestResultPf.edit();
            while (iterator.hasNext()) {
              key = iterator.next();
              value = mResult.get(key);
              editor.putString(key, value);
            }
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }
    protected void clearAllResult(){
        Editor editor = mTestResultPf.edit();
        editor.clear();
        editor.commit();
    }
    protected  void writeResultToFile(String key,String value){
        if(mTestResultPf !=null){
             Editor editor = mTestResultPf.edit();
             editor.putString(key, value);
             editor.commit();
        }
     }
    
    protected boolean isPrepared(){
        int type = mTestTypePf.getInt(Configs.TEST_TYPE, Configs.TYPE_BOARD_TEST);
        if(Configs.TYPE_BOARD_TEST == type){
            File disk = new File(Configs.DEFALUT_DISK);
            String path = CheckTestConfigFilePath();
            File file = new File(path);
            mDisks = DeviceInfoProvider.loadDiskInfo();
            if(file.exists() && disk.exists() && mDisks.size() >=2){
                return true;
            }else{
                return false;
            }
            
        }else {
           // File disk = new File(Configs.DEFALUT_DISK);
        	String path = CheckTestConfigFilePath();
            File file = new File(path);
            mDisks = DeviceInfoProvider.loadDiskInfo();
            if(file.exists() && /*disk.exists() &&*/ mDisks.size() >=2){
                return true;
            }else{
                return false;
            }
        }
      
     }
}
