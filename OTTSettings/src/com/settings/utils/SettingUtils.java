package com.settings.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.AuthScheme;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.settings.components.NetStatusViewWrapper;
import com.settings.components.SysUpgradeSettingsViewWrapper;
import com.settings.ottsettings.R;

public class SettingUtils {


	//**-----------------------关于网络通用的方法-------------------------------**//

	/**
	 * 检查网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			LogUtil.i("NetWorkState", "Unavailabel");
			return false;
		} else {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED || info[i].getState() == NetworkInfo.State.CONNECTING) {
						LogUtil.i("NetWorkState", "Availabel");
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获取本机MAC地址方法
	 * 
	 * @param isEthernet
	 * @return
	 */
	public static String getLocalMacAddress(boolean isEthernet) {
		LogUtil.d("DbstarUtil", "getLocalMacAddress");

		String macAddress = "";
		if (isEthernet) {
			String addressFileName = "/sys/class/net/eth0/address";
			File addressFile = new File(addressFileName);
			if (addressFile.exists()) {
				macAddress = readString(addressFile);
				LogUtil.d("DbstarUtil", "getLocalMacAddress" + macAddress);
			}
		} else {
			String addressFileName = "/sys/class/net/wlan0/address";
			File addressFile = new File(addressFileName);
			if (addressFile.exists()) {
				macAddress = readString(addressFile);
				LogUtil.d("DbstarUtil", "getLocalMacAddress" + macAddress);
			}
		}

		if (macAddress != null && !macAddress.isEmpty()) {
			macAddress = macAddress.toUpperCase();
		}

		return macAddress;
	}

	private static String readString(File file) {
		String value = "";
		int BUFFER_SIZE = 8892;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"), BUFFER_SIZE);
			value = reader.readLine();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return value;
	}

	/**
	 * 获取有线和无线的IP地址
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				// TODO：现在先不管无线 || intf.getName().toLowerCase().equals("wlan0")
				if (intf.getName().toLowerCase().equals("eth0")) {
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String ipaddress = inetAddress.getHostAddress().toString();
							if (!ipaddress.contains("::")) {// ipV6的地址
								return ipaddress;
							}
							LogUtil.e("----++++IpAddress--------", ipaddress);
						}
					}
				} else {
					continue;
				}
			}

		} catch (SocketException e) {
			LogUtil.e("getLocalIpAddress", "获取本机Ip地址方法", e);
		}

		return "";
	}
	
	/**
	 * 获取DNS
	 * @param context
	 * @return
	 */
	public static String getDNS(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		EthernetManager ethernetManager = (EthernetManager)context.getSystemService(Context.ETH_SERVICE);
		int dns1 = 0;
		int dns2 = 0; 
		String dns1String = "";
		String dns2String = ""; 
//		if (wifiManager != null) {
//			dns1 = wifiManager.getDhcpInfo().dns1;
//			dns2 = wifiManager.getDhcpInfo().dns2;
//			
//			dns1String = getAddress(dns1);
//			dns2String = getAddress(dns2);
//		} 
		if (ethernetManager != null) {
			dns1 = ethernetManager.getDhcpInfo().dns1;
			dns2 = ethernetManager.getDhcpInfo().dns2;
			
			dns1String = getAddress(dns1);
			dns2String = getAddress(dns2);
		}
		LogUtil.d("getDNS()::", "dns1String = " + dns1String);
		LogUtil.d("getDNS()::", "dns2String = " + dns2String);
		LogUtil.d("getDNS()::", "dns1 = " + dns1);
		LogUtil.d("getDNS()::", "dns2 = " + dns2);
		
		if (dns1String != null && !"".equals(dns1String)) {
			return dns1String;
		} else if (dns2String != null && !"".equals(dns2String)) {
			return dns2String;
		}
		return "";
	}
	
	public static String getAddress(int addr) {
		LogUtil.d("getAddress::", "addr = " + addr);
		LogUtil.d("getAddress::", "getHostAddress = " + NetworkUtils.intToInetAddress(addr).getHostAddress());
		return NetworkUtils.intToInetAddress(addr).getHostAddress();
	}
	
