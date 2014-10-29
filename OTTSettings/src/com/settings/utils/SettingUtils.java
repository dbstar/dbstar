package com.settings.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScheme;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
import android.os.UserHandle;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.settings.bean.UpgradeInfo;
import com.settings.bean.Vapks;
import com.settings.components.NetStatusViewWrapper;
import com.settings.components.SysUpgradeSettingsViewWrapper;
import com.settings.ottsettings.R;
import com.settings.service.OTTSettingsModeService;

public class SettingUtils {

	public static final String IsUpgrading_File = "/data/dbstar/isupgrade.upgrade";
	public static final String Sys_Upgrade_Settings_Progress = "sysUpgradeSettingsProgress";
	public static final String Sys_Auto_Upgrade_Settings_Progress = "sysAutoUpgradeSettingsProgress";

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
//						LogUtil.i("NetWorkState", "Availabel");
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
//		LogUtil.d("DbstarUtil", "getLocalMacAddress");

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
				if (intf.getName().toLowerCase().equals("eth0") || intf.getName().toLowerCase().equals("wlan0")) {
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String ipaddress = inetAddress.getHostAddress().toString();
							LogUtil.e("----++++IpAddress--------", ipaddress);
							if (!ipaddress.contains("::")) {// ipV6的地址
								return ipaddress;
							}
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
		LogUtil.d("SettingUtils", "dns1String = " + dns1String);
//		LogUtil.d("getDNS()::", "dns2String = " + dns2String);
//		LogUtil.d("getDNS()::", "dns1 = " + dns1);
//		LogUtil.d("getDNS()::", "dns2 = " + dns2);
		
		if (dns1String != null && !"".equals(dns1String)) {
			return dns1String;
		} else if (dns2String != null && !"".equals(dns2String)) {
			return dns2String;
		}
		return "";
	}
	
