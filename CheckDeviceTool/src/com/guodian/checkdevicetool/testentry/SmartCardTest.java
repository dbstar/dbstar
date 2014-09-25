package com.guodian.checkdevicetool.testentry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;

import com.dbstar.DbstarDVB.DbstarServiceApi;
import com.dbstar.DbstarDVB.IDbstarService;
import com.dbstar.DbstarDVB.common.Configs;
import com.dbstar.DbstarDVB.common.GDCommon;
import com.guodian.checkdevicetool.BoardOrAllTestActivity;
import com.guodian.checkdevicetool.R;

public class SmartCardTest extends TestTask{
    private IDbstarService service;
    private boolean isRegistedReciver = false;
    public SmartCardTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
        registerSystemMessageReceiver();
    }
    
    @Override
    public void start() {
        super.start();
        service = ((BoardOrAllTestActivity)context).getDbService();
        if(service == null){
            sendFailMsg(null);
        }else{
            try {
              
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {  
                            mLog.i("sendCommand  DbstarServiceApi.CMD_DRM_SC_INSERT");
                            service.sendCommand(DbstarServiceApi.CMD_DRM_SC_INSERT, null, 0);
                            
                        } catch (Exception e) {
                        }
                    }
                }, 0);
               mHandler.postDelayed(TimeOutTask, 20 * 1000);
            } catch (Exception e) {
                sendFailMsg(null);
                e.printStackTrace();
            }
        }
        
    }
    private void registerSystemMessageReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DbstarServiceApi.ACTION_NOTIFY);
        filter.addAction(GDCommon.ActionAddFavourite);
        filter.addAction(GDCommon.ActionDelete);
        filter.addAction(GDCommon.ActionBookmark);
        filter.addAction(GDCommon.ActionUpgradeCancelled);
        filter.addAction(GDCommon.ActionPlayCompleted);

        filter.addAction(GDCommon.ActionGetNetworkInfo);
        filter.addAction(GDCommon.ActionSetNetworkInfo);
        filter.addAction(GDCommon.ActionGetEthernetInfo);

        filter.addAction(GDCommon.ActionScreenOn);
        filter.addAction(GDCommon.ActionScreenOff);

        filter.addAction(DbstarServiceApi.ACTION_HDMI_IN);
        filter.addAction(DbstarServiceApi.ACTION_HDMI_OUT);

        filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_IN);
        filter.addAction(DbstarServiceApi.ACTION_SMARTCARD_OUT);
        filter.addAction(GDCommon.ACTION_BOOT_COMPLETED);
        
        filter.addAction(GDCommon.ActionClearSettings);
        filter.addAction(GDCommon.ActionSystemRecovery);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(GDCommon.ActionChannelModeChange);
        
        context.registerReceiver(mSystemMessageReceiver, filter);
        isRegistedReciver = true;
        mLog.i("registerSystemMessageReceiver");
    }
    
    private BroadcastReceiver mSystemMessageReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mLog.i("mSystemMessageReceiver" + action);
            if (action.equals(DbstarServiceApi.ACTION_NOTIFY)) {
                int type = intent.getIntExtra("type", 0);
                switch (type) {
                case DbstarServiceApi.DRM_SC_INSERT_OK:
                    mHandler.removeCallbacks(TimeOutTask);
                    getSmartcardInfo(DbstarServiceApi.CMD_DRM_SC_SN_READ);
                    break;

                case DbstarServiceApi.DRM_SC_INSERT_FAILED:
                    mHandler.removeCallbacks(TimeOutTask);
                    sendFailMsg(context.getResources().getString(R.string.test_smartcard_reset_error));
                    break;
                    
                case DbstarServiceApi.STATUS_DVBPUSH_INIT_FAILED:
                    mLog.i("STATUS_DVBPUSH_INIT_FAILED" + action);
                    break;
                    
                case DbstarServiceApi.STATUS_DVBPUSH_INIT_SUCCESS:
                    mLog.i("STATUS_DVBPUSH_INIT_SUCCESS" + action);
                    break;
                }
            }
        }
    };
   
    public void getSmartcardInfo(int type) {
        
        if (service == null){
            sendFailMsg(null);
            return ;
        }
        new AsyncTask<Integer, Integer, String>(){
            @Override
            protected String doInBackground(Integer... params) {
                try {
                    Intent intent = service.sendCommand(params[0], null, 0);
                    byte[] bytes = intent.getByteArrayExtra("result");
                    if (bytes != null) {
                        String resultCmd = new String(bytes,"utf-8");
                        if(resultCmd != null && !resultCmd.isEmpty()){
                            mLog.i("resultCmd = " + resultCmd);
                            Configs configs =  ((BoardOrAllTestActivity)context).getConfig();
                            
                            if(configs != null && configs.mSmartCardNum != null && !configs.mSmartCardNum.isEmpty()){
                                sendSuccessMsg();
                            }else{
                                sendFailMsg(context.getResources().getString(R.string.test_read_configfile_fail));
                            }
                            return null;
                        }else {
                            sendFailMsg(null);
                            mLog.i("resultCom = null");
                            return null;
                        }
                    }
                } catch (Exception e) {
                    sendFailMsg(null);
                    e.printStackTrace();
                }
                return null;
            }
            
        }.execute(type);
        
      

    

    }
    
    Runnable TimeOutTask = new Runnable() {
        
        @Override
        public void run() {
            sendFailMsg(null);
        }
    };
    @Override
    public void stop() {
            mHandler.removeCallbacks(TimeOutTask);
            if(isRegistedReciver){
                mLog.i("unregisterReceiver");
                context.unregisterReceiver(mSystemMessageReceiver);
                isRegistedReciver = false;
            }
           
    }
}
