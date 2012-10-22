/*
 * Copyright (C) 2010 The Android-x86 Open Source Project
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

package com.dbstar.settings.ethernet;


import java.util.List;

import com.dbstar.settings.R;
import com.dbstar.settings.util.Utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.net.ethernet.EthernetDevInfo;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.util.Slog;

public class EthernetConfigDialog extends AlertDialog implements
        DialogInterface.OnClickListener, DialogInterface.OnShowListener,
        DialogInterface.OnDismissListener {
    private final String TAG = "EthConfDialog";

    private View mView;
    private Spinner mDevList;
    private TextView mDevs;
    private RadioButton mConTypeDhcp;
    private RadioButton mConTypeManual;
    private EditText mIpaddr;
    private EditText mDns;
    private EditText mGw;
    private EditText mMask;

    private EthernetLayer mEthLayer;
    private EthernetManager mEthManager;
    private EthernetDevInfo mEthInfo;
    private boolean mEnablePending;

    private Context mContext;

    public EthernetConfigDialog(Context context, EthernetManager ethManager) {
        super(context);
        mEthManager = ethManager;
        mEthLayer = new EthernetLayer(this, ethManager);
        mContext = context;
        buildDialogContent(context);
        setOnShowListener(this);
        setOnDismissListener(this);
		enableAfterConfig();
    }

    public void onShow(DialogInterface dialog) {
        mEthLayer.resume();
        // soft keyboard pops up on the disabled EditText. Hide it.
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void onDismiss(DialogInterface dialog) {
        mEthLayer.pause();
    }

    private static String getAddress(int addr) {
        return NetworkUtils.intToInetAddress(addr).getHostAddress();
    }

    public int buildDialogContent(Context context) {
        this.setTitle(R.string.eth_config_title);
        this.setView(mView = getLayoutInflater().inflate(R.layout.eth_configure, null));
        mDevs = (TextView) mView.findViewById(R.id.eth_dev_list_text);
        mDevList = (Spinner) mView.findViewById(R.id.eth_dev_spinner);
        mConTypeDhcp = (RadioButton) mView.findViewById(R.id.dhcp_radio);
        mConTypeManual = (RadioButton) mView.findViewById(R.id.manual_radio);
        mIpaddr = (EditText)mView.findViewById(R.id.ipaddr_edit);
        mMask = (EditText)mView.findViewById(R.id.netmask_edit);
        mDns = (EditText)mView.findViewById(R.id.eth_dns_edit);
        mGw = (EditText)mView.findViewById(R.id.eth_gw_edit);

        mConTypeDhcp.setChecked(true);
        mConTypeManual.setChecked(false);
        mIpaddr.setEnabled(false);
        mMask.setEnabled(false);
        mDns.setEnabled(false);
        mGw.setEnabled(false);
        mConTypeManual.setOnClickListener(new RadioButton.OnClickListener() {
            public void onClick(View v) {
                mIpaddr.setEnabled(true);
                mDns.setEnabled(true);
                mGw.setEnabled(true);
                mMask.setEnabled(true);
            }
        });

        mConTypeDhcp.setOnClickListener(new RadioButton.OnClickListener() {
            public void onClick(View v) {
                mIpaddr.setEnabled(false);
                mDns.setEnabled(false);
                mGw.setEnabled(false);
                mMask.setEnabled(false);
            }
        });

        this.setInverseBackgroundForced(true);
        this.setButton(BUTTON_POSITIVE, context.getText(R.string.menu_save), this);
        this.setButton(BUTTON_NEGATIVE, context.getText(R.string.menu_cancel), this);
        String[] Devs = mEthManager.getDeviceNameList();
        updateDevNameList(Devs);
        if (Devs != null) {
            if (mEthManager.isEthConfigured()) {
                String propties = Utils.getEtherProperties(mContext);
                //Slog.d(TAG, "Properties: " + propties);

                mEthInfo = mEthManager.getSavedEthConfig();
                for (int i = 0 ; i < Devs.length; i++) {
                    if (Devs[i].equals(mEthInfo.getIfName())) {
                        mDevList.setSelection(i);
                        break;
                    }
                }
				/*
				 * if (mEthInfo.getConnectMode().equals(EthernetDevInfo.
				 * ETH_CONN_MODE_DHCP)) { DhcpInfo dhcpInfo =
				 * mEthManager.getDhcpInfo(); Slog.d(TAG, "ip  : " +
				 * getAddress(dhcpInfo.ipAddress)); Slog.d(TAG, "gw  : " +
				 * getAddress(dhcpInfo.gateway)); Slog.d(TAG, "mask: " +
				 * getAddress(dhcpInfo.netmask)); Slog.d(TAG, "dns1:" +
				 * getAddress(dhcpInfo.dns1)); Slog.d(TAG, "dns2:" +
				 * getAddress(dhcpInfo.dns2)); }
				 */
                mIpaddr.setText(mEthInfo.getIpAddress());
                mGw.setText(mEthInfo.getRouteAddr());
                mDns.setText(mEthInfo.getDnsAddr());
                mMask.setText(mEthInfo.getNetMask());
                if (mEthInfo.getConnectMode().equals(EthernetDevInfo.ETH_CONN_MODE_DHCP)) {
                    mIpaddr.setEnabled(false);
                    mDns.setEnabled(false);
                    mGw.setEnabled(false);
                    mMask.setEnabled(false);
                } else {
                    mConTypeDhcp.setChecked(false);
                    mConTypeManual.setChecked(true);
                    mIpaddr.setEnabled(true);
                    mDns.setEnabled(true);
                    mGw.setEnabled(true);
                    mMask.setEnabled(true);
                }
            }
        }
        return 0;
    }

    private void handle_saveconf() {
        String selected = null;
        if (mDevList.getSelectedItem() != null)
            selected = mDevList.getSelectedItem().toString();
        if (selected == null || selected.isEmpty())
            return;
        EthernetDevInfo info = new EthernetDevInfo();
        info.setIfName(selected);

        if (mConTypeDhcp.isChecked()) {
            info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP);
            info.setIpAddress(null);
            info.setRouteAddr(null);
            info.setDnsAddr(null);
            info.setNetMask(null);
        } else {
            if (isIpAddress(mIpaddr.getText().toString())
                    && isIpAddress(mGw.getText().toString())
                    && isIpAddress(mDns.getText().toString())
                    && isIpAddress(mMask.getText().toString())) {
                info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_MANUAL);
                info.setIpAddress(mIpaddr.getText().toString());
                info.setRouteAddr(mGw.getText().toString());
                info.setDnsAddr(mDns.getText().toString());
                info.setNetMask(mMask.getText().toString());
            } else {
                Toast.makeText(mContext, R.string.eth_settings_error, Toast.LENGTH_LONG).show();
                return;
            }
        }
        mEthManager.updateEthDevInfo(info);
         if (mEnablePending) {
            	if(mEthManager.getEthState()==mEthManager.ETH_STATE_ENABLED){
					mEthManager.setEthEnabled(true);
				}
            mEnablePending = false;
        }
    }


    private boolean isIpAddress(String value) {
        int start = 0;
        int end = value.indexOf('.');
        int numBlocks = 0;

        while (start < value.length()) {
            if (end == -1) {
                end = value.length();
            }

            try {
                int block = Integer.parseInt(value.substring(start, end));
                if ((block > 255) || (block < 0)) {
                        return false;
                }
            } catch (NumberFormatException e) {
                    return false;
            }

            numBlocks++;

            start = end + 1;
            end = value.indexOf('.', start);
        }
        return numBlocks == 4;
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                handle_saveconf();
                break;
            case BUTTON_NEGATIVE:
                //Don't need to do anything
                break;
            default:
        }
    }

    public void updateDevNameList(String[] DevList) {
        if (DevList == null) {
            DevList = new String[] {};
        }
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                getContext(), android.R.layout.simple_spinner_item, DevList);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        mDevList.setAdapter(adapter);
    }

    public void enableAfterConfig() {
        mEnablePending = true;
    }
}
