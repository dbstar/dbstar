package com.settings.components;

import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo.State;
import android.net.ethernet.EthernetManager;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import com.settings.ottsettings.R;
import com.settings.utils.SettingUtils;

public class NetStatusViewWrapper{
	
	public static String Child_Net_IP = "netmask";
	public static String Family_Net_IP = "gateway";
	
	private TextView txtNetWork, txtNetWorkStatus, txtIPAddress, txtMACAddress, txtChildNet, txtFamilyNet, txtDNS;
	private Context context;
	private int ethernetMode;
	private EthernetManager mEthernetManager;
	
	public NetStatusViewWrapper(Context context, int mode) {
		this.context = context;
		this.ethernetMode = mode;
		mEthernetManager = (EthernetManager) context.getSystemService(Context.ETH_SERVICE);
	}
	
	public void initView(View view) {
		findViews(view);
		
		// 判断网络是有线还是无线
		netWorkStyle();
		
		DataTask task = new DataTask();
		task.execute();
	}

	private void findViews(View view) {
		txtNetWork = (TextView) view.findViewById(R.id.network_settings_network);
		txtNetWorkStatus = (TextView) view.findViewById(R.id.network_settings_network_status);
		txtIPAddress = (TextView) view.findViewById(R.id.network_settings_IP);
		txtMACAddress = (TextView) view.findViewById(R.id.network_settings_MAC);
		txtChildNet = (TextView) view.findViewById(R.id.network_settings_childNet);
		txtFamilyNet = (TextView) view.findViewById(R.id.network_settings_fimalyNet);
		txtDNS = (TextView) view.findViewById(R.id.network_settings_DNS);
	}
	
	/**
	 * 判断网络是有线还是无线
	 */
	private void netWorkStyle() {
		// 得到网络连接信息
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 先判断网络是否连接
		if (manager != null && manager.getActiveNetworkInfo() != null) {
			// 连接之后再判断是无线还是有线
			// wifi
			State wifiState = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			// 以太网
			State wiredState = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
			
			if (wifiState == State.CONNECTED) {
				txtNetWork.setText(context.getResources().getString(R.string.page_net_status_wifi_net));
			}
			
			if (wiredState == State.CONNECTED) {
				txtNetWork.setText(context.getResources().getString(R.string.page_net_status_wired_net));
			}
		} else {
			txtNetWork.setText(context.getResources().getString(R.string.page_net_status_internet));
		}
	}
	
	private class DataTask extends AsyncTask<Void, Void, HashMap<String, String>> {

		@Override
		protected HashMap<String, String> doInBackground(Void... params) {
			HashMap<String, String> map = new HashMap<String, String>();
			boolean isNetworkAvailable = SettingUtils.isNetworkAvailable(context);
			map.put("isNetworkAvailable", isNetworkAvailable + "");
			
			// IP地址
			String ipAddress;
			DhcpInfo dhcpInfo = mEthernetManager.getDhcpInfo();
			ipAddress = SettingUtils.getAddress(dhcpInfo.ipAddress);
//			if (ethernetMode == 1) {
//			} else {
//				ipAddress = SettingUtils.getLocalIpAddress();
//			}
			map.put("ipAdddress", ipAddress);
			
			// MAC地址
			String macAddress = SettingUtils.getLocalMacAddress(true);
			map.put("macAddress", macAddress );
			
			HashMap<String,String> hashMap = SettingUtils.getChildNet(context);
			if (hashMap != null && !hashMap.isEmpty()) {
				// 子网掩码
				String childNetIP = hashMap.get(Child_Net_IP);
				map.put("childNetIP", childNetIP);
				// 家庭网关
				String familyNetIP = hashMap.get(Family_Net_IP);
				map.put("familyNetIP", familyNetIP);				
			}
			
			// DNS
			String dns = SettingUtils.getDNS(context);
			map.put("dns", dns);
			return map;
		}
		
		@Override
		protected void onPostExecute(HashMap<String, String> map) {
			super.onPostExecute(map);
			
			if (map == null || map.isEmpty()) {
				return;
			}
			
			String isNetworkAvailable = map.get("isNetworkAvailable");
			//判断网络是否连接
			if (isNetworkAvailable.equals("true")) { // 已连接
				txtNetWorkStatus.setText(context.getResources().getString(R.string.page_net_status_connected));
			} else { // 不可用
				txtNetWorkStatus.setText(context.getResources().getString(R.string.page_net_status));			
			}
			
			txtIPAddress.setText(map.get("ipAdddress"));
			
			txtMACAddress.setText(map.get("macAddress"));
			
			txtChildNet.setText(map.get("childNetIP"));
			txtFamilyNet.setText(map.get("familyNetIP"));
			
			txtDNS.setText(map.get("dns"));
		}
	}
	
}
