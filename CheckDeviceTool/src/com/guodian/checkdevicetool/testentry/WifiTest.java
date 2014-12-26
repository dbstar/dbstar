package com.guodian.checkdevicetool.testentry;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.dbstar.DbstarDVB.common.Configs;
import com.guodian.checkdevicetool.BoardOrAllTestActivity;
import com.guodian.checkdevicetool.R;
import com.guodian.checkdevicetool.util.APPVersion;
import com.guodian.checkdevicetool.util.APUtil;

public class WifiTest extends TestTask{
    private  String SSID ;
    private String password ;
    private WifiManager  mWifiManager;
    private Scanner mScanner;
    private List<ScanResult> mScanResults;
    private WifiConfiguration mDTConfig;
    private int mScanCount = 0;
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    private boolean isRegistedReceiver;
    public WifiTest(Context context, Handler handler, int viewId, boolean isAuto) {
        super(context, handler, viewId, isAuto);
        
        mWifiManager =  (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        mScanner = new Scanner();
      /*  if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED 
                || mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING){
            mWifiManager.setWifiEnabled(false);
        }*/
    }

	public void start() {
		super.start();

		if (isConnectedNetWork()) {
			sendSuccessMsg();
			return;
		}
		
		Configs config = ((BoardOrAllTestActivity) context).getConfig();
		Log.d("WifiTest", "wifi config = " + config);
		if (config != null) {
			SSID = ((BoardOrAllTestActivity) context).getConfig().mWifiSSID;
			password = ((BoardOrAllTestActivity) context).getConfig().mWifiPassword;
		}
		
		Log.d("WifiTest", "wifi SSID = " + SSID);
		Log.d("WifiTest", "wifi password = " + password);
		if (SSID == null || SSID.isEmpty()) {
			sendFailMsg(context.getResources().getString(R.string.test_read_configfile_fail));
			return;
		}
		
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				registerReceiver();
				IfNeedToOpenWifi();
				mHandler.removeCallbacks(timeOutTask);
				mHandler.postDelayed(timeOutTask, 1000 * 120);
			}
		}, 1000 * 1);

	}
    private void registerReceiver(){
        IntentFilter mFilter = new IntentFilter();
        
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        context.registerReceiver(wifiReceiver, mFilter);
        isRegistedReceiver = true;
    }
    private void unRegisterReceiver(){
        if(isRegistedReceiver){
           context.unregisterReceiver(wifiReceiver);
           isRegistedReceiver = false;
        }
    }
    private void IfNeedToOpenWifi(){
    	Log.d("WifiTest", "need to open wifi!");
        if(!mWifiManager.isWifiEnabled() && mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING){
        	Log.d("WifiTest", "wifi is openning!");
        	APUtil.closeWifiAp(mWifiManager);
            mWifiManager.setWifiEnabled(true);
            this.isAutoToNext = false;
            this.isShowResult = false;
            sendFailMsg(getString(R.string.test_wifi_enabling));
        }
    }
    
    private String getString(int id){
        return context.getResources().getString(id);
    }
    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)){
                handWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_UNKNOWN));
            }else if(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)){
                handleScanResultsAvailable();
            }else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)){
                NetworkInfo info = (NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
               mLog.i("state = " + info.getState().toString());
                mConnected.set(info.isConnected());
               if(info.isConnected()){
                   mHandler.removeMessages(Configs.TEST_FAIL);
                   mHandler.removeCallbacks(timeOutTask);
                   isAutoToNext = true;
                   isShowResult = true;
                   sendSuccessMsg();
                   unRegisterReceiver();
               }
            }else if(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)){
               if(!mConnected.get()){
                   DetailedState state = WifiInfo
                           .getDetailedStateOf((SupplicantState) intent
                                   .getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                   if(state == DetailedState.AUTHENTICATING){
                       isAutoToNext = false;
                       isShowResult = false;
                       sendFailMsg(getString(R.string.test_wifi_authenting));
                   }else if(state == DetailedState.OBTAINING_IPADDR){
                       isAutoToNext = false;
                       isShowResult = false;
                       sendFailMsg(getString(R.string.test_wifi_obtain_ipadress));
                   }else if(state == DetailedState.DISCONNECTED){
                   }
               } 
            }
        }
    };
    
    
    private void handWifiState(int state){
        switch (state) {
        case WifiManager.WIFI_STATE_ENABLED:
            this.isAutoToNext = false;
            this.isShowResult = false;
            sendFailMsg(getString(R.string.test_wifi_scaning));
            if(!isConnectedNetWork()){
                mScanner.resume();
            }
            break;
        }
    }
    
    private boolean isConnectedNetWork(){
       ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
       return wifi.isConnected();
    }
    private synchronized void handleScanResultsAvailable(){
        mScanResults = mWifiManager.getScanResults();
        mScanCount ++;
       if(mScanResults != null && !mScanResults.isEmpty() && mDTConfig == null){
           for(ScanResult result : mScanResults){
               if(result.SSID.equals(SSID)){
                   mDTConfig = new WifiConfiguration();
                   mDTConfig.SSID = convertToQuotedString(SSID);
                   mDTConfig.preSharedKey = '"' + password + '"';
                   if (result.capabilities.contains("WEP")) {
                       mDTConfig.allowedKeyManagement.set(KeyMgmt.NONE);
                       mDTConfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                       mDTConfig.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                   } else if (result.capabilities.contains("PSK")) {
                       mDTConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                   } else if (result.capabilities.contains("EAP")) {
                       mDTConfig.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                       mDTConfig.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                   }else{
                       mDTConfig.allowedKeyManagement.set(KeyMgmt.NONE);
                   }
                   mScanner.pause();
                   connectWork();
                   return;
               }
               
           }
       }else{
           if(mScanCount ==3 && mDTConfig == null){
               mScanner.pause();
               unRegisterReceiver();
               this.isAutoToNext = true;
               this.isShowResult = true;
               sendFailMsg(getString(R.string.test_wifi_not_scan_dt) + "\"" + SSID + "\"");
           }
       }
    }
    
    private void connectWork(){
        if(mDTConfig != null){
            this.isAutoToNext = false;
            this.isShowResult = false;
            sendFailMsg(getString(R.string.test_wifi_connecting) + "\"" + SSID + "\"");
            if(APPVersion.SINGLE){
                int networkId = mWifiManager.addNetwork(mDTConfig);
                boolean state = mWifiManager.enableNetwork(networkId, true);
            }else{
                mWifiManager.save(mDTConfig, null);
                mWifiManager.connect(mDTConfig,null);
            }
           
        }
    }
    private String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }
    
    Runnable timeOutTask = new Runnable() {
        
        @Override
        public void run() {
            if(!isConnectedNetWork()){
                isAutoToNext = true;
                isShowResult = true;
                sendFailMsg(null);
            }else{
                isAutoToNext = true;
                isShowResult = true;
                sendSuccessMsg();
            }
        }
    };
    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                Toast.makeText(context,"No scan any wifi",
                        Toast.LENGTH_LONG).show();
                return;
            }
            sendEmptyMessageDelayed(0, 1000 * 5);
        }
    }
    @Override
    public void stop() {
        super.stop();
        mScanCount = 0;
        unRegisterReceiver();
        mScanner.pause();
        mHandler.removeCallbacks(timeOutTask);
    }
}