	public static String getAddress(int addr) {
//		LogUtil.d("getAddress::", "addr = " + addr);
//		LogUtil.d("getAddress::", "getHostAddress = " + NetworkUtils.intToInetAddress(addr).getHostAddress());
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
//			LogUtil.d("SettingUtils", "dhcpInfo.gateway = " + gateway);
			LogUtil.d("SettingUtils", "geteway = " + familyNetIP);
			
			int netmask = dhcpInfo.netmask;
			String childNetIP = getAddress(netmask);
//			LogUtil.d("SettingUtils", "dhcpInfo.netmask = " + netmask);
			LogUtil.d("SettingUtils", "netmask = " + childNetIP);
			
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
	 * 解析调用升级接口返回的数据
	 * @param entity
	 * @return 升级信息
	 */
	public static UpgradeInfo parseUpgradeEntity(HttpEntity entity) {
		UpgradeInfo upgradeInfo = new UpgradeInfo();
		
		if (entity == null) {
			return null;
		}
		
		try {
			String entiryString = EntityUtils.toString(entity, "UTF-8");
			JSONObject jsonObject = new JSONObject(entiryString);
			JSONObject json = jsonObject.getJSONObject("Response");
			JSONObject jsonObj = json.getJSONObject("Body");
			JSONArray array = jsonObj.getJSONArray("Vapks");
			JSONObject object = json.getJSONObject("Header");
			
			int rc = object.getInt("RC");
			upgradeInfo.setRc(rc);
			String rm = object.getString("RM");
			upgradeInfo.setRm(rm);
			
			List<Vapks> vapksList = new ArrayList<Vapks>();
			if (rc == 0) {
				for (int i = 0; i < array.length(); i++) {
					Vapks vapks = new Vapks();
					JSONObject obj = array.getJSONObject(i);
					int compressType = obj.getInt("COMPRESSTYPE");
					vapks.setCompressType(compressType);
					String profileUrl = obj.getString("PROFILEURL");
					vapks.setProfileUrl(profileUrl);
					String version = obj.getString("VERSION");
					vapks.setVersion(version);
					String remake = obj.getString("REMARK");
					vapks.setRemake(remake);
					double profileSize = obj.getDouble("PROFILESIZE");
					vapks.setProfileSize(profileSize);
					String vapk = obj.getString("VAPK");
					vapks.setVapk(vapk);
					int upgradeMode = obj.getInt("UPGRADEMODE");
					vapks.setUpgradeMode(upgradeMode);
					String apkProfileName = obj.getString("APKPROFILENAME");
					vapks.setApkProfileName(apkProfileName);
					vapksList.add(vapks);
				}
			}
			upgradeInfo.setVapksList(vapksList);
		} catch (ParseException e) {
			LogUtil.d("SysUpgradeSettingsViewWrapper", "升级：：解析异常" + e);
		} catch (IOException e) {
			LogUtil.d("SysUpgradeSettingsViewWrapper", "升级：：解析异常" + e);
		} catch (JSONException e) {
			LogUtil.d("SysUpgradeSettingsViewWrapper", "升级：：解析异常" + e);
		}
		
		return upgradeInfo;
	}
	
	public static boolean compareSoftVersionAndIsOrNotUpgrade(final String softVersion, final UpgradeInfo upgradeInfo, boolean isNeedUpgrade) {
		// 判断版本号，如果取出的版本号大于本地的，就升级
		if (softVersion == null || softVersion.equals("")) {
			isNeedUpgrade = true;
		} else {
			String[] localSoft = softVersion.split("\\.");
			LogUtil.d("SettingUtils", "localSoft.length = " + localSoft.length);
			List<Vapks> vapksList = upgradeInfo.getVapksList();
			if (vapksList != null && vapksList.size() > 0) {						
				// 只处理一个，这样写只是因为数据结果定义成了多个，其实只有一个升级包
				for (Vapks vapks : vapksList) {
					String newVersion = vapks.getVersion();
					LogUtil.d("SettingUtils", "newVersion = " + newVersion);
					if (newVersion != null && !newVersion.equals("")) {
						String[] newSoft = newVersion.split("\\.");							
						 Pattern pattern = Pattern.compile("[0-9]*"); 
						for (int i = 0; i < newSoft.length; i++) {
							boolean isNum = pattern.matcher(localSoft[i]).matches();
							if (isNum) {
								if (Integer.parseInt(localSoft[i]) < Integer.parseInt(newSoft[i])) {
									isNeedUpgrade = true;
									LogUtil.d("SettingUtils", "Integer.parseInt(localSoft[" + i + "]) = " 
											+ Integer.parseInt(localSoft[i]) + " < Integer.parseInt(newSoft[" + i + "]) = " 
											+ Integer.parseInt(newSoft[i]) + " (need upgrade)");											
									break;
								} else if(Integer.parseInt(localSoft[i]) > Integer.parseInt(newSoft[i])) {
									isNeedUpgrade = false;
									LogUtil.d("SettingUtils", "Integer.parseInt(localSoft[" + i + "]) = " 
											+ Integer.parseInt(localSoft[i]) + " > Integer.parseInt(newSoft[" + i + "]) = " 
											+ Integer.parseInt(newSoft[i]) + " (no need upgrade)");											
									break;
								}
							} else {
								isNeedUpgrade = true;
								LogUtil.d("SettingUtils", "--version is invalid----need upgrade!");											
								break;
							}
						}
					} else {
						isNeedUpgrade = false;
						LogUtil.d("SettingUtils", "need not upgrade!");											
					}
				}
			}						
		}
		return isNeedUpgrade;
	}
	
	/**
	 * 保存文件
	 * @param softVersion 
	 */
	public static boolean SaveFile(Context context,InputStream is, long contentLength, String softVersion, boolean isShow) {
		try {
			// 当已下载文件的大小大于400M时，就不下载了，当做错误文件
			if(contentLength > 419430400){
				LogUtil.d("SettingUtils", "-----too long file, fileTotalSize = " + contentLength);
				is.close();
				// 将“0”写进
				save0ToFile();
				return false;
			}
			LogUtil.d("downloadAndSaveFile", "文件大小" + contentLength);
			
			if (is == null) {
				LogUtil.d("downloadAndSaveFile", "无法获取文件");
				// 将“0”写进
				save0ToFile();
				return false;
			}
			
			if (contentLength <= 0) {
				LogUtil.d("downloadAndSaveFile", "无法获取文件大小");	    	
				// 将“0”写进
				save0ToFile();
				return false;
			}
			
			String filePath = "/cache/upgrade.zip";
			
			File file = new File(filePath);
			
			if (file.exists()) {
				file.delete();
			}
			
			boolean success = file.createNewFile();
			
			if (!success) {
				LogUtil.d("SettingUtils", "-----file create failed!");
				// 将“0”写进
				save0ToFile();
				return false;
			}
			
			FileOutputStream fos = new FileOutputStream(filePath);
			byte[] buf = new byte[1024 * 30];
			
			int numread = 0;
			long has_recv=0l;
			long pin_recv = 0l;
			
			// 将“1”写进
			save1ToFile();
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
				
//				LogUtil.d("SettingUtils", "-----has_recv - pin_recv = " + (has_recv - pin_recv));
				
				if(has_recv - pin_recv > 1024000) {
					LogUtil.d("SettingUtils", "-----pin_recv = " + pin_recv);
					
					Intent intent;
					
					if (isShow)
						intent = new Intent(Sys_Upgrade_Settings_Progress);
					else {
						intent = new Intent(Sys_Auto_Upgrade_Settings_Progress);
						intent.putExtra("fileTotalSize", contentLength);
					}
					
					if (intent != null) {
						intent.putExtra("has_recv", has_recv);
						context.sendBroadcastAsUser(intent, UserHandle.ALL); 						
					}
					pin_recv = has_recv;
				}
				
//				if (!isNetworkAvailable(context)) {
//					save0ToFile();
//				}
			}
			
			fos.flush();
			fos.close();
			is.close();
			
			if (has_recv != contentLength) {
				LogUtil.d("SettingUtils", "-----has_recv = " + has_recv + " contentLength = " + contentLength);
				// 将“0”写进
				save0ToFile();
				return false;
			} else {
				Intent intent;
				if (isShow) {
					intent = new Intent(Sys_Upgrade_Settings_Progress);
				} else {
					intent = new Intent(Sys_Auto_Upgrade_Settings_Progress);
					intent.putExtra("fileTotalSize", contentLength);
				}
				
				if (intent != null) {
					intent.putExtra("has_recv", has_recv);
					LogUtil.d("SettingUtils", "-----has_recv = " + has_recv);
					context.sendBroadcastAsUser(intent, UserHandle.ALL); 
				}
				
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
			// 将“0”写进
			save0ToFile();
		} catch (FileNotFoundException e) {
			save0ToFile();
			e.printStackTrace();
		} catch (IOException e) {
			save0ToFile();
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 将“1”保存在文件中
	 */
	public static void save1ToFile() {
		try {
			File file = new File(IsUpgrading_File);
			if (file.exists())
				file.delete();
			boolean success = file.createNewFile();
			if (success) {				
				LogUtil.d("SettingUtils", " isupgrade.upgrade '1' file createFile successed!");
				String isUpgrade = "1";
				FileOutputStream stream = new FileOutputStream(file);
				stream.write(isUpgrade.getBytes("utf-8"));
				stream.flush();
				stream.close();
			} else 
				LogUtil.d("SettingUtils", " when save isupgrade.upgrade '1' file, createFile failed !");
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将“0”保存在文件中
	 */
	public static void save0ToFile() {
		try {
			File file = new File(IsUpgrading_File);
			if (file.exists())
				file.delete();
			boolean success = file.createNewFile();
			if (success) {
				LogUtil.d("SettingUtils", " isupgrade.upgrade '0' file createFile successed!");
				String notUpgrade = "0";
				FileOutputStream stream = new FileOutputStream(file);
				stream.write(notUpgrade.getBytes("utf-8"));
				stream.flush();
				stream.close();				
			} else
				LogUtil.d("SettingUtils", " when save isupgrade.upgrade '0' file, createFile failed !");
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取文件看是否正在升级
	 */
	public static boolean readIsUpgrade() {
		boolean isUpgrading = false;
		File file = new File(IsUpgrading_File);
		if (!file.exists()) {
			return false;
		}
		
		try {
			int count = 0;
			byte[] buf = new byte[100];
			FileInputStream inputStream = new FileInputStream(file);
			BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
			count = bufferedIn.read(buf, 0, buf.length);
			bufferedIn.close();
			if (count > 0) {
				String values = new String(buf, 0, count);
				LogUtil.d("SettingUtils", " in readUpgradeFile(), values = " + values); 
				
				if (values.equals("1"))
					isUpgrading = true;
				else
					isUpgrading = false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return isUpgrading;
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

		stateList.addState(new int[] { android.R.attr.state_checked, android.R.attr.state_pressed }, checked);
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