	/**
	 * 掩码和网关
	 * @param context
	 * @return
	 */
	public static HashMap<String, String> getChildNet(Context context) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		EthernetManager ethernetManager = (EthernetManager)context.getSystemService(Context.ETH_SERVICE);
//		if (wifiManager != null) {
//			DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
//			int familyNet = dhcpInfo.gateway;
////			String familyNetIP = ipToString(familyNet);
//			String familyNetIP = getAddress(familyNet);
//			LogUtil.d("wifiManager----dhcpInfo.gateway:::::", "dhcpInfo.gateway = " + familyNet);
//			LogUtil.d("wifiManager----geteway:::::", "geteway = " + familyNetIP);
//			int childNet = dhcpInfo.netmask;
////			String childNetIP = ipToString(childNet);
//			String childNetIP = getAddress(childNet);
//			LogUtil.d("wifiManager----dhcpInfo.netmask:::::", "dhcpInfo.netmask = " + childNet);
//			LogUtil.d("wifiManager----netmask:::::", "netmask = " + childNetIP);
//			
//			hashMap.put(NetStatusViewWrapper.Family_Net_IP, familyNetIP);
//			hashMap.put(NetStatusViewWrapper.Child_Net_IP, childNetIP);
//		} 
		
		if (ethernetManager != null) {
			DhcpInfo dhcpInfo = ethernetManager.getDhcpInfo();
			int gateway = dhcpInfo.gateway;
			String familyNetIP = getAddress(gateway);
			LogUtil.d("ethernetManager----dhcpInfo.gateway:::::", "dhcpInfo.gateway = " + gateway);
			LogUtil.d("ethernetManager----geteway:::::", "geteway = " + familyNetIP);
			
			int netmask = dhcpInfo.netmask;
			String childNetIP = getAddress(netmask);
			LogUtil.d("ethernetManager----dhcpInfo.netmask:::::", "dhcpInfo.netmask = " + netmask);
			LogUtil.d("ethernetManager----netmask:::::", "netmask = " + childNetIP);
			
			// TODO：
			hashMap.put(NetStatusViewWrapper.Family_Net_IP, familyNetIP);
			hashMap.put(NetStatusViewWrapper.Child_Net_IP, childNetIP);			
		}
		return hashMap;
	}
	
	private static String ipToString(long ip) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.valueOf((int) (ip & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 8) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 16) & 0xff)));
		sb.append('.');
		sb.append(String.valueOf((int) ((ip >> 24) & 0xff)));
		return sb.toString();
	}
	
	
	
	
	//**-------------------------其他通用-------------------------------**//
	/**
	 * 保存文件
	 * @param softVersion 
	 */
	public static boolean SaveFile(InputStream is, long contentLength, String softVersion) {
		try {
			// 当已下载文件的大小大于400M时，就不下载了，当做错误文件
			if(contentLength > 419430400){
				LogUtil.d("SettingUtils", "-----too loong file, fileTotalSize = " + contentLength);
				is.close();
				return false;
			}
			LogUtil.d("downloadAndSaveFile", "文件大小" + contentLength);
			
			if (is == null) {
				LogUtil.d("downloadAndSaveFile", "无法获取文件");
			}
			
			if (contentLength <= 0) {
				LogUtil.d("downloadAndSaveFile", "无法获取文件大小");	    	
			}
			
			String filePath = "/cache/upgrade.zip";
			
			File file = new File(filePath);
			
			if (file.exists()) {
				file.delete();
			}
			
			boolean success = file.createNewFile();
			
			if (!success) {
				LogUtil.d("SettingUtils", "-----file create failed!");
				return false;
			}
			
			FileOutputStream fos = new FileOutputStream(filePath);
			byte[] buf = new byte[1024 * 30];
			
			int numread = 0;
			int has_recv=0;
			int pin_recv = 0;

			while(true) {
				numread = is.read(buf);
				if(-1 == numread) {
					LogUtil.d("SettingUtils", "numread is -1");
					break;
				} else if (0 == numread){
					LogUtil.d("SettingUtils", "numread is 0!!");
				}
				
				fos.write(buf, 0, numread);
				
				has_recv += numread;
				
//				LogUtil.d("SettingUtils", "-----has_recv = " + has_recv);
				
				if(has_recv - pin_recv > 1024000) {
					Message msg = new Message();
					msg.what = 1;
					msg.obj = has_recv;
					LogUtil.d("SettingUtils", "-----pin_recv = " + pin_recv);
					SysUpgradeSettingsViewWrapper.handler.sendMessage(msg);
					
					pin_recv = has_recv;
				}
			}
			
			fos.flush();
			fos.close();
			is.close();
			
			if(has_recv != contentLength){
				LogUtil.d("SettingUtils", "-----has_recv = " + has_recv + " contentLength = " + contentLength);
				return false;
			} else {
				Message msg = new Message();
				msg.what = 1;
				msg.obj = has_recv;
				LogUtil.d("SettingUtils", "-----pin_recv = " + has_recv);
				SysUpgradeSettingsViewWrapper.handler.sendMessage(msg);
			}
			
			// 等升级包下载完成之后，就将string写入/cache/command1文件
			File resultFile = new File("/cache/command1");
			if (resultFile.exists()) {
				resultFile.delete();
			}
			boolean isCreateSuccess = resultFile.createNewFile();
			if (isCreateSuccess) {
				String string = "--orifile=/cache/upgrade.zip\n" + softVersion + "\n";
				FileOutputStream stream = new FileOutputStream(resultFile);
				stream.write(string.getBytes("utf-8"));
				stream.flush();
				stream.close();
				LogUtil.d("SettingUtils", " command1 file createFile successed!");
				return true;
			} else {
				LogUtil.d("SettingUtils", " when save command1 file, createFile failed !");
				return false;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断指定的参数中是否包含空值，如果任何一个参数为空值，则返回true。
	 * 
	 * @param objects
	 *            要测试的参数
	 * @return 是否含有空值
	 * */
	public static boolean hasEmpty(Object... objects) {
		if (objects == null || objects.length == 0) {
			return true;
		}
		for (Object obj : objects) {
			if (isEmpty(obj)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断指定的参数是否为空，包括空值，空字符串，空Collection，空Map，空数组，都会返回true
	 * 
	 * @param obj
	 *            要测试的参数
	 * @return 是否为空
	 * */
	public static boolean isEmpty(Object obj) {
		try {
			if (obj == null) {
				return true;
			}
			if (obj.toString().trim().length() == 0) {
				return true;
			}
			if (obj instanceof Collection<?>) {
				if (((Collection<?>) obj).size() == 0) {
					return true;
				}
			}
			if (obj instanceof Map<?, ?>) {
				if (((Map<?, ?>) obj).size() == 0) {
					return true;
				}
			}
			if (obj instanceof Object[]
					|| obj.getClass().getName().startsWith("[")) {
				int length = Array.getLength(obj);
				if (length == 0) {
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	
	//**---------------------------关于控件通用的方法---------------------------------**//
	
	private static void formatEmptyPlaceholder(Context context, TextView emptyPlaceholder, CharSequence text) {
		emptyPlaceholder.setGravity(Gravity.CENTER_HORIZONTAL);
		emptyPlaceholder.getPaint().setAntiAlias(true);
		emptyPlaceholder.setText(text);
	}
	
	
	/**
	 * 固定中间的图片大小，但按钮实际大小本方法不设置，建议设置一个大于图片的大小，方便点击
	 * 
	 * @param context
	 * @param cb
	 */
	private static void formatCompoundButton(Context context, CompoundButton cb) {
		Drawable checked = context.getResources().getDrawable(R.drawable.on);
		Drawable unchecked = context.getResources().getDrawable(R.drawable.off);
		formatButton(context, cb, checked, unchecked);
	}

	private static void formatButton(Context context, CompoundButton cb,
			Drawable checked, Drawable unchecked) {
		// int width = ScreenAdapter.getInstance().ComputeWidth(34);

		StateListDrawable stateList = new StateListDrawable();

		stateList.addState(new int[] { android.R.attr.state_checked,
				android.R.attr.state_pressed }, checked);
		stateList.addState(new int[] { android.R.attr.state_checked }, checked);
		stateList.addState(new int[] {}, unchecked);
		// stateList.setBounds(0, 0, width, width);

		// int margin = DynamicSize.getMediumGap();
		// 透明背景色
		cb.setBackgroundColor(Color.TRANSPARENT);
		// 透明前景按钮
		cb.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 真正的按钮图片用这个设
		cb.setCompoundDrawables(stateList, null, null, null);
		// cb.setCompoundDrawablePadding(margin);

		// 左边不设margin的话drawable会默认紧贴左，且gravity不起作用。由于已经射了compund
		// padding，就不再设右padding了
		// cb.setPadding(margin, 0, 0, 0);
		// cb.setGravity(Gravity.CENTER);
		// cb.setTextColor(context.getResources().getColor(R.color.text_normal));
		// cb.setTextSize(TypedValue.COMPLEX_UNIT_PX,
		// DynamicSize.getContentFontSize());
	}
}
