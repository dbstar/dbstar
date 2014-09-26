package com.dbstar.app;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.dbstar.http.HttpConnect;
import com.dbstar.http.SimpleWorkPool.ConnectWork;
import com.dbstar.http.SimpleWorkPool.SimpleWorkPoolInstance;
import com.dbstar.model.GDDataModel;
import com.dbstar.model.GDSystemConfigure;
import com.dbstar.util.Constants;
import com.dbstar.util.DbstarUtil;
import com.dbstar.util.LogUtil;

public class DbstarService extends Service {

//	private CheckNetworkThread thread = null;
//	private EthernetManager mEthManager;
	private EthConnectReceiver connectReceiver;
	
	private boolean isConnected = true;
	// 网络连接状态
	private boolean isNetworkConnected = false;
	
	private MyTimerTask timerTask;
	private Timer timer = new Timer();
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				isConnected = getHeartbeat();
				break;
			case 1:
				// 表示连接成功， 就需要每隔15-30分钟检查一次网络
				isNetworkConnected = DbstarUtil.isNetworkAvailable(DbstarService.this);
				// TODO：如果网络断开
				if (!isNetworkConnected) {
//					ToastUtils.showToast(DbstarService.this, "无法连接网络，请检查网络！");
					isConnected = getHeartbeat();
				}
				break;
			default:
				LogUtil.d("Heartbeat", "心跳接口连接成功");
				break;
			}
		};
	};
	
	@Override
	public void onCreate() {
		// 注册广播，检查网络状态
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(receiver, filter);
		
		// 检查联网情况
		isNetworkConnected = DbstarUtil.isNetworkAvailable(this);
		if (isNetworkConnected) {
			isConnected = getHeartbeat();
		};
		
		super.onCreate();
		
//		mEthManager = this.getSystemService(getApplicationContext().ETHERNET_SERVICE);
//		if (mEthManager.isEthDeviceAdded()) {
//			configEthernet();
//		}
		
//		IntentFilter filter = new IntentFilter(EthernetManager.ETH_STATE_CHANGED_ACTION);
//		filter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
//		registerReceiver(connectReceiver, filter);
		
//		thread = new CheckNetworkThread(this);
//		thread.start();
		LogUtil.e("DbstarService", "service创建");
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				if (timer != null) {
					if (timerTask != null) {
						timerTask.cancel();
					}
					
					timerTask = new MyTimerTask();
					if (isConnected) {
						Random random = new Random();
						int time = random.nextInt(15) + 15; 
						timer.schedule(timerTask, time * 60 * 1000, 60 * 1000);				
					} else {
						if (isNetworkConnected) {
							LogUtil.d("DbstarService", "isNetworkConnected = " + isNetworkConnected);
							DbstarUtil.login(DbstarService.this);
						}
						timer.schedule(timerTask, 60 * 1000, 60 * 1000);
					}
				}
			}
			
		}
	};
	
	private boolean getHeartbeat() {
		GDDataModel dataModel = new GDDataModel();
		GDSystemConfigure mConfigure = new GDSystemConfigure();
		dataModel.initialize(mConfigure);
		String deviceModel = dataModel.getHardwareType();
		String productSN = dataModel.getDeviceSearialNumber();
		LogUtil.d(getClass().getName() + "getHeartbeat", "<<<-----------==========" + deviceModel);
		LogUtil.d(getClass().getName() + "getHeartbeat", "<<<-----------==========" + productSN);	
		
		// 先将参数放入List,再对参数进行URL编码
		List<NameValuePair> paramsList = new LinkedList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("TERMINALUNIQUE", "OEM$" + deviceModel + "$" + productSN));
		paramsList.add(new BasicNameValuePair("TERMINALPROFILE", "1.0.0.0"));
		// TODO：现在没有处理日志
		paramsList.add(new BasicNameValuePair("LOGS", null));
		// 对参数进行编码
		String param = URLEncodedUtils.format(paramsList, "UTF-8");
		
		String url = Constants.Server_Url_Heartbeat + param;
		
		ConnectWork<HashMap<Integer, String>> work = new ConnectWork<HashMap<Integer, String>>(HttpConnect.POST, url, paramsList) {
			
			@Override
			public HashMap<Integer, String> processResult(HttpEntity entity) {
					HashMap<Integer, String> map = null;
					if (entity != null) {
						map = parseHeartbeatResponse(entity);
					}
					return map;
			}
			
			@Override
			public void connectComplete(HashMap<Integer, String> hashMap) {
				
				if (hashMap == null || hashMap.isEmpty()) {
					return;
				}
				
				if (hashMap.containsKey(0)) {
					isConnected = true;
					LogUtil.d("getHeartbeat", "心跳接口调用返回正常，成功");
				} else if (hashMap.containsKey(-2101)) {
					isConnected = false;
					LogUtil.d("getHeartbeat", "心跳接口调用返回异常，终端未登记");						
				} else {
					isConnected = false;
					LogUtil.d("getHeartbeat", "心跳接口调用返回异常,访问失败");											
				}
			}
		};
		SimpleWorkPoolInstance.instance().execute(work);
		return isConnected;
	}
	
	private HashMap<Integer, String> parseHeartbeatResponse(HttpEntity entity) {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		try {
			String entityString = EntityUtils.toString(entity, "UTF-8");
			JSONObject jsonObject = new JSONObject(entityString);
			
			JSONObject json = jsonObject.getJSONObject("Response");
			JSONObject object = json.getJSONObject("Header");
			
			int rc = object.getInt("RC");
			String rm = object.getString("RM");
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
	
	private void configEthernet() {
//		if (mEthManager.isEthConfigured()) {
//			EthernetDevInfo devInfo = mEthManager.getSavedEthConfig();
//			if (devInfo.getConnectMode().equals(EthernetDevInfo.ETH_CONN_MODE_DHCP)) {
//				mEthManager.updateEthDevInfo(devInfo);
//			}
//		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		SimpleWorkPoolInstance.instance().shutdown();
		LogUtil.e("DbstarService", "service销毁，线程池关闭");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			Message message = new Message();
			
			if (isConnected) {
				message.what = 1;
			} else {
				message.what = 2;
			}
			handler.sendMessage(message);
		}
		
	}
	
	private class EthConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			if (action.equals(EthernetManager.ETH_STATE_CHANGED_ACTION)) {
//				updateEth(intent);
//			}
			
		}

		private void updateEth(Intent intent) {
//			int event = intent.getIntExtra(EthernetManager.EXTRA_ETH_STATE, EthernetStateTracker.)
			
		}
		
	}

}
