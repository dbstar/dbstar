/*
 * Copyright (C) 2009 The Android-x86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */

package com.android.server;

import java.net.UnknownHostException;
import android.net.ethernet.EthernetNative;
import android.net.ethernet.IEthernetManager;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetStateTracker;
import android.net.ethernet.EthernetDevInfo;
import android.provider.Settings;
import android.util.Slog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import android.net.DhcpInfo;

public class EthernetService<syncronized> extends IEthernetManager.Stub{
    private Context mContext;
    private EthernetStateTracker mTracker;
    private String[] DevName;
    private static final String TAG = "EthernetService";
    private int isEthEnabled ;
    private int mEthState= EthernetManager.ETH_STATE_UNKNOWN;
    
    private Handler mDelayedHandler;    
    private boolean isEthernetServiceInited = false;    


    public EthernetService(Context context, EthernetStateTracker Tracker){
        mTracker = Tracker;
        mContext = context;

        isEthEnabled = getPersistedState();
        Slog.v(TAG,"Ethernet dev enabled " + isEthEnabled );
        getDeviceNameList();
        setEthState(isEthEnabled);
        registerForBroadcasts();
        Slog.v(TAG, "Trigger the ethernet monitor");
        mTracker.StartPolling();
        mDelayedHandler = new Handler();  
        isEthernetServiceInited = true;
    }

    public boolean isEthConfigured() {
        final ContentResolver cr = mContext.getContentResolver();
        int x = Settings.Secure.getInt(cr, Settings.Secure.ETH_CONF,0);

        if (x == 1)
            return true;
        return false;
    }

    public synchronized EthernetDevInfo getSavedEthConfig() {
        if (isEthConfigured()) {
            final ContentResolver cr = mContext.getContentResolver();
            EthernetDevInfo info = new EthernetDevInfo();
            info.setConnectMode(Settings.Secure.getString(cr, Settings.Secure.ETH_MODE));
            info.setIfName(Settings.Secure.getString(cr, Settings.Secure.ETH_IFNAME));
            info.setIpAddress(Settings.Secure.getString(cr, Settings.Secure.ETH_IP));
            info.setDnsAddr(Settings.Secure.getString(cr, Settings.Secure.ETH_DNS));
            info.setNetMask(Settings.Secure.getString(cr, Settings.Secure.ETH_MASK));
            info.setRouteAddr(Settings.Secure.getString(cr, Settings.Secure.ETH_ROUTE));

            final String host = Settings.Secure.getString(cr, Settings.Secure.ETH_PROXY_HOST);
            if (host != null && host.length() != 0) {
                info.setProxy(host,
                    Settings.Secure.getInt(cr, Settings.Secure.ETH_PROXY_PORT, 8080),
                    Settings.Secure.getString(cr, Settings.Secure.ETH_PROXY_EXCLUSION_LIST));
            }

            return info;
        }
        return null;
    }

    public synchronized void setEthMode(String mode) {
        final ContentResolver cr = mContext.getContentResolver();
        Slog.v(TAG,"Set ethernet mode " + DevName + " -> " + mode);
        if (DevName != null) {
            Settings.Secure.putString(cr, Settings.Secure.ETH_IFNAME, DevName[0]);
            Settings.Secure.putInt(cr, Settings.Secure.ETH_CONF,1);
            Settings.Secure.putString(cr, Settings.Secure.ETH_MODE, mode);
        }
    }

    public synchronized void UpdateEthDevInfo(EthernetDevInfo info) {
        final ContentResolver cr = mContext.getContentResolver();
        Settings.Secure.putInt(cr, Settings.Secure.ETH_CONF,1);
        Settings.Secure.putString(cr, Settings.Secure.ETH_IFNAME, info.getIfName());
        Settings.Secure.putString(cr, Settings.Secure.ETH_IP, info.getIpAddress());
        Settings.Secure.putString(cr, Settings.Secure.ETH_MODE, info.getConnectMode());
        Settings.Secure.putString(cr, Settings.Secure.ETH_DNS, info.getDnsAddr());
        Settings.Secure.putString(cr, Settings.Secure.ETH_ROUTE, info.getRouteAddr());
        Settings.Secure.putString(cr, Settings.Secure.ETH_MASK,info.getNetMask());

        if (info.hasProxy()) {
            Settings.Secure.putString(cr, Settings.Secure.ETH_PROXY_HOST, info.getProxyHost());
            Settings.Secure.putInt(cr, Settings.Secure.ETH_PROXY_PORT, info.getProxyPort());
            Settings.Secure.putString(cr, Settings.Secure.ETH_PROXY_EXCLUSION_LIST,
                info.getProxyExclusionList());
        } else {
            Settings.Secure.putString(cr, Settings.Secure.ETH_PROXY_HOST, "");
            Settings.Secure.putInt(cr, Settings.Secure.ETH_PROXY_PORT, 8080);
            Settings.Secure.putString(cr, Settings.Secure.ETH_PROXY_EXCLUSION_LIST, "");
        }
        if (mEthState == EthernetManager.ETH_STATE_ENABLED) {
            try {
                mTracker.resetInterface();
                Slog.i(TAG, "$$UpdateEthDevInfo() call resetInterface()");
            } catch (UnknownHostException e) {
                Slog.e(TAG, "Wrong ethernet configuration");
            }
        }
    }

