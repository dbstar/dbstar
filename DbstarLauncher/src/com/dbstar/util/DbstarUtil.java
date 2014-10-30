package com.dbstar.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.dbstar.http.HttpConnect;
import com.dbstar.http.SimpleWorkPool.ConnectWork;
import com.dbstar.http.SimpleWorkPool.SimpleWorkPoolInstance;
import com.dbstar.model.GDDataModel;
import com.dbstar.model.GDSystemConfigure;
import com.dbstar.openuser.activity.UserAgreementActivity;

public class DbstarUtil {
	
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
	 *  获取本机MAC地址方法
	 * @param isEthernet
	 * @return
	 */
	 
	public static String getLocalMacAddress(boolean isEthernet) {
		
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
	 * 获取本机IP地址方法
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface inter = en.nextElement();
				for (Enumeration<InetAddress> enumAddress = inter.getInetAddresses(); enumAddress.hasMoreElements();) {
					InetAddress inetAddress = enumAddress.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}

		} catch (SocketException e) {
			LogUtil.e("getLocalIpAddress", "获取本机Ip地址方法", e);
		}
		
		return null;
	}
	
	/**
	 * 判断网络是否连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			LogUtil.i("NetWorkState", "Unavailabel");
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						LogUtil.i("NetWorkState", "Availabel");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取硬盘格式
	 * @return
	 */
	public static String fetchDiskInfo() {
		String result = null;
		CMDExecute cmdExecute = new CMDExecute();
		String[] args = {"ls", "/dev/block/"};
		result = cmdExecute.run(args, "/system/bin/");
		LogUtil.d("fetchDiskInfo()", " result = " + result);
		
//		if (result != null) {
//			if (result.contains("fuseblk") && result.contains("ext4")) {
//				result = "ntfs";
//			} else if (!result.contains("fuseblk") && result.contains("ext4")) {
//				result = "ext4";				
//			}
//		}
//		
//		LogUtil.d("fetchDiskInfo()", "last result = " + result);
		return result;
	}
	
	/**
	 * 保存数据到SD卡中
	 * @param context
	 * @param map
	 * @param fileName
	 */
	public static void saveHashMap(Context context, HashMap<String, String> map, String fileName) {
		
		if (map == null || map.isEmpty()) {
			ToastUtils.showToast(context, "没有要保存的图片和文字!");
			return;
		}
		
		// 保存在本地
		try {
			// 通过openFileOutput方法得到一个输出流，方法参数为创建的文件名（不能有斜杠），操作模式(可以覆盖原有的)
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_WORLD_WRITEABLE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(map);
			oos.flush();
			oos.close(); // 关闭输出流
			LogUtil.d("saveHashMap", " save " + fileName + " success!");
		} catch (FileNotFoundException e) {
			LogUtil.d("saveHashMap", "found Exception = " + e + ", fileName = " + fileName);
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.d("saveHashMap", "found Exception = " + e + ", fileName = " + fileName);
		}
		
		// 如果不存在sd卡就直接返回
		if (!existSDCard()) {
			return;
		}
		
		// 保存在文件中
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			String filePath = "/data/dbstar/" + fileName;
			
			File file = new File(filePath);
			try {
				if (!file.exists()) {
					file.createNewFile();
				} else {
					file.delete();
					file.createNewFile();
				}
			} catch (IOException e) {
				LogUtil.i("saveHsahMap", "make file failed!");
				e.printStackTrace();
			}
			
			try {
				FileOutputStream  fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(map);
				oos.flush();
				oos.close(); // 关闭输出流				
			} catch (FileNotFoundException e) {
				LogUtil.d("savaHashMap", "found exception when save file, and Exception = " + e);
			} catch (IOException e) {
				LogUtil.d("savaHashMap", "found exception when save file, and Exception = " + e);
			}
			LogUtil.d("savaHashMap", "save file success!");
			
		}
	}
	
