package com.guodian.checkdevicetool.testentry;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.os.Handler;

import com.guodian.checkdevicetool.R;

public class EthernetTest extends TestTask{
    public static final String DefaultEthernetDeviceName = "eth0";
    private EthernetManager mEthManager;
    private String mDev = null;
    private EthernetDevInfo mEthInfo;
    public EthernetTest(Context context, Handler handler, int viewId,
            boolean isAuto) {
        super(context, handler, viewId, isAuto);
        mEthManager = (EthernetManager)context.getSystemService(Context.ETH_SERVICE);
        int state = mEthManager.getEthState();
        if(state !=EthernetManager.ETH_STATE_ENABLED){
            mEthManager.setEthEnabled(true);
        }
        String[] Devs = mEthManager.getDeviceNameList();
        if (Devs != null) {
            mLog.i("Devices = " + Devs + " count " + Devs.length);
            if (mEthManager.isEthConfigured()) {
                mEthInfo = mEthManager.getSavedEthConfig();
                mDev = mEthInfo.getIfName();
            } else {
                getEthernetDevice(Devs);
            }
        }
    }
    private void getEthernetDevice(String[] Devs) {
        for (int i = 0; i < Devs.length; i++) {
            if (Devs[i].equalsIgnoreCase(DefaultEthernetDeviceName)) {
                mDev = Devs[i];
                mLog.i(" device = " + mDev);
                break;
            }
        }
    }
    
    public void start() {
      super.start();
     ConnectivityManager m = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
     NetworkInfo ethernet =  m.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
     mLog.i("ethernet = "+ ethernet.getState().toString() + " / " + ethernet.isAvailable() );
     saveConfig();
     testEthernet();
    }
    void saveConfig(){
        if (mDev == null || mDev.isEmpty())
            return;

        if (mEthInfo == null) {
            if (mEthManager.isEthConfigured()) {
                mEthInfo = mEthManager.getSavedEthConfig();
            }
        }
        EthernetDevInfo info = new EthernetDevInfo();
        info.setIfName(mDev);
        info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP);
        info.setIpAddress(null);
        info.setRouteAddr(null);
        info.setDnsAddr(null);
        info.setNetMask(null);
        mEthManager.updateEthDevInfo(info);
    }
    public void testEthernet(){
        ConnectivityManager m = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethernet =  m.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if(ethernet == null){
            sendFailMsg(null);
            return ;
        }
        State state = ethernet.getState();
        if(ethernet.isAvailable()){
            if(state == State.CONNECTED){
                this.isShowResult = true;
                this.isAutoToNext = true;
                sendSuccessMsg(); 
            }else if(state == State.DISCONNECTED){
                this.isShowResult = true;
                this.isAutoToNext = true;
                sendFailMsg(context.getResources().getString(R.string.test_not_connected_any_network));
            }else if(state == State.CONNECTING ||state == State.DISCONNECTING){
                this.isShowResult = false;
                this.isAutoToNext = false;
                sendFailMsg(context.getResources().getString(R.string.test_disk_network_connectiong));
                mHandler.postDelayed(timeOutTask, 1000 * 10);
            }else{
                this.isShowResult = false;
                this.isAutoToNext = false;
                sendFailMsg(null);
                mHandler.postDelayed(timeOutTask, 1000 * 2);
            }
        }else{
            this.isShowResult = false;
            this.isAutoToNext = false;
            sendFailMsg(context.getResources().getString(R.string.test_disk_network_connectiong));
            mHandler.postDelayed(timeOutTask, 1000 * 10);
        }
    }
    Runnable timeOutTask = new Runnable() {
        int count = 0;
        @Override
        public void run() {
            if(count <= 5){
                testEthernet();
                count ++;
            }else{
                isShowResult = true;
                isAutoToNext = true;
                sendFailMsg(context.getResources().getString(R.string.test_please_check_network)); 
            }
        }
    };
}