    private void registerForBroadcasts() {
        IntentFilter intentFilter = new IntentFilter();
        //intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        //intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mContext.registerReceiver(mReceiver, intentFilter);
    }

    public int getTotalInterface() {
        return EthernetNative.getInterfaceCnt();
    }


    private int scanEthDevice() {
        int i = 0,j;
        if ((i = EthernetNative.getInterfaceCnt()) != 0) {
            Slog.i(TAG, "total found " + i + " net devices");
            if (DevName == null || DevName.length != i)
                DevName = new String[i];
        }
        else
            return i;

        for (j = 0; j < i; j++) {
            DevName[j] = EthernetNative.getInterfaceName(j);
            if (DevName[j] == null)
                break;
            Slog.i(TAG," device " + j + " name " + DevName[j]);
        }

        return i;
    }

    public String[] getDeviceNameList() {
        return (scanEthDevice() > 0 ) ? DevName : null;
    }

    private int getPersistedState() {
        final ContentResolver cr = mContext.getContentResolver();
        try {
            return Settings.Secure.getInt(cr, Settings.Secure.ETH_ON);
        } catch (Settings.SettingNotFoundException e) {
            //return EthernetManager.ETH_STATE_UNKNOWN;
            return EthernetManager.ETH_STATE_DISABLED;
        }
    }

    private synchronized void persistEthEnabled(boolean enabled) {
        final ContentResolver cr = mContext.getContentResolver();
        Settings.Secure.putInt(cr, Settings.Secure.ETH_ON,
        enabled ? EthernetManager.ETH_STATE_ENABLED : EthernetManager.ETH_STATE_DISABLED);
    }

    private final Runnable mResetInterface = new Runnable() {
        public void run() {
            try {
                mTracker.resetInterface();
                Slog.i(TAG, "$$ mResetInterface call resetInterface()");
            } catch (UnknownHostException e) {
                Slog.e(TAG, "Wrong ethernet configuration");
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                Slog.d(TAG, "ACTION_SCREEN_ON");
                if(getPersistedState() == 2){
                    mDelayedHandler.postDelayed(mResetInterface, 5*1000); // wait 5s for device ready
                }
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Slog.d(TAG, "ACTION_SCREEN_OFF");
                mDelayedHandler.removeCallbacks(mResetInterface);
                mTracker.stopInterface(false);
            }
        }
    };

    public synchronized void setEthState(int state) {
        Slog.i(TAG, "setEthState from " + mEthState + " to "+ state);
		final ContentResolver cr = mContext.getContentResolver();
        if (mEthState != state) {
            mEthState = state;
            if (state == EthernetManager.ETH_STATE_DISABLED) {
                persistEthEnabled(false);
//              mTracker.stopInterface(false);
                new Thread("stopInterface") {
                    @Override
                    public void run() {
                    
                        mTracker.stopInterface(false);
                    }
                }.start();
            } else {
                persistEthEnabled(true);
                if (!isEthConfigured()) {
                    // If user did not configure any interfaces yet, pick the first one
                    // and enable it.
                    setEthMode(EthernetDevInfo.ETH_CONN_MODE_DHCP);
                }
                if (!isEthernetServiceInited){ 
                	Slog.i(TAG, "$$ EthernetService uninited,disable setEthState() call resetInterface()");                
                	Slog.i(TAG, "$$ resetInterface() will be called in reconnect()");                
                }
                else {
                Slog.i(TAG, "$$ setEthState() start thread to resetInterface()"); 
                new Thread("resetInterface") {
                    @Override
                    public void run() {
                        try {
                            mTracker.resetInterface();
                        } catch (UnknownHostException e) {
                            Slog.e(TAG, "Wrong ethernet configuration");
                        }
                    }
                }.start();
                }
            }
        }
    }

    public int getEthState() {
        return mEthState;
    }

    public boolean isEthDeviceUp() {
        try {
            boolean retval = false;
            FileReader fr = new FileReader("/sys/class/net/" + DevName[0] +"/operstate");
            BufferedReader br = new BufferedReader(fr, 32);
            String status = br.readLine();
            if (status != null && status.equals("up")) {
                Slog.d(TAG, "EthDevice status:" + status);
                retval = true;
            }
            else if (status != null && status.equals("down")) {
                Slog.d(TAG, "EthDevice status:" + status);
                retval = false;
            }
            else {
                retval =  false;
            }
            br.close();
            fr.close();
            return retval;
        } catch (IOException e) {
            Slog.d(TAG, "get EthDevice status error");
            return false;
        }
    }

    public boolean isEthDeviceAdded() {
        if (null == DevName || null == DevName[0]) {
            Slog.d(TAG, "isEthDeviceAdded: trigger scanEthDevice");
            scanEthDevice();
        }

        if (null == DevName || null == DevName[0]) {
            Slog.d(TAG, "EthernetNative.isEthDeviceAdded: No Device Found");
            return false;
        }

        int retval = EthernetNative.isInterfaceAdded(DevName[0]);
        Slog.d(TAG, "EthernetNative.isEthDeviceAdded(" + DevName[0] +") return " + (0 == retval));
        if (retval == 0)
            return true;
        else
            return false;
    }

    public DhcpInfo getDhcpInfo() {
        return mTracker.getDhcpInfo();
    }
}