//	/**
//	 * 从网络上获取图片，如果图片在本地存在的话就直接拿，如果不存在再去服务器上下载图片 这里的path是图片的地址
//	 */
//	public Uri getImageURI(String path, File cache) throws Exception {
//		String name = MD5.getMD5(path) + path.substring(path.lastIndexOf("."));
//		File file = new File(cache, name);
//		// 如果图片存在本地缓存目录，则不去服务器下载
//		if (file.exists()) {
//			return Uri.fromFile(file);// Uri.fromFile(path)这个方法能得到文件的URI
//		} else {
//			
//			ConnectWork<Uri> work = new ConnectWork<Uri>(HttpConnect.GET, path, null) {
//				
//				@Override
//				public Uri processResult(HttpEntity entity) {
//					if (entity != null) {
//						InputStream is = entity.getContent();
//						FileOutputStream fos = new FileOutputStream(file);
//						byte[] buffer = new byte[1024];
//						int len = 0;
//						while ((len = is.read(buffer)) != -1) {
//							fos.write(buffer, 0, len);
//						}
//						is.close();
//						fos.close();
//						// 返回一个URI对象
//						return Uri.fromFile(file);
//					}
//					return null;
//				}
//				
//				@Override
//				public void connectComplete(Uri result) {
//					// TODO Auto-generated method stub
//					
//				}
//			};
//			
//		}
//
//	}
	
	
	
	/**
	 * 从sd卡中取得保存的数据
	 * @return
	 */
	public static HashMap<String, String> readQueryPosterFromSDcard(String filePath) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		
		// 如果不存在sd卡
		if (!existSDCard()) {
			return null;
		}
		
		try {
			FileInputStream fis = new FileInputStream(filePath); // 获得输入流
			ObjectInputStream ois = new ObjectInputStream(fis);
			hashMap = (HashMap<String, String>) ois.readObject();
			ois.close();
			LogUtil.d("readQueryPosterFromSDCard", "read success!");
		} catch (StreamCorruptedException e) {
			LogUtil.d("readQueryPosterFromSDCard", "读取失败");			
			e.printStackTrace();
		} catch (OptionalDataException e) {
			LogUtil.d("readQueryPosterFromSDCard", "读取失败");			
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			LogUtil.d("readQueryPosterFromSDCard", "读取失败");			
			e.printStackTrace();
		} catch (IOException e) {
			LogUtil.d("readQueryPosterFromSDCard", "读取失败");			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			LogUtil.d("readQueryPosterFromSDCard", "读取失败");			
			e.printStackTrace();
		}
		return hashMap;
	}

	/**
	 * 判断sd卡是否存在
	 * @return
	 */
	private static boolean existSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断文件是否存在,  如果存在则删除
	 * 
	 * @return
	 */
	private static boolean fileIsExists(String fileName) {
		String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		try {
			File file = new File(SDCardRoot + fileName);
			if (!file.exists()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			LogUtil.e("fileIsExists", "判断文件是否存在时出现异常");
			return false;
		}
	}
	
	/**
	 * 登录
	 * @param context
	 */
	public static void login(final Context context) {
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure mConfigure = new GDSystemConfigure();
		dataModel.initialize(mConfigure);
		String deviceModel = dataModel.getHardwareType();
		String productSN = dataModel.getDeviceSearialNumber();
		LogUtil.d("login:", "deviceModel = " + deviceModel + ", productSN = " + productSN);
		
		String mac = DbstarUtil.getLocalMacAddress(true);
		// md5加密
		String md5String = MD5.getMD5("OEM$" + deviceModel + "$" + productSN + "$" + mac);

		// 先将参数放入List,再对参数进行URL编码
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		paramsList.add(new BasicNameValuePair("AUTHENTICATOR", md5String));
		// 对参数进行编码
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		String url = Constants.Server_Url_Login + param;
		LogUtil.d("login:", "url = " + url);
		
		ConnectWork<HashMap<Integer,String>> work = new ConnectWork<HashMap<Integer,String>>(HttpConnect.POST, url, paramsList) {
			@Override
			public HashMap<Integer,String> processResult(HttpEntity entity) {
				// Http响应成功，但是需要解析返回的内容，判断rc值，才能知道是否登录成功
				HashMap<Integer, String> map = null;
				if (entity != null) {
					map = parseLoginResponse(entity);
				}
				return map;
			}

			@Override
			public void connectComplete(HashMap<Integer,String> hashMap) {
				
				long lastToAgreement = 0l;
				
				if (hashMap == null || hashMap.isEmpty()) {
					return;
				}
				
				if (hashMap.containsKey(0)) {
					LogUtil.i("login", "登录成功");
				} else if (hashMap.containsKey(-2101)) {
					ToastUtils.showToast(context, "终端未登记");
				} else if (hashMap.containsKey(-2113)) {
					ToastUtils.showToast(context, "MAC地址不匹配");
				} else if (hashMap.containsKey(-2102)) {
					LogUtil.i("login", "未开户");
					
					if (System.currentTimeMillis() - lastToAgreement <  1000l) {
						return;
					}
					lastToAgreement = System.currentTimeMillis();
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setClass(context.getApplicationContext(), UserAgreementActivity.class);
					context.startActivity(intent);
				}
			}
		};
		SimpleWorkPoolInstance.instance().execute(work);
	}
	
	/**
	 * 解析调用登录接口返回的实体
	 * @param entity
	 * @return
	 */
	private static HashMap<Integer, String> parseLoginResponse(HttpEntity entity) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		try {
			String entityString = EntityUtils.toString(entity, "UTF-8");
			JSONObject jsonObject = new JSONObject(entityString);
			
			JSONObject json = jsonObject.getJSONObject("Response");
			JSONObject object = json.getJSONObject("Header");
			
			int rc = object.getInt("RC");
			LogUtil.i("login", "rc = " + rc);
			String rm = object.getString("RM");
			LogUtil.i("login", "rm = " + rm);
			map.put(rc, rm);
		} catch (ParseException e) {
			LogUtil.d("parserLoginResponse::", "解析异常");
		} catch (IOException e) {
			LogUtil.d("parserLoginResponse::", "解析时IO异常");
		} catch (JSONException e) {
			LogUtil.d("parserLoginResponse::", "解析时Json异常");
		}
		return map;
	}
}
